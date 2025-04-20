import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class AdminView {
    public static void checkOptions(boolean login) {
        Scanner scanner = new Scanner(System.in);
        int option = 0;
        while (login) {
            try{
                System.out.println("\nSelecione a opção desejada: ");
                System.out.println("0 - Voltar");
                System.out.println("1 - Login");
                option = scanner.nextInt();

                switch (option) {
                    case 0: login = false; break;
                    case 1:
                        boolean admin = login();
                        if (admin) {
                            System.out.println("\nLogin efetuado com sucesso!");
                            menu();
                            login = false;
                        } else{
                            System.out.println("Login ou Senha incorreta..");
                        }
                        break;
                    default: System.out.println("Opção Inválida!"); break;
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static boolean login() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite o usuário: ");
        String username = scanner.nextLine();
        System.out.print("Digite a senha: ");
        String password = scanner.nextLine();

        if(username.equals("admin") && password.equals("admin1234")){
            return true;
        } else {
            return false;
        }
    }

    private static void menu() {
        Scanner scanner = new Scanner(System.in);
        int option = 1;

        while (option != 0) {
            try{
                System.out.println("\nInterface de Administrador");
                System.out.println("0 - Voltar ao Menu Principal");
                System.out.println("1 - Cadastrar Médico");
                System.out.println("2 - Cadastrar Paciente");
                option = scanner.nextInt();

                switch (option) {
                    case 0: break;
                    case 1: registerDoctor(); break;
                    case 2: registerPatient(); break;
                    default: System.out.println("Opção inválida.."); break;
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void registerDoctor() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite o nome do médico: ");
        String doctorName = scanner.nextLine();
        System.out.println("Digite o código do médico: ");
        String doctorCode = scanner.nextLine();

        saveToCSV("doctors.csv", doctorName, doctorCode);
    }

    private static void registerPatient() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Digite o nome do paciente: ");
        String patientName = scanner.nextLine();
        System.out.println("Digite o CPF do paciente: ");
        String patientCode = scanner.nextLine();

        saveToCSV("patients.csv", patientName, patientCode);
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

                // Verifica se já existe um paciente com esse CPF
                boolean exists = false;
                for (Patient p : currentPatients) {
                    if (p.getCpf().equals(cpf)) {
                        exists = true;
                        break;
                    }
                }

                // Se não existe, cria novo e adiciona à lista
                if (!exists) {
                    currentPatients.add(new Patient(name, cpf));
                }
            }

        } catch (Exception e) {
            System.out.println("Erro ao atualizar lista de pacientes: " + e.getMessage());
        }
    }

    // TODO: Descomentar quando tiver classe de Doctor
//    public static void updateDoctorsFromCSV(List<Doctor> currentDoctors, String filename) {
//        try (Scanner scanner = new Scanner(new File(filename))) {
//            if (scanner.hasNextLine()) scanner.nextLine();
//
//            while (scanner.hasNextLine()) {
//                String line = scanner.nextLine().trim();
//                if (line.isEmpty()) continue;
//
//                String[] parts = line.split(",");
//                if (parts.length < 2) continue;
//
//                String name = parts[0].trim();
//                int code = Integer.parseInt(parts[1].trim());
//
//                boolean exists = false;
//                for (Doctor d : currentDoctors) {
//                    if (d.getCode() == code) {
//                        exists = true;
//                        break;
//                    }
//                }
//
//                if (!exists) {
//                    currentDoctors.add(new Doctor(name, code));
//                }
//            }
//
//        } catch (Exception e) {
//            System.out.println("Erro ao atualizar lista de médicos: " + e.getMessage());
//        }
//    }
}
