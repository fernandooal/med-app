import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

        System.out.println("Digite o usuário: ");
        String username = scanner.nextLine();
        System.out.println("Digite a senha: ");
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
                System.out.println("1 - Cadastrar Médico");
                System.out.println("2 - Cadastrar Paciente");
                System.out.println("\n0 - Voltar ao Menu Principal");
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
}
