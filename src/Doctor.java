import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Doctor {

    private String name;
    private String code; // MELHORIA 1: código agora é do tipo String

    // Construtor atualizado
    public Doctor(String name, String code) {
        this.name = name;
        this.code = code;
    }

    // Getter e Setter para name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Getter e Setter para code
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Valida se um CRM tem o formato correto
     *
     * @param crm CRM a validar
     * @return true se o CRM é válido, false caso contrário
     */
    public static boolean isValidCRM(String crm) {
        return crm != null && crm.matches("\\d+");
    }

    // Método para carregar lista de médicos do CSV
    public static List<Doctor> loadFromCSV(String filename) {
        List<Doctor> doctors = new ArrayList<>();

        // MELHORIA 5: uso de try-with-resources
        try (Scanner scanner = new Scanner(new File(filename))) {
            // Pular o cabeçalho
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
                    String code = parts[1].trim(); // MELHORIA 1: mantido como string

                    doctors.add(new Doctor(name, code));
                } catch (NumberFormatException e) { // MELHORIA 4: exceção específica
                    System.out.println("Erro de formatação no código: " + e.getMessage());
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Erro de formato no CSV: " + e.getMessage());
                }
            }
        } catch (IOException e) { // MELHORIA 4
            System.out.println("Erro ao carregar os médicos: " + e.getMessage());
        }

        return doctors;
    }

    // Representação em string
    @Override
    public String toString() {
        return "Médico: " + name + ", Código: " + code;
    }
}
