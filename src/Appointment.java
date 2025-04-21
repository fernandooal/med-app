import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
}