package mud;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class CLI {
    private static Map<String, User> users = new ConcurrentHashMap<>();
    private static User authenticatedUser = null;
    private static Map<Room, Tile[][]> roomLayouts = new HashMap<>();
    private static Player player;
    @SuppressWarnings("unused")
    private static Inventory inventory;
    private static Room currentRoom;
    private static Room exitRoom;
    private static Random random = new Random();

    public static void main(String[] args) {
        loadUsers();
        showMainMenu();
    }

    private static void loadUsers() {
        users.put("admin", new User("admin", "admin", 0, 0, 0, 0, 0));
        users.put("guest", new User("guest", "guest", 0, 0, 0, 0, 0));
    }

    private static void showMainMenu() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n");
            System.out.println("--- Main Menu ---");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Browse Premade Map");
            System.out.println("4. Quit");
            System.out.print("Choose an option: ");
            System.out.println("\n");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> registerUser(scanner);
                case 2 -> loginUser(scanner);
                case 3 -> browsePremadeMap(scanner);
                case 4 -> {
                    System.out.println("\n");
                    System.out.println("Goodbye!");
                    System.out.println("\n");
                    System.exit(0);
                }
                default -> {
                    System.out.println("\n");
                    System.out.println("Invalid choice. Try again.");
                    System.out.println("\n");
                }
            }
        }
    }

    private static void registerUser(Scanner scanner) {
        System.out.println("\n");
        System.out.print("Enter a username: ");
        System.out.println("\n");
        String username = scanner.nextLine();
        if (users.containsKey(username)) {
            System.out.println("\n");
            System.out.println("Username already exists. Please choose another.");
            System.out.println("\n");
            return;
        }
        System.out.println("\n");
        System.out.print("Enter a password: ");
        System.out.println("\n");
        String password = scanner.nextLine();

        User newUser = new User(username, password, 0, 0, 0, 0, 0);
        users.put(username, newUser);
        System.out.println("\n");
        System.out.println("Registration successful! Please log in.");
        System.out.println("\n");
    }

    private static void loginUser(Scanner scanner) {
        System.out.println("\n");
        System.out.print("Enter your username: ");
        System.out.println("\n");
        String username = scanner.nextLine();
        System.out.println("\n");
        System.out.print("Enter your password: ");
        System.out.println("\n");
        String password = scanner.nextLine();

        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            authenticatedUser = user;
            System.out.println("\n");
            System.out.println("Login successful! Welcome, " + username + ".");
            System.out.println("\n");
            showUserMenu(scanner);
        } else {
            System.out.println("\n");
            System.out.println("Invalid username or password.");
            System.out.println("\n");
        }
    }

    private static void showUserMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n");
            System.out.println("--- User Menu ---");
            System.out.println("1. Start New Game");
            System.out.println("2. Resume Game");
            System.out.println("3. Change Password");
            System.out.println("4. View Profile");
            System.out.println("5. Export Profile");
            System.out.println("6. Import Profile");
            System.out.println("7. Logout");
            System.out.print("Choose an option: ");
            System.out.println("\n");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> selectGameMode(scanner);
                case 2 -> resumeGame();
                case 3 -> changePassword(scanner);
                case 4 -> viewProfile();
                case 5 -> exportProfile(scanner);
                case 6 -> importProfile(scanner);
                case 7 -> logout();
                default -> {
                    System.out.println("\n");
                    System.out.println("Invalid choice. Try again.");
                    System.out.println("\n");
                }
            }
        }
    }

    private static void exportProfile(Scanner scanner) {
        System.out.println("\n");
        System.out.println("--- User Profile Export Menu ---");
        System.out.println("1. CSV");
        System.out.println("2. JSON");
        System.out.println("3. XML");
        System.out.print("Choose export format: ");
        System.out.println("\n");
        int formatChoice = scanner.nextInt();
        scanner.nextLine();
        if (formatChoice < 1 || formatChoice > 3) {
            System.out.println("\n");
            System.out.println("Invalid export format choice.");
            System.out.println("\n");
            return;
        }
        System.out.println("\n");
        System.out.print("Enter player filename (without extension): ");
        System.out.println("\n");
        String playerFileName = scanner.nextLine();
        System.out.println("\n");
        System.out.print("Enter user filename (without extension): ");
        System.out.println("\n");
        String userFileName = scanner.nextLine();
        try {
            switch (formatChoice) {
                case 1 -> {
                    player.exportToCSV(playerFileName + ".csv");
                    authenticatedUser.exportToCSV(userFileName + ".csv");
                }
                case 2 -> {
                    player.exportToJSON(playerFileName + ".json");
                    authenticatedUser.exportToJSON(userFileName + ".json");
                }
                case 3 -> {
                    player.exportToXML(playerFileName + ".xml");
                    authenticatedUser.exportToXML(userFileName + ".xml");
                }
            }
            System.out.println("\n");
            System.out.println("User Profile exported successfully.\n");
            System.out.println("\n");
        } catch (Exception e) {
            System.out.println("\n");
            System.out.println("Error exporting profile: " + e.getMessage());
            System.out.println("\n");
        }
    }

    private static void importProfile(Scanner scanner) {
        System.out.println("\n");
        System.out.println("--- User Profile Import Menu ---");
        System.out.println("1. CSV");
        System.out.println("2. JSON");
        System.out.println("3. XML");
        System.out.print("Choose import format: ");
        System.out.println("\n");
        int formatChoice = scanner.nextInt();
        scanner.nextLine();
        if (formatChoice < 1 || formatChoice > 3) {
            System.out.println("\n");
            System.out.println("Invalid import format choice.");
            System.out.println("\n");
            return;
        }
        System.out.println("\n");
        System.out.print("Enter player filename (with extension): ");
        System.out.println("\n");
        String playerFileName = scanner.nextLine();
        System.out.println("\n");
        System.out.print("Enter user filename (with extension): ");
        System.out.println("\n");
        String userFileName = scanner.nextLine();
        try {
            switch (formatChoice) {
                case 1 -> {
                    player.importFromCSV(playerFileName);
                    authenticatedUser.importFromCSV(userFileName);
                }
                case 2 -> {
                    player.importFromJSON(playerFileName);
                    authenticatedUser.importFromJSON(userFileName);
                }
                case 3 -> {
                    player.importFromXML(playerFileName);
                    authenticatedUser.importFromXML(userFileName);
                }
            }
            System.out.println("\n");
            System.out.println("User Profile imported successfully.");
            System.out.println("\n");
        } catch (Exception e) {
            System.out.println("\n");
            System.out.println("Error importing profile: " + e.getMessage());
            System.out.println("\n");
        }
    }

    private static void changePassword(Scanner scanner) {
        System.out.println("\n");
        System.out.print("Enter your new password: ");
        System.out.println("\n");
        String newPassword = scanner.nextLine();
        authenticatedUser.setPassword(newPassword);
        System.out.println("\n");
        System.out.println("Password changed successfully.");
        System.out.println("\n");
    }

    private static void viewProfile() {
        System.out.println("\n");
        System.out.println("--- User Profile ---");
        System.out.println("Games Played: " + authenticatedUser.getGamesPlayed());
        System.out.println("Lives Lost: " + authenticatedUser.getLivesLost());
        System.out.println("Monsters Slain: " + authenticatedUser.getMonstersSlain());
        System.out.println("Gold Earned: " + authenticatedUser.getGoldEarned());
        System.out.println("Items Found: " + authenticatedUser.getItemsFound());
        System.out.println("\n");
    }

    private static void logout() {
        System.out.println("\n");
        System.out.println("Logged out.");
        System.out.println("\n");
        authenticatedUser = null;
        showMainMenu();
    }

    private static void startNewGame(String mode) {
        authenticatedUser.setGamesPlayed(authenticatedUser.getGamesPlayed() + 1);
        if ("premade".equals(mode)) {
            setupPremadeMap();
        } else if ("endless".equals(mode)) {
            setupEndlessAdventure();
        }
    }

    private static void resumeGame() {
        if (player == null || currentRoom == null || exitRoom == null) {
            System.out.println("\n");
            System.out.println("No game in progress. Start a new game.");
            System.out.println("\n");
        } else {
            gameLoop(new Scanner(System.in));
        }
    }

    private static void browsePremadeMap(Scanner scanner) {
        System.out.println("\n");
        System.out.println("--- Guest Browse Premade Map ---");
        System.out.println("\n");
        User user = users.get("guest");
        authenticatedUser = user;
        resetGameState();
        setupPreGame();
        preGameLoop(new Scanner(System.in));
    }

    private static void selectGameMode(Scanner scanner) {
        while (true) {
            System.out.println("\n");
            System.out.println("--- Game Mode ---");
            System.out.println("1. Premade Map");
            System.out.println("2. Endless Adventure");
            System.out.print("Choose a game mode: ");
            System.out.println("\n");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    startNewGame("premade");
                    return;
                case 2:
                    startNewGame("endless");
                    return;
                default:
                    System.out.println("\n");
                    System.out.println("Invalid choice. Please try again.");
                    System.out.println("\n");
            }
        }
    }

    private static void setupPremadeMap() {
        System.out.println("\n");
        System.out.println("--- Premade Map ---");
        System.out.println("\n");
        resetGameState();
        setupGame();
        gameLoop(new Scanner(System.in));
    }

    private static void setupEndlessAdventure() {
        System.out.println("\n");
        System.out.println("--- Endless Adventure ---");
        System.out.println("\n");
        resetGameState();
        List<Room> rooms = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            int length = 1;
            int width = 1;
            String description = generateRandomRoomDescription();
            Room room = new Room(length, width, description);
            rooms.add(room);
            roomLayouts.put(room, createAndPopulateTileGrid(room));
        }
        currentRoom = rooms.get(0);
        exitRoom = rooms.get(rooms.size() - 1);
        for (Room room : rooms) {
            int connections = random.nextInt(4) + 1;
            for (int j = 0; j < connections; j++) {
                Room targetRoom = rooms.get(random.nextInt(rooms.size()));
                if (room != targetRoom) {
                    Direction randomDirection = getRandomDirection();
                    room.connectRooms(targetRoom, randomDirection);
                }
            }
        }
        player = new Player("Adventurer", "A brave explorer in an endless dungeon", 100, 0, 0);
        inventory = new Inventory(100);
        System.out.println("You have entered an endless dungeon filled with danger and rewards.");
        System.out.println("Find your way through 40 rooms and reach the exit to survive!");
        System.out.println("Good luck, " + player.getName() + "!\n");
        System.out.println("Type 'help' for a list of commands.\n");
        gameLoop(new Scanner(System.in));
    }

    private static String generateRandomRoomDescription() {
        String[] roomTypes = {
                "A dusty chamber filled with broken furniture.",
                "A dark tunnel with strange noises echoing through.",
                "An overgrown garden with glowing plants.",
                "A treasure room, but most of the gold is gone.",
                "A flooded cavern with slippery rocks.",
                "A guard post with skeletons of ancient warriors.",
                "A smithy filled with rusted weapons.",
                "An ancient library filled with forbidden texts.",
                "A mystical altar glowing faintly.",
                "A dining hall, strangely set for a feast.",
                "A prison cell with broken chains on the wall.",
                "A hallway with flickering torchlight.",
                "A cavern covered in glowing moss.",
                "A storage room cluttered with broken crates.",
                "A pit filled with bones of fallen adventurers.",
                "A ruined chapel with shattered stained glass.",
                "A barracks with rusted armor lying around.",
                "A collapsed tunnel with debris everywhere.",
                "A market square, eerily quiet.",
                "A room filled with mirrors that reflect nothing.",
                "A cold crypt with a faint, unnatural chill.",
                "A banquet hall, though the food looks spoiled.",
                "A workshop with strange, half-finished machines.",
                "A ritual chamber with ancient symbols etched in stone.",
                "A well-lit corridor that leads nowhere.",
                "A hot, humid room with steam rising from vents.",
                "A wine cellar, with barrels that seem untouched for ages.",
                "A shrine dedicated to a long-forgotten deity.",
                "A training room with dummies and old weapons.",
                "A treasury filled with cursed coins.",
                "A gallery with torn paintings hanging askew.",
                "A tunnel that appears to loop back on itself.",
                "A blacksmith's forge, though the fire is cold.",
                "A mossy cave where water drips from the ceiling.",
                "A tower room with a large, shattered window.",
                "A labyrinthine corridor with no visible end.",
                "A bridge suspended over an endless abyss.",
                "A throne room, abandoned long ago.",
                "A clockwork room filled with ticking gears.",
                "A secret passage hidden behind a false wall."
        };
        return roomTypes[random.nextInt(roomTypes.length)];
    }

    private static Direction getRandomDirection() {
        Direction[] directions = Direction.values();
        return directions[random.nextInt(directions.length)];
    }

    private static void resetGameState() {
        currentRoom = null;
        exitRoom = null;
    }

    private static void setupGame() {
        player = new Player("Adventurer", "A brave soul exploring the dungeon", 100, 0, 0);
        inventory = new Inventory(100);
        Room entryRoom = new Room(1, 1, "The Entry Hall, a grand hall with cobwebs in the corners.");
        Room corridor = new Room(1, 1, "A narrow corridor with walls close on both sides.");
        Room armory = new Room(1, 1, "A small armory filled with rusted weapons and broken shields.");
        Room library = new Room(1, 1, "A library filled with ancient books covered in dust.");
        Room diningRoom = new Room(1, 1, "A large dining hall with a grand table and empty chairs.");
        Room treasureRoom = new Room(1, 1, "A small room glittering with gold and treasures.");
        Room darkRoom = new Room(1, 1, "A pitch-black room where you can barely see anything.");
        Room guardRoom = new Room(1, 1, "A guard post with a wooden table and empty seats.");
        Room throneRoom = new Room(1, 1, "The grand throne room, where the dungeons ruler once sat.");
        Room potionRoom = new Room(1, 1, "A small room filled with bubbling potions and strange herbs.");
        Room chapel = new Room(1, 1, "An ancient chapel with a broken altar and flickering candles.");
        Room tortureChamber = new Room(1, 1, "A dark and eerie chamber filled with torture devices.");
        Room treasureVault = new Room(1, 1, "A heavily secured vault filled with ancient treasures.");
        Room garden = new Room(1, 1, "A hidden garden with strange plants and a fountain.");
        Room crypt = new Room(1, 1, "A crypt containing old coffins and the remains of ancient warriors.");
        Room storageRoom = new Room(1, 1, "A cluttered storage room filled with old supplies and junk.");
        Room workshop = new Room(1, 1, "A workshop with tools and materials for crafting.");
        Room observationRoom = new Room(1, 1, "An observation room with a large telescope for stargazing.");
        Room secretRoom = new Room(1, 1, "A hidden room with a mysterious aura and strange symbols.");
        Room exit = new Room(1, 1, "The Exit Room, the end of your journey.");
        roomLayouts.put(entryRoom, createAndPopulateTileGrid(entryRoom));
        roomLayouts.put(corridor, createAndPopulateTileGrid(corridor));
        roomLayouts.put(armory, createAndPopulateTileGrid(armory));
        roomLayouts.put(library, createAndPopulateTileGrid(library));
        roomLayouts.put(diningRoom, createAndPopulateTileGrid(diningRoom));
        roomLayouts.put(treasureRoom, createAndPopulateTileGrid(treasureRoom));
        roomLayouts.put(darkRoom, createAndPopulateTileGrid(darkRoom));
        roomLayouts.put(guardRoom, createAndPopulateTileGrid(guardRoom));
        roomLayouts.put(throneRoom, createAndPopulateTileGrid(throneRoom));
        roomLayouts.put(potionRoom, createAndPopulateTileGrid(potionRoom));
        roomLayouts.put(chapel, createAndPopulateTileGrid(chapel));
        roomLayouts.put(tortureChamber, createAndPopulateTileGrid(tortureChamber));
        roomLayouts.put(treasureVault, createAndPopulateTileGrid(treasureVault));
        roomLayouts.put(garden, createAndPopulateTileGrid(garden));
        roomLayouts.put(crypt, createAndPopulateTileGrid(crypt));
        roomLayouts.put(storageRoom, createAndPopulateTileGrid(storageRoom));
        roomLayouts.put(workshop, createAndPopulateTileGrid(workshop));
        roomLayouts.put(observationRoom, createAndPopulateTileGrid(observationRoom));
        roomLayouts.put(secretRoom, createAndPopulateTileGrid(secretRoom));
        roomLayouts.put(exit, createAndPopulateTileGrid(exit));
        entryRoom.connectRooms(corridor, Direction.NORTH);
        corridor.connectRooms(armory, Direction.EAST);
        corridor.connectRooms(library, Direction.NORTH);
        library.connectRooms(diningRoom, Direction.EAST);
        diningRoom.connectRooms(treasureRoom, Direction.EAST);
        treasureRoom.connectRooms(darkRoom, Direction.SOUTH);
        darkRoom.connectRooms(guardRoom, Direction.WEST);
        guardRoom.connectRooms(throneRoom, Direction.SOUTH);
        corridor.connectRooms(potionRoom, Direction.SOUTH);
        potionRoom.connectRooms(chapel, Direction.NORTH);
        chapel.connectRooms(tortureChamber, Direction.EAST);
        tortureChamber.connectRooms(treasureVault, Direction.SOUTH);
        treasureVault.connectRooms(garden, Direction.WEST);
        garden.connectRooms(crypt, Direction.EAST);
        crypt.connectRooms(storageRoom, Direction.NORTH);
        storageRoom.connectRooms(workshop, Direction.WEST);
        workshop.connectRooms(observationRoom, Direction.SOUTH);
        observationRoom.connectRooms(secretRoom, Direction.NORTH);
        throneRoom.connectRooms(exit, Direction.WEST);
        armory.connectRooms(chapel, Direction.SOUTH);
        armory.connectRooms(guardRoom, Direction.NORTH);
        diningRoom.connectRooms(workshop, Direction.NORTH);
        darkRoom.connectRooms(treasureVault, Direction.WEST);
        guardRoom.connectRooms(crypt, Direction.SOUTH);
        crypt.connectRooms(exit, Direction.WEST);
        chapel.connectRooms(treasureRoom, Direction.WEST);
        garden.connectRooms(observationRoom, Direction.SOUTH);
        tortureChamber.connectRooms(diningRoom, Direction.NORTH);
        potionRoom.connectRooms(armory, Direction.SOUTH);
        currentRoom = entryRoom;
        exitRoom = exit;
        System.out.println("\n");
        System.out.println("Welcome to the Dungeon Adventure!");
        System.out.println("Explore the dungeon and find the exit to win the game!");
        System.out.println("Good luck, " + player.getName() + ", on completing the adventure!");
        System.out.println("Type 'help' for all the available commands!");
        System.out.println("\n");
    }

    private static void setupPreGame() {
        player = new Player("Adventurer", "A brave soul exploring the dungeon", 100, 0, 0);
        inventory = new Inventory(100);
        Room entryRoom = new Room(1, 1, "The Entry Hall, a grand hall with cobwebs in the corners.");
        Room corridor = new Room(1, 1, "A narrow corridor with walls close on both sides.");
        Room armory = new Room(1, 1, "A small armory filled with rusted weapons and broken shields.");
        Room library = new Room(1, 1, "A library filled with ancient books covered in dust.");
        Room diningRoom = new Room(1, 1, "A large dining hall with a grand table and empty chairs.");
        Room treasureRoom = new Room(1, 1, "A small room glittering with gold and treasures.");
        Room darkRoom = new Room(1, 1, "A pitch-black room where you can barely see anything.");
        Room guardRoom = new Room(1, 1, "A guard post with a wooden table and empty seats.");
        Room throneRoom = new Room(1, 1, "The grand throne room, where the dungeons ruler once sat.");
        Room potionRoom = new Room(1, 1, "A small room filled with bubbling potions and strange herbs.");
        Room chapel = new Room(1, 1, "An ancient chapel with a broken altar and flickering candles.");
        Room tortureChamber = new Room(1, 1, "A dark and eerie chamber filled with torture devices.");
        Room treasureVault = new Room(1, 1, "A heavily secured vault filled with ancient treasures.");
        Room garden = new Room(1, 1, "A hidden garden with strange plants and a fountain.");
        Room crypt = new Room(1, 1, "A crypt containing old coffins and the remains of ancient warriors.");
        Room storageRoom = new Room(1, 1, "A cluttered storage room filled with old supplies and junk.");
        Room workshop = new Room(1, 1, "A workshop with tools and materials for crafting.");
        Room observationRoom = new Room(1, 1, "An observation room with a large telescope for stargazing.");
        Room secretRoom = new Room(1, 1, "A hidden room with a mysterious aura and strange symbols.");
        Room exit = new Room(1, 1, "The Exit Room, the end of your journey.");
        entryRoom.connectRooms(corridor, Direction.NORTH);
        corridor.connectRooms(armory, Direction.EAST);
        corridor.connectRooms(library, Direction.NORTH);
        library.connectRooms(diningRoom, Direction.EAST);
        diningRoom.connectRooms(treasureRoom, Direction.EAST);
        treasureRoom.connectRooms(darkRoom, Direction.SOUTH);
        darkRoom.connectRooms(guardRoom, Direction.WEST);
        guardRoom.connectRooms(throneRoom, Direction.SOUTH);
        corridor.connectRooms(potionRoom, Direction.SOUTH);
        potionRoom.connectRooms(chapel, Direction.NORTH);
        chapel.connectRooms(tortureChamber, Direction.EAST);
        tortureChamber.connectRooms(treasureVault, Direction.SOUTH);
        treasureVault.connectRooms(garden, Direction.WEST);
        garden.connectRooms(crypt, Direction.EAST);
        crypt.connectRooms(storageRoom, Direction.NORTH);
        storageRoom.connectRooms(workshop, Direction.WEST);
        workshop.connectRooms(observationRoom, Direction.SOUTH);
        observationRoom.connectRooms(secretRoom, Direction.NORTH);
        throneRoom.connectRooms(exit, Direction.WEST);
        armory.connectRooms(chapel, Direction.SOUTH);
        armory.connectRooms(guardRoom, Direction.NORTH);
        diningRoom.connectRooms(workshop, Direction.NORTH);
        darkRoom.connectRooms(treasureVault, Direction.WEST);
        guardRoom.connectRooms(crypt, Direction.SOUTH);
        crypt.connectRooms(exit, Direction.WEST);
        chapel.connectRooms(treasureRoom, Direction.WEST);
        garden.connectRooms(observationRoom, Direction.SOUTH);
        tortureChamber.connectRooms(diningRoom, Direction.NORTH);
        potionRoom.connectRooms(armory, Direction.SOUTH);
        currentRoom = entryRoom;
        exitRoom = exit;
        System.out.println("\n");
        System.out.println("Welcome to the Dungeon Adventure!");
        System.out.println("Explore the dungeon and find the exit to win the game!");
        System.out.println("Good luck, " + player.getName() + ", on completing the adventure!");
        System.out.println("Type 'help' for all the available commands!");
        System.out.println("\n");
    }

    private static Tile[][] createAndPopulateTileGrid(Room room) {
        int length = room.getLength();
        int width = room.getWidth();
        Tile[][] grid = new Tile[length][width];
        for (int y = 0; y < width; y++) {
            for (int x = 0; x < length; x++) {
                grid[y][x] = new Tile();
                if (random.nextInt(100) < 50) {
                    grid[y][x].placeChest();
                }
            }
        }
        return grid;
    }

    private static void gameLoop(Scanner scanner) {
        while (true) {
            displayCurrentRoom();
            System.out.println("\n");
            System.out.print("What will you do next, " + player.getName() + ": ");
            String command = scanner.nextLine().trim().toLowerCase();
            System.out.println("\n");
            if (command.equals("quit")) {
                System.out.println("\n");
                System.out.println("Thank you for embarking on this adventure, " + player.getName() + "!");
                System.out.println("\n");
                break;
            } else if (command.startsWith("move ")) {
                String directionStr = command.split(" ")[1];
                movePlayer(directionStr);
            } else if (command.equals("open chest")) {
                openChest();
            } else if (command.equals("view inventory")) {
                openInventory();
            } else if (command.equals("health")) {
                showHealth();
            } else if (command.equals("help")) {
                showHelp();
            } else {
                System.out.println("\n");
                System.out.println("Unknown command. Try 'help'.");
                System.out.println("\n");
            }
            if (player.getHealth() <= 0) {
                System.out.println("\n");
                System.out
                        .println("Game Over! " + player.getName() + " has perished, a tragic end to a valiant quest.");
                System.out.println("\n");
                authenticatedUser.setLivesLost(authenticatedUser.getLivesLost() + 1);
                resetGameState();
                break;
            }
        }
    }

    private static void preGameLoop(Scanner scanner) {
        while (true) {
            displayPreCurrentRoom();
            System.out.println("\n");
            System.out.print("What will you do next, " + player.getName() + ": ");
            String command = scanner.nextLine().trim().toLowerCase();
            System.out.println("\n");
            if (command.equals("quit")) {
                System.out.println("\n");
                System.out.println("Thank you for embarking on this adventure, " + player.getName() + "!");
                System.out.println("\n");
                break;
            } else if (command.startsWith("move ")) {
                String directionStr = command.split(" ")[1];
                movePlayer(directionStr);
            } else if (command.equals("help")) {
                preShowHelp();
            } else {
                System.out.println("\n");
                System.out.println("Unknown command. Try 'help'.");
                System.out.println("\n");
            }
        }
    }

    private static void displayCurrentRoom() {
        if (currentRoom == null) {
            System.out.println("Error: currentRoom is null.");
            return;
        }
        System.out.println("\n");
        System.out.println("You are in: " + currentRoom.getDescription());
        System.out.println("Available Exits: " + currentRoom.getExits().keySet() + ".");
        if (currentRoom.equals(exitRoom)) {
            System.out.println("\n");
            System.out.println(
                    "You arrived at the exit room. Suddenly, a mysterious chest emerges from the ground, casting a radiant glow that fills the entire chamber.");
            Tile[][] currentTiles = roomLayouts.get(currentRoom);
            if (currentTiles[0][0].getContent() != Content.CHEST) {
                currentTiles[0][0].placeChest();
                System.out.println("\n");
            }
            System.out.println("There is a chest in front of you, waiting to be opened.");
            System.out.println("\n");
        } else {
            Tile[][] currentTiles = roomLayouts.get(currentRoom);
            if (currentTiles[0][0].getContent() == Content.CHEST) {
                System.out.println("\n");
                System.out.println("There is a chest in front of you.");
                System.out.println("\n");
            }
        }
    }

    private static void displayPreCurrentRoom() {
        if (currentRoom == null) {
            System.out.println("Error: currentRoom is null.");
            return;
        }
        System.out.println("\n");
        System.out.println("You are in: " + currentRoom.getDescription());
        System.out.println("Available Exits: " + currentRoom.getExits().keySet() + ".");
        if (currentRoom.equals(exitRoom)) {
            System.out.println("\n");
            System.out.println(
                    "You arrived at the exit room. Suddenly, a mysterious chest emerges from the ground, casting a radiant glow that fills the entire chamber.");
        }
    }

    private static void movePlayer(String directionStr) {
        Direction direction = parseDirection(directionStr);
        if (direction == null) {
            System.out.println("\n");
            System.out.println("Invalid direction! Use 'north', 'east', 'south', or 'west'.");
            System.out.println("\n");
            return;
        }
        if (currentRoom.getExits().containsKey(direction)) {
            currentRoom = currentRoom.getExits().get(direction);
            System.out.println("\n");
            System.out.println("You move " + directionStr + " and enter a new room.");
            System.out.println("\n");
        } else {
            System.out.println("\n");
            System.out.println("You cannot go that way!");
            System.out.println("\n");
        }
    }

    private static Direction parseDirection(String directionStr) {
        switch (directionStr) {
            case "north":
                return Direction.NORTH;
            case "east":
                return Direction.EAST;
            case "south":
                return Direction.SOUTH;
            case "west":
                return Direction.WEST;
            default:
                return null;
        }
    }

    private static void openChest() {
        int gold = random.nextInt(50);
        Scanner scanner = new Scanner(System.in);
        Tile[][] currentTiles = roomLayouts.get(currentRoom);
        if (currentTiles[0][0].getContent() == Content.CHEST) {
            if (currentRoom.equals(exitRoom)) {
                System.out.println("\n");
                System.out.println(
                        "You open the chest and discover a glittering diamond inside! With your prize in hand, you leave the dungeon victorious.");
                System.out.println("You found " + gold + " gold!");
                System.out.printf("Congratulations, " + player.getName() + ", on completing the adventure!");
                System.out.println("\n");
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                resetGameState();
                showUserMenu(scanner);
            } else {
                int chestContent = random.nextInt(16);
                handleChestContent(chestContent);
            }
            currentTiles[0][0].removeChest();
        } else {
            System.out.println("\n");
            System.out.println("Theres no chest here to open.");
            System.out.println("\n");
        }
    }

    private static void handleChestContent(int chestContent) {
        Scanner scanner = new Scanner(System.in);
        int gold = random.nextInt(50);
        switch (chestContent) {

            case 0:
                System.out.println("\n");
                System.out.println(
                        "As you slowly lift the lid of the chest, it creaks open to reveal... nothing. A wave of disappointment washes over you as you realize its completely barren, holding only your dashed hopes.");
                System.out.println("\n");
                break;

            case 1:
                System.out.println("\n");
                System.out.println(
                        "You eagerly lift the lid of the chest, only to find it filled with stale air and disappointment. This treasure has long been forgotten.");
                System.out.println("\n");
                break;

            case 2:
                System.out.println("\n");
                System.out.println(
                        "With a cautious glance inside, you discover a dull, jagged rock resting within. Its weight is burdensome, utterly useless, and it seems to mock your adventurous spirit as you ponder its presence.");
                System.out.println("You found " + gold + " gold!");
                System.out.println("\n");
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                break;

            case 3:
                System.out.println("\n");
                System.out.println(
                        "You open the chest to find a strange, glittering object: a shiny button. It seems utterly useless, yet its allure catches your eye, almost daring you to press it.");
                System.out.println("You found " + gold + " gold!");
                System.out.println("\n");
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                break;

            case 4:
                int trapDamage = 10;
                System.out.println("\n");
                System.out.println(
                        "The moment you lift the lid, a sharp click resonates in the stillness. Before you can react, concealed spikes spring forth, piercing you. Pain surges through you as you take "
                                + trapDamage + " damage, reminding you of the dangers lurking within.");
                player.takeDamage(trapDamage);
                System.out.println("\n");
                break;

            case 5:
                int newTrapDamage = 10;
                System.out.println("\n");
                System.out.println(
                        "As you open the chest, a hidden mechanism activates, releasing a torrent of arrows! You quickly try to evade but are struck, taking "
                                + newTrapDamage + " damage.");
                player.takeDamage(newTrapDamage);
                System.out.println("\n");
                break;

            case 6:
                int npcDamage = 10;
                System.out.println("\n");
                System.out.println(
                        "With a sudden crash, the chest lid bursts open, unleashing a shrill screech that echoes through the chamber. A menacing figure leaps out, claws bared! It lunges at you, landing a vicious blow, and you scramble back, taking "
                                + npcDamage + " damage before you can fend it off.");
                player.takeDamage(npcDamage);
                System.out.println("You found " + gold + " gold!");
                System.out.println("\n");
                authenticatedUser.setMonstersSlain(authenticatedUser.getMonstersSlain() + 1);
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                break;

            case 7:
                int newNPCDamage = 10;
                System.out.println("\n");
                System.out.println(
                        "As you cautiously lift the lid, a sinister figure bursts forth from the chest, a wicked grin stretching across its face! It lunges at you with a wicked dagger, striking you with surprising speed. You stagger back as pain sears through your body, taking "
                                + newNPCDamage + " damage.");
                player.takeDamage(newNPCDamage);
                System.out.println("You found " + gold + " gold!");
                System.out.println("\n");
                authenticatedUser.setMonstersSlain(authenticatedUser.getMonstersSlain() + 1);
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                break;

            case 8:
                Weapon weapon = new Weapon("Sword of Valor",
                        "A finely crafted sword with a gleaming blade, known for its strength and precision.", 10, 10);
                System.out.println("\n");
                System.out.println("You open the chest to discover the legendary " + weapon.getName());
                player.getInventory().addItem(weapon);
                System.out.println("You add the " + weapon.getName() + " to your inventory.");
                System.out.println("You found " + gold + " gold!");
                System.out.println("\n");
                authenticatedUser.setItemsFound(authenticatedUser.getItemsFound() + 1);
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                break;

            case 9:
                Weapon newWeapon = new Weapon("Axe of the Ancients",
                        "A massive, two-handed axe that resonates with the power of ancient warriors, known for its devastating strikes.",
                        10, 10);
                System.out.println("\n");
                System.out.println("You open the chest and behold the mighty " + newWeapon.getName() + "!");
                player.getInventory().addItem(newWeapon);
                System.out.println("You add the " + newWeapon.getName() + " to your inventory.");
                System.out.println("You found " + gold + " gold!");
                System.out.println("\n");
                authenticatedUser.setItemsFound(authenticatedUser.getItemsFound() + 1);
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                break;

            case 10:
                Armor armor = new Armor("Dragon Scale Armor",
                        "An armor forged from the scales of a legendary dragon. Offers exceptional protection.", 10,
                        10);
                System.out.println("\n");
                System.out.println(
                        "You cautiously lift the lid and find a majestic piece of armor: the " + armor.getName());
                player.getInventory().addItem(armor);
                System.out.println("You add the " + armor.getName() + " to your inventory.");
                System.out.println("You found " + gold + " gold!");
                System.out.println("\n");
                authenticatedUser.setItemsFound(authenticatedUser.getItemsFound() + 1);
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                break;

            case 11:
                Armor newArmor = new Armor("Shield of the Guardian",
                        "A sturdy shield adorned with the emblem of a guardian. Provides excellent defense against attacks.",
                        10, 10);
                System.out.println("\n");
                System.out.println(
                        "As you carefully open the chest, you discover the impressive " + newArmor.getName() + "!");
                player.getInventory().addItem(newArmor);
                System.out.println("You add the " + newArmor.getName() + " to your inventory.");
                System.out.println("You found " + gold + " gold!");
                System.out.println("\n");
                authenticatedUser.setItemsFound(authenticatedUser.getItemsFound() + 1);
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                break;

            case 12:
                Item food = new Food("Mystical Apple", "A glowing apple that restores a portion of your health.", 10,
                        10);
                System.out.println("\n");
                System.out.println("As you pry open the chest, you find a " + food.getName() + " nestled within!");
                player.getInventory().addItem(food);
                System.out.println("You add the " + food.getName() + " to your inventory.");
                System.out.println("You found " + gold + " gold!");
                System.out.println("\n");
                authenticatedUser.setItemsFound(authenticatedUser.getItemsFound() + 1);
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                break;

            case 13:
                Item newFood = new Food("Healing Herb",
                        "A fragrant herb that has healing properties. Restores a significant portion of your health.",
                        10, 10);
                System.out.println("\n");
                System.out.println(
                        "As you carefully open the chest, you discover a " + newFood.getName() + " resting inside!");
                player.getInventory().addItem(newFood);
                System.out.println("You add the " + newFood.getName() + " to your inventory.");
                System.out.println("You found " + gold + " gold!");
                System.out.println("\n");
                authenticatedUser.setItemsFound(authenticatedUser.getItemsFound() + 1);
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                break;

            case 14:
                System.out.println("\n");
                System.out.println(
                        "You open the chest, and in an instant-BOOM! A hidden bomb detonates with a deafening roar.");
                System.out.println(
                        "A sharp pain sears through you, and darkness envelops your vision... You met an untimely demise.");
                player.setHealth(0);
                System.out
                        .println("Game Over! " + player.getName() + " has perished, a tragic end to a valiant quest.");
                System.out.println("\n");
                resetGameState();
                showUserMenu(scanner);
                break;

            case 15:
                System.out.println("\n");
                System.out.println(
                        "You open the chest and find a portal shimmering with ethereal light. As you step inside, a feeling of warmth envelops you, and you realize you've won the game!");
                System.out.println("You found " + gold + " gold!");
                System.out.println(
                        "Congratulations, " + player.getName() + "! You have completed your quest successfully!");
                System.out.println("\n");
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                resetGameState();
                showUserMenu(scanner);
                break;

            default:
                System.out.println("\n");
                System.out.println("You opened the chest, but nothing happened. Its a mystery.");
                System.out.println("\n");
                break;
        }
    }

    private static void openInventory() {
        Item[] items = player.getInventory().getItems();
        if (player.getInventory().isEmpty()) {
            System.out.println("\n");
            System.out.println("Your inventory is empty. You have nothing to use.");
            System.out.println("\n");
        } else {
            System.out.println("\n");
            System.out.println("Your inventory contains the following items:");
            for (Item item : items) {
                if (item != null) {
                    System.out.println(item.getName() + ": " + item.getDescription());
                }
            }
            System.out.println("\n");
        }
    }

    private static void showHealth() {
        if (player != null) {
            System.out.println("\n");
            System.out.println(player.getName() + "'s" + " current health points are " + player.getHealth() + ".");
            System.out.println("\n");
        } else {
            System.out.println("\n");
            System.out.println("No player found!");
            System.out.println("\n");
        }
    }

    private static void showHelp() {
        System.out.println("\n");
        System.out.println("Available commands:");
        System.out.println("- move <direction>: Move in a direction.");
        System.out.println("- open chest: Open the chest in front of you.");
        System.out.println("- view inventory: View your inventory.");
        System.out.println("- health: Show the players health.");
        System.out.println("- help: Show all the available commands.");
        System.out.println("- quit: Exit the game.");
        System.out.println("\n");
    }

    private static void preShowHelp() {
        System.out.println("\n");
        System.out.println("Available commands:");
        System.out.println("- move <direction>: Move in a direction.");
        System.out.println("- help: Show all the available commands.");
        System.out.println("- quit: Exit the game.");
        System.out.println("\n");
    }
}