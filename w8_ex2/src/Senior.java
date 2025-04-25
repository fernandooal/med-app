public class Senior extends Professional {
    // attributes
    private Double prize;

    // constructor method
    public Senior (String name, int score, Double capital, Double prize) {
        // invoke superclass constructor
        super(name, score, capital);
        this.prize = prize;
    }

    // behavior methods
    public void print() {
        super.print();
        System.out.println("Senior:");
        System.out.println("Prize: " + prize);
    }

    public void win(int points) {
        super.win(points);
        prize *= 2;
    }

    public void lose(int points) {
        super.lose(points);
        prize /= 2;
    }
}
