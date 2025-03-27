package mud;

public class Food extends Item {
    private int healthRestored;

    public Food(String name, String description, int value, int healthRestored) {
        super(name, description, value);
        this.healthRestored = healthRestored;
    }

    public int getHealthRestored() {
        return healthRestored;
    }

    public void setHealthRestored(int healthRestored) {
        this.healthRestored = healthRestored;
    }

    public void use(Player player) {
        player.setHealth(player.getHealth() + healthRestored);
        System.out.println(player.getName() + " used " + name + " and restored " + healthRestored + " health.");
    }

    @Override
    public String toString() {
        return "Food [healthRestored=" + healthRestored + "]";
    }
}
