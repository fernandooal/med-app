/**
 * Enumerado que representa os possíveis estados de uma consulta médica.
 * Este enumerado facilita o gerenciamento do ciclo de vida das consultas
 * e melhora a legibilidade do código.
 */
public enum AppointmentStatus {
    /**
     * Consulta agendada para uma data futura
     */
    PENDING("Agendada"),

    /**
     * Consulta que já ocorreu
     */
    COMPLETED("Realizada"),

    /**
     * Consulta cancelada pelo médico ou paciente
     */
    CANCELLED("Cancelada");

    private final String description;

    /**
     * Construtor do enumerado
     *
     * @param description Descrição em português do status
     */
    AppointmentStatus(String description) {
        this.description = description;
    }

    /**
     * Retorna a descrição do status em português
     *
     * @return String contendo a descrição
     */
    public String getDescription() {
        return description;
    }
}