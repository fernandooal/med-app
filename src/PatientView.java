import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Classe responsável pela interface com o usuário para a visão do paciente.
 * Gerencia as funcionalidades disponíveis para pacientes no sistema,
 * como agendamento, visualização e gerenciamento de consultas.
 */
public class PatientView {

    private static final String APPOINTMENT_CSV = "appointments.csv";
    private static final String DOCTOR_CSV = "doctors_clean.csv";


    /**
     * Ponto de entrada principal para a interface do paciente
     *
     * @param patients Lista de pacientes cadastrados no sistema
     * @param search Flag para controlar o loop do menu
     * @param scanner Scanner para leitura de entrada do usuário
     */
    public static void checkOptions(List<Patient> patients, boolean search, Scanner scanner) {
        while (search) {
            try {
                // Solicitar CPF ao paciente
                System.out.print("Digite seu CPF (somente números): ");
                String cpf = scanner.nextLine().trim();

                if (!cpf.matches("\\d{11}")) {
                    System.out.println("CPF inválido. Deve conter exatamente 11 dígitos.");
                    continue;
                }

                // Buscar paciente pelo CPF
                Patient patient = findPatientByCPF(patients, cpf);

                if (patient == null) {
                    System.out.println("Paciente não encontrado. Deseja tentar novamente? (s/n): ");
                    String retry = scanner.nextLine();
                    if (!retry.equalsIgnoreCase("s")) {
                        search = false;
                    }
                    continue;
                }

                // Dar boas-vindas ao paciente e mostrar o menu principal
                System.out.println("\nBem-vindo(a), " + patient.getName() + "!");

                // Carregar consultas do paciente
                List<Appointment> allAppointments = Appointment.loadFromCSV(APPOINTMENT_CSV);
                List<Appointment> patientAppointments = Appointment.filterByPatient(allAppointments, patient.getCpf());

                // Atualizar a lista de consultas do paciente
                patient.getAppointmentList().clear();
                for (Appointment app : patientAppointments) {
                    patient.addAppointment(app);
                }

                patientMenu(patient, allAppointments, scanner);
                search = false;

            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
                e.printStackTrace(System.err);

            }
        }
    }

