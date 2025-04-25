public class Beginner extends Player {
    // attributes
    private Double bonus;

    // constructor method
    public Beginner(String name, int score, Double bonus) {
        // invoke superclass constructor
        super(name, score);
        this.bonus = bonus;
    }

    // behavior methods
    public void print() {
        super.print();
        System.out.println("Beginner:");
        System.out.println("Bônus: " + bonus);
    }

    public void win(int points) {
        // add points to score
        score += points;
        // add 10% from points to bonus
        bonus += (points * 0.1);
    }

    public void lose(int points) {
        // subtract points from score
        score -= points;
        // subtract 10% from bonus
        bonus -= (points * 0.1);
    }
}
