import java.util.List;

public class Patient {

    private String name;
    private String cpf;
    private List<Appointment> appointmentList;

    public Patient(String name, String cpf, List<Appointment> appointmentList)
    {
        this.name = name;
        this.cpf = cpf;
        this.appointmentList = appointmentList;
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
    public List<Appointment> getAppointmentList()
    {
        return appointmentList;
    }
    public void addAppointment(Appointment appointment)
    {
        this.appointmentList.add(appointment);
    }
}
