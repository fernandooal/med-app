import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;


/**
 * Classe responsável pela interface com o usuário para a visão do médico.
 * Gerencia as funcionalidades disponíveis para médicos no sistema, como
 * visualização de pacientes e consultas.
 */
public class DoctorView {
    // Constantes para arquivos
    private static final String PATIENT_CSV = "patients.csv";
    private static final String APPOINTMENT_CSV = "appointments.csv";
    private static final String DOCTOR_CSV = "doctors_clean.csv";

    /**
     * Exibe as opções disponíveis para o médico e processa a seleção do usuário
     *
     * @param doctors Lista de médicos cadastrados no sistema
     * @param appointments Lista de consultas marcadas
     * @param patients Lista de pacientes cadastrados
     * @param search Flag para controlar o loop do menu
     * @param scanner Scanner para leitura de entrada do usuário
     */
    public static void checkOptions(List<Doctor> doctors, List<Appointment> appointments, List<Patient> patients, boolean search, Scanner scanner) {
        while (search) {
            try {
                // Solicitar CRM ao médico
                System.out.print("Digite seu CRM (somente números): ");
                String crm = scanner.nextLine().trim();

                if (!crm.matches("\\d+")) {
                    System.out.println("CRM inválido. Deve conter apenas números.");
                    continue;
                }

                // Buscar médico pelo CRM
                Doctor doctor = findDoctorByCRM(doctors, crm);

                if (doctor == null) {
                    System.out.println("Médico não encontrado. Deseja tentar novamente? (s/n): ");
                    String retry = scanner.nextLine();
                    if (!retry.equalsIgnoreCase("s")) {
                        search = false;
                    }
                    continue;
                }

                // Dar boas-vindas ao médico e mostrar o menu principal
                System.out.println("\nBem-vindo(a), Dr(a). " + doctor.getName() + "!");

                // Mostrar o menu para o médico
                doctorMenu(doctor, appointments, patients, scanner);
                search = false;

            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }
    }

    /**
     * Busca um médico pelo CRM na lista de médicos
     *
     * @param doctors Lista de médicos
     * @param crm CRM a ser buscado
     * @return Médico encontrado ou null se não encontrado
     */
    private static Doctor findDoctorByCRM(List<Doctor> doctors, String crm) {
        for (Doctor d : doctors) {
            if (d.getCode().equals(crm)) {
                return d;
            }
        }
        return null;
    }

    /**
     * Exibe o menu principal para o médico e processa a opção escolhida
     *
     * @param doctor Médico logado
     * @param allAppointments Todas as consultas do sistema
     * @param allPatients Todos os pacientes do sistema
     * @param scanner Scanner para leitura de entrada do usuário
     */
    private static void doctorMenu(Doctor doctor, List<Appointment> allAppointments, List<Patient> allPatients, Scanner scanner) {
        boolean continueMenu = true;

        while (continueMenu) {
            try {
                System.out.println("\n===== MENU DO MÉDICO =====");
                System.out.println("1 - Agendar nova consulta para paciente");
                System.out.println("2 - Ver consultas agendadas (futuras)");
                System.out.println("3 - Ver consultas realizadas");
                System.out.println("4 - Remarcar consultas");
                System.out.println("5 - Cancelar consultas agendadas");
                System.out.println("6 - Ver todos os pacientes atendidos");
                System.out.println("7 - Ver pacientes sem consulta há mais de N meses");
                System.out.println("0 - Sair");
                System.out.print("\nEscolha uma opção: ");

                int option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 0:
                        continueMenu = false;
                        break;
                    case 1:
                        scheduleNewAppointment(doctor, allAppointments, allPatients, scanner);
                        break;
                    case 2:
                        viewFutureAppointments(doctor, allAppointments, allPatients, scanner);
                        break;
                    case 3:
                        viewPastAppointments(doctor, allAppointments, allPatients, scanner);
                        break;
                    case 4:
                        rescheduleAppointmentForDoctor(doctor, allAppointments, allPatients, scanner);
                        break;
                    case 5:
                        cancelAppointmentForDoctor(doctor, allAppointments, allPatients, scanner);
                        break;
                    case 6:
                        viewAllDoctorPatients(doctor, allAppointments, allPatients, scanner);
                        break;
                    case 7:
                        viewPatientsWithoutRecentAppointment(doctor, allAppointments, allPatients, scanner);
                        break;
                    default:
                        System.out.println("Opção inválida!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número.");
            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }
    }

    /**
     * Agenda uma nova consulta para um paciente (existente ou novo)
     *
     * @param doctor Médico que está agendando a consulta
     * @param allAppointments Lista de todas as consultas
     * @param allPatients Lista de todos os pacientes
     * @param scanner Scanner para leitura de entrada
     */
    private static void scheduleNewAppointment(Doctor doctor, List<Appointment> allAppointments, List<Patient> allPatients, Scanner scanner) {
        System.out.println("\n=== AGENDAR NOVA CONSULTA ===");
        System.out.println("1 - Para paciente existente");
        System.out.println("2 - Para novo paciente");
        System.out.println("0 - Voltar");
        System.out.print("Escolha uma opção: ");

        try {
            int option = Integer.parseInt(scanner.nextLine());

            Patient patient;

            switch (option) {
                case 0:
                    return;
                case 1:
                    patient = PatientView.selectExistingPatient(allPatients, scanner);
                    break;
                case 2:
                    patient = PatientView.registerNewPatient(scanner);
                    if (patient != null) {
                        allPatients.add(patient);
                    }
                    break;
                default:
                    System.out.println("Opção inválida!");
                    return;
            }

            if (patient == null) {
                return;
            }

            // Obter data da consulta
            System.out.print("Digite a data da consulta (yyyy-MM-dd): ");
            String dateStr = scanner.nextLine();

            // Obter hora da consulta
            System.out.print("Digite o horário da consulta (HH:mm): ");
            String timeStr = scanner.nextLine();

            try {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                LocalDate appointmentDate = LocalDate.parse(dateStr, dateFormatter);
                LocalTime appointmentTime = LocalTime.parse(timeStr, timeFormatter);

                // Verificar se a data é no futuro
                if (appointmentDate.isBefore(LocalDate.now())) {
                    System.out.println("A data da consulta deve ser futura.");
                    return;
                }

                // Verificar se já existe consulta no mesmo horário para o médico
                boolean conflictFound = false;
                for (Appointment app : allAppointments) {
                    if (app.getDoctorCRM().equals(doctor.getCode()) &&
                            app.getDate().equals(appointmentDate) &&
                            app.getTime().equals(appointmentTime) &&
                            app.getStatus() == AppointmentStatus.PENDING) {
                        conflictFound = true;
                        break;
                    }
                }

                if (conflictFound) {
                    System.out.println("Já existe uma consulta agendada neste horário.");
                    return;
                }

                // Criar e salvar a nova consulta
                Appointment appointment = new Appointment(
                        appointmentDate,
                        appointmentTime,
                        patient.getCpf(),
                        doctor.getCode(),
                        AppointmentStatus.PENDING
                );

                appointment.saveToCSVFile(APPOINTMENT_CSV, true);

                // Adicionar a consulta à lista geral
                allAppointments.add(appointment);

                System.out.println("\nConsulta agendada com sucesso!");
                System.out.println("Paciente: " + patient.getName());
                System.out.println("Data e hora: " + appointment.getFormattedDateTime());

            } catch (DateTimeParseException e) {
                System.out.println("Formato de data ou hora inválido: " + e.getMessage());
            }

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número.");
        }
    }

    /**
     * Exibe as consultas futuras (agendadas) do médico com opções de gerenciamento
     *
     * @param doctor Médico logado
     * @param allAppointments Todas as consultas
     * @param allPatients Todos os pacientes
     * @param scanner Scanner para leitura
     */
    private static void viewFutureAppointments(Doctor doctor, List<Appointment> allAppointments, List<Patient> allPatients, Scanner scanner) {
        List<Appointment> futureAppointments = new ArrayList<>();

        // Filtrar consultas pendentes (futuras) do médico
        for (Appointment app : allAppointments) {
            if (app.getDoctorCRM().equals(doctor.getCode()) && app.getStatus() == AppointmentStatus.PENDING) {
                futureAppointments.add(app);
            }
        }

        if (futureAppointments.isEmpty()) {
            System.out.println("Não há consultas agendadas para você.");
            return;
        }

        // Ordenar por data/hora
        futureAppointments.sort(Comparator.comparing(Appointment::getDate)
                .thenComparing(Appointment::getTime));

        System.out.println("\nSuas consultas agendadas:");

        for (int i = 0; i < futureAppointments.size(); i++) {
            Appointment app = futureAppointments.get(i);
            String patientName = getPatientName(allPatients, app.getPatientCPF());
            System.out.println((i + 1) + " - " + app.getFormattedDateTime() + " - Paciente: " + patientName);
        }

        System.out.println("\nDeseja gerenciar alguma consulta? (s/n): ");
        String response = scanner.nextLine();

        if (response.equalsIgnoreCase("s")) {
            System.out.print("Digite o número da consulta: ");
            try {
                int selection = Integer.parseInt(scanner.nextLine()) - 1;

                if (selection < 0 || selection >= futureAppointments.size()) {
                    System.out.println("Seleção inválida.");
                    return;
                }

                Appointment selectedAppointment = futureAppointments.get(selection);

                System.out.println("\nO que deseja fazer com esta consulta?");
                System.out.println("1 - Confirmar consulta");
                System.out.println("2 - Cancelar consulta");
                System.out.println("3 - Remarcar consulta");
                System.out.println("0 - Voltar");

                int action = Integer.parseInt(scanner.nextLine());

                switch (action) {
                    case 0:
                        return;
                    case 1:
                        System.out.println("Consulta confirmada para " + selectedAppointment.getFormattedDateTime());
                        break;
                    case 2:
                        Appointment.cancelAppointment(selectedAppointment, allAppointments);
                        break;
                    case 3:
                        rescheduleAppointment(selectedAppointment, doctor, allAppointments, scanner);
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número.");
            }
        }
    }

    /**
     * Exibe as consultas já realizadas pelo médico
     *
     * @param doctor Médico logado
     * @param allAppointments Todas as consultas
     * @param allPatients Todos os pacientes
     * @param scanner Scanner para leitura
     */
    private static void viewPastAppointments(Doctor doctor, List<Appointment> allAppointments, List<Patient> allPatients, Scanner scanner) {
        System.out.println("\n=== CONSULTAR HISTÓRICO DE CONSULTAS ===");
        System.out.println("Selecione o período:");
        System.out.println("1 - Última semana");
        System.out.println("2 - Últimos 30 dias");
        System.out.println("3 - Últimos 90 dias");
        System.out.println("4 - Últimos 180 dias");
        System.out.println("5 - Período personalizado");
        System.out.println("0 - Voltar");
        System.out.print("Escolha uma opção: ");

        try {
            int option = Integer.parseInt(scanner.nextLine());

            if (option == 0) {
                return;
            }

            LocalDate endDate = LocalDate.now();
            LocalDate startDate;

            switch (option) {
                case 1: // Última semana
                    startDate = endDate.minusWeeks(1);
                    break;
                case 2: // Últimos 30 dias
                    startDate = endDate.minusDays(30);
                    break;
                case 3: // Últimos 90 dias
                    startDate = endDate.minusDays(90);
                    break;
                case 4: // Últimos 180 dias
                    startDate = endDate.minusDays(180);
                    break;
                case 5: // Período personalizado
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                        System.out.print("Digite a data inicial (yyyy-MM-dd): ");
                        startDate = LocalDate.parse(scanner.nextLine(), formatter);

                        System.out.print("Digite a data final (yyyy-MM-dd): ");
                        endDate = LocalDate.parse(scanner.nextLine(), formatter);
                    } catch (DateTimeParseException e) {
                        System.out.println("Formato de data inválido: " + e.getMessage());
                        return;
                    }
                    break;
                default:
                    System.out.println("Opção inválida!");
                    return;
            }

            showAppointmentsByPeriod(doctor, allAppointments, allPatients, startDate, endDate, scanner);

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número.");
        }
    }

    /**
     * Exibe consultas de um médico em um período específico
     *
     * @param doctor Médico selecionado
     * @param allAppointments Todas as consultas
     * @param allPatients Todos os pacientes
     * @param startDate Data inicial do período
     * @param endDate Data final do período
     * @param scanner Scanner para leitura de entrada
     */
    private static void showAppointmentsByPeriod(Doctor doctor, List<Appointment> allAppointments, List<Patient> allPatients,
                                                 LocalDate startDate, LocalDate endDate, Scanner scanner) {
        List<Appointment> filtered = new ArrayList<>();

        for (Appointment appointment : allAppointments) {
            if (appointment.getDoctorCRM().equals(doctor.getCode()) &&
                    (appointment.getStatus() == AppointmentStatus.COMPLETED ||
                            (appointment.hasOccurred() && appointment.getStatus() != AppointmentStatus.CANCELLED)) &&
                    appointment.isInPeriod(startDate, endDate)) {
                filtered.add(appointment);
            }
        }

        if (filtered.isEmpty()) {
            System.out.println("Nenhuma consulta encontrada no período informado.");
            return;
        }

        // Ordenar por data/hora (mais recente primeiro)
        filtered.sort(Comparator.comparing(Appointment::getDate)
                .thenComparing(Appointment::getTime).reversed());

        System.out.println("\nConsultas realizadas no período de " +
                startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " a " +
                endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ":");

        List<String> formattedAppointments = new ArrayList<>();
        for (Appointment ap : filtered) {
            String patientName = getPatientName(allPatients, ap.getPatientCPF());
            formattedAppointments.add(ap.getFormattedDateTime() + " - Paciente: " + patientName +
                    " (CPF: " + UIUtils.formatCPF(ap.getPatientCPF()) + ")");
        }

        UIUtils.paginateList(formattedAppointments, 10, scanner);
    }

    /**
     * Interface para remarcar consultas do médico
     *
     * @param doctor Médico logado
     * @param allAppointments Todas as consultas
     * @param allPatients Todos os pacientes
     * @param scanner Scanner para leitura
     */
    private static void rescheduleAppointmentForDoctor(Doctor doctor, List<Appointment> allAppointments, List<Patient> allPatients, Scanner scanner) {
        System.out.println("\n=== REMARCAR CONSULTAS ===");
        System.out.println("Selecione o filtro:");
        System.out.println("1 - Consultas da semana");
        System.out.println("2 - Consultas do mês");
        System.out.println("3 - Consultas por paciente");
        System.out.println("0 - Voltar");
        System.out.print("Escolha uma opção: ");

        try {
            int option = Integer.parseInt(scanner.nextLine());

            if (option == 0) {
                return;
            }

            List<Appointment> filteredAppointments = new ArrayList<>();
            LocalDate today = LocalDate.now();

            switch (option) {
                case 1: // Consultas da semana
                    LocalDate endOfWeek = today.plusDays(7);
                    for (Appointment app : allAppointments) {
                        if (app.getDoctorCRM().equals(doctor.getCode()) &&
                                app.getStatus() == AppointmentStatus.PENDING &&
                                app.isInPeriod(today, endOfWeek)) {
                            filteredAppointments.add(app);
                        }
                    }
                    break;
                case 2: // Consultas do mês
                    LocalDate endOfMonth = today.plusMonths(1);
                    for (Appointment app : allAppointments) {
                        if (app.getDoctorCRM().equals(doctor.getCode()) &&
                                app.getStatus() == AppointmentStatus.PENDING &&
                                app.isInPeriod(today, endOfMonth)) {
                            filteredAppointments.add(app);
                        }
                    }
                    break;
                case 3: // Consultas por paciente
                    Patient patient = PatientView.selectExistingPatient(allPatients, scanner);
                    if (patient == null) {
                        return;
                    }

                    for (Appointment app : allAppointments) {
                        if (app.getDoctorCRM().equals(doctor.getCode()) &&
                                app.getPatientCPF().equals(patient.getCpf()) &&
                                app.getStatus() == AppointmentStatus.PENDING) {
                            filteredAppointments.add(app);
                        }
                    }
                    break;
                default:
                    System.out.println("Opção inválida!");
                    return;
            }

            if (filteredAppointments.isEmpty()) {
                System.out.println("Nenhuma consulta encontrada para o filtro selecionado.");
                return;
            }

            // Ordenar por data/hora
            filteredAppointments.sort(Comparator.comparing(Appointment::getDate)
                    .thenComparing(Appointment::getTime));

            System.out.println("\nConsultas disponíveis para remarcação:");
            for (int i = 0; i < filteredAppointments.size(); i++) {
                Appointment app = filteredAppointments.get(i);
                String patientName = getPatientName(allPatients, app.getPatientCPF());
                System.out.println((i + 1) + " - " + app.getFormattedDateTime() +
                        " - Paciente: " + patientName);
            }

            System.out.print("\nDigite o número da consulta para remarcar (0 para voltar): ");
            int selection = Integer.parseInt(scanner.nextLine());

            if (selection == 0) {
                return;
            }

            if (selection < 1 || selection > filteredAppointments.size()) {
                System.out.println("Seleção inválida.");
                return;
            }

            Appointment selectedAppointment = filteredAppointments.get(selection - 1);
            rescheduleAppointment(selectedAppointment, doctor, allAppointments, scanner);

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número.");
        }
    }

    /**
     * Executa a remarcação de uma consulta
     *
     * @param appointment Consulta a ser remarcada
     * @param doctor Médico dono da consulta
     * @param allAppointments Todas as consultas
     * @param scanner Scanner para leitura
     */
    private static void rescheduleAppointment(Appointment appointment, Doctor doctor, List<Appointment> allAppointments, Scanner scanner) {
        try {
            // Obter nova data
            System.out.print("Digite a nova data da consulta (yyyy-MM-dd): ");
            String dateStr = scanner.nextLine();

            // Obter nova hora
            System.out.print("Digite o novo horário da consulta (HH:mm): ");
            String timeStr = scanner.nextLine();

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            LocalDate newDate = LocalDate.parse(dateStr, dateFormatter);
            LocalTime newTime = LocalTime.parse(timeStr, timeFormatter);

            // Verificar se a data é no futuro
            if (newDate.isBefore(LocalDate.now())) {
                System.out.println("A data da consulta deve ser futura.");
                return;
            }

            // Verificar se já existe consulta no mesmo horário para o médico
            boolean conflictFound = false;
            for (Appointment app : allAppointments) {
                if (app != appointment &&
                        app.getDoctorCRM().equals(doctor.getCode()) &&
                        app.getDate().equals(newDate) &&
                        app.getTime().equals(newTime) &&
                        app.getStatus() == AppointmentStatus.PENDING) {
                    conflictFound = true;
                    break;
                }
            }

            if (conflictFound) {
                System.out.println("Já existe uma consulta agendada neste horário.");
                return;
            }

            // Remover a consulta antiga
            int indexToRemove = -1;
            for (int i = 0; i < allAppointments.size(); i++) {
                Appointment app = allAppointments.get(i);
                if (app.getDate().equals(appointment.getDate()) &&
                        app.getTime().equals(appointment.getTime()) &&
                        app.getPatientCPF().equals(appointment.getPatientCPF()) &&
                        app.getDoctorCRM().equals(appointment.getDoctorCRM())) {

                    indexToRemove = i;
                    break;
                }
            }

            if (indexToRemove != -1) {
                // Criar nova consulta com os mesmos dados, exceto data e hora
                Appointment newAppointment = new Appointment(
                        newDate,
                        newTime,
                        appointment.getPatientCPF(),
                        appointment.getDoctorCRM(),
                        AppointmentStatus.PENDING
                );

                // Substituir na lista
                allAppointments.set(indexToRemove, newAppointment);

                // Salvar a lista atualizada
                Appointment.saveAppointmentsToCSV(allAppointments, APPOINTMENT_CSV);

                System.out.println("Consulta remarcada com sucesso!");
                System.out.println("Nova data e hora: " + newAppointment.getFormattedDateTime());
            } else {
                System.out.println("Erro: Consulta não encontrada na lista.");
            }

        } catch (DateTimeParseException e) {
            System.out.println("Formato de data ou hora inválido: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    /**
     * Interface para cancelar consultas agendadas do médico
     *
     * @param doctor Médico logado
     * @param allAppointments Todas as consultas
     * @param allPatients Todos os pacientes
     * @param scanner Scanner para leitura
     */
    private static void cancelAppointmentForDoctor(Doctor doctor, List<Appointment> allAppointments, List<Patient> allPatients, Scanner scanner) {
        List<Appointment> futureAppointments = new ArrayList<>();

        // Filtrar consultas pendentes (futuras) do médico
        for (Appointment app : allAppointments) {
            if (app.getDoctorCRM().equals(doctor.getCode()) && app.getStatus() == AppointmentStatus.PENDING) {
                futureAppointments.add(app);
            }
        }

        if (futureAppointments.isEmpty()) {
            System.out.println("Não há consultas agendadas para cancelar.");
            return;
        }

        // Ordenar por data/hora
        futureAppointments.sort(Comparator.comparing(Appointment::getDate)
                .thenComparing(Appointment::getTime));

        System.out.println("\nConsultas que podem ser canceladas:");

        for (int i = 0; i < futureAppointments.size(); i++) {
            Appointment app = futureAppointments.get(i);
            String patientName = getPatientName(allPatients, app.getPatientCPF());
            System.out.println((i + 1) + " - " + app.getFormattedDateTime() + " - Paciente: " + patientName);
        }

        System.out.print("\nDigite o número da consulta para cancelar (0 para voltar): ");
        try {
            int selection = Integer.parseInt(scanner.nextLine());

            if (selection == 0) {
                return;
            }

            if (selection < 1 || selection > futureAppointments.size()) {
                System.out.println("Seleção inválida.");
                return;
            }

            Appointment selectedAppointment = futureAppointments.get(selection - 1);

            System.out.println("\nTem certeza que deseja cancelar a consulta de " +
                    getPatientName(allPatients, selectedAppointment.getPatientCPF()) +
                    " em " + selectedAppointment.getFormattedDateTime() + "? (s/n): ");
            String confirm = scanner.nextLine();

            if (confirm.equalsIgnoreCase("s")) {
                Appointment.cancelAppointment(selectedAppointment, allAppointments);
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número.");
        }
    }

    /**
     * Exibe todos os pacientes que já foram atendidos pelo médico
     *
     * @param doctor Médico logado
     * @param allAppointments Todas as consultas
     * @param allPatients Todos os pacientes
     * @param scanner Scanner para leitura
     */
    private static void viewAllDoctorPatients(Doctor doctor, List<Appointment> allAppointments, List<Patient> allPatients, Scanner scanner) {
        // Conjunto para evitar duplicatas
        Set<String> patientCPFs = new HashSet<>();

        // Encontrar todos os CPFs dos pacientes atendidos pelo médico
        for (Appointment app : allAppointments) {
            if (app.getDoctorCRM().equals(doctor.getCode())) {
                patientCPFs.add(app.getPatientCPF());
            }
        }

        if (patientCPFs.isEmpty()) {
            System.out.println("Você ainda não atendeu nenhum paciente.");
            return;
        }

        // Criar lista de pacientes do médico
        List<Patient> doctorPatients = new ArrayList<>();
        for (String cpf : patientCPFs) {
            for (Patient p : allPatients) {
                if (p.getCpf().equals(cpf)) {
                    doctorPatients.add(p);
                    break;
                }
            }
        }

        // Ordenar alfabeticamente
        doctorPatients.sort(Comparator.comparing(Patient::getName));

        System.out.println("\nSeus pacientes (ordem alfabética):");

        for (int i = 0; i < doctorPatients.size(); i++) {
            Patient p = doctorPatients.get(i);
            System.out.println((i + 1) + " - " + p.getName() + " (CPF: " + UIUtils.formatCPF(p.getCpf()) + ")");
        }

        // Opção para ver detalhes de um paciente específico
        System.out.print("\nDeseja ver detalhes de algum paciente? (s/n): ");
        String response = scanner.nextLine();

        if (response.equalsIgnoreCase("s")) {
            System.out.print("Digite o número do paciente: ");
            int selection = Integer.parseInt(scanner.nextLine()) - 1;

            if (selection >= 0 && selection < doctorPatients.size()) {
                Patient selectedPatient = doctorPatients.get(selection);
                showPatientDetails(selectedPatient, doctor, allAppointments, scanner);
            } else {
                System.out.println("Seleção inválida.");
            }
        }
    }

    /**
     * Mostra detalhes de um paciente específico
     */
    private static void showPatientDetails(Patient patient, Doctor doctor, List<Appointment> allAppointments, Scanner scanner) {
        System.out.println("\n=== DETALHES DO PACIENTE ===");
        System.out.println("Nome: " + patient.getName());
        System.out.println("CPF: " + UIUtils.formatCPF(patient.getCpf()));

        // Filtrar consultas deste paciente com este médico
        List<Appointment> patientAppointments = new ArrayList<>();
        for (Appointment app : allAppointments) {
            if (app.getPatientCPF().equals(patient.getCpf()) &&
                    app.getDoctorCRM().equals(doctor.getCode())) {
                patientAppointments.add(app);
            }
        }

        if (patientAppointments.isEmpty()) {
            System.out.println("Nenhuma consulta encontrada para este paciente.");
            return;
        }

        // Ordenar por data (mais recente primeiro)
        patientAppointments.sort(Comparator.comparing(Appointment::getDate)
                .thenComparing(Appointment::getTime).reversed());

        System.out.println("\nHistórico de consultas:");
        for (Appointment app : patientAppointments) {
            System.out.println("- " + app.getFormattedDateTime() +
                    " | Status: " + app.getStatus().getDescription());
        }
    }

    /**
     * Exibe os pacientes que não consultam com o médico há mais que um determinado tempo
     *
     * @param doctor Médico logado
     * @param allAppointments Todas as consultas
     * @param allPatients Todos os pacientes
     * @param scanner Scanner para leitura
     */
    private static void viewPatientsWithoutRecentAppointment(Doctor doctor, List<Appointment> allAppointments, List<Patient> allPatients, Scanner scanner) {
        System.out.print("Informe o número de meses: ");
        int months;
        try {
            months = Integer.parseInt(scanner.nextLine());
            if (months <= 0) {
                System.out.println("Por favor, digite um número positivo de meses.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número inteiro.");
            return;
        }

        // Data de corte (hoje menos o número de meses)
        LocalDate cutoffDate = LocalDate.now().minusMonths(months);

        // Mapa para armazenar a data da última consulta de cada paciente
        Map<String, LocalDate> lastAppointmentDates = new HashMap<>();

        // Conjunto para todos os CPFs de pacientes atendidos pelo médico
        Set<String> allPatientCPFs = new HashSet<>();

        // Encontrar a data da última consulta para cada paciente
        for (Appointment app : allAppointments) {
            if (app.getDoctorCRM().equals(doctor.getCode())) {
                String cpf = app.getPatientCPF();
                allPatientCPFs.add(cpf);

                // Verificar se a consulta não foi cancelada
                if (app.getStatus() != AppointmentStatus.CANCELLED) {
                    LocalDate appointmentDate = app.getDate();

                    // Atualizar a data mais recente
                    if (!lastAppointmentDates.containsKey(cpf) ||
                            appointmentDate.isAfter(lastAppointmentDates.get(cpf))) {
                        lastAppointmentDates.put(cpf, appointmentDate);
                    }
                }
            }
        }

        // Filtrar pacientes sem consulta recente
        List<String> inactiveCPFs = new ArrayList<>();
        for (String cpf : allPatientCPFs) {
            if (!lastAppointmentDates.containsKey(cpf) ||
                    lastAppointmentDates.get(cpf).isBefore(cutoffDate)) {
                inactiveCPFs.add(cpf);
            }
        }

        if (inactiveCPFs.isEmpty()) {
            System.out.println("Todos os seus pacientes tiveram consultas nos últimos " + months + " meses.");
            return;
        }

        // Obter os objetos Patient correspondentes
        List<Patient> inactivePatients = new ArrayList<>();
        for (String cpf : inactiveCPFs) {
            for (Patient p : allPatients) {
                if (p.getCpf().equals(cpf)) {
                    inactivePatients.add(p);
                    break;
                }
            }
        }

        // Ordenar alfabeticamente
        inactivePatients.sort(Comparator.comparing(Patient::getName));

        System.out.println("\nPacientes sem consulta há mais de " + months + " meses:");

        for (int i = 0; i < inactivePatients.size(); i++) {
            Patient p = inactivePatients.get(i);
            LocalDate lastDate = lastAppointmentDates.get(p.getCpf());
            String lastDateStr = (lastDate != null) ?
                    lastDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "Nunca consultou";

            System.out.println((i + 1) + " - " + p.getName() +
                    " (CPF: " + UIUtils.formatCPF(p.getCpf()) + ") | Última consulta: " + lastDateStr);
        }
    }

    /**
     * Obtém o nome do médico a partir do CRM
     *
     * @param doctors Lista de médicos
     * @param crm CRM do médico
     * @return Nome do médico ou "CRM não encontrado" se não encontrado
     */
    public static String getDoctorName(List<Doctor> doctors, String crm) {
        for (Doctor doctor : doctors) {
            if (doctor.getCode().equals(crm)) {
                return doctor.getName();
            }
        }

        return "CRM " + crm + " (Médico não encontrado)";
    }

    /**
     * Obtém o nome do médico a partir do CRM
     *
     * @param crm CRM do médico
     * @return Nome do médico ou "CRM não encontrado" se não encontrado
     */
    public static String getDoctorName(String crm) {
        try {
            List<Doctor> doctors = Doctor.loadFromCSV(DOCTOR_CSV);

            for (Doctor doctor : doctors) {
                if (doctor.getCode().equals(crm)) {
                    return doctor.getName();
                }
            }

            return "CRM " + crm + " (Médico não encontrado)";
        } catch (Exception e) {
            return "Erro ao buscar médico: " + e.getMessage();
        }
    }

    /**
     * Obtém o nome do paciente a partir do CPF
     *
     * @param allPatients Lista de todos os pacientes
     * @param cpf CPF do paciente
     * @return Nome do paciente ou "(Paciente não encontrado)" se não encontrado
     */
    private static String getPatientName(List<Patient> allPatients, String cpf) {
        for (Patient p : allPatients) {
            if (p.getCpf().equals(cpf)) {
                return p.getName();
            }
        }
        return "(Paciente não encontrado)";
    }
}