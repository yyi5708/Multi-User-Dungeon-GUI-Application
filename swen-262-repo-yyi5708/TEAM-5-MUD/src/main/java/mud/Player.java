package mud;

import java.io.*;
import com.opencsv.CSVWriter;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class Player {
    private String name;
    private String description;
    private int health;
    private int attack;
    private int defense;
    private Inventory inventory;
    private Weapon equippedWeapon;
    private Armor equippedArmor;

    public Player(String name, String description, int health, int attack, int defense) {
        this.name = name;
        this.description = description;
        this.health = health;
        this.attack = attack;
        this.defense = defense;
        this.inventory = new Inventory(100);
        this.equippedWeapon = null;
        this.equippedArmor = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public void setEquippedWeapon(Weapon equippedWeapon) {
        this.equippedWeapon = equippedWeapon;
    }

    public Armor getEquippedArmor() {
        return equippedArmor;
    }

    public void setEquippedArmor(Armor equippedArmor) {
        this.equippedArmor = equippedArmor;
    }

    public void attack(Player target) {
        int damage = this.attack;
        if (equippedWeapon != null) {
            damage += equippedWeapon.getAttackBonus();
        }
        System.out.println(this.name + " attacks " + target.getName() + " for " + damage + " damage.");
        target.takeDamage(damage);
    }

    public void takeDamage(int damage) {
        int effectiveDamage = damage - this.defense;
        if (equippedArmor != null) {
            effectiveDamage -= equippedArmor.getDefenseBonus();
        }
        if (effectiveDamage < 0)
            effectiveDamage = 0;
        this.health -= effectiveDamage;
        System.out.println(this.name + " takes " + effectiveDamage + " damage.");
    }

    public void equipWeapon(Weapon weapon) {
        this.equippedWeapon = weapon;
        System.out.println(this.name + " equipped " + weapon.getName());
    }

    public void equipArmor(Armor armor) {
        this.equippedArmor = armor;
        System.out.println(this.name + " equipped " + armor.getName());
    }

    public void pickUpItem(Item item) {
        if (this.inventory.addItem(item)) {
            System.out.println(this.name + " picked up " + item.getName());
        } else {
            System.out.println("Cannot pick up " + item.getName() + ". Bag is full.");
        }
    }

    public void destroyItem(Item item) {
        inventory.destroyItem(item);
    }

    @Override
    public String toString() {
        return "Player [name=" + name + ", description=" + description + ", health=" + health + ", attack=" + attack
                + ", defense=" + defense + ", inventory=" + inventory + ", equippedWeapon=" + equippedWeapon
                + ", equippedArmor=" + equippedArmor + "]";
    }

    public void exportToCSV(String filename) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filename))) {
            String[] data = {
                    name,
                    description,
                    String.valueOf(health),
                    String.valueOf(attack),
                    String.valueOf(defense),
                    inventory.toString()
            };
            writer.writeNext(data);
        }
    }

    public Player importFromCSV(String filename) throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReader(new FileReader(filename))) {
            String[] data = reader.readNext();
            Player player = new Player(data[0], data[1], Integer.parseInt(data[2]), Integer.parseInt(data[3]),
                    Integer.parseInt(data[4]));
            Inventory playerInventory = new Inventory(100);
            playerInventory.loadFromString(data[5]);
            player.setInventory(playerInventory);

            return player;
        }
    }

    public void exportToJSON(String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(filename), this);
    }

    public Player importFromJSON(String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filename), Player.class);
    }

    public void exportToXML(String filename) throws IOException {
        XmlMapper mapper = new XmlMapper();
        mapper.writeValue(new File(filename), this);
    }

    public Player importFromXML(String filename) throws IOException {
        XmlMapper mapper = new XmlMapper();
        return mapper.readValue(new File(filename), Player.class);
    }
}
