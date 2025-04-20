import java.util.List;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        // Criamos apenas um scanner para toda a aplicação
        Scanner scanner = new Scanner(System.in);

        System.out.println("Bem-vindo ao seu aplicativo de gerenciamento de clínica!\n");

        try {
            // Carregamento dos dados
            List<Doctor> doctors = Doctor.loadFromCSV("doctors_clean.csv");
            List<Patient> patients = Patient.loadFromCSV("patients.csv");
            List<Appointment> appointments = Appointment.loadFromCSV("appointments.csv");
            associateAppointmentsToPatients(patients, appointments);

            // Mapeamento de médicos para acesso eficiente
            Map<String, Doctor> doctorMap = new HashMap<>();
            for (Doctor doctor : doctors) {
                doctorMap.put(doctor.getCode(), doctor);
            }

            int option = 0;
            while (option != -1) {
                try {
                    System.out.println("\nQual painel você gostaria de acessar?");
                    System.out.println("0 - Administrador");
                    System.out.println("1 - Paciente");
                    System.out.println("2 - Médico");
                    System.out.println("-1 - Sair");

                    String input = scanner.nextLine();
                    option = Integer.parseInt(input);

                    switch (option) {
                        case 0:
                            // AdminView já aceita scanner como parâmetro
                            AdminView.checkOptions(true, scanner);
                            break;
                        case 1:
                            // PatientView já aceita scanner como parâmetro
                            PatientView.checkOptions(patients, true, scanner);
                            break;
                        case 2:
                            // Passando o scanner para DoctorView
                            DoctorView.checkOptions(doctors, appointments, patients, true, scanner);
                            break;
                        case -1:
                            System.out.println("Encerrando o sistema...");
                            break;
                        default:
                            System.out.println("Opção inválida!");
                            break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Entrada inválida. Por favor, digite um número.");
                } catch (Exception e) {
                    System.out.println("Erro inesperado: " + e.getMessage());
                    // Imprimir stack trace para facilitar a depuração
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("Erro fatal ao inicializar a aplicação: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Fechar o scanner somente ao final do programa
            scanner.close();
        }

        System.out.println("\nAté a próxima! =)");
    }

    public static void associateAppointmentsToPatients(List<Patient> patients, List<Appointment> appointments) {
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