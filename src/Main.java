import java.util.List;
import java.util.Scanner;

/**
 * Classe principal do sistema de gerenciamento de clínica médica.
 * Responsável por inicializar a aplicação, carregar os dados e direcionar
 * para as interfaces específicas.
 */
public class Main {
    /**
     * Método principal de entrada do programa
     *
     * @param args Argumentos de linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        // Criamos apenas um scanner para toda a aplicação

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Bem-vindo ao seu aplicativo de gerenciamento de clínica!\n");

            // Carregamento dos dados
            List<Doctor> doctors = Doctor.loadFromCSV("doctors_clean.csv");
            List<Patient> patients = Patient.loadFromCSV("patients.csv");
            List<Appointment> appointments = Appointment.loadFromCSV("appointments.csv");
            associateAppointmentsToPatients(patients, appointments);

            int option = 0;
            while (option != -1) {
                try {
                    System.out.println("\nQual painel você gostaria de acessar?");
                    System.out.println("1 - Administrador");
                    System.out.println("2 - Sou Paciente");
                    System.out.println("3 - Sou Médico");
                    System.out.println("0 - Sair");
                    option = scanner.nextInt();
                    scanner.nextLine();
                    switch (option) {
                        case 1:
                            AdminView.checkOptions(doctors, patients, appointments,true, scanner);
                            associateAppointmentsToPatients(patients, appointments);
                            break;
                        case 2:
                            PatientView.checkOptions(patients, true, scanner);
                            associateAppointmentsToPatients(patients, appointments);
                            break;
                        case 3:
                            DoctorView.checkOptions(doctors, appointments, patients, true, scanner);
                            break;
                        case 0:
                            System.out.println("Encerrando o sistema...");
                            option = -1;
                            break;
                        default:
                            System.out.println("Opção inválida!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida. Por favor, digite um número.");
                } catch (Exception e) {
                    System.err.println("Erro inesperado:");
                    e.printStackTrace(System.err);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro fatal ao inicializar a aplicação: " + e.getMessage());
            e.printStackTrace(System.err);
        }


        System.out.println("\nAté a próxima! =)");
    }

    /**
     * Associa as consultas aos seus respectivos pacientes
     *
     * @param patients Lista de pacientes
     * @param appointments Lista de consultas
     */
    public static void associateAppointmentsToPatients(List<Patient> patients, List<Appointment> appointments) {
        // Limpar listas de consultas existentes para evitar duplicatas
        for (Patient patient : patients) {
            patient.getAppointmentList().clear();
        }

        // Associar cada consulta ao paciente correspondente
        for (Appointment appointment : appointments) {
            for (Patient patient : patients) {
                if (patient.getCpf().equals(appointment.getPatientCPF())) {
                    patient.addAppointment(appointment);
                    break;
                }
            }
        }
    }
}