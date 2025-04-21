import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Classe responsável pela interface com o usuário para a visão do paciente.
 * Gerencia as funcionalidades disponíveis para pacientes no sistema,
 * como agendamento, visualização e gerenciamento de consultas.
 */
public class PatientView {

    private static final String APPOINTMENT_CSV = "appointments.csv";
    private static final String DOCTOR_CSV = "doctors_clean.csv";


    /**
     * Ponto de entrada principal para a interface do paciente
     *
     * @param patients Lista de pacientes cadastrados no sistema
     * @param search Flag para controlar o loop do menu
     * @param scanner Scanner para leitura de entrada do usuário
     */
    public static void checkOptions(List<Patient> patients, boolean search, Scanner scanner) {
        while (search) {
            try {
                // Solicitar CPF ao paciente
                System.out.print("Digite seu CPF (somente números): ");
                String cpf = scanner.nextLine().trim();

                if (!cpf.matches("\\d{11}")) {
                    System.out.println("CPF inválido. Deve conter exatamente 11 dígitos.");
                    continue;
                }

                // Buscar paciente pelo CPF
                Patient patient = findPatientByCPF(patients, cpf);

                if (patient == null) {
                    System.out.println("Paciente não encontrado. Deseja tentar novamente? (s/n): ");
                    String retry = scanner.nextLine();
                    if (!retry.equalsIgnoreCase("s")) {
                        search = false;
                    }
                    continue;
                }

                // Dar boas-vindas ao paciente e mostrar o menu principal
                System.out.println("\nBem-vindo(a), " + patient.getName() + "!");

                // Carregar consultas do paciente
                List<Appointment> allAppointments = Appointment.loadFromCSV(APPOINTMENT_CSV);
                List<Appointment> patientAppointments = Appointment.filterByPatient(allAppointments, patient.getCpf());

                // Atualizar a lista de consultas do paciente
                patient.getAppointmentList().clear();
                for (Appointment app : patientAppointments) {
                    patient.addAppointment(app);
                }

                patientMenu(patient, allAppointments, scanner);
                search = false;

            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
                e.printStackTrace(System.err);

            }
        }
    }

    /**
     * Busca um paciente pelo CPF na lista de pacientes
     *
     * @param patients Lista de pacientes
     * @param cpf CPF a ser buscado
     * @return Paciente encontrado ou null se não encontrado
     */
    private static Patient findPatientByCPF(List<Patient> patients, String cpf) {
        for (Patient p : patients) {
            if (p.getCpf().equals(cpf)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Exibe o menu principal para o paciente e processa a opção escolhida
     *
     * @param patient Paciente logado
     * @param allAppointments Todas as consultas do sistema
     * @param scanner Scanner para leitura de entrada do usuário
     */
    private static void patientMenu(Patient patient, List<Appointment> allAppointments, Scanner scanner) {
        boolean continueMenu = true;

        while (continueMenu) {
            try {
                System.out.println("\n===== MENU DO PACIENTE =====");
                System.out.println("1 - Agendar nova consulta");
                System.out.println("2 - Ver consultas agendadas (futuras)");
                System.out.println("3 - Ver consultas realizadas");
                System.out.println("4 - Remarcar consulta");
                System.out.println("5 - Cancelar consulta");
                System.out.println("0 - Sair");
                System.out.print("\nEscolha uma opção: ");

                int option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 0:
                        continueMenu = false;
                        break;
                    case 1:
                        scheduleNewAppointment(patient, allAppointments, scanner);
                        break;
                    case 2:
                        viewFutureAppointments(patient, allAppointments, scanner);
                        break;
                    case 3:
                        viewPastAppointments(patient, scanner);
                        break;
                    case 4:
                        rescheduleAppointment(patient, allAppointments, scanner);
                        break;
                    case 5:
                        cancelAppointment(patient, allAppointments, scanner);
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
     * Agenda uma nova consulta para o paciente
     *
     * @param patient Paciente
     * @param allAppointments Todas as consultas
     * @param scanner Scanner para leitura
     */
    private static void scheduleNewAppointment(Patient patient, List<Appointment> allAppointments, Scanner scanner) {
        try {
            // Carregar a lista de médicos
            List<Doctor> doctors = Doctor.loadFromCSV(DOCTOR_CSV);

            if (doctors.isEmpty()) {
                System.out.println("Não há médicos cadastrados no sistema.");
                return;
            }

            // Exibir lista de médicos
            System.out.println("\nSelecione o médico para a consulta:");
            for (int i = 0; i < doctors.size(); i++) {
                System.out.println((i + 1) + " - " + doctors.get(i).getName() + " (CRM: " + doctors.get(i).getCode() + ")");
            }

            System.out.print("\nDigite o número correspondente ao médico: ");
            int doctorIndex = Integer.parseInt(scanner.nextLine()) - 1;

            if (doctorIndex < 0 || doctorIndex >= doctors.size()) {
                System.out.println("Seleção inválida.");
                return;
            }

            // Obter data da consulta
            System.out.print("Digite a data da consulta (yyyy-MM-dd): ");
            String dateStr = scanner.nextLine();

            // Obter hora da consulta
            System.out.print("Digite o horário da consulta (HH:mm): ");
            String timeStr = scanner.nextLine();

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

            LocalDate appointmentDate = LocalDate.parse(dateStr, dateFormatter);
            LocalTime appointmentTime = LocalTime.parse(timeStr, timeFormatter);

            // Verificar se a data é no futuro
            if (appointmentDate.isBefore(LocalDate.now())) {
                System.out.println("A data da consulta deve ser futura.");
                return;
            }

            Doctor selectedDoctor = doctors.get(doctorIndex);

            // Verificar se já existe consulta no mesmo horário para o médico
            boolean conflictFound = false;
            for (Appointment app : allAppointments) {
                if (app.getDoctorCRM().equals(selectedDoctor.getCode()) &&
                        app.getDate().equals(appointmentDate) &&
                        app.getTime().equals(appointmentTime) &&
                        app.getStatus() == AppointmentStatus.PENDING) {
                    conflictFound = true;
                    break;
                }
            }

            if (conflictFound) {
                System.out.println("Já existe uma consulta agendada com este médico neste horário.");
                return;
            }

            // Criar e salvar a nova consulta
            Appointment appointment = new Appointment(
                    appointmentDate,
                    appointmentTime,
                    patient.getCpf(),
                    selectedDoctor.getCode(),
                    AppointmentStatus.PENDING
            );

            appointment.saveToCSVFile(APPOINTMENT_CSV, true);

            // Adicionar a consulta à lista do paciente
            patient.addAppointment(appointment);

            // Adicionar a consulta à lista geral
            allAppointments.add(appointment);

            System.out.println("\nConsulta agendada com sucesso!");
            System.out.println("Médico: " + selectedDoctor.getName());
            System.out.println("Data e hora: " + appointment.getFormattedDateTime());

        } catch (DateTimeParseException e) {
            System.out.println("Formato de data ou hora inválido: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace(System.err);

        }
    }

    /**
     * Exibe as consultas futuras (agendadas) do paciente com opções de gerenciamento
     *
     * @param patient Paciente
     * @param allAppointments Todas as consultas
     * @param scanner Scanner para leitura
     */
    private static void viewFutureAppointments(Patient patient, List<Appointment> allAppointments, Scanner scanner) {
        List<Appointment> futureAppointments = new ArrayList<>();

        // Filtrar consultas pendentes (futuras)
        for (Appointment app : patient.getAppointmentList()) {
            if (app.getStatus() == AppointmentStatus.PENDING) {
                futureAppointments.add(app);
            }
        }

        if (futureAppointments.isEmpty()) {
            System.out.println("Você não tem consultas agendadas.");
            return;
        }

        // Ordenar por data/hora
        futureAppointments.sort(Comparator.comparing(Appointment::getDate)
                .thenComparing(Appointment::getTime));

        System.out.println("\nSuas consultas agendadas:");

        for (int i = 0; i < futureAppointments.size(); i++) {
            Appointment app = futureAppointments.get(i);
            System.out.println((i + 1) + " - " + app.getFormattedDateTime() + " (Médico: " + getDoctorName(app.getDoctorCRM()) + ")");
        }

        System.out.println("\nDeseja gerenciar alguma consulta? (s/n): ");
        String response = scanner.nextLine();

        if (response.equalsIgnoreCase("s")) {
            System.out.print("Digite o número da consulta: ");
            int selection = Integer.parseInt(scanner.nextLine()) - 1;

            if (selection < 0 || selection >= futureAppointments.size()) {
                System.out.println("Seleção inválida.");
                return;
            }

            Appointment selectedAppointment = futureAppointments.get(selection);

            System.out.println("\nO que deseja fazer com esta consulta?");
            System.out.println("1 - Confirmar presença");
            System.out.println("2 - Cancelar consulta");
            System.out.println("3 - Remarcar consulta");
            System.out.println("0 - Voltar");

            int action = Integer.parseInt(scanner.nextLine());

            switch (action) {
                case 0:
                    return;
                case 1:
                    System.out.println("Presença confirmada para a consulta em " + selectedAppointment.getFormattedDateTime());
                    break;
                case 2:
                    doCancelAppointment(selectedAppointment, allAppointments);
                    break;
                case 3:
                    doRescheduleAppointment(selectedAppointment, patient, allAppointments, scanner);
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    /**
     * Exibe as consultas já realizadas pelo paciente
     *
     * @param patient Paciente
     * @param scanner Scanner para leitura
     */
    private static void viewPastAppointments(Patient patient, Scanner scanner) {
        List<Appointment> pastAppointments = new ArrayList<>();

        // Filtrar consultas realizadas
        for (Appointment app : patient.getAppointmentList()) {
            if (app.getStatus() == AppointmentStatus.COMPLETED ||
                    (app.hasOccurred() && app.getStatus() != AppointmentStatus.CANCELLED)) {
                pastAppointments.add(app);
            }
        }

        if (pastAppointments.isEmpty()) {
            System.out.println("Você não tem consultas realizadas.");
            return;
        }

        // Ordenar por data/hora (mais recente primeiro)
        pastAppointments.sort(Comparator.comparing(Appointment::getDate)
                .thenComparing(Appointment::getTime).reversed());

        System.out.println("\nSuas consultas realizadas:");
        UIUtils.paginateList(pastAppointments, 5, scanner);
    }

    /**
     * Interface para remarcar uma consulta existente
     *
     * @param patient Paciente
     * @param allAppointments Todas as consultas
     * @param scanner Scanner para leitura
     */
    private static void rescheduleAppointment(Patient patient, List<Appointment> allAppointments, Scanner scanner) {
        List<Appointment> futureAppointments = new ArrayList<>();

        // Filtrar consultas pendentes (futuras)
        for (Appointment app : patient.getAppointmentList()) {
            if (app.getStatus() == AppointmentStatus.PENDING) {
                futureAppointments.add(app);
            }
        }

        if (futureAppointments.isEmpty()) {
            System.out.println("Você não tem consultas agendadas para remarcar.");
            return;
        }

        // Ordenar por data/hora
        futureAppointments.sort(Comparator.comparing(Appointment::getDate)
                .thenComparing(Appointment::getTime));

        System.out.println("\nSelecione a consulta que deseja remarcar:");

        for (int i = 0; i < futureAppointments.size(); i++) {
            Appointment app = futureAppointments.get(i);
            System.out.println((i + 1) + " - " + app.getFormattedDateTime() + " (Médico: " + getDoctorName(app.getDoctorCRM()) + ")");
        }

        System.out.print("\nDigite o número da consulta (0 para voltar): ");
        int selection = Integer.parseInt(scanner.nextLine()) - 1;

        if (selection == -1) {
            return;
        }

        if (selection < 0 || selection >= futureAppointments.size()) {
            System.out.println("Seleção inválida.");
            return;
        }

        Appointment selectedAppointment = futureAppointments.get(selection);
        doRescheduleAppointment(selectedAppointment, patient, allAppointments, scanner);
    }

    /**
     * Executa a remarcação de uma consulta
     *
     * @param appointment Consulta a ser remarcada
     * @param patient Paciente dono da consulta
     * @param allAppointments Todas as consultas
     * @param scanner Scanner para leitura
     */
    private static void doRescheduleAppointment(Appointment appointment, Patient patient, List<Appointment> allAppointments, Scanner scanner) {
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
                        app.getDoctorCRM().equals(appointment.getDoctorCRM()) &&
                        app.getDate().equals(newDate) &&
                        app.getTime().equals(newTime) &&
                        app.getStatus() == AppointmentStatus.PENDING) {
                    conflictFound = true;
                    break;
                }
            }

            if (conflictFound) {
                System.out.println("Já existe uma consulta agendada com este médico neste horário.");
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

                // Substituir na lista geral
                allAppointments.set(indexToRemove, newAppointment);

                // Salvar a lista atualizada
                Appointment.saveAppointmentsToCSV(allAppointments, APPOINTMENT_CSV);

                // Substituir na lista do paciente
                for (int i = 0; i < patient.getAppointmentList().size(); i++) {
                    Appointment app = patient.getAppointmentList().get(i);
                    if (app.getDate().equals(appointment.getDate()) &&
                            app.getTime().equals(appointment.getTime()) &&
                            app.getDoctorCRM().equals(appointment.getDoctorCRM())) {

                        // Substituir na lista de consultas do paciente
                        patient.getAppointmentList().set(i, newAppointment);
                        break;
                    }
                }

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
     * Interface para cancelar uma consulta existente
     *
     * @param patient Paciente
     * @param allAppointments Todas as consultas
     * @param scanner Scanner para leitura
     */
    private static void cancelAppointment(Patient patient, List<Appointment> allAppointments, Scanner scanner) {
        List<Appointment> futureAppointments = new ArrayList<>();

        // Filtrar consultas pendentes (futuras)
        for (Appointment app : patient.getAppointmentList()) {
            if (app.getStatus() == AppointmentStatus.PENDING) {
                futureAppointments.add(app);
            }
        }

        if (futureAppointments.isEmpty()) {
            System.out.println("Você não tem consultas agendadas para cancelar.");
            return;
        }

        // Ordenar por data/hora
        futureAppointments.sort(Comparator.comparing(Appointment::getDate)
                .thenComparing(Appointment::getTime));

        System.out.println("\nSelecione a consulta que deseja cancelar:");

        for (int i = 0; i < futureAppointments.size(); i++) {
            Appointment app = futureAppointments.get(i);
            System.out.println((i + 1) + " - " + app.getFormattedDateTime() + " (Médico: " + getDoctorName(app.getDoctorCRM()) + ")");
        }

        System.out.print("\nDigite o número da consulta (0 para voltar): ");
        int selection = Integer.parseInt(scanner.nextLine()) - 1;

        if (selection == -1) {
            return;
        }

        if (selection < 0 || selection >= futureAppointments.size()) {
            System.out.println("Seleção inválida.");
            return;
        }

        Appointment selectedAppointment = futureAppointments.get(selection);

        System.out.println("\nTem certeza que deseja cancelar a consulta em " +
                selectedAppointment.getFormattedDateTime() + "? (s/n): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("s")) {
            doCancelAppointment(selectedAppointment, allAppointments);
        }
    }

    /**
     * Executa o cancelamento de uma consulta
     *
     * @param appointment Consulta a ser cancelada
     * @param allAppointments Todas as consultas
     */
    private static void doCancelAppointment(Appointment appointment, List<Appointment> allAppointments) {
        try {
            // Encontrar e atualizar a consulta na lista
            for (Appointment app : allAppointments) {
                if (app.getDate().equals(appointment.getDate()) &&
                        app.getTime().equals(appointment.getTime()) &&
                        app.getPatientCPF().equals(appointment.getPatientCPF()) &&
                        app.getDoctorCRM().equals(appointment.getDoctorCRM())) {

                    app.setStatus(AppointmentStatus.CANCELLED);
                    break;
                }
            }

            // Atualizar o status na lista do paciente
            appointment.setStatus(AppointmentStatus.CANCELLED);

            // Salvar a lista atualizada
            Appointment.saveAppointmentsToCSV(allAppointments, APPOINTMENT_CSV);

            System.out.println("Consulta cancelada com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace(System.err);

        }
    }

    /**
     * Obtém o nome do médico a partir do CRM
     *
     * @param crm CRM do médico
     * @return Nome do médico ou "CRM não encontrado" se não encontrado
     */
    private static String getDoctorName(String crm) {
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
}