import java.util.List;
import java.util.Scanner;

/**
 * Classe utilitária para funções de interface com o usuário.
 * Contém métodos comuns utilizados em diferentes partes da aplicação,
 * seguindo o princípio DRY (Don't Repeat Yourself).
 */
public class UIUtils {

    /**
     * Formata um CPF adicionando pontos e traço no formato padrão brasileiro.
     *
     * @param cpf String contendo 11 dígitos do CPF
     * @return CPF formatado (ex: 123.456.789-00) ou o CPF original se inválido
     */
    public static String formatCPF(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.substring(0, 3) + "." +
                cpf.substring(3, 6) + "." +
                cpf.substring(6, 9) + "-" +
                cpf.substring(9);
    }

    /**
     * Exibe uma lista de itens com paginação.
     * Este método substitui as implementações duplicadas em DoctorView e PatientView.
     *
     * @param <T> Tipo dos itens na lista
     * @param list Lista de itens a exibir
     * @param pageSize Número de itens por página
     * @param scanner Scanner para leitura da entrada do usuário
     */
    public static <T> void paginateList(List<T> list, int pageSize, Scanner scanner) {
        if (list == null || list.isEmpty()) {
            System.out.println("Nenhum item para exibir.");
            return;
        }

        try {
            int total = list.size();
            int pages = (int) Math.ceil((double) total / pageSize);
            int page = 0;

            while (page < pages) {
                int start = page * pageSize;
                int end = Math.min(start + pageSize, total);

                System.out.println("\nPágina " + (page + 1) + "/" + pages);
                for (int i = start; i < end; i++) {
                    System.out.println(list.get(i));
                }

                if (page < pages - 1) { // Se não for a última página
                    System.out.print("Deseja ver a próxima página? (s/n): ");
                    String input = scanner.nextLine();
                    if (!input.equalsIgnoreCase("s")) break;
                }

                page++;
            }
        } catch (Exception e) {
            System.err.println("Erro durante a paginação: " + e.getMessage());
            e.printStackTrace(System.err);

        }
    }


}
