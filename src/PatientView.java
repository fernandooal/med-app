import java.util.List;
import java.util.Scanner;

public class PatientView {
    public static void checkOptions(List<Patient> patients, boolean search) {
        Scanner scanner = new Scanner(System.in);
        int option = 0;
        while (search) {
            try{
                System.out.println("\nSelecione a opção desejada: ");
                System.out.println("0 - Voltar");
                System.out.println("1 - Pesquisar Paciente");
                option = scanner.nextInt();

                switch (option) {
                    case 0: search = false; break;
                    case 1: checkPatientByCPF(patients); break;
                    default: System.out.println("Opção Inválida!"); break;
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void checkPatientByCPF(List<Patient> patients) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Informe o CPF do paciente: ");
        String cpf = scanner.nextLine();

        Patient patientFound = null;
        for (Patient p : patients) {
            if (p.getCpf().equals(cpf)) {
                patientFound = p;
                break;
            }
        }

        if (patientFound == null) {
            System.out.println("Paciente não encontrado.");
        } else {
            menu(patientFound);
        }
    }

    private static void menu(Patient patient) {
        Scanner scanner = new Scanner(System.in);
        int option = 1;
        while (option != 0) {
            try{
                System.out.println("\n**** MENU - Paciente " + patient.getName() + "***\n");
                System.out.println("0 - Voltar ao Menu Principal");
                System.out.println("1 - Verificar médicos do paciente");
                System.out.println("2 - Consultas realizadas do paciente");
                System.out.println("3 - Consultas agendadas do paciente");
                option = scanner.nextInt();

                switch (option) {
                    case 0: break;
                    case 1: patientsDoctors(patient); break;
                    case 2: appointmentsOccurred(patient.getAppointmentList()); break;
                    case 3: appointmentsPending(patient.getAppointmentList()); break;
                    default: System.out.println("Opção Inválida!"); break;
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void patientsDoctors(Patient patient) {
        List<String> doctors = Appointment.findAllDoctorsForPatient(patient.getAppointmentList(), patient.getCpf());
        //TODO: quando a classe de medico estiver pronta melhorar esse for para buscar o nome do médico também
        for (String doctor : doctors) {
            System.out.println("Médico - " + doctor);
        }
    }

    private static void appointmentsOccurred(List<Appointment> a) {
        List<Appointment> appointments = Appointment.filterOccurred(a);
        for (Appointment appointment : appointments) {
            System.out.println(appointment.toString());
        }
    }

    private static void appointmentsPending(List<Appointment> a) {
        List<Appointment> appointments = Appointment.filterPending(a);
        for (Appointment appointment : appointments) {
            System.out.println(appointment.toString());
        }
    }
}