    /**
     * Exibe o menu principal para o paciente e processa a opção escolhida
     *
     * @param patient Paciente logado
     * @param allAppointments Todas as consultas do sistema
     * @param scanner Scanner para leitura de entrada do usuário
     */
    private static void patientMenu(Patient patient, List<Appointment> allAppointments, Scanner scanner) {
        boolean continueMenu = true;

        while (continueMenu) {
            try {
                System.out.println("\n===== MENU DO PACIENTE =====");
                System.out.println("1 - Agendar nova consulta");
                System.out.println("2 - Ver consultas agendadas (futuras)");
                System.out.println("3 - Ver consultas realizadas");
                System.out.println("4 - Remarcar consulta");
                System.out.println("5 - Cancelar consulta");
                System.out.println("6 - Ver todos os médicos do paciente");
                System.out.println("7 - Ver consultas realizadas com um médico específico");
                System.out.println("0 - Sair");
                System.out.print("\nEscolha uma opção: ");

                int option = Integer.parseInt(scanner.nextLine());

                switch (option) {
                    case 0:
                        continueMenu = false;
                        break;
                    case 1:
                        Appointment.scheduleNewAppointment(patient, allAppointments, scanner);
                        break;
                    case 2:
                        Appointment.viewFutureAppointments(patient, allAppointments, scanner);
                        break;
                    case 3:
                        Appointment.viewPastAppointments(patient, scanner);
                        break;
                    case 4:
                        Appointment.rescheduleAppointment(patient, allAppointments, scanner);
                        break;
                    case 5:
                        Appointment.cancelAppointment(patient, allAppointments, scanner);
                        break;
                    case 6:
                        viewAllPatientDoctors(patient, allAppointments, scanner);
                        break;
                    case 7:
                        viewPatientAppointmentsWithDoctor(patient, allAppointments, scanner);
                        break;
                    default:
                        System.out.println("Opção inválida!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Digite um número.");
            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
                e.printStackTrace(System.err);

            }
        }
    }

    /**
     * Exibe todos os médicos do paciente (médicos que ele já se consultou ou tem consulta agendada)
     *
     * @param patient Paciente atual
     * @param allAppointments Todas as consultas do sistema
     * @param scanner Scanner para leitura de entrada do usuário
     */
    private static void viewAllPatientDoctors(Patient patient, List<Appointment> allAppointments, Scanner scanner) {
        // Lista para armazenar os CRMs únicos dos médicos
        List<String> doctorCRMs = new ArrayList<>();

        // Extrair CRMs únicos de todas as consultas do paciente
        for (Appointment app : patient.getAppointmentList()) {
            String crm = app.getDoctorCRM();
            if (!doctorCRMs.contains(crm)) {
                doctorCRMs.add(crm);
            }
        }

        if (doctorCRMs.isEmpty()) {
            System.out.println("Você ainda não tem nenhuma consulta com médicos.");
            return;
        }

        System.out.println("\nMédicos que você já consultou ou tem consulta agendada:");

        // Carregar lista de médicos para buscar os nomes
        List<Doctor> doctors = Doctor.loadFromCSV(DOCTOR_CSV);

        for (int i = 0; i < doctorCRMs.size(); i++) {
            String crm = doctorCRMs.get(i);
            String doctorName = DoctorView.getDoctorName(doctors, crm);
            System.out.println((i + 1) + " - " + doctorName + " (CRM: " + crm + ")");
        }
    }

    /**
     * Exibe todas as consultas realizadas pelo paciente com um médico específico
     *
     * @param patient Paciente atual
     * @param allAppointments Todas as consultas do sistema
     * @param scanner Scanner para leitura de entrada do usuário
     */
    private static void viewPatientAppointmentsWithDoctor(Patient patient, List<Appointment> allAppointments, Scanner scanner) {
        // Lista para armazenar os CRMs únicos dos médicos
        List<String> doctorCRMs = new ArrayList<>();

        // Extrair CRMs únicos de todas as consultas do paciente
        for (Appointment app : patient.getAppointmentList()) {
            String crm = app.getDoctorCRM();
            if (!doctorCRMs.contains(crm)) {
                doctorCRMs.add(crm);
            }
        }

        if (doctorCRMs.isEmpty()) {
            System.out.println("Você ainda não tem nenhuma consulta com médicos.");
            return;
        }

        System.out.println("\nSelecione o médico para ver as consultas realizadas:");

        // Carregar lista de médicos para buscar os nomes
        List<Doctor> doctors = Doctor.loadFromCSV(DOCTOR_CSV);

        for (int i = 0; i < doctorCRMs.size(); i++) {
            String crm = doctorCRMs.get(i);
            String doctorName = DoctorView.getDoctorName(doctors, crm);
            System.out.println((i + 1) + " - " + doctorName + " (CRM: " + crm + ")");
        }

        System.out.print("\nDigite o número do médico (0 para voltar): ");
        int selection = Integer.parseInt(scanner.nextLine());

        if (selection <= 0 || selection > doctorCRMs.size()) {
            return;
        }

        String selectedCRM = doctorCRMs.get(selection - 1);

        // Filtrar apenas consultas realizadas com o médico selecionado
        List<Appointment> pastAppointmentsWithDoctor = new ArrayList<>();
        for (Appointment app : patient.getAppointmentList()) {
            if (app.getDoctorCRM().equals(selectedCRM) &&
                    (app.getStatus() == AppointmentStatus.COMPLETED ||
                            (app.hasOccurred() && app.getStatus() != AppointmentStatus.CANCELLED))) {

                pastAppointmentsWithDoctor.add(app);
            }
        }

        if (pastAppointmentsWithDoctor.isEmpty()) {
            System.out.println("Você não tem consultas realizadas com este médico.");
            return;
        }

        // Ordenar por data/hora (mais recente primeiro)
        pastAppointmentsWithDoctor.sort(Comparator.comparing(Appointment::getDate)
                .thenComparing(Appointment::getTime).reversed());

        String doctorName = DoctorView.getDoctorName(doctors, selectedCRM);
        System.out.println("\nConsultas realizadas com " + doctorName + ":");

        for (Appointment app : pastAppointmentsWithDoctor) {
            System.out.println("- " + app.getFormattedDateTime());
        }
    }

    /**
     * Busca um paciente pelo CPF
     *
     * @param allPatients Lista de todos os pacientes
     * @param cpf CPF a ser buscado
     * @return Paciente encontrado ou null se não encontrado
     */
    public static Patient findPatientByCPF(List<Patient> allPatients, String cpf) {
        for (Patient p : allPatients) {
            if (p.getCpf().equals(cpf)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Busca um paciente pelo nome
     *
     * @param allPatients Lista de todos os pacientes
     * @param scanner Scanner para leitura de entrada
     * @return Paciente selecionado ou null se cancelado
     */
    public static Patient findPatientByName(List<Patient> allPatients, Scanner scanner) {
        System.out.print("Digite o nome do paciente (ou parte do nome): ");
        String searchName = scanner.nextLine().trim().toLowerCase();

        List<Patient> matches = new ArrayList<>();
        for (Patient p : allPatients) {
            if (p.getName().toLowerCase().contains(searchName)) {
                matches.add(p);
            }
        }

        if (matches.isEmpty()) {
            System.out.println("Nenhum paciente encontrado com esse nome.");
            return null;
        }

        System.out.println("\nPacientes encontrados:");
        for (int i = 0; i < matches.size(); i++) {
            Patient p = matches.get(i);
            System.out.println((i + 1) + " - " + p.getName() + " (CPF: " + UIUtils.formatCPF(p.getCpf()) + ")");
        }

        System.out.print("\nDigite o número do paciente (0 para voltar): ");
        try {
            int selection = Integer.parseInt(scanner.nextLine());

            if (selection == 0) {
                return null;
            }

            if (selection < 1 || selection > matches.size()) {
                System.out.println("Seleção inválida.");
                return null;
            }

            return matches.get(selection - 1);

        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número.");
            return null;
        }
    }

    /**
     * Seleciona um paciente existente no sistema
     *
     * @param allPatients Lista de todos os pacientes
     * @param scanner Scanner para leitura de entrada
     * @return Paciente selecionado ou null se cancelado
     */
    public static Patient selectExistingPatient(List<Patient> allPatients, Scanner scanner) {
        System.out.println("\nSelecione como deseja buscar o paciente:");
        System.out.println("1 - Por CPF");
        System.out.println("2 - Por nome");
        System.out.println("0 - Voltar");
        System.out.print("Escolha uma opção: ");

        try {
            int option = Integer.parseInt(scanner.nextLine());

            switch (option) {
                case 0:
                    return null;
                case 1:
                    System.out.print("Digite o CPF do paciente (somente números): ");
                    String cpf = scanner.nextLine().trim();

                    if (!cpf.matches("\\d{11}")) {
                        System.out.println("CPF inválido. Deve conter exatamente 11 dígitos.");
                        return null;
                    }

                    Patient p = findPatientByCPF(allPatients, cpf);
                    if (p != null) {
                        System.out.println("Paciente encontrado: " + p.getName());
                    } else {
                        System.out.println("Paciente não encontrado com o CPF informado.");
                    }
                    return p;
                case 2:
                    return findPatientByName(allPatients, scanner);
                default:
                    System.out.println("Opção inválida!");
                    return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Digite um número.");
            return null;
        }
    }

    /**
     * Cadastra um novo paciente no sistema
     *
     * @param scanner Scanner para leitura de entrada
     * @return Novo paciente cadastrado ou null se cancelado
     */
    public static Patient registerNewPatient(Scanner scanner) {
        System.out.println("\n=== CADASTRAR NOVO PACIENTE ===");

        System.out.print("Digite o nome do paciente: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Nome não pode ser vazio.");
            return null;
        }

        System.out.print("Digite o CPF do paciente (somente números): ");
        String cpf = scanner.nextLine().trim();

        if (!cpf.matches("\\d{11}")) {
            System.out.println("CPF inválido. Deve conter exatamente 11 dígitos.");
            return null;
        }

        // Verificar se o paciente já existe
        List<Patient> existingPatients = Patient.loadFromCSV("patients.csv");
        for (Patient p : existingPatients) {
            if (p.getCpf().equals(cpf)) {
                System.out.println("Já existe um paciente com este CPF: " + p.getName());
                return p;
            }
        }

        // Salvar no arquivo CSV
        try (PrintWriter writer = new PrintWriter(new FileWriter("patients.csv", true))) {
            writer.println(name + "," + cpf);
        } catch (IOException error) {
            System.out.println("Erro ao salvar no arquivo CSV: " + error.getMessage());
            return null;
        }

        System.out.println("Paciente cadastrado com sucesso!");
        return new Patient(name, cpf);
    }
}