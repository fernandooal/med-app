import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class AdminView {

    private static final String DOCTOR_CSV = "doctors_clean.csv"; // PADRONIZAÇÃO: arquivo único para médicos
    private static final String PATIENT_CSV = "patients.csv";

    public static void checkOptions(boolean login, Scanner scanner) {
        int option = 0;
        while (login) {
            try {
                System.out.println("\nSelecione a opção desejada: ");
                System.out.println("0 - Voltar");
                System.out.println("1 - Login");
                option = Integer.parseInt(scanner.nextLine()); // MELHORIA 4

                switch (option) {
                    case 0: login = false; break;
                    case 1:
                        boolean admin = login(scanner); // AUTENTICAÇÃO SEGURA
                        if (admin) {
                            System.out.println("\nLogin efetuado com sucesso!");
                            menu(scanner);
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

    private static void menu(Scanner scanner) {
        int option = 1;

        while (option != 0) {
            try {
                System.out.println("\nInterface de Administrador");
                System.out.println("0 - Voltar ao Menu Principal");
                System.out.println("1 - Cadastrar Médico");
                System.out.println("2 - Cadastrar Paciente");
                option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 0: break;
                    case 1: registerDoctor(scanner); break;
                    case 2: registerPatient(scanner); break;
                    default: System.out.println("Opção inválida.."); break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número.");
            }
        }
    }

    private static void registerDoctor(Scanner scanner) {
        System.out.println("Digite o nome do médico: ");
        String doctorName = scanner.nextLine();
        System.out.println("Digite o código do médico: ");
        String doctorCode = scanner.nextLine();

        saveToCSV(DOCTOR_CSV, doctorName, doctorCode); // PADRONIZAÇÃO: usa doctors_clean.csv
    }

    private static void registerPatient(Scanner scanner) {
        System.out.println("Digite o nome do paciente: ");
        String patientName = scanner.nextLine();
        System.out.println("Digite o CPF do paciente: ");
        String patientCPF = scanner.nextLine();

        saveToCSV(PATIENT_CSV, patientName, patientCPF);
    }

    private static void saveToCSV(String filename, String name, String code) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename, true))) {
            writer.println(name + "," + code);
        } catch (IOException error) {
            System.out.println("Erro ao salvar no arquivo CSV: " + error.getMessage());
        }

        System.out.println("\nCadastro efetuado com sucesso!");
    }

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