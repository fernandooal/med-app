import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Bem-vindo ao seu aplicativo de gerenciamento de clínica!\n");

        //setup
//        List<Doctor> doctors = Doctor.loadFromCSV("doctors.csv"); TODO: Classe Doctor + Function de load
        List<Patient> patients = Patient.loadFromCSV("patients.csv");
        List<Appointment> appointments = Appointment.loadFromCSV("appointments.csv");
        associateAppointmentsToPatients(patients, appointments);

        int option = 0;
        while (option != -1){
            try{
                System.out.println("\nQual painel você gostaria de acessar?");
                System.out.println("0 - Administrador");
                System.out.println("1 - Paciente");
                System.out.println("2 - Médico");
                System.out.println("\n-1 - Sair");
                option = scanner.nextInt();

                switch (option){
                    case 0:
                        AdminView.checkOptions(true);
                        AdminView.updatePatientsFromCSV(patients, "patients.csv");
//                        AdminView.updateDoctorsFromCSV(doctors, "doctors.csv"); TODO: Descomentar quando tiver classe de Doctor
                        break;
                    case 1: PatientView.checkOptions(patients, true); break;
//                    case 2: DoctorView.checkOptions(doctors, true); break; TODO: descomentar quando a function estiver ok
                    case -1: break;
                    default: System.out.println("Opção inválida!"); break;
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
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