import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Patient {

    private String name;
    private String cpf;
    private List<Appointment> appointmentList;

    public Patient(String name, String cpf, List<Appointment> appointmentList)
    {
        this.name = name;
        this.cpf = cpf;
        this.appointmentList = appointmentList;
    }

    public Patient(String name, String cpf){
        this.name = name;
        this.cpf = cpf;
        this.appointmentList = new ArrayList<>();
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getCpf()
    {
        return cpf;
    }
    public void setCpf(String cpf)
    {
        this.cpf = cpf;
    }
    public List<Appointment> getAppointmentList()
    {
        return appointmentList;
    }
    public void addAppointment(Appointment appointment)
    {
        this.appointmentList.add(appointment);
    }

    public static List<Patient> loadFromCSV(String filename) {
        List<Patient> patients = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(filename))) {
            // skip header
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                try {
                    String[] parts = line.split(",");
                    if (parts.length < 2) continue;

                    String name = parts[0].trim();
                    String cpf = parts[1].trim();
                    patients.add(new Patient(name, cpf));
                } catch (Exception e) {
                    System.out.println("Erro ao processar linha: " + line + " - " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar os pacientes: " + e.getMessage());
        }

        return patients;
    }
}
