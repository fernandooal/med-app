import java.util.ArrayList;

public class Patient {

    private String name;
    private String cpf;
//    private ArrayList<apoointment> appointmentList; #Array para as consultas


    public Patient(String name, String cpf)
    {
        this.name = name;
        this.cpf = cpf;
//        this.appointment = new ArrayList<>();
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getCpf()
    {
        return cpf;
    }
    public void setCpf(String cpf)
    {
        this.cpf = cpf;
    }

//    public ArrayList<appointment> getAppointmentList()
//    {
//        return appointmentList;
//    }
//    public void addAppointment(Appointment appointment)
//    {
//        this.appointmentList.add(appointment);
//    }

}
