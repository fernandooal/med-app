import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Classe responsável pela interface de administrador do sistema.
 * Gerencia funcionalidades como cadastro de médicos, pacientes e agendamento de consultas.
 */
public class AdminView {

    private static final String DOCTOR_CSV = "doctors_clean.csv"; // PADRONIZAÇÃO: arquivo único para médicos
    private static final String PATIENT_CSV = "patients.csv";
    private static final String APPOINTMENT_CSV = "appointments.csv";

    /**
     * Exibe as opções de autenticação para o administrador
     *
     * @param login Flag para controle do loop de autenticação
     * @param scanner Scanner para leitura de entrada do usuário
     */
    public static void checkOptions(List<Doctor> doctors, List<Patient> patients, List<Appointment> appointments, boolean login, Scanner scanner) {
        int option = -1;
        while (login) {
            try {
                System.out.println("\nSelecione a opção desejada: ");
                System.out.println("0 - Voltar");
                System.out.println("1 - Login");
                option = scanner.nextInt();
                scanner.nextLine();
                switch (option) {
                    case 0: login = false; break;
                    case 1:
                        boolean admin = login(scanner);
                        if (admin) {
                            System.out.println("\nLogin efetuado com sucesso!");
                            menu(doctors, patients, appointments, scanner);
                            login = false;
                        } else {
                            System.out.println("Login ou senha incorreta.");
                        }
                        break;
                    default:
                        System.out.println("Opção Inválida!");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número.");
            }
        }
    }

    /**
     * Realiza a autenticação do administrador
     *
     * @param scanner Scanner para leitura de entrada do usuário
     * @return true se o login for bem-sucedido, false caso contrário
     */
    private static boolean login(Scanner scanner) {
        System.out.print("Digite o usuário: ");
        String username = scanner.nextLine();
        System.out.print("Digite a senha: ");
        String password = scanner.nextLine();

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("credentials.properties")) {
            props.load(fis);
        } catch (IOException e) {
            System.out.println("Erro ao carregar credenciais: " + e.getMessage());
            return false;
        }

        String storedUser = props.getProperty("username");
        String storedPass = props.getProperty("password");

        return username.equals(storedUser) && password.equals(storedPass);
    }

