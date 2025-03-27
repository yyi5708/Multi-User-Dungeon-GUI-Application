package mud;

public class Armor extends Item {
    private int defenseBonus;

    public Armor(String name, String description, int value, int defenseBonus) {
        super(name, description, value);
        this.defenseBonus = defenseBonus;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    public void setDefenseBonus(int defenseBonus) {
        this.defenseBonus = defenseBonus;
    }

    public void use(Player player) {
        player.setDefense(defenseBonus);
        System.out.println(player.getName() + " used " + name + " and gained " + defenseBonus + " defense.");
    }

    @Override
    public String toString() {
        return "Armor [defenseBonus=" + defenseBonus + "]";
    }
}
