package mud;

import java.io.*;
import com.opencsv.CSVWriter;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class User {
    private String username;
    private String password;
    private int gamesPlayed;
    private int livesLost;
    private int monstersSlain;
    private int goldEarned;
    private int itemsFound;

    public User(String username, String password, int gamesPlayed, int livesLost, int monstersSlain, int goldEarned,
            int itemsFound) {
        this.username = username;
        this.password = password;
        this.gamesPlayed = gamesPlayed;
        this.livesLost = livesLost;
        this.monstersSlain = monstersSlain;
        this.goldEarned = goldEarned;
        this.itemsFound = itemsFound;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getLivesLost() {
        return livesLost;
    }

    public void setLivesLost(int livesLost) {
        this.livesLost = livesLost;
    }

    public int getMonstersSlain() {
        return monstersSlain;
    }

    public void setMonstersSlain(int monstersSlain) {
        this.monstersSlain = monstersSlain;
    }

    public int getGoldEarned() {
        return goldEarned;
    }

    public void setGoldEarned(int goldEarned) {
        this.goldEarned = goldEarned;
    }

    public int getItemsFound() {
        return itemsFound;
    }

    public void setItemsFound(int itemsFound) {
        this.itemsFound = itemsFound;
    }

    @Override
    public String toString() {
        return "User [username=" + username + ", password=" + password + ", gamesPlayed=" + gamesPlayed + ", livesLost="
                + livesLost + ", monstersSlain=" + monstersSlain + ", goldEarned=" + goldEarned + ", itemsFound="
                + itemsFound + "]";
    }

    public void exportToCSV(String filename) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filename))) {
            String[] data = { username, password, String.valueOf(gamesPlayed),
                    String.valueOf(livesLost), String.valueOf(monstersSlain),
                    String.valueOf(goldEarned), String.valueOf(itemsFound) };
            writer.writeNext(data);
        }
    }

    public User importFromCSV(String filename) throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReader(new FileReader(filename))) {
            String[] data = reader.readNext();
            return new User(data[0], data[1], Integer.parseInt(data[2]),
                    Integer.parseInt(data[3]), Integer.parseInt(data[4]),
                    Integer.parseInt(data[5]), Integer.parseInt(data[6]));
        }
    }

    public void exportToJSON(String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File(filename), this);
    }

    public User importFromJSON(String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(new File(filename), User.class);
    }

    public void exportToXML(String filename) throws IOException {
        XmlMapper mapper = new XmlMapper();
        mapper.writeValue(new File(filename), this);
    }

    public User importFromXML(String filename) throws IOException {
        XmlMapper mapper = new XmlMapper();
        return mapper.readValue(new File(filename), User.class);
    }

}
