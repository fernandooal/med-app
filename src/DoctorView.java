import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DoctorView {

    public static void checkOptions(List<Doctor> doctors, List<Appointment> appointments, List<Patient> patients, boolean search, Scanner scanner) {
        int option = 0;
        while (search) {
            try {
                System.out.println("\nSelecione a opção desejada: ");
                System.out.println("0 - Voltar");
                System.out.println("1 - Pesquisar Médico");
                option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 0: search = false; break;
                    case 1: checkDoctorByCode(doctors, appointments, patients, scanner); break;
                    default: System.out.println("Opção Inválida!"); break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número.");
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void checkDoctorByCode(List<Doctor> doctors, List<Appointment> appointments, List<Patient> patients, Scanner scanner) {
        try {
            List<Doctor> sortedDoctors = new ArrayList<>(doctors);
            sortedDoctors.sort(Comparator.comparing(Doctor::getName));

            System.out.println("\nLista de Médicos:");
            for (int i = 0; i < sortedDoctors.size(); i++) {
                Doctor d = sortedDoctors.get(i);
                System.out.println((i + 1) + " - " + d.getName() + " (CRM: " + d.getCode() + ")");
            }

            System.out.print("\nDigite o número correspondente ao médico: ");
            int selection = Integer.parseInt(scanner.nextLine());

            if (selection < 1 || selection > sortedDoctors.size()) {
                System.out.println("Seleção inválida.");
                return;
            }

            Doctor doctorSelected = sortedDoctors.get(selection - 1);
            menu(doctorSelected, appointments, patients, scanner);
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número válido.");
        } catch (Exception e) {
            System.out.println("Erro ao buscar médico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void menu(Doctor doctor, List<Appointment> allAppointments, List<Patient> allPatients, Scanner scanner) {
        int option = 1;
        while (option != 0) {
            try {
                System.out.println("\n**** MENU - Médico " + doctor.getName() + " ***\n");
                System.out.println("0 - Voltar ao Menu Principal");
                System.out.println("1 - Verificar pacientes atendidos");
                System.out.println("2 - Consultas agendadas em período");
                System.out.println("3 - Pacientes que não consultam há X meses");
                option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 0: break;
                    case 1: showPatients(doctor, allAppointments, allPatients); break;
                    case 2: showAppointmentsByPeriod(doctor, allAppointments, allPatients, scanner); break;
                    case 3: showInactivePatients(doctor, allAppointments, scanner); break;
                    default: System.out.println("Opção Inválida!"); break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número.");
            } catch (Exception e) {
                System.out.println("Erro no menu: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void showPatients(Doctor doctor, List<Appointment> appointments, List<Patient> allPatients) {
        Map<String, String> cpfToName = new HashMap<>();
        for (Patient p : allPatients) {
            cpfToName.put(p.getCpf(), p.getName());
        }

        Set<String> printedCPFs = new HashSet<>();
        List<String> patientInfo = new ArrayList<>();

        for (Appointment appointment : appointments) {
            if (appointment.getDoctorCRM().equals(doctor.getCode())) {
                String cpf = appointment.getPatientCPF();
                if (!printedCPFs.contains(cpf)) {
                    String nome = cpfToName.getOrDefault(cpf, "(Nome não encontrado)");
                    patientInfo.add(formatCPF(cpf) + " - " + nome);
                    printedCPFs.add(cpf);
                }
            }
        }

        System.out.println("Pacientes atendidos pelo médico:");
        if (patientInfo.isEmpty()) {
            System.out.println("Nenhum paciente encontrado.");
        } else {
            patientInfo.forEach(System.out::println);
        }
    }

    private static void showAppointmentsByPeriod(Doctor doctor, List<Appointment> appointments, List<Patient> patients, Scanner scanner) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            System.out.print("Informe a data inicial (yyyy-MM-dd): ");
            LocalDate startDate = LocalDate.parse(scanner.nextLine(), formatter);

            System.out.print("Informe a data final (yyyy-MM-dd): ");
            LocalDate endDate = LocalDate.parse(scanner.nextLine(), formatter);

            List<Appointment> filtered = new ArrayList<>();
            for (Appointment appointment : appointments) {
                if (appointment.getDoctorCRM().equals(doctor.getCode()) &&
                        appointment.isInPeriod(startDate, endDate)) {
                    filtered.add(appointment);
                }
            }

            if (filtered.isEmpty()) {
                System.out.println("Nenhuma consulta encontrada no período informado.");
                return;
            }

            filtered.sort(Comparator.comparing(Appointment::getTime));
            List<String> formattedAppointments = new ArrayList<>();

            for (Appointment ap : filtered) {
                String cpf = ap.getPatientCPF();
                String nome = "(Nome não encontrado)";
                for (Patient p : patients) {
                    if (p.getCpf().equals(cpf)) {
                        nome = p.getName();
                        break;
                    }
                }
                String formattedCPF = formatCPF(cpf);
                formattedAppointments.add(ap.getFormattedDateTime() + " - " + nome + " (" + formattedCPF + ")");
            }

            paginateList(formattedAppointments, 10, scanner);

        } catch (Exception e) {
            System.out.println("Erro ao processar datas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void showInactivePatients(Doctor doctor, List<Appointment> appointments, Scanner scanner) {
        try {
            System.out.print("Informe o número de meses: ");
            int months = Integer.parseInt(scanner.nextLine());

            List<String> inactivePatients = new ArrayList<>();
            for (Appointment appointment : appointments) {
                if (appointment.getDoctorCRM().equals(doctor.getCode()) &&
                        appointment.patientHasNotVisitedFor(months)) {
                    String cpf = appointment.getPatientCPF();
                    if (!inactivePatients.contains(cpf)) {
                        inactivePatients.add(formatCPF(cpf));
                    }
                }
            }

            System.out.println("Pacientes que não consultam há mais de " + months + " meses:");
            if (inactivePatients.isEmpty()) {
                System.out.println("Nenhum paciente encontrado nessa condição.");
            } else {
                paginateList(inactivePatients, 10, scanner);
            }

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número.");
        } catch (Exception e) {
            System.out.println("Erro ao buscar pacientes inativos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String formatCPF(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.substring(0, 3) + "." +
                cpf.substring(3, 6) + "." +
                cpf.substring(6, 9) + "-" +
                cpf.substring(9);
    }

    private static <T> void paginateList(List<T> list, int pageSize, Scanner scanner) {
        if (list == null || list.isEmpty()) {
            System.out.println("Nenhum item para exibir.");
            return;
        }

        try {
            int total = list.size();
            int pages = (int) Math.ceil((double) total / pageSize);
            int page = 0;

            while (page < pages) {
                int start = page * pageSize;
                int end = Math.min(start + pageSize, total);

                System.out.println("\nPágina " + (page + 1) + "/" + pages);
                for (int i = start; i < end; i++) {
                    System.out.println(list.get(i));
                }

                if (page < pages - 1) { // Se não for a última página
                    System.out.print("Deseja ver a próxima página? (s/n): ");
                    String input = scanner.nextLine();
                    if (!input.equalsIgnoreCase("s")) break;
                }

                page++;
            }
        } catch (Exception e) {
            System.out.println("Erro durante a paginação: " + e.getMessage());
            e.printStackTrace();
        }
    }
}