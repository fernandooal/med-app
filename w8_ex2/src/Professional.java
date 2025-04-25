public class Professional extends Player{
    // attributes
    private Double capital;

    // constructor method
    public Professional(String name, int score, Double capital) {
        // invoke superclass constructor
        super(name, score);
        this.capital = capital;
    }

    // behavior methods
    public void print() {
        super.print();
        System.out.println("Professional:");
        System.out.println("Capital: " + capital);
    }

    public void win(int points) {
        // add points to score
        score += points;
        // add 10% from points to capital
        capital += points * 4;
    }

    public void lose(int points) {
        // subtract points from score
        score -= points;
        // subtract 10% from capital
        capital -= points * 4;
    }
}