import java.util.List;
import java.util.Scanner;

public class PatientView {

    public static void checkOptions(List<Patient> patients, boolean search, Scanner scanner) {
        int option = 0;
        while (search) {
            try {
                System.out.println("\nSelecione a opção desejada: ");
                System.out.println("0 - Voltar");
                System.out.println("1 - Pesquisar Paciente");
                option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 0: search = false; break;
                    case 1: checkPatientByCPF(patients, scanner); break;
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

    private static void checkPatientByCPF(List<Patient> patients, Scanner scanner) {
        try {
            System.out.print("Informe o CPF do paciente: ");
            String cpf = scanner.nextLine().trim();

            if (!cpf.matches("\\d{11}")) {
                System.out.println("CPF inválido. Deve conter exatamente 11 dígitos.");
                return;
            }

            Patient patientFound = null;
            for (Patient p : patients) {
                if (p.getCpf().equals(cpf)) {
                    patientFound = p;
                    break;
                }
            }

            if (patientFound == null) {
                System.out.println("Paciente não encontrado.");
            } else {
                menu(patientFound, scanner);
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar paciente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void menu(Patient patient, Scanner scanner) {
        int option = 1;
        while (option != 0) {
            try {
                System.out.println("\n**** MENU - Paciente " + patient.getName() + " ***\n");
                System.out.println("0 - Voltar ao Menu Principal");
                System.out.println("1 - Verificar médicos do paciente");
                System.out.println("2 - Consultas realizadas do paciente");
                System.out.println("3 - Consultas agendadas do paciente");
                option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 0: break;
                    case 1: patientsDoctors(patient); break;
                    case 2: appointmentsOccurred(patient.getAppointmentList(), scanner); break;
                    case 3: appointmentsPending(patient.getAppointmentList(), scanner); break;
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

    private static void patientsDoctors(Patient patient) {
        List<String> doctors = Appointment.findAllDoctorsForPatient(patient.getAppointmentList(), patient.getCpf());
        if (doctors.isEmpty()) {
            System.out.println("Não foram encontrados médicos para este paciente.");
        } else {
            System.out.println("Médicos do paciente:");
            for (String doctor : doctors) {
                System.out.println("Médico - " + doctor);
            }
        }
    }

    private static void appointmentsOccurred(List<Appointment> a, Scanner scanner) {
        List<Appointment> appointments = Appointment.filterOccurred(a);
        if (appointments.isEmpty()) {
            System.out.println("Não há consultas realizadas para este paciente.");
        } else {
            System.out.println("Consultas realizadas:");
            paginateList(appointments, 10, scanner);
        }
    }

    private static void appointmentsPending(List<Appointment> a, Scanner scanner) {
        List<Appointment> appointments = Appointment.filterPending(a);
        if (appointments.isEmpty()) {
            System.out.println("Não há consultas agendadas para este paciente.");
        } else {
            System.out.println("Consultas agendadas:");
            paginateList(appointments, 10, scanner);
        }
    }

    // Método de paginação modificado que recebe o scanner como parâmetro
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