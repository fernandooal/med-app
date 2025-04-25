public class Player {
    // attributes
    private String name;
    protected int score;

    // constructor method
    public Player(String name, int score) {
        this.name = name;
        this.score = score;
    }

    // method
    public void print() {
        System.out.println("Nome: " + name);
        System.out.println("Score: " + score);
    }

    public boolean whatIsYourName(String name) {
        return this.name.equals(name);
    }
}
