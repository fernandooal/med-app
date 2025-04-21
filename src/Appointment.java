import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * Classe que representa uma consulta médica no sistema.
 * Gerencia dados como data, hora, paciente e médico, além de
 * oferecer funcionalidades para filtrar e gerenciar consultas.
 */
public class Appointment {
    private final LocalDate date;
    private final LocalTime time;
    private final String patientCPF;
    private final String doctorCRM;
    private AppointmentStatus status; // Novo atributo para status

    private static final String APPOINTMENT_CSV = "appointments.csv";
    private static final String DOCTOR_CSV = "doctors_clean.csv";

    /**
     * Construtor para criação de uma nova consulta
     *
     * @param date Data da consulta
     * @param time Hora da consulta
     * @param patientCPF CPF do paciente
     * @param doctorCRM CRM do médico
     */
    public Appointment(LocalDate date, LocalTime time, String patientCPF, String doctorCRM) {
        this.date = date;
        this.time = time;
        this.patientCPF = patientCPF;
        this.doctorCRM = doctorCRM;
        // Define o status inicial com base na data
        this.status = LocalDateTime.of(date, time).isBefore(LocalDateTime.now())
                ? AppointmentStatus.COMPLETED
                : AppointmentStatus.PENDING;
    }

    /**
     * Construtor completo incluindo status
     *
     * @param date Data da consulta
     * @param time Hora da consulta
     * @param patientCPF CPF do paciente
     * @param doctorCRM CRM do médico
     * @param status Status da consulta
     */
    public Appointment(LocalDate date, LocalTime time, String patientCPF, String doctorCRM, AppointmentStatus status) {
        this.date = date;
        this.time = time;
        this.patientCPF = patientCPF;
        this.doctorCRM = doctorCRM;
        this.status = status;
    }

    /**
     * Verifica se a consulta pertence a um determinado paciente
     *
     * @param patientCPF CPF do paciente a verificar
     * @return true se a consulta pertence ao paciente, false caso contrário
     */
    public boolean belongsToPatient(String patientCPF) {
        return this.patientCPF.equals(patientCPF);
    }

    /**
     * Verifica se a consulta já ocorreu com base na data e hora atuais
     *
     * @return true se a consulta já ocorreu, false caso contrário
     */
    public boolean hasOccurred() {
        return LocalDateTime.of(date, time).isBefore(LocalDateTime.now());
    }

    /**
     * Verifica se a consulta está pendente (ainda não ocorreu)
     *
     * @return true se a consulta ainda não ocorreu, false caso contrário
     */
    public boolean isPending() {
        return status == AppointmentStatus.PENDING;
    }

    /**
     * Verifica se a consulta está dentro de um determinado período
     *
     * @param startDate Data inicial do período
     * @param endDate Data final do período
     * @return true se a consulta está no período, false caso contrário
     */
    public boolean isInPeriod(LocalDate startDate, LocalDate endDate) {
        return (date.isEqual(startDate) || date.isAfter(startDate)) &&
                (date.isEqual(endDate)   || date.isBefore(endDate));
    }

