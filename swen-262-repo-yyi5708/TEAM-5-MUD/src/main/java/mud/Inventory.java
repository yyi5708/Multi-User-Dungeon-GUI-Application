package mud;

import java.util.Arrays;

public class Inventory {
    private int capacity;
    private Item[] items;

    public Inventory(int capacity) {
        this.capacity = capacity;
        this.items = new Item[capacity];
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
        this.items = new Item[capacity];
    }

    public Item[] getItems() {
        return items;
    }

    public void setItems(Item[] items) {
        this.items = items;
    }

    public boolean addItem(Item item) {
        for (int i = 0; i < capacity; i++) {
            if (items[i] == null) {
                items[i] = item;
                return true;
            }
        }
        System.out.println("Inventory is full! Cannot add " + item.getName());
        return false;
    }

    public boolean removeItem(Item item) {
        for (int i = 0; i < capacity; i++) {
            if (items[i] != null && items[i].equals(item)) {
                items[i] = null;
                return true;
            }
        }
        System.out.println(item.getName() + " not found in inventory.");
        return false;
    }

    public boolean isFull() {
        return Arrays.stream(items).noneMatch(i -> i == null);
    }

    public boolean isEmpty() {
        return Arrays.stream(items).allMatch(i -> i == null);
    }

    public Item getItemByName(String name) {
        for (Item item : items) {
            if (item != null && item.getName().equals(name)) {
                return item;
            }
        }
        System.out.println(name + " not found in inventory.");
        return null;
    }

    public void destroyItem(Item item) {
        boolean found = removeItem(item);
        if (found) {
            System.out.println(item.getName() + " has been destroyed.");
        } else {
            System.out.println(item.getName() + " not found in inventory.");
        }
    }

    public Item getItem(int index) {
        if (index >= 0 && index < capacity) {
            return items[index];
        }
        System.out.println("Index out of bounds.");
        return null;
    }

    // New method to load inventory from a string
    public void loadFromString(String data) {
        String[] itemFields = data.split(";");
        for (int i = 0; i < itemFields.length && i < capacity; i++) {
            if (!itemFields[i].equals("null")) {
                String[] fields = itemFields[i].split(":");
                String itemName = fields[0];
                String itemDesc = fields.length > 1 ? fields[1] : "No description";
                int itemValue = fields.length > 2 ? Integer.parseInt(fields[2]) : 0;
                items[i] = new Item(itemName, itemDesc, itemValue);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Item item : items) {
            if (item != null) {
                sb.append(item.getName()).append(":")
                        .append(item.getDescription()).append(":")
                        .append(item.getValue()).append(";");
            } else {
                sb.append("null;");
            }
        }
        return sb.toString();
    }
}