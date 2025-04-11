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
import java.util.List;
import java.util.Scanner;


public class Appointment {
    // immutable attributes
    private final LocalDate date;
    private final LocalTime time;
    private final String patientCPF;
    private final String doctorCRM;

    // constructor method
    public Appointment(LocalDate date, LocalTime time, String patientCPF, String doctorCRM) {
        this.date = date;
        this.time = time;
        this.patientCPF = patientCPF;
        this.doctorCRM = doctorCRM;
    }

    /* methods for specific behaviors or appointments */
    // check appointment for specific doctor
    public boolean belongsToDoctor(String doctorCRM) {
        return this.doctorCRM.equals(doctorCRM);
    }

    // check appointment for specific patient
    public boolean belongsToPatient(String patientCPF) {
        return this.patientCPF.equals(patientCPF);
    }

    // check appointment for a specific doctor AND a spectific patient
    public boolean belongsToDoctorAndPatient(String doctorCRM, String patientCPF) {
        return belongsToDoctor(doctorCRM) && belongsToPatient(patientCPF);
    }

    // check if the appointment has already happened
    public boolean hasOccurred() {
        return LocalDateTime.of(date, time).isBefore(LocalDateTime.now());
    }

    // check if the appointment will still happen
    public boolean isPending() {
        return LocalDateTime.of(date, time).isAfter(LocalDateTime.now());
    }

    // check if the appoint is within a specific period
    public boolean isInPeriod(LocalDate startDate, LocalDate endDate) {
        return (date.isEqual(startDate) || date.isAfter(startDate)) &&
               (date.isEqual(endDate)   || date.isBefore(endDate));
    }

    // calculate how much time has passed since the appointment in months
    public int monthsSince() {
        if (!hasOccurred()) {
            return 0;
        }
        // explicit type cast: long to int
        return (int) Period.between(date, LocalDate.now()).toTotalMonths();
    }

    // check if the patient has not seen a doctor for more than X months
    public boolean patientHasNotVisitedFor(int months) {
        return hasOccurred() && monthsSince() > months;
    }

    // get the formatted date and time for display
    public String getFormattedDateTime() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return date.format(dateFormatter) + " às " + time.format(timeFormatter);
    }

    // overriding toString function to provide a custom string representation of an object
    @Override
    public String toString() {
        return "Consulta em " + getFormattedDateTime() +
                ", Paciente: " + patientCPF +
                ", Médico: " + doctorCRM;
    }

    // show appointment details
    public void displayDetails() {
        System.out.println(toString());
    }

    // method to get CSV format representation of the appointment
    public String toCSVFormat() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        return date.format(dateFormatter) + "," +
                time.format(timeFormatter) + "," +
                patientCPF + "," +
                doctorCRM;
    }

    // method to add appointment to a CSV file or create a new CSV file
    // appointment.saveToCSVFile("appointments.csv", true); // true = append (add)
    // appointment.saveToCSVFile("new_appointments.csv", false); // false = create new
    public void saveToCSVFile(String filename, boolean append) {
        // "try-with-resources"
        // https://docs.oracle.com/javase/8/docs/technotes/guides/language/try-with-resources.html
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, append))) {
            // If the file is new, add the header
            if (!append) {
                writer.println("Data,Horario,CPF_Paciente,CRM_Medico");
            }
            writer.println(toCSVFormat());
        } catch (IOException error) {
            System.out.println("Erro ao salvar no arquivo CSV: " + error.getMessage());
        }
    }

    // compare with another appointment for sorting by date and time
    public int compareByDateTime(Appointment other) {
        LocalDateTime thisDateTime  = LocalDateTime.of(this.date, this.time);
        LocalDateTime otherDateTime = LocalDateTime.of(other.date, other.time);
        return thisDateTime.compareTo(otherDateTime);
    }

    // basic getters for few operations
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

    /* static methods for operations with appointments */
    // filter appointments by specific doctor
    public static List<Appointment> filterByDoctor(List<Appointment> appointments, String doctorCRM) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.belongsToDoctor(doctorCRM)) {
                result.add(appointment);
            }
        }
        return result;
    }

    // filter appointments by specific patient
    public static List<Appointment> filterByPatient(List<Appointment> appointments, String patientCPF) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.belongsToPatient(patientCPF)) {
                result.add(appointment);
            }
        }
        return result;
    }

    // filter appointments by specific period
    public static List<Appointment> filterByPeriod(
            List<Appointment> appointments,
            LocalDate startDate, LocalDate endDate
    ) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.isInPeriod(startDate, endDate)) {
                result.add(appointment);
            }
        }
        return  result;
    }

    // filter pending appointments
    public static List<Appointment> filterPending(List<Appointment> appointments) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.isPending()) {
                result.add(appointment);
            }
        }
        return result;
    }

    // filter previous appointments
    public static List<Appointment> filterOccurred(List<Appointment> appointments) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.hasOccurred()) {
                result.add(appointment);
            }
        }
        return result;
    }

    // load appointment from CSV file
    public static List<Appointment> loadFromCSV(String filename) {
        List<Appointment> appointments = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // "try-with-resources"
        try (Scanner scanner = new Scanner(new File(filename))) {
            // skip header if exists
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                try {
                    String[] parts = line.split(",");
                    // if parts is incomplete, skip
                    if (parts.length < 4) continue;

                    LocalDate date = LocalDate.parse(parts[0].trim(), dateFormatter);
                    LocalTime time = LocalTime.parse(parts[1].trim(), timeFormatter);

                    String patientCPF = parts[2].trim();
                    String doctorCRM = parts[3].trim();
                    appointments.add(new Appointment(date, time, patientCPF, doctorCRM));
                } catch (Exception e) {
                    System.out.println("Erro ao processar linha: " + line + " - " + e.getMessage());
                }
            }
        } catch (Exception error) {
            System.out.println("Erro ao carregar as consultas: " + error.getMessage());
        }

        return appointments;
    }
}