    /**
     * Exibe o menu de opções do administrador
     *
     * @param scanner Scanner para leitura de entrada do usuário
     */
    private static void menu(List<Doctor> doctors, List<Patient> patients, List<Appointment> appointments, Scanner scanner) {
        int option = 1;
        while (option != 0) {
            try {
                System.out.println("\nInterface de Administrador");
                System.out.println("0 - Voltar ao Menu Principal");
                System.out.println("1 - Gerenciar Médicos");
                System.out.println("2 - Gerenciar Pacientes");
                System.out.println("3 - Gerenciar Consultas");
                option = scanner.nextInt();
                scanner.nextLine();
                switch (option) {
                    case 0: break;
                    case 1: manageDoctors(doctors, scanner); break;
                    case 2:
                        managePatients(patients, scanner);
                        appointments.clear();
                        appointments.addAll(Appointment.loadFromCSV(APPOINTMENT_CSV));
                        break;
                    case 3: manageAppointments(appointments, patients, scanner); break;
                    default: System.out.println("Opção inválida."); break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número.");
            }
        }
    }

    private static void manageDoctors(List<Doctor> doctors, Scanner scanner) {
        System.out.println("\n1 - Cadastrar Médico");
        System.out.println("2 - Excluir Médico");
        System.out.println("3 - Corrigir Médico");
        System.out.println("4 - Ver Médicos Cadastrados");
        System.out.println("5 - Reintegrar Médico");
        System.out.println("0 - Voltar");
        int option = scanner.nextInt();
        scanner.nextLine();
        boolean doctorsUpdated = false;
        switch (option) {
            case 1: registerDoctor(scanner); break;
            case 2: deleteDoctor(doctors, scanner); break;
            case 3: editDoctor(doctors, scanner); doctorsUpdated = true; break;
            case 4: listDoctors(doctors); break;
            case 5: reintegrateDoctor(doctors, scanner); break;
            case 0: return;
            default: System.out.println("Opção inválida.");
        }

        if(doctorsUpdated) {
            doctors.clear();
            doctors.addAll(Doctor.loadFromCSV(DOCTOR_CSV));
        } else{
            updateDoctorsFromCSV(doctors, DOCTOR_CSV);
        }
    }

    /**
     * Cadastra um novo médico no sistema
     * @param scanner Scanner para leitura de entrada do usuário
     */
    private static void registerDoctor(Scanner scanner) {
        System.out.println("Digite o nome do médico: ");
        String doctorName = scanner.nextLine();
        System.out.println("Digite o código do médico: ");
        String doctorCode = scanner.nextLine();

        saveToCSV(DOCTOR_CSV, doctorName, doctorCode);
    }

    private static void deleteDoctor(List<Doctor> doctors, Scanner scanner) {
        System.out.print("Digite o CRM do médico a ser marcado como removido: ");
        String crm = scanner.nextLine().trim();

        boolean found = false;
        for (Doctor d : doctors) {
            if (d.getCode().equals(crm)) {
                if (!d.getName().contains("(Removido)")) {
                    d.setName(d.getName() + " (Removido)");
                }
                found = true;
                break;
            }
        }

        if (found) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(DOCTOR_CSV))) {
                writer.println("Nome,CRM");
                for (Doctor d : doctors) {
                    writer.println(d.getName() + "," + d.getCode());
                }
                System.out.println("Médico marcado como removido com sucesso!");
            } catch (IOException e) {
                System.out.println("Erro ao salvar o arquivo: " + e.getMessage());
            }
        } else {
            System.out.println("Médico com CRM " + crm + " não encontrado.");
        }
    }

    private static void editDoctor(List<Doctor> doctors, Scanner scanner) {
        System.out.print("Digite o CRM do médico a ser corrigido: ");
        String crm = scanner.nextLine().trim();

        boolean found = false;
        for (Doctor d : doctors) {
            if (d.getCode().equals(crm)) {
                System.out.print("Digite o novo nome do médico: ");
                String newName = scanner.nextLine().trim();
                d.setName(newName);
                found = true;
                break;
            }
        }

        if (found) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(DOCTOR_CSV))) {
                writer.println("Nome,CRM");
                for (Doctor d : doctors) {
                    writer.println(d.getName() + "," + d.getCode());
                }
                System.out.println("Dados do médico atualizados com sucesso!");
            } catch (IOException e) {
                System.out.println("Erro ao salvar o arquivo: " + e.getMessage());
            }
        } else {
            System.out.println("Médico com CRM " + crm + " não encontrado.");
        }
    }

    private static void listDoctors(List<Doctor> doctors) {
        if (doctors.isEmpty()) {
            System.out.println("\nNenhum médico cadastrado.");
            return;
        }

        // Ordenar alfabeticamente por nome
        doctors.sort(Comparator.comparing(Doctor::getName));

        System.out.println("\nLista de médicos cadastrados:");
        int index = 1;
        for (Doctor d : doctors) {
            System.out.println(index++ + " - Nome: " + d.getName() + " | CRM: " + d.getCode());
        }
    }

    private static void reintegrateDoctor(List<Doctor> doctors, Scanner scanner) {
        System.out.print("Digite o CRM do médico a ser reintegrado: ");
        String crm = scanner.nextLine().trim();

        boolean found = false;
        for (Doctor d : doctors) {
            if (d.getCode().equals(crm) && d.getName().contains("(Removido)")) {
                d.setName(d.getName().replace(" (Removido)", "").trim());
                found = true;
                break;
            }
        }

        if (found) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(DOCTOR_CSV))) {
                writer.println("Nome,CRM");
                for (Doctor d : doctors) {
                    writer.println(d.getName() + "," + d.getCode());
                }
                System.out.println("Médico reintegrado com sucesso!");
            } catch (IOException e) {
                System.out.println("Erro ao salvar o arquivo: " + e.getMessage());
            }
        } else {
            System.out.println("Médico com CRM " + crm + " não encontrado ou não está marcado como removido.");
        }
    }


    private static void managePatients(List<Patient> patients, Scanner scanner) {
        System.out.println("\n1 - Cadastrar Paciente");
        System.out.println("2 - Excluir Paciente");
        System.out.println("3 - Corrigir Paciente");
        System.out.println("0 - Voltar");
        int option = scanner.nextInt();
        scanner.nextLine();
        boolean patientEdited = false;
        switch (option) {
            case 1: registerPatient(scanner); break;
            case 2: deletePatient(patients, scanner); break;
            case 3: editPatient(patients, scanner); patientEdited = true; break;
            case 0: return;
            default: System.out.println("Opção inválida.");
        }

        if(patientEdited) {
            patients.clear();
            patients.addAll(Patient.loadFromCSV(PATIENT_CSV));
        } else {
            updatePatientsFromCSV(patients, PATIENT_CSV);
        }
    }

    /**
     * Cadastra um novo paciente no sistema
     *
     * @param scanner Scanner para leitura de entrada do usuário
     */
    private static void registerPatient(Scanner scanner) {
        System.out.println("Digite o nome do paciente: ");
        String patientName = scanner.nextLine();
        System.out.println("Digite o CPF do paciente: ");
        String patientCPF = scanner.nextLine();

        if (!patientCPF.matches("\\d{11}")) {
            System.out.println("CPF inválido.");
            return;
        }

        saveToCSV(PATIENT_CSV, patientName, patientCPF);

        Patient newPatient = new Patient(patientName, patientCPF);
        offerScheduleAppointment(newPatient, scanner);
    }


    private static void deletePatient(List<Patient> patients, Scanner scanner) {
        System.out.print("Digite o CPF do paciente a ser excluído: ");
        String cpf = scanner.nextLine().trim();

        boolean removed = patients.removeIf(p -> p.getCpf().equals(cpf));
        if (removed) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(PATIENT_CSV))) {
                writer.println("Nome,CPF");
                for (Patient p : patients) {
                    writer.println(p.getName() + "," + p.getCpf());
                }
                System.out.println("Paciente removido com sucesso!");
            } catch (IOException e) {
                System.out.println("Erro ao salvar o arquivo: " + e.getMessage());
            }
        } else {
            System.out.println("Paciente com CPF " + cpf + " não encontrado.");
        }
    }

    private static void editPatient(List<Patient> patients, Scanner scanner) {
        System.out.print("Digite o CPF do paciente a ser corrigido: ");
        String cpf = scanner.nextLine().trim();

        boolean found = false;
        for (Patient p : patients) {
            if (p.getCpf().equals(cpf)) {
                System.out.print("Digite o novo nome do paciente: ");
                String newName = scanner.nextLine().trim();
                patients.set(patients.indexOf(p), new Patient(newName, cpf));
                found = true;
                break;
            }
        }

        if (found) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(PATIENT_CSV))) {
                writer.println("Nome,CPF");
                for (Patient p : patients) {
                    writer.println(p.getName() + "," + p.getCpf());
                }
                System.out.println("Dados do paciente atualizados com sucesso!");
            } catch (IOException e) {
                System.out.println("Erro ao salvar o arquivo: " + e.getMessage());
            }
        } else {
            System.out.println("Paciente com CPF " + cpf + " não encontrado.");
        }
    }

    /**
     * Oferece a opção de agendar uma consulta após o cadastro de um paciente
     *
     * @param patient Paciente recém-cadastrado
     * @param scanner Scanner para leitura de entrada do usuário
     */
    private static void offerScheduleAppointment(Patient patient, Scanner scanner) {
        System.out.println("\nDeseja agendar uma consulta para este paciente? (s/n): ");
        String response = scanner.nextLine();

        if (response.equalsIgnoreCase("s")) {
            scheduleAppointment(patient, scanner);
        }
    }

    /**
     * Agenda uma nova consulta para um paciente
     *
     * @param patient Paciente para o qual a consulta será agendada
     * @param scanner Scanner para leitura de entrada do usuário
     */
    private static void scheduleAppointment(Patient patient, Scanner scanner) {
        try {
            List<Doctor> doctors = Doctor.loadFromCSV(DOCTOR_CSV);

            if (doctors.isEmpty()) {
                System.out.println("Não há médicos cadastrados. Cadastre um médico primeiro.");
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

            try {
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

                LocalDate appointmentDate = LocalDate.parse(dateStr, dateFormatter);
                LocalTime appointmentTime = LocalTime.parse(timeStr, timeFormatter);

                Doctor selectedDoctor = doctors.get(doctorIndex);

                // Criar e salvar a nova consulta
                Appointment appointment = new Appointment(
                        appointmentDate,
                        appointmentTime,
                        patient.getCpf(),
                        selectedDoctor.getCode(),
                        AppointmentStatus.PENDING
                );

                appointment.saveToCSVFile(APPOINTMENT_CSV, true);

                System.out.println("\nConsulta agendada com sucesso!");
                System.out.println("Paciente: " + patient.getName());
                System.out.println("Médico: " + selectedDoctor.getName());
                System.out.println("Data e hora: " + appointment.getFormattedDateTime());

            } catch (DateTimeParseException e) {
                System.out.println("Formato de data ou hora inválido: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace(System.err);

        }
    }

    /**
     * Salva um registro no arquivo CSV especificado
     *
     * @param filename Nome do arquivo CSV
     * @param name Nome (médico ou paciente)
     * @param code Código ou CPF
     */
    private static void saveToCSV(String filename, String name, String code) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            writer.println(name + "," + code);
        } catch (IOException error) {
            System.out.println("Erro ao salvar no arquivo CSV: " + error.getMessage());
        }

        System.out.println("\nCadastro efetuado com sucesso!");
    }

    /**
     * Interface para gerenciar consultas (cancelar ou alterar)
     *
     * @param scanner Scanner para leitura de entrada do usuário
     */
    private static void manageAppointments(List<Appointment> appointments, List<Patient> patients, Scanner scanner) {
        try {
            System.out.print("Informe o CPF do paciente: ");
            String cpf = scanner.nextLine().trim();

            if (!cpf.matches("\\d{11}")) {
                System.out.println("CPF inválido. Deve conter exatamente 11 dígitos.");
                return;
            }

            Patient patient = null;
            for (Patient p : patients) {
                if (p.getCpf().equals(cpf)) {
                    patient = p;
                    break;
                }
            }

            if (patient == null) {
                System.out.println("Paciente não encontrado.");
                return;
            }

            // Filtrar consultas futuras do paciente
            List<Appointment> futureAppointments = new ArrayList<>();
            for (Appointment appointment : appointments) {
                if (appointment.belongsToPatient(cpf) &&
                        appointment.isPending()) {
                    futureAppointments.add(appointment);
                }
            }

            if (futureAppointments.isEmpty()) {
                System.out.println("Não há consultas futuras para este paciente.");
                return;
            }

            // Ordenar por data/hora
            futureAppointments.sort(Comparator.comparing(a -> LocalDate.parse(a.getFormattedDateTime().split(" ")[0], DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

            System.out.println("\nConsultas futuras do paciente " + patient.getName() + ":");
            for (int i = 0; i < futureAppointments.size(); i++) {
                Appointment app = futureAppointments.get(i);
                System.out.println((i + 1) + " - " + app.getFormattedDateTime() + " (Médico: " + app.getDoctorCRM() + ")");
            }

            System.out.print("\nDigite o número da consulta que deseja gerenciar (0 para voltar): ");
            int selection = scanner.nextInt();

            if (selection == 0) {
                return;
            }

            if (selection < 1 || selection > futureAppointments.size()) {
                System.out.println("Seleção inválida.");
                return;
            }

            Appointment selectedAppointment = futureAppointments.get(selection - 1);

            System.out.println("\nO que deseja fazer com esta consulta?");
            System.out.println("1 - Cancelar");
            System.out.println("2 - Alterar data/hora");
            System.out.println("0 - Voltar");

            int action = scanner.nextInt();
            scanner.nextLine();
            switch (action) {
                case 0:
                    return;
                case 1:
                    cancelAppointment(selectedAppointment, appointments);
                    break;
                case 2:
                    changeAppointmentDateTime(selectedAppointment, appointments, scanner);
                    break;
                default:
                    System.out.println("Opção inválida.");
            }

            appointments.clear();
            appointments.addAll(Appointment.loadFromCSV(APPOINTMENT_CSV));
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número.");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace(System.err);

        }
    }

    /**
     * Cancela uma consulta agendada
     *
     * @param appointment Consulta a ser cancelada
     * @param allAppointments Lista de todas as consultas
     */
    private static void cancelAppointment(Appointment appointment, List<Appointment> allAppointments) {
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

            // Salvar a lista atualizada
            Appointment.saveAppointmentsToCSV(allAppointments, APPOINTMENT_CSV);

            System.out.println("Consulta cancelada com sucesso!");

        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace(System.err);

        }
    }

    /**
     * Altera a data e hora de uma consulta agendada
     *
     * @param appointment Consulta a ser alterada
     * @param allAppointments Lista de todas as consultas
     * @param scanner Scanner para leitura de entrada do usuário
     */
    private static void changeAppointmentDateTime(Appointment appointment, List<Appointment> allAppointments, Scanner scanner) {
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

                System.out.println("Data e hora da consulta alteradas com sucesso!");
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
     * Atualiza a lista de pacientes a partir do arquivo CSV
     *
     * @param currentPatients Lista atual de pacientes
     * @param filename Nome do arquivo CSV
     */
    public static void updatePatientsFromCSV(List<Patient> currentPatients, String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            if (scanner.hasNextLine()) scanner.nextLine();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 2) continue;

                String name = parts[0].trim();
                String cpf = parts[1].trim();

                boolean exists = false;
                for (Patient p : currentPatients) {
                    if (p.getCpf().equals(cpf)) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    currentPatients.add(new Patient(name, cpf));
                }
            }

        } catch (IOException e) {
            System.out.println("Erro ao atualizar lista de pacientes: " + e.getMessage());
        }
    }

    /**
     * Atualiza a lista de médicos a partir do arquivo CSV
     *
     * @param currentDoctors Lista atual de médicos
     * @param filename Nome do arquivo CSV
     */
    public static void updateDoctorsFromCSV(List<Doctor> currentDoctors, String filename) {
        // PADRÃO CONSOLIDADO: filename deve ser doctors_clean.csv
        try (Scanner scanner = new Scanner(new File(filename))) {
            if (scanner.hasNextLine()) scanner.nextLine();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < 2) continue;

                String name = parts[0].trim();
                String code = parts[1].trim(); // código como string

                boolean exists = false;
                for (Doctor d : currentDoctors) {
                    if (d.getCode().equals(code)) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    currentDoctors.add(new Doctor(name, code));
                }
            }

        } catch (IOException e) {
            System.out.println("Erro ao atualizar lista de médicos: " + e.getMessage());
        }
    }
}