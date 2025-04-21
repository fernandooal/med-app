import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Classe que representa um paciente no sistema.
 * Armazena informações como nome, CPF e a lista de consultas do paciente.
 */
public class Patient {

    private final String name;
    private final String cpf;
    private final List<Appointment> appointmentList;


    /**
     * Construtor completo de um paciente
     *
     * @param name Nome do paciente
     * @param cpf CPF do paciente
     * @param appointmentList Lista de consultas do paciente
     */
    public Patient(String name, String cpf, List<Appointment> appointmentList)
    {
        this.name = name;
        this.cpf = cpf;
        this.appointmentList = appointmentList;
    }

    /**
     * Construtor simplificado de um paciente, inicializa a lista de consultas vazia
     *
     * @param name Nome do paciente
     * @param cpf CPF do paciente
     */
    public Patient(String name, String cpf){
        this.name = name;
        this.cpf = cpf;
        this.appointmentList = new ArrayList<>();
    }

    /**
     * Retorna o nome do paciente
     *
     * @return Nome do paciente
     */
    public String getName()
    {
        return name;
    }

    /**
     * Retorna o CPF do paciente
     *
     * @return CPF do paciente
     */
    public String getCpf()
    {
        return cpf;
    }

    /**
     * Retorna a lista de consultas do paciente
     *
     * @return Lista de consultas
     */
    public List<Appointment> getAppointmentList()
    {
        return appointmentList;
    }

    /**
     * Adiciona uma consulta à lista do paciente
     *
     * @param appointment Consulta a ser adicionada
     */
    public void addAppointment(Appointment appointment)
    {
        this.appointmentList.add(appointment);
    }

    /**
     * Valida se um CPF tem o formato correto
     *
     * @param cpf CPF a validar
     * @return true se o CPF é válido, false caso contrário
     */
    public static boolean isValidCPF(String cpf) {
        return cpf != null && cpf.matches("\\d{11}");
    }

    /**
     * Carrega pacientes a partir de um arquivo CSV
     *
     * @param filename Nome do arquivo CSV
     * @return Lista de pacientes carregados
     */
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

    /**
     * Representação em string do paciente
     *
     * @return String representando o paciente
     */
    @Override
    public String toString() {
        return "Paciente: " + name + " (CPF: " + UIUtils.formatCPF(cpf) + ")";
    }
}