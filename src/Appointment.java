import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Scanner;

public class Appointment {
    private final LocalDate date;
    private final LocalTime time;
    private final String patientCPF;
    private final String doctorCRM;

    public Appointment(LocalDate date, LocalTime time, String patientCPF, String doctorCRM) {
        this.date = date;
        this.time = time;
        this.patientCPF = patientCPF;
        this.doctorCRM = doctorCRM;
    }

    public boolean belongsToDoctor(String doctorCRM) {
        return this.doctorCRM.equals(doctorCRM);
    }

    public boolean belongsToPatient(String patientCPF) {
        return this.patientCPF.equals(patientCPF);
    }

    public boolean belongsToDoctorAndPatient(String doctorCRM, String patientCPF) {
        return belongsToDoctor(doctorCRM) && belongsToPatient(patientCPF);
    }

    public boolean hasOccurred() {
        return LocalDateTime.of(date, time).isBefore(LocalDateTime.now());
    }

    public boolean isPending() {
        return LocalDateTime.of(date, time).isAfter(LocalDateTime.now());
    }

    public boolean isInPeriod(LocalDate startDate, LocalDate endDate) {
        return (date.isEqual(startDate) || date.isAfter(startDate)) &&
               (date.isEqual(endDate)   || date.isBefore(endDate));
    }

    public int monthsSince() {
        if (!hasOccurred()) {
            return 0;
        }
        return (int) Period.between(date, LocalDate.now()).toTotalMonths();
    }

    public boolean patientHasNotVisitedFor(int months) {
        return hasOccurred() && monthsSince() > months;
    }

    public String getFormattedDateTime() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return date.format(dateFormatter) + " às " + time.format(timeFormatter);
    }

    @Override
    public String toString() {
        return "Consulta em " + getFormattedDateTime() +
                ", Paciente: " + patientCPF +
                ", Médico: " + doctorCRM;
    }

    public void displayDetails() {
        System.out.println(toString());
    }

    public String toCSVFormat() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        return date.format(dateFormatter) + "," +
                time.format(timeFormatter) + "," +
                patientCPF + "," +
                doctorCRM;
    }

    public void saveToCSVFile(String filename, boolean append) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, append))) {
            if (!append) {
                writer.println("Data,Horario,CPF_Paciente,CRM_Medico");
            }
            writer.println(toCSVFormat());
        } catch (IOException error) {
            System.out.println("Erro ao salvar no arquivo CSV: " + error.getMessage());
        }
    }

    public int compareByDateTime(Appointment other) {
        LocalDateTime thisDateTime  = LocalDateTime.of(this.date, this.time);
        LocalDateTime otherDateTime = LocalDateTime.of(other.date, other.time);
        return thisDateTime.compareTo(otherDateTime);
    }

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

    public static List<Appointment> filterByDoctor(List<Appointment> appointments, String doctorCRM) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.belongsToDoctor(doctorCRM)) {
                result.add(appointment);
            }
        }
        return result;
    }

    public static List<Appointment> filterByPatient(List<Appointment> appointments, String patientCPF) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.belongsToPatient(patientCPF)) {
                result.add(appointment);
            }
        }
        return result;
    }

    public static List<String> findAllDoctorsForPatient(List<Appointment> allAppointments, String patientCPF) {
        List<Appointment> patientAppointments = filterByPatient(allAppointments, patientCPF);
        Set<String> uniqueDoctorCRMs = new HashSet<>();
        for (Appointment appointment : patientAppointments) {
            uniqueDoctorCRMs.add(appointment.getDoctorCRM());
        }
        return new ArrayList<>(uniqueDoctorCRMs);
    }

    public static List<Appointment> filterByPeriod(List<Appointment> appointments, LocalDate startDate, LocalDate endDate) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.isInPeriod(startDate, endDate)) {
                result.add(appointment);
            }
        }
        return result;
    }

    public static List<Appointment> filterPending(List<Appointment> appointments) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.isPending()) {
                result.add(appointment);
            }
        }
        return result;
    }

    public static List<Appointment> filterOccurred(List<Appointment> appointments) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.hasOccurred()) {
                result.add(appointment);
            }
        }
        return result;
    }

    // MELHORIA 4: validação com regex e controle de erro
    private static boolean isValidCPF(String cpf) {
        return cpf.matches("\\d{11}");
    }

    private static boolean isValidCRM(String crm) {
        return crm.matches("\\d+");
    }

    private static Appointment parseLine(String line) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        String[] parts = line.split(",");

        if (parts.length < 4) return null;

        String dateStr = parts[0].trim();
        String timeStr = parts[1].trim();
        String cpf = parts[2].trim();
        String crm = parts[3].trim();

        if (!isValidCPF(cpf) || !isValidCRM(crm)) {
            System.out.println("CPF ou CRM inválido na linha: " + line);
            return null;
        }

        try {
            LocalDate date = LocalDate.parse(dateStr, dateFormatter);
            LocalTime time = LocalTime.parse(timeStr, timeFormatter);
            return new Appointment(date, time, cpf, crm);
        } catch (Exception e) {
            System.out.println("Erro ao converter data/hora na linha: " + line);
            return null;
        }
    }

    // MELHORIA 2, 4, 5: exceção específica + try-with-resources + validação
    public static List<Appointment> loadFromCSV(String filename) {
        List<Appointment> appointments = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(filename))) {
            if (scanner.hasNextLine()) scanner.nextLine(); // pula cabeçalho

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                Appointment ap = parseLine(line); // MELHORIA 6: separação de parsing
                if (ap != null) {
                    appointments.add(ap);
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar as consultas: " + e.getMessage());
        }

        return appointments;
    }
}
