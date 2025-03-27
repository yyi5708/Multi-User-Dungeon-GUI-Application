package mud;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class GUI extends Application {
    private static Map<String, User> users = new ConcurrentHashMap<>();
    private static User authenticatedUser = null;
    private static Map<Room, Tile[][]> roomLayouts = new HashMap<>();
    private static Player player;
    @SuppressWarnings("unused")
    private static Inventory inventory;
    private static Room currentRoom;
    private static Room exitRoom;
    private static Random random = new Random();
    private static String currentGameMode;
    private static Scene gameScene;
    private static Stage primaryStage;
    private static TextArea gameLog;
    @SuppressWarnings("unused")
    private static VBox gameControls;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        GUI.primaryStage = primaryStage;
        primaryStage.setTitle("MUD");
        loadUsers();
        showMainMenu();
        primaryStage.show();
    }

    private static void loadUsers() {
        users.put("Admin", new User("Admin", "Admin", 0, 0, 0, 0, 0));
        users.put("Guest", new User("Guest", "Guest", 0, 0, 0, 0, 0));
    }

    @SuppressWarnings("unused")
    private static void showMainMenu() {
        VBox menuLayout = new VBox(20);
        menuLayout.getStyleClass().add("vbox");
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.setPadding(new Insets(20));
        Text title = new Text("MUD");
        title.getStyleClass().add("title");
        title.setFont(Font.font(50));
        Button registerButton = new Button("Register");
        Button loginButton = new Button("Login");
        Button browseButton = new Button("Browse Premade Map");
        Button quitButton = new Button("Quit");
        registerButton.setOnAction(e -> registerUser());
        loginButton.setOnAction(e -> loginUser());
        browseButton.setOnAction(e -> browsePremadeMap());
        quitButton.setOnAction(e -> {
            showAlert("MUD", "Goodbye");
            primaryStage.close();
        });
        menuLayout.getChildren().addAll(title, registerButton, loginButton, browseButton, quitButton);
        Scene scene = new Scene(menuLayout, 800, 600);
        scene.getStylesheets().add(GUI.class.getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    private static void registerUser() {
        Dialog<User> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().add(GUI.class.getResource("style.css").toExternalForm());
        dialog.setTitle("MUD");
        dialog.setHeaderText("Register");
        ButtonType registerButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(registerButtonType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        TextField username = new TextField();
        PasswordField password = new PasswordField();
        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == registerButtonType) {
                String user = username.getText();
                String pass = password.getText();
                if (user.isEmpty() || pass.isEmpty()) {
                    showAlert("MUD", "Both fields are required, please fill them in");
                    return null;
                }
                if (users.containsKey(user)) {
                    showAlert("MUD", "Username already exists, please choose another");
                    return null;
                } else {
                    User newUser = new User(user, pass, 0, 0, 0, 0, 0);
                    users.put(user, newUser);
                    showAlert("MUD", "Registration successful, please log in");
                    return newUser;
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    @SuppressWarnings("unused")
    private static void loginUser() {
        Dialog<User> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().add(GUI.class.getResource("style.css").toExternalForm());
        dialog.setTitle("MUD");
        dialog.setHeaderText("Login");
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        TextField username = new TextField();
        PasswordField password = new PasswordField();
        grid.add(new Label("Username:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Password:"), 0, 1);
        grid.add(password, 1, 1);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                String user = username.getText();
                String pass = password.getText();
                if (user.isEmpty() || pass.isEmpty()) {
                    showAlert("MUD", "Both fields are required, please fill them in");
                    return null;
                }
                User userObj = users.get(user);
                if (userObj != null && userObj.getPassword().equals(pass)) {
                    authenticatedUser = userObj;
                    showAlert("MUD", "Login successful, welcome, " + user);
                    return userObj;
                } else {
                    showAlert("MUD", "Invalid username or password, please try again");
                    return null;
                }
            }
            return null;
        });
        Optional<User> result = dialog.showAndWait();
        result.ifPresent(user -> showUserMenu());
    }

    private static void browsePremadeMap() {
        User user = users.get("Guest");
        authenticatedUser = user;
        setupPrePremadeMap();

    }

    private static String generateRandomRoomDescription() {
        String[] roomTypes = {
                "A dusty chamber filled with broken furniture",
                "A dark tunnel with strange noises echoing through",
                "An overgrown garden with glowing plants",
                "A treasure room, but most of the gold is gone",
                "A flooded cavern with slippery rocks",
                "A guard post with skeletons of ancient warriors",
                "A smithy filled with rusted weapons",
                "An ancient library filled with forbidden texts",
                "A mystical altar glowing faintly",
                "A dining hall, strangely set for a feast",
                "A prison cell with broken chains on the wall",
                "A hallway with flickering torchlight",
                "A cavern covered in glowing moss",
                "A storage room cluttered with broken crates",
                "A pit filled with bones of fallen adventurers",
                "A ruined chapel with shattered stained glass",
                "A barracks with rusted armor lying around",
                "A collapsed tunnel with debris everywhere",
                "A market square, eerily quiet",
                "A room filled with mirrors that reflect nothing",
                "A cold crypt with a faint, unnatural chill",
                "A banquet hall, though the food looks spoiled",
                "A workshop with strange, half-finished machines",
                "A ritual chamber with ancient symbols etched in stone",
                "A well-lit corridor that leads nowhere",
                "A hot, humid room with steam rising from vents",
                "A wine cellar, with barrels that seem untouched for ages",
                "A shrine dedicated to a long-forgotten deity",
                "A training room with dummies and old weapons",
                "A treasury filled with cursed coins",
                "A gallery with torn paintings hanging askew",
                "A tunnel that appears to loop back on itself",
                "A blacksmith's forge, though the fire is cold",
                "A mossy cave where water drips from the ceiling",
                "A tower room with a large, shattered window",
                "A labyrinthine corridor with no visible end",
                "A bridge suspended over an endless abyss",
                "A throne room, abandoned long ago",
                "A clockwork room filled with ticking gears",
                "A secret passage hidden behind a false wall"
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
        Room entryRoom = new Room(1, 1, "The entry hall, a grand hall with cobwebs in the corners");
        Room corridor = new Room(1, 1, "A narrow corridor with walls close on both sides");
        Room armory = new Room(1, 1, "A small armory filled with rusted weapons and broken shields");
        Room library = new Room(1, 1, "A library filled with ancient books covered in dust");
        Room diningRoom = new Room(1, 1, "A large dining hall with a grand table and empty chairs");
        Room treasureRoom = new Room(1, 1, "A small room glittering with gold and treasures");
        Room darkRoom = new Room(1, 1, "A pitch-black room where you can barely see anything");
        Room guardRoom = new Room(1, 1, "A guard post with a wooden table and empty seats");
        Room throneRoom = new Room(1, 1, "The grand throne room, where the dungeons ruler once sat");
        Room potionRoom = new Room(1, 1, "A small room filled with bubbling potions and strange herbs");
        Room chapel = new Room(1, 1, "An ancient chapel with a broken altar and flickering candles");
        Room tortureChamber = new Room(1, 1, "A dark and eerie chamber filled with torture devices");
        Room treasureVault = new Room(1, 1, "A heavily secured vault filled with ancient treasures");
        Room garden = new Room(1, 1, "A hidden garden with strange plants and a fountain");
        Room crypt = new Room(1, 1, "A crypt containing old coffins and the remains of ancient warriors");
        Room storageRoom = new Room(1, 1, "A cluttered storage room filled with old supplies and junk");
        Room workshop = new Room(1, 1, "A workshop with tools and materials for crafting");
        Room observationRoom = new Room(1, 1, "An observation room with a large telescope for stargazing");
        Room secretRoom = new Room(1, 1, "A hidden room with a mysterious aura and strange symbols");
        Room exit = new Room(1, 1, "The exit room, the end of your journey");
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
    }

    private static void setupPreGame() {
        player = new Player("Adventurer", "A brave soul exploring the dungeon", 100, 0, 0);
        inventory = new Inventory(100);
        Room entryRoom = new Room(1, 1, "The entry hall, a grand hall with cobwebs in the corners");
        Room corridor = new Room(1, 1, "A narrow corridor with walls close on both sides");
        Room armory = new Room(1, 1, "A small armory filled with rusted weapons and broken shields");
        Room library = new Room(1, 1, "A library filled with ancient books covered in dust");
        Room diningRoom = new Room(1, 1, "A large dining hall with a grand table and empty chairs");
        Room treasureRoom = new Room(1, 1, "A small room glittering with gold and treasures");
        Room darkRoom = new Room(1, 1, "A pitch-black room where you can barely see anything");
        Room guardRoom = new Room(1, 1, "A guard post with a wooden table and empty seats");
        Room throneRoom = new Room(1, 1, "The grand throne room, where the dungeons ruler once sat");
        Room potionRoom = new Room(1, 1, "A small room filled with bubbling potions and strange herbs");
        Room chapel = new Room(1, 1, "An ancient chapel with a broken altar and flickering candles");
        Room tortureChamber = new Room(1, 1, "A dark and eerie chamber filled with torture devices");
        Room treasureVault = new Room(1, 1, "A heavily secured vault filled with ancient treasures");
        Room garden = new Room(1, 1, "A hidden garden with strange plants and a fountain");
        Room crypt = new Room(1, 1, "A crypt containing old coffins and the remains of ancient warriors.");
        Room storageRoom = new Room(1, 1, "A cluttered storage room filled with old supplies and junk");
        Room workshop = new Room(1, 1, "A workshop with tools and materials for crafting");
        Room observationRoom = new Room(1, 1, "An observation room with a large telescope for stargazing");
        Room secretRoom = new Room(1, 1, "A hidden room with a mysterious aura and strange symbols");
        Room exit = new Room(1, 1, "The exit room, the end of your journey");
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

    @SuppressWarnings("unused")
    private static void showUserMenu() {
        VBox menuLayout = new VBox(20);
        menuLayout.getStyleClass().add("vbox");
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.setPadding(new Insets(20));
        Text title = new Text("MUD");
        title.getStyleClass().add("title");
        title.setFont(Font.font(50));
        Button newGameButton = new Button("Start New Game");
        Button resumeGameButton = new Button("Resume Game");
        Button changePasswordButton = new Button("Change Password");
        Button viewProfileButton = new Button("View Profile");
        Button exportProfileButton = new Button("Export Profile");
        Button importProfileButton = new Button("Import Profile");
        Button logoutButton = new Button("Logout");
        newGameButton.setOnAction(e -> selectGameMode());
        resumeGameButton.setOnAction(e -> resumeGame());
        changePasswordButton.setOnAction(e -> changePassword());
        viewProfileButton.setOnAction(e -> viewProfile());
        exportProfileButton.setOnAction(e -> exportProfile());
        importProfileButton.setOnAction(e -> importProfile());
        logoutButton.setOnAction(e -> logout());
        menuLayout.getChildren().addAll(title, newGameButton, resumeGameButton, changePasswordButton, viewProfileButton,
                exportProfileButton, importProfileButton, logoutButton);
        Scene scene = new Scene(menuLayout, 800, 600);
        scene.getStylesheets().add(GUI.class.getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    private static void logout() {
        showAlert("MUD", "Goodbye");
        authenticatedUser = null;
        showMainMenu();
    }

    @SuppressWarnings("unused")
    private static void selectGameMode() {
        VBox modeLayout = new VBox(20);
        modeLayout.getStyleClass().add("vbox");
        modeLayout.setAlignment(Pos.CENTER);
        modeLayout.setPadding(new Insets(20));
        Text title = new Text("MUD");
        title.getStyleClass().add("title");
        title.setFont(Font.font(50));
        Button premadeMapButton = new Button("Premade Map");
        Button endlessMapButton = new Button("Endless Adventure");
        Button backButton = new Button("Back");
        premadeMapButton.setOnAction(e -> startNewGame("premade"));
        endlessMapButton.setOnAction(e -> startNewGame("endless"));
        backButton.setOnAction(e -> showUserMenu());
        modeLayout.getChildren().addAll(title, premadeMapButton, endlessMapButton, backButton);
        Scene scene = new Scene(modeLayout, 800, 600);
        scene.getStylesheets().add(GUI.class.getResource("style.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    private static void startNewGame(String mode) {
        currentGameMode = mode;
        authenticatedUser.setGamesPlayed(authenticatedUser.getGamesPlayed() + 1);
        if ("premade".equals(mode)) {
            setupPremadeMap();
        } else if ("endless".equals(mode)) {
            setupEndlessAdventure();
        }
    }

    private static void resumeGame() {
        if (player == null || currentRoom == null || exitRoom == null || roomLayouts.get(currentRoom) == null) {
            showAlert("MUD", "No game in progress, start a new game");
        } else {
            if ("premade".equals(currentGameMode) || "endless".equals(currentGameMode)) {
                displayCurrentRoom();
                gameScene.getStylesheets().add(GUI.class.getResource("style.css").toExternalForm());
                primaryStage.setScene(gameScene);
            }
        }
    }

    private static void initializeGameScene() {
        BorderPane gameLayout = new BorderPane();
        gameLayout.setPadding(new Insets(20));
        gameLog = new TextArea();
        gameLog.setEditable(false);
        gameLog.setWrapText(true);
        gameLog.setPrefRowCount(10);
        VBox statusPane = new VBox(10);
        statusPane.getStyleClass().add("vbox");
        GridPane movementControls = createMovementControls();
        VBox actionButtons = createActionButtons();
        VBox rightPane = new VBox(20);
        rightPane.getStyleClass().add("vbox");
        rightPane.getChildren().addAll(statusPane, movementControls, actionButtons);
        gameLayout.setCenter(gameLog);
        gameLayout.setRight(rightPane);
        gameScene = new Scene(gameLayout, 800, 600);
        gameScene.getStylesheets().add(GUI.class.getResource("style.css").toExternalForm());
    }

    private static void initializePreGameScene() {
        BorderPane gameLayout = new BorderPane();
        gameLayout.setPadding(new Insets(20));
        gameLog = new TextArea();
        gameLog.setEditable(false);
        gameLog.setWrapText(true);
        gameLog.setPrefRowCount(10);
        VBox statusPane = new VBox(10);
        statusPane.getStyleClass().add("vbox");
        GridPane movementControls = preCreateMovementControls();
        VBox actionButtons = preCreateActionButtons();
        VBox rightPane = new VBox(20);
        rightPane.getStyleClass().add("vbox");
        rightPane.getChildren().addAll(statusPane, movementControls, actionButtons);
        gameLayout.setCenter(gameLog);
        gameLayout.setRight(rightPane);
        gameScene = new Scene(gameLayout, 800, 600);
        gameScene.getStylesheets().add(GUI.class.getResource("style.css").toExternalForm());
    }

    private static void setupPrePremadeMap() {
        currentGameMode = "guest";
        initializePreGameScene();
        resetGameState();
        setupPreGame();
        gameLog.appendText("Guest Browse Premade Map\n");
        gameLog.appendText("Welcome to the Dungeon Adventure\n");
        gameLog.appendText("Explore the dungeon and find the exit to win the game\n");
        gameLog.appendText("Good luck, " + player.getName() + ", on completing the adventure\n");
        displayPreCurrentRoom();
        primaryStage.setScene(gameScene);
    }

    private static void setupPremadeMap() {
        initializeGameScene();
        resetGameState();
        setupGame();
        gameLog.appendText("Premade Map\n");
        gameLog.appendText("Welcome to the Dungeon Adventure\n");
        gameLog.appendText("Explore the dungeon and find the exit to win the game\n");
        gameLog.appendText("Good luck, " + player.getName() + ", on completing the adventure\n");
        displayCurrentRoom();
        if (player.getHealth() <= 0) {
            showAlert("MUD", "Game over, " + player.getName() + " has perished, a tragic end to a valiant quest");
            authenticatedUser.setLivesLost(authenticatedUser.getLivesLost() + 1);
            resetGameState();
            showUserMenu();
        }
        primaryStage.setScene(gameScene);
    }

    private static void setupEndlessAdventure() {
        currentGameMode = "endless";
        initializeGameScene();
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
        player = new Player("Adventurer", "A brave soul exploring the dungeon", 100, 0, 0);
        inventory = new Inventory(100);
        gameLog.appendText("Endless Adventure\n");
        gameLog.appendText("Welcome to the Dungeon Adventure\n");
        gameLog.appendText("Explore the dungeon and find the exit to win the game\n");
        gameLog.appendText("Good luck, " + player.getName() + ", on completing the adventure\n");
        displayCurrentRoom();
        if (player.getHealth() <= 0) {
            showAlert("MUD", "Game over, " + player.getName() + " has perished, a tragic end to a valiant quest");
            authenticatedUser.setLivesLost(authenticatedUser.getLivesLost() + 1);
            resetGameState();
            showUserMenu();
        }
        primaryStage.setScene(gameScene);
    }

    @SuppressWarnings("unused")
    private static GridPane createMovementControls() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        Button northBtn = new Button("North");
        Button southBtn = new Button("South");
        Button eastBtn = new Button("East");
        Button westBtn = new Button("West");
        grid.add(northBtn, 1, 0);
        grid.add(westBtn, 0, 1);
        grid.add(eastBtn, 2, 1);
        grid.add(southBtn, 1, 2);
        northBtn.setOnAction(e -> movePlayer("north"));
        southBtn.setOnAction(e -> movePlayer("south"));
        eastBtn.setOnAction(e -> movePlayer("east"));
        westBtn.setOnAction(e -> movePlayer("west"));
        return grid;
    }

    @SuppressWarnings("unused")
    private static GridPane preCreateMovementControls() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        Button northBtn = new Button("North");
        Button southBtn = new Button("South");
        Button eastBtn = new Button("East");
        Button westBtn = new Button("West");
        grid.add(northBtn, 1, 0);
        grid.add(westBtn, 0, 1);
        grid.add(eastBtn, 2, 1);
        grid.add(southBtn, 1, 2);
        northBtn.setOnAction(e -> preMovePlayer("north"));
        southBtn.setOnAction(e -> preMovePlayer("south"));
        eastBtn.setOnAction(e -> preMovePlayer("east"));
        westBtn.setOnAction(e -> preMovePlayer("west"));
        return grid;
    }

    @SuppressWarnings("unused")
    private static VBox createActionButtons() {
        VBox actions = new VBox(10);
        actions.getStyleClass().add("vbox");
        actions.setAlignment(Pos.CENTER);
        Button openChestBtn = new Button("Open Chest");
        Button viewInventoryBtn = new Button("View Inventory");
        Button viewHealthBtn = new Button("Health");
        Button helpBtn = new Button("Help");
        Button quitBtn = new Button("Quit");
        openChestBtn.setOnAction(e -> openChest());
        viewInventoryBtn.setOnAction(e -> openInventory());
        viewHealthBtn.setOnAction(e -> showHealth());
        helpBtn.setOnAction(e -> showHelp());
        quitBtn.setOnAction(e -> {
            showAlert("MUD", "Goodbye");
            showUserMenu();
        });
        actions.getChildren().addAll(openChestBtn, viewInventoryBtn, viewHealthBtn, helpBtn, quitBtn);
        return actions;
    }

    @SuppressWarnings("unused")
    private static VBox preCreateActionButtons() {
        VBox actions = new VBox(10);
        actions.getStyleClass().add("vbox");
        actions.setAlignment(Pos.CENTER);
        Button helpBtn = new Button("Help");
        Button quitBtn = new Button("Quit");
        helpBtn.setOnAction(e -> preShowHelp());
        quitBtn.setOnAction(e -> {
            showAlert("MUD", "Goodbye");
            showMainMenu();
        });
        actions.getChildren().addAll(helpBtn, quitBtn);
        return actions;
    }

    private static void movePlayer(String directionStr) {
        Direction direction = parseDirection(directionStr);
        if (direction == null) {
            showAlert("MUD", "Invalid direction");
            return;
        }
        if (currentRoom.getExits().containsKey(direction)) {
            currentRoom = currentRoom.getExits().get(direction);
            gameLog.appendText("You move " + directionStr + " and enter a new room\n");
            displayCurrentRoom();
        } else {
            gameLog.appendText("You cannot go that way\n");
            gameLog.appendText("What will you do next, " + player.getName() + "?\n");
        }
    }

    private static void preMovePlayer(String directionStr) {
        Direction direction = parseDirection(directionStr);
        if (direction == null) {
            showAlert("MUD", "Invalid direction");
            return;
        }
        if (currentRoom.getExits().containsKey(direction)) {
            currentRoom = currentRoom.getExits().get(direction);
            gameLog.appendText("You move " + directionStr + " and enter a new room\n");
            displayPreCurrentRoom();
        } else {
            gameLog.appendText("You cannot go that way\n");
            gameLog.appendText("What will you do next, " + player.getName() + "?\n");
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

    private static void openInventory() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().add(GUI.class.getResource("style.css").toExternalForm());
        dialog.setTitle("MUD");
        dialog.setHeaderText("Inventory");
        VBox content = new VBox(10);
        content.getStyleClass().add("vbox");
        content.setPadding(new Insets(20));
        Item[] items = player.getInventory().getItems();
        if (player.getInventory().isEmpty()) {
            content.getChildren().add(new Label("Your inventory is empty, you have nothing to use"));
        } else {
            for (Item item : items) {
                if (item != null) {
                    content.getChildren().add(new Label(item.getName() + ": " + item.getDescription()));
                }
            }
        }
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private static void showHealth() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().add(GUI.class.getResource("style.css").toExternalForm());
        dialog.setTitle("MUD");
        dialog.setHeaderText("Health");
        VBox content = new VBox(10);
        content.getStyleClass().add("vbox");
        content.setPadding(new Insets(20));
        if (player != null) {
            content.getChildren()
                    .add(new Label(player.getName() + "'s" + " current health points are " + player.getHealth()));
        } else {
            content.getChildren().add(new Label("No player found"));
        }
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private static void showHelp() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().add(GUI.class.getResource("style.css").toExternalForm());
        dialog.setTitle("MUD");
        dialog.setHeaderText("Available Commands");
        VBox content = new VBox(10);
        content.getStyleClass().add("vbox");
        content.setPadding(new Insets(20));
        content.getChildren().addAll(
                new Label("Move <Direction>: Move in a direction"),
                new Label("Open Chest: Open the chest in front of you"),
                new Label("View Inventory: View your inventory"),
                new Label("Health: Show the players health"),
                new Label("Quit: Exit the current game"));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private static void preShowHelp() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().add(GUI.class.getResource("style.css").toExternalForm());
        dialog.setTitle("MUD");
        dialog.setHeaderText("Available Commands");
        VBox content = new VBox(10);
        content.getStyleClass().add("vbox");
        content.setPadding(new Insets(20));
        content.getChildren().addAll(
                new Label("Move <Direction>: Move in a direction"),
                new Label("Quit: Exit the current game"));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(GUI.class.getResource("style.css").toExternalForm());
        dialogPane.getStyleClass().add("alert");
        alert.showAndWait();
    }

    private static void exportProfile() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().add(GUI.class.getResource("style.css").toExternalForm());
        dialog.setTitle("MUD");
        dialog.setHeaderText("Export Profile");
        ChoiceBox<String> formatChoiceBox = new ChoiceBox<>();
        formatChoiceBox.getItems().addAll("CSV", "JSON", "XML");
        formatChoiceBox.setValue("CSV");
        TextField playerFileNameField = new TextField();
        TextField userFileNameField = new TextField();
        VBox content = new VBox(10);
        content.getStyleClass().add("vbox");
        content.getChildren().addAll(
                new Label("Choose Format:"), formatChoiceBox,
                new Label("Player Filename:"), playerFileNameField,
                new Label("User Filename:"), userFileNameField);
        content.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String format = formatChoiceBox.getValue();
                String playerFileName = playerFileNameField.getText().trim();
                String userFileName = userFileNameField.getText().trim();
                if (playerFileName.isEmpty() || userFileName.isEmpty()) {
                    showAlert("MUD", "Both filenames are required");
                    return null;
                }
                try {
                    switch (format) {
                        case "CSV" -> {
                            player.exportToCSV(playerFileName + ".csv");
                            authenticatedUser.exportToCSV(userFileName + ".csv");
                        }
                        case "JSON" -> {
                            player.exportToJSON(playerFileName + ".json");
                            authenticatedUser.exportToJSON(userFileName + ".json");
                        }
                        case "XML" -> {
                            player.exportToXML(playerFileName + ".xml");
                            authenticatedUser.exportToXML(userFileName + ".xml");
                        }
                    }
                    showAlert("MUD", "User profile exported successfully");
                } catch (Exception e) {
                    showAlert("MUD", "Error exporting profile");
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    private static void importProfile() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().add(GUI.class.getResource("style.css").toExternalForm());
        dialog.setTitle("MUD");
        dialog.setHeaderText("Import Profile");
        ChoiceBox<String> formatChoiceBox = new ChoiceBox<>();
        formatChoiceBox.getItems().addAll("CSV", "JSON", "XML");
        formatChoiceBox.setValue("CSV");
        TextField playerFileNameField = new TextField();
        TextField userFileNameField = new TextField();
        VBox content = new VBox(10);
        content.getStyleClass().add("vbox");
        content.getChildren().addAll(
                new Label("Choose Format:"), formatChoiceBox,
                new Label("Player Filename:"), playerFileNameField,
                new Label("User Filename:"), userFileNameField);
        content.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                String format = formatChoiceBox.getValue();
                String playerFileName = playerFileNameField.getText().trim();
                String userFileName = userFileNameField.getText().trim();
                if (playerFileName.isEmpty() || userFileName.isEmpty()) {
                    showAlert("MUD", "Both filenames are required");
                    return null;
                }
                try {
                    switch (format) {
                        case "CSV" -> {
                            player.importFromCSV(playerFileName);
                            authenticatedUser.importFromCSV(userFileName);
                        }
                        case "JSON" -> {
                            player.importFromJSON(playerFileName);
                            authenticatedUser.importFromJSON(userFileName);
                        }
                        case "XML" -> {
                            player.importFromXML(playerFileName);
                            authenticatedUser.importFromXML(userFileName);
                        }
                    }
                    showAlert("MUD", "User profile imported successfully");
                } catch (Exception e) {
                    showAlert("MUD", "Error importing profile");
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    private static void changePassword() {
        Dialog<String> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().add(GUI.class.getResource("style.css").toExternalForm());
        dialog.setTitle("MUD");
        dialog.setHeaderText("Change Password");
        ButtonType changeButtonType = new ButtonType("Change", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(changeButtonType, ButtonType.CANCEL);
        PasswordField newPassword = new PasswordField();
        VBox content = new VBox(10);
        content.getStyleClass().add("vbox");
        content.getChildren().addAll(new Label("New Password:"), newPassword);
        content.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(content);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == changeButtonType) {
                String newPass = newPassword.getText();
                if (newPass.isEmpty()) {
                    showAlert("MUD", "Field is required, please fill it in");
                    return null;
                }
                if (newPass.equals(authenticatedUser.getPassword())) {
                    showAlert("MUD", "The password is the same, please choose another one");
                    return null;
                } else {
                    authenticatedUser.setPassword(newPass);
                    showAlert("MUD", "Password changed successfully");
                }
            }
            return null;
        });
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(password -> {
            authenticatedUser.setPassword(password);
            showAlert("MUD", "Password changed successfully");
        });
    }

    private static void viewProfile() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.getDialogPane().getStylesheets().add(GUI.class.getResource("style.css").toExternalForm());
        dialog.setTitle("MUD");
        dialog.setHeaderText("View Profile");
        VBox content = new VBox(10);
        content.getStyleClass().add("vbox");
        content.setPadding(new Insets(20));
        content.getChildren().addAll(
                new Label("Games Played: " + authenticatedUser.getGamesPlayed()),
                new Label("Lives Lost: " + authenticatedUser.getLivesLost()),
                new Label("Monsters Slain: " + authenticatedUser.getMonstersSlain()),
                new Label("Gold Earned: " + authenticatedUser.getGoldEarned()),
                new Label("Items Found: " + authenticatedUser.getItemsFound()));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    private static void handleChestContent(int chestContent) {
        int gold = random.nextInt(100);
        switch (chestContent) {
            case 0:
                gameLog.appendText(
                        "As you slowly lift the lid of the chest, it creaks open to reveal nothing, a wave of disappointment washes over you as you realize its completely barren, holding only your dashed hopes\n");
                break;
            case 1:
                gameLog.appendText(
                        "You eagerly lift the lid of the chest, only to find it filled with stale air and disappointment, this treasure has long been forgotten\n");
                break;
            case 2:
                gameLog.appendText(
                        "With a cautious glance inside, you discover a dull, jagged rock resting within, its weight is burdensome, utterly useless, and it seems to mock your adventurous spirit as you ponder its presence\n");
                gameLog.appendText("You found " + gold + " gold\n");
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                break;
            case 3:
                gameLog.appendText(
                        "You open the chest to find a strange, glittering object called a shiny button, it seems utterly useless, yet its allure catches your eye, almost daring you to press it\n");
                gameLog.appendText("You found " + gold + " gold\n");
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                break;
            case 4:
                int trapDamage = 10;
                gameLog.appendText(
                        "The moment you lift the lid, a sharp click resonates in the stillness, before you can react, concealed spikes spring forth, piercing you, pain surges through you as you take "
                                + trapDamage + " damage, reminding you of the dangers lurking within\n");
                player.takeDamage(trapDamage);
                break;
            case 5:
                int newTrapDamage = 10;
                gameLog.appendText(
                        "As you open the chest, a hidden mechanism activates, releasing a torrent of arrows, you quickly try to evade but are struck, taking "
                                + newTrapDamage + " damage\n");
                player.takeDamage(newTrapDamage);
                break;
            case 6:
                int npcDamage = 10;
                gameLog.appendText(
                        "With a sudden crash, the chest lid bursts open, unleashing a shrill screech that echoes through the chamber, a menacing figure leaps out, claws bared! It lunges at you, landing a vicious blow, and you scramble back, taking "
                                + npcDamage + " damage before you can fend it off\n");
                player.takeDamage(npcDamage);
                gameLog.appendText("You found " + gold + " gold\n");
                authenticatedUser.setMonstersSlain(authenticatedUser.getMonstersSlain() + 1);
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                break;
            case 7:
                int newNPCDamage = 10;
                gameLog.appendText(
                        "As you cautiously lift the lid, a sinister figure bursts forth from the chest, a wicked grin stretching across its face, it lunges at you with a wicked dagger, striking you with surprising speed, you stagger back as pain sears through your body, taking "
                                + newNPCDamage + " damage\n");
                player.takeDamage(newNPCDamage);
                gameLog.appendText("You found " + gold + " gold\n");
                authenticatedUser.setMonstersSlain(authenticatedUser.getMonstersSlain() + 1);
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                break;
            case 8:
                Weapon weapon = new Weapon("Sword of valor",
                        "A finely crafted sword with a gleaming blade, known for its strength and precision", 10, 10);
                gameLog.appendText("You open the chest to discover the legendary " + weapon.getName() + "\n");
                player.getInventory().addItem(weapon);
                gameLog.appendText("You add the " + weapon.getName() + " to your inventory\n");
                gameLog.appendText("You found " + gold + " gold\n");
                authenticatedUser.setItemsFound(authenticatedUser.getItemsFound() + 1);
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                break;
            case 9:
                Weapon newWeapon = new Weapon("Axe of the ancients",
                        "A massive, two-handed axe that resonates with the power of ancient warriors, known for its devastating strikes",
                        10, 10);
                gameLog.appendText("You open the chest and behold the mighty " + newWeapon.getName() + "\n");
                player.getInventory().addItem(newWeapon);
                gameLog.appendText("You add the " + newWeapon.getName() + " to your inventory\n");
                gameLog.appendText("You found " + gold + " gold\n");
                authenticatedUser.setItemsFound(authenticatedUser.getItemsFound() + 1);
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                break;
            case 10:
                Armor armor = new Armor("Dragon scale armor",
                        "An armor forged from the scales of a legendary dragon, offers exceptional protection", 10,
                        10);
                gameLog.appendText(
                        "You cautiously lift the lid and find a majestic piece of armor called the " + armor.getName()
                                + "\n");
                player.getInventory().addItem(armor);
                gameLog.appendText("You add the " + armor.getName() + " to your inventory\n");
                gameLog.appendText("You found " + gold + " gold\n");
                authenticatedUser.setItemsFound(authenticatedUser.getItemsFound() + 1);
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                break;
            case 11:
                Armor newArmor = new Armor("Shield of the guardian",
                        "A sturdy shield adorned with the emblem of a guardian, provides excellent defense against attacks",
                        10, 10);
                gameLog.appendText(
                        "As you carefully open the chest, you discover the impressive " + newArmor.getName() + "\n");
                player.getInventory().addItem(newArmor);
                gameLog.appendText("You add the " + newArmor.getName() + " to your inventory\n");
                gameLog.appendText("You found " + gold + " gold\n");
                authenticatedUser.setItemsFound(authenticatedUser.getItemsFound() + 1);
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                break;
            case 12:
                Item food = new Food("Mystical apple", "A glowing apple that restores a portion of your health", 10,
                        10);
                gameLog.appendText("As you pry open the chest, you find a " + food.getName() + " nestled within\n");
                player.getInventory().addItem(food);
                gameLog.appendText("You add the " + food.getName() + " to your inventory\n");
                gameLog.appendText("You found " + gold + " gold\n");
                authenticatedUser.setItemsFound(authenticatedUser.getItemsFound() + 1);
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                break;
            case 13:
                Item newFood = new Food("Healing herb",
                        "A fragrant herb that has healing properties, restores a significant portion of your health",
                        10, 10);
                gameLog.appendText(
                        "As you carefully open the chest, you discover a " + newFood.getName() + " resting inside\n");
                player.getInventory().addItem(newFood);
                gameLog.appendText("You add the " + newFood.getName() + " to your inventory\n");
                gameLog.appendText("You found " + gold + " gold\n");
                authenticatedUser.setItemsFound(authenticatedUser.getItemsFound() + 1);
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                break;
            case 14:
                gameLog.appendText(
                        "You open the chest, and in an instant-boom, a hidden bomb detonates with a deafening roar\n");
                gameLog.appendText(
                        "A sharp pain sears through you, and darkness envelops your vision, you met an untimely demise\n");
                player.setHealth(0);
                showAlert("MUD", "Game over, " + player.getName() + " has perished, a tragic end to a valiant quest");
                resetGameState();
                showUserMenu();
                break;
            case 15:
                gameLog.appendText(
                        "You open the chest and find a portal shimmering with ethereal light, as you step inside, a feeling of warmth envelops you, and you realize you've won the game\n");
                gameLog.appendText("You found " + gold + " gold\n");
                showAlert("MUD",
                        "Congratulations, " + player.getName() + ", you have completed your quest successfully");
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                resetGameState();
                showUserMenu();
                break;
            case 16:
                gameLog.appendText("You found a shrine! You can pray here to create a save point\n");
                gameLog.appendText("You have prayed at the shrine. Your current state has been saved\n");
                int savedHealth = player.getHealth();
                int savedAttack = player.getAttack();
                int savedDefense = player.getDefense();
                Weapon savedWeapon = player.getEquippedWeapon();
                Armor savedArmor = player.getEquippedArmor();
                Inventory savedInventory = player.getInventory();
                gameLog.appendText("Saved state - Health: " + savedHealth + ", Attack: " + savedAttack + ", Defense: "
                        + savedDefense + "\n");
                gameLog.appendText(
                        "Saved state - Equipped Weapon: " + (savedWeapon != null ? savedWeapon.getName() : "None")
                                + ", Equipped Armor: " + (savedArmor != null ? savedArmor.getName() : "None")
                                + ", Inventory: " + savedInventory.toString() + "\n");
                break;
            case 17:
                gameLog.appendText("You found a merchant! The merchant offers to trade with you\n");
                String[] items = { "Health Potion", "Attack Potion", "Defense Potion" };
                int[] prices = { 10, 10, 10 };
                int totalCost = 0;
                gameLog.appendText("The merchant has 3 items for sale:\n");
                for (int i = 0; i < items.length; i++) {
                    gameLog.appendText("- " + items[i] + " (Price: " + prices[i] + " gold)\n");
                    totalCost += prices[i];
                }
                int currentGold = authenticatedUser.getGoldEarned();
                gameLog.appendText("You have " + currentGold + " gold\n");
                if (currentGold >= totalCost) {
                    for (String item : items) {
                        player.getInventory().addItem(new Item(item, "A potion bought from the merchant", 25));
                    }
                    authenticatedUser.setItemsFound(authenticatedUser.getItemsFound() + 3);
                    authenticatedUser.setGoldEarned(currentGold - totalCost);
                    gameLog.appendText("You bought all items for " + totalCost + " gold\n");
                    gameLog.appendText("The items have been added to your inventory\n");
                    gameLog.appendText("You now have " + authenticatedUser.getGoldEarned() + " gold remaining\n");
                } else {
                    gameLog.appendText("You don't have enough gold to buy all items\n");
                }
                break;
            default:
                gameLog.appendText("You opened the chest, but nothing happened, its a mystery\n");
                break;
        }
    }

    private static void openChest() {
        int gold = random.nextInt(100);
        Tile[][] currentTiles = roomLayouts.get(currentRoom);
        if (currentTiles[0][0].getContent() == Content.CHEST) {
            if (currentRoom.equals(exitRoom)) {
                gameLog.appendText(
                        "You open the chest and discover a glittering diamond inside, with your prize in hand, you leave the dungeon victorious\n");
                gameLog.appendText("You found " + gold + " gold\n");
                showAlert("MUD",
                        "Congratulations, " + player.getName() + ", you have completed your quest successfully");
                authenticatedUser.setGoldEarned(authenticatedUser.getGoldEarned() + gold);
                resetGameState();
                showUserMenu();
            } else {
                int chestContent = random.nextInt(18);
                handleChestContent(chestContent);
                gameLog.appendText("What will you do next, " + player.getName() + "?\n");
            }
            currentTiles[0][0].removeChest();
        } else {
            gameLog.appendText("Theres no chest here to open\n");
            gameLog.appendText("What will you do next, " + player.getName() + "?\n");
        }
    }

    private static void displayCurrentRoom() {
        if (currentRoom == null) {
            gameLog.appendText("Error: currentRoom is null\n");
            return;
        }
        gameLog.appendText("You are in: " + currentRoom.getDescription() + "\n");
        gameLog.appendText("Available exits: " + currentRoom.getExits().keySet() + "\n");
        if (currentRoom.equals(exitRoom)) {
            gameLog.appendText(
                    "You arrived at the exit room, suddenly, a mysterious chest emerges from the ground, casting a radiant glow that fills the entire chamber\n");
            gameLog.appendText("What will you do next, " + player.getName() + "?\n");
            Tile[][] currentTiles = roomLayouts.get(currentRoom);
            if (currentTiles[0][0].getContent() != Content.CHEST) {
                currentTiles[0][0].placeChest();
            }
            gameLog.appendText("There is a chest in front of you, waiting to be opened\n");
            gameLog.appendText("What will you do next, " + player.getName() + "?\n");
        } else {
            Tile[][] currentTiles = roomLayouts.get(currentRoom);
            if (currentTiles[0][0].getContent() == Content.CHEST) {
                gameLog.appendText("There is a chest in front of you\n");
                gameLog.appendText("What will you do next, " + player.getName() + "?\n");
            }
        }
        gameLog.appendText("What will you do next, " + player.getName() + "?\n");
    }

    private static void displayPreCurrentRoom() {
        if (currentRoom == null) {
            gameLog.appendText("Error: currentRoom is null\n");
            return;
        }
        gameLog.appendText("You are in: " + currentRoom.getDescription() + "\n");
        gameLog.appendText("Available exits: " + currentRoom.getExits().keySet() + ".\n");
        if (currentRoom.equals(exitRoom)) {
            gameLog.appendText(
                    "You arrived at the exit room, suddenly, a mysterious chest emerges from the ground, casting a radiant glow that fills the entire chamber\n");
            gameLog.appendText(
                    "You open the chest and discover a glittering diamond inside, with your prize in hand, you leave the dungeon victorious\n");
            showAlert("MUD", "Congratulations, " + player.getName() + ", you have completed your quest successfully");
            resetGameState();
            showMainMenu();
        }
        gameLog.appendText("What will you do next, " + player.getName() + "?\n");
    }

}