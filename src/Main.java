import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Bem-vindo ao seu aplicativo de gerenciamento de clínica!\n");

        int option = 0;
        while (option != -1){
            try{
                System.out.println("\nQual painel você gostaria de acessar?");
                System.out.println("0 - Administrador");
                System.out.println("1 - Paciente");
                System.out.println("2 - Médico");
                System.out.println("-1 - Sair");
                option = scanner.nextInt();

                switch (option){
                    case 0: AdminView.checkOptions(true); break;
                    case 1: PatientView.checkOptions(true); break;
                    case 2: DoctorView.checkOptions(true); break;
                    case -1: break;
                    default: System.out.println("Opção inválida!"); break;
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }

        System.out.println("\nAté a próxima! =)");
    }
}