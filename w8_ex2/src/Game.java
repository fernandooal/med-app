import java.util.ArrayList;

public class Game {
    // new collection
    private static ArrayList<Player> players = new ArrayList<Player>();

    public static void main(String[] args) {
        // create 9 objects
        players.add(new Beginner("Hugo", 200, 80.0));
        players.add(new Beginner("Fernando", 200, 80.0));
        players.add(new Beginner("Ângelo", 200, 80.0));
        players.add(new Professional("Spencer", 200, 80.0));
        players.add(new Professional("Jafte", 200, 80.0));
        players.add(new Professional("Renato", 200, 80.0));
        players.add(new Senior("Alcides", 200, 80.0, 400.0));
        players.add(new Senior("Vilmar", 200, 80.0, 400.0));
        players.add(new Senior("Frank", 200, 80.0, 400.0));
        print_collection();
    }

    private static void print_menu() {
        System.out.println("\nEscolha uma das opções abaixo:");
        System.out.println("1 - Criar jogador Principiante");
        System.out.println("2 - Criar jogador Profissional");
        System.out.println("3 - Criar jogador Senior");
        System.out.println("4 - Sair");
        System.out.println("5 - Administrador");
        System.out.println("6 - Paciente");
        System.out.println("7 - Médico");
        System.out.println("8 - Sair");
    }

    private static void print_collection() {
        System.out.println("Coleção de Jogadores: ");
        for (Player player : players) {
            player.print();
            System.out.println("");
        }
    }

    private static  Player search(String name) {
        Player response = null;
        for (Player player : players) {
            if (player.whatIsYourName(name)){
                response = player;
                break;
            }
        }
        return response;
    }
}
