package mud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room {
    private int length;
    private int width;
    private ArrayList<Tile> tiles;
    private String description;
    private String note;
    private Map<Direction, Room> exits;

    public Room(int length, int width, String description) {
        this.length = length;
        this.width = width;
        this.description = description;
        this.note = null;
        this.tiles = new ArrayList<>();
        this.exits = new HashMap<>();
        initializeTiles();
    }

    private void initializeTiles() {
        for (int i = 0; i < length * width; i++) {
            tiles.add(new Tile());
        }
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public ArrayList<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(ArrayList<Tile> tiles) {
        this.tiles = tiles;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Map<Direction, Room> getExits() {
        return exits;
    }

    public void setExits(Map<Direction, Room> exits) {
        this.exits = exits;
    }

    public void connectRooms(Room otherRoom, Direction direction) {
        exits.put(direction, otherRoom);
        Direction oppositeDirection = getOppositeDirection(direction);
        otherRoom.getExits().put(oppositeDirection, this);
    }

    private Direction getOppositeDirection(Direction direction) {
        switch (direction) {
            case NORTH:
                return Direction.SOUTH;
            case SOUTH:
                return Direction.NORTH;
            case EAST:
                return Direction.WEST;
            case WEST:
                return Direction.EAST;
            default:
                return null;
        }
    }
}
