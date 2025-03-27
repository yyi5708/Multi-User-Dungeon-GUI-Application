package mud;

import java.util.ArrayList;

public class Tile {
    private Content content;
    private ArrayList<Tile> adjacentTiles;
    private Integer trapDamageValue;

    public Tile() {
        this.content = Content.EMPTY;
        this.adjacentTiles = new ArrayList<>();
        this.trapDamageValue = null;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public ArrayList<Tile> getAdjacentTiles() {
        return adjacentTiles;
    }

    public void setAdjacentTiles(ArrayList<Tile> adjacentTiles) {
        this.adjacentTiles = adjacentTiles;
    }

    public Integer getTrapDamageValue() {
        return trapDamageValue;
    }

    public void setTrapDamageValue(Integer trapDamageValue) {
        this.trapDamageValue = trapDamageValue;
    }

    public void placeChest() {
        if (content == Content.EMPTY) {
            content = Content.CHEST;
            // System.out.println("A chest has been placed on the tile.");
        } else {
            // System.out.println("Tile is already occupied.");
        }
    }

    public void removeChest() {
        if (content == Content.CHEST) {
            content = Content.EMPTY;
            // System.out.println("The chest has been removed from the tile.");
        } else {
            // System.out.println("No chest found on this tile.");
        }
    }

    @Override
    public String toString() {
        return "Tile [content=" + content + ", adjacentTiles=" + adjacentTiles + ", trapDamageValue=" + trapDamageValue
                + "]";
    }
}
