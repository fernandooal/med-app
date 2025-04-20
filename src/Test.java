import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.io.File;


public class Test {
    public static void main(String[] args) {
        // Test: Saving to appointments.csv respecting CSV format
        System.out.println("=== Teste: Salvando no appointments.csv ===");

        try {
            // Check if the file exists and read existing appointments
            File file = new File("appointments.csv");
            boolean fileExists = file.exists();
            List<Appointment> existingAppointments = null;

            if (fileExists) {
                existingAppointments = Appointment.loadFromCSV("appointments.csv");
                System.out.println("Arquivo appointments.csv encontrado com " +
                        existingAppointments.size() + " appointments.");
            } else {
                System.out.println("Arquivo appointments.csv não encontrado. Será criado um novo.");
            }

            // Create some example appointments
            Appointment appointment1 = new Appointment(
                    LocalDate.of(2023, 5, 15),
                    LocalTime.of(14, 30),
                    "12345678900",
                    "12345"
            );

            Appointment appointment2 = new Appointment(
                    LocalDate.now().plusDays(7),
                    LocalTime.of(10, 0),
                    "12345678900",
                    "54321"
            );

            // Show details of the new appointments
            System.out.println("\nNovas consultas a serem adicionadas:");
            appointment1.displayDetails();
            appointment2.displayDetails();

            // Save the appointments to the file (append if file exists, create if it doesn't)
            appointment1.saveToCSVFile("appointments.csv", fileExists);
            appointment2.saveToCSVFile("appointments.csv", true);

            System.out.println("\nConsultas salvas com sucesso em appointments.csv");

            // Read again to verify if everything was saved correctly
            List<Appointment> allAppointments = Appointment.loadFromCSV("appointments.csv");
            System.out.println("\nConsultas presentes no arquivo após salvamento (" +
                    allAppointments.size() + " consultas):");

            // Display only the first 5 and last 5 if there are many appointments
            if (allAppointments.size() <= 10) {
                for (Appointment appointment : allAppointments) {
                    appointment.displayDetails();
                }
            } else {
                System.out.println("Primeiras 5 consultas:");
                for (int i = 0; i < 5; i++) {
                    allAppointments.get(i).displayDetails();
                }

                System.out.println("\nÚltimas 5 consultas:");
                for (int i = allAppointments.size() - 5; i < allAppointments.size(); i++) {
                    allAppointments.get(i).displayDetails();
                }
            }

            // Verify if the new appointments are present
            boolean appointment1Present = false;
            boolean appointment2Present = false;

            for (Appointment appointment : allAppointments) {
                if (appointment.getDate().equals(appointment1.getDate()) &&
                        appointment.getTime().equals(appointment1.getTime()) &&
                        appointment.getPatientCPF().equals(appointment1.getPatientCPF()) &&
                        appointment.getDoctorCRM().equals(appointment1.getDoctorCRM())) {
                    appointment1Present = true;
                }

                if (appointment.getDate().equals(appointment2.getDate()) &&
                        appointment.getTime().equals(appointment2.getTime()) &&
                        appointment.getPatientCPF().equals(appointment2.getPatientCPF()) &&
                        appointment.getDoctorCRM().equals(appointment2.getDoctorCRM())) {
                    appointment2Present = true;
                }
            }

            System.out.println("\nVerificação final:");
            System.out.println("Consulta 1 está presente no arquivo? " + appointment1Present);
            System.out.println("Consulta 2 está presente no arquivo? " + appointment2Present);

        } catch (Exception e) {
            System.out.println("Erro durante o teste: " + e.getMessage());
            e.printStackTrace();
        }
    }
}