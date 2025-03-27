package mud;

public class Weapon extends Item {
    private int attackBonus;

    public Weapon(String name, String description, int value, int attackBonus) {
        super(name, description, value);
        this.attackBonus = attackBonus;
    }

    public int getAttackBonus() {
        return attackBonus;
    }

    public void setAttackBonus(int attackBonus) {
        this.attackBonus = attackBonus;
    }

    public void use(Player player) {
        player.setAttack(player.getAttack() + attackBonus);
        System.out.println(player.getName() + " used " + name + " and gained " + attackBonus + " attack.");
    }

    @Override
    public String toString() {
        return "Weapon [attackBonus=" + attackBonus + "]";
    }
}