    /**
     * Retorna a data e hora formatadas para exibição
     *
     * @return String com data e hora no formato "dd/MM/yyyy às HH:mm"
     */
    public String getFormattedDateTime() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return date.format(dateFormatter) + " às " + time.format(timeFormatter);
    }

    @Override
    public String toString() {
        return "Consulta em " + getFormattedDateTime() +
                ", Paciente: " + patientCPF +
                ", Médico: " + doctorCRM +
                ", Status: " + status.getDescription();
    }

    /**
     * Formata a consulta para salvamento em CSV
     *
     * @return String formatada para CSV
     */
    public String toCSVFormat() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        return date.format(dateFormatter) + "," +
                time.format(timeFormatter) + "," +
                patientCPF + "," +
                doctorCRM + "," +
                status.name(); // Adicionado o status
    }

    /**
     * Salva a consulta em um arquivo CSV
     *
     * @param filename Nome do arquivo
     * @param append Se true, adiciona ao final do arquivo; se false, sobrescreve o arquivo
     */
    public void saveToCSVFile(String filename, boolean append) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, append))) {
            if (!append) {
                writer.println("Data,Horario,CPF_Paciente,CRM_Medico,Status");
            }
            writer.println(toCSVFormat());
        } catch (IOException error) {
            System.out.println("Erro ao salvar no arquivo CSV: " + error.getMessage());
        }
    }

    // Getters

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getPatientCPF() {
        return patientCPF;
    }

    public String getDoctorCRM() {
        return doctorCRM;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    /**
     * Filtra consultas por paciente
     *
     * @param appointments Lista de consultas a filtrar
     * @param patientCPF CPF do paciente
     * @return Lista de consultas do paciente especificado
     */
    public static List<Appointment> filterByPatient(List<Appointment> appointments, String patientCPF) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.belongsToPatient(patientCPF)) {
                result.add(appointment);
            }
        }
        return result;
    }

    /**
     * Processa uma linha do arquivo CSV e converte em um objeto Appointment
     *
     * @param line Linha do arquivo CSV
     * @return Objeto Appointment ou null em caso de erro
     */
    private static Appointment parseLine(String line) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String[] parts = line.split(",");

        if (parts.length < 4) return null;

        String dateStr = parts[0].trim();
        String timeStr = parts[1].trim();
        String cpf = parts[2].trim();
        String crm = parts[3].trim();

        // Usar os métodos de validação das classes responsáveis
        if (!Patient.isValidCPF(cpf) || !Doctor.isValidCRM(crm)) {
            System.out.println("CPF ou CRM inválido na linha: " + line);
            return null;
        }

        try {
            LocalDate date = LocalDate.parse(dateStr, dateFormatter);
            LocalTime time = LocalTime.parse(timeStr, timeFormatter);

            // Processar status, se existir
            AppointmentStatus status = AppointmentStatus.PENDING; // Padrão
            if (parts.length >= 5) {
                try {
                    status = AppointmentStatus.valueOf(parts[4].trim());
                } catch (IllegalArgumentException e) {
                    System.out.println("Status inválido na linha: " + line + ". Usando status padrão.");
                }
            }

            return new Appointment(date, time, cpf, crm, status);
        } catch (Exception e) {
            System.out.println("Erro ao converter data/hora na linha: " + line);
            return null;
        }
    }

    /**
     * Carrega consultas de um arquivo CSV
     *
     * @param filename Nome do arquivo CSV
     * @return Lista de consultas carregadas
     */
    public static List<Appointment> loadFromCSV(String filename) {
        List<Appointment> appointments = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(filename))) {
            if (scanner.hasNextLine()) scanner.nextLine(); // pula cabeçalho

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                Appointment ap = parseLine(line);
                if (ap != null) {
                    appointments.add(ap);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar as consultas: " + e.getMessage());
        }

        return appointments;
    }

    /**
     * Salva uma lista de consultas em um arquivo CSV
     *
     * @param appointments Lista de consultas a salvar
     * @param filename Nome do arquivo CSV
     * @throws IOException se ocorrer erro ao escrever no arquivo
     */
    public static void saveAppointmentsToCSV(List<Appointment> appointments, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Data,Horario,CPF_Paciente,CRM_Medico,Status");

            for (Appointment appointment : appointments) {
                writer.println(appointment.toCSVFormat());
            }
        }
    }

    /**
     * Agenda uma nova consulta para um paciente
     *
     * @param patient Paciente que está agendando a consulta
     * @param allAppointments Todas as consultas
     * @param scanner Scanner para leitura
     */
    public static void scheduleNewAppointment(Patient patient, List<Appointment> allAppointments, Scanner scanner) {
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
    public static void viewFutureAppointments(Patient patient, List<Appointment> allAppointments, Scanner scanner) {
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
            System.out.println((i + 1) + " - " + app.getFormattedDateTime() + " (Médico: " + DoctorView.getDoctorName(app.getDoctorCRM()) + ")");
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
                    cancelAppointment(selectedAppointment, allAppointments);
                    break;
                case 3:
                    rescheduleAppointment(selectedAppointment, patient, allAppointments, scanner);
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
    public static void viewPastAppointments(Patient patient, Scanner scanner) {
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
    public static void rescheduleAppointment(Patient patient, List<Appointment> allAppointments, Scanner scanner) {
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
            System.out.println((i + 1) + " - " + app.getFormattedDateTime() + " (Médico: " + DoctorView.getDoctorName(app.getDoctorCRM()) + ")");
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
        rescheduleAppointment(selectedAppointment, patient, allAppointments, scanner);
    }

    /**
     * Executa a remarcação de uma consulta
     *
     * @param appointment Consulta a ser remarcada
     * @param patient Paciente dono da consulta
     * @param allAppointments Todas as consultas
     * @param scanner Scanner para leitura
     */
    public static void rescheduleAppointment(Appointment appointment, Patient patient, List<Appointment> allAppointments, Scanner scanner) {
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
    public static void cancelAppointment(Patient patient, List<Appointment> allAppointments, Scanner scanner) {
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
            System.out.println((i + 1) + " - " + app.getFormattedDateTime() + " (Médico: " + DoctorView.getDoctorName(app.getDoctorCRM()) + ")");
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
            cancelAppointment(selectedAppointment, allAppointments);
        }
    }

    /**
     * Executa o cancelamento de uma consulta
     *
     * @param appointment Consulta a ser cancelada
     * @param allAppointments Todas as consultas
     */
    public static void cancelAppointment(Appointment appointment, List<Appointment> allAppointments) {
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
    }}