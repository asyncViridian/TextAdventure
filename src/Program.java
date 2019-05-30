import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class Program {
    static final String[] MESSAGE_INVALID_USER_INPUT = {"You tried to do something obviously impossible. Nothing happened."};
    static final String[] MESSAGE_INVALID_MOVE_DIRECTION = {"You can't move in that direction."};

    static Map<String, DataRoom> allRooms = new HashMap<>();
    static Map<String, DataItem> allThings = new HashMap<>();
    static DataRoom currentRoom;
    static List<DataItem> inventory = new ArrayList();
    static Scanner console = new Scanner(System.in);
    static boolean playing = true;
    static boolean printDesc = true;

    static Random r = new Random();

    public static void main(String[] args) {
        try {
            loadRooms();
        } catch (IOException e) {
            System.err.println("Unable to load input file");
            e.printStackTrace();
            return;
        }

        while (playing) {
            if (printDesc) {
                printDescription();
                printDesc = false;
            }
            Action a = getUserInput();
            switch (a) {
                case NORTH:
                case SOUTH:
                case EAST:
                case WEST:
                case UP:
                case DOWN:
                    moveInDirection(a);
                    break;
                case LOOK:
                    printDescription();
                    break;
                case EXAMINE:
                    // TODO
                    break;
                case INVENTORY:
                    // TODO
                    break;
                case HELP:
                    printHelp();
                    break;
                case QUIT:
                    playing = false;
                    break;
            }
        }
    }

    public static void loadRooms() throws IOException {
        String filename = "house";
        Files.lines(Paths.get(filename))
                .filter(l -> l.length() != 0)
                .forEach(l -> {
                    String[] e = l.split(","); // TODO might need to change delimiter n stuff
                    e[0] = e[0].toUpperCase();
                    if (e[0].charAt(0) == 'R') {
                        // Handle creating a room
                        DataRoom room = new DataRoom(e[2].trim(), e[3].trim());
                        // Load exits from the room
                        Stream.of(e[4].substring(1, e[4].length() - 1).split(";")).forEach(s -> {
                            // Add each pair of directions/exits
                            String[] p = s.split(":");
                            room.exits.put(Action.valueOf(p[0].trim()), p[1].trim());
                        });
                        // Load items in the room
                        Stream.of(e[5].substring(1, e[5].length() - 1).split(";")).forEach(s -> {
                            // Add each item
                            room.objects.add(s.trim());
                        });
                        allRooms.put(e[1].trim(), room);
                        if (e[0].charAt(e[0].length() - 1) == '*') {
                            // Make it the default room!
                            currentRoom = room;
                        }
                    } else if (e[0].charAt(0) == 'I') {
                        // Handle creating an item
                        DataItem item = new DataItem(e[2].trim(), e[3].trim());
                        allThings.put(e[1].trim(), item);
                    }
                });
    }

    public static void printDescription() {
        System.out.println("You are in: " + currentRoom.name);
        System.out.println(currentRoom.desc);
        if (currentRoom.exits.size() != 0) {
            System.out.print("Exits to the ");
            for (Action exitDir : currentRoom.exits.keySet()) {
                System.out.print(exitDir.toString().toLowerCase() + ", ");
            }
            System.out.println();
        } else {
            System.out.println("There aren't any exits out from here.");
        }
        if (currentRoom.objects.size() != 0) {
            System.out.print("There is ");
            for (String itemNickname : currentRoom.objects) {
                System.out.print(allThings.get(itemNickname).name + ", ");
            }
            System.out.println("here.");
        }
    }

    public static Action getUserInput() {
        while (true) {
            System.out.print("> ");
            String input = console.nextLine().toLowerCase();
            if (input.equals("north") || input.equals("n")) {
                return Action.NORTH;
            } else if (input.equals("south") || input.equals("s")) {
                return Action.SOUTH;
            } else if (input.equals("east") || input.equals("e")) {
                return Action.EAST;
            } else if (input.equals("west") || input.equals("w")) {
                return Action.WEST;
            } else if (input.equals("up") || input.equals("u")) {
                return Action.UP;
            } else if (input.equals("down") || input.equals("d")) {
                return Action.DOWN;
            } else if (input.equals("look") || input.equals("l")) {
                return Action.LOOK;
            } else if (input.equals("examine") || input.equals("x")) {
                // todo fix conditional
                return Action.EXAMINE;
            } else if (input.equals("inventory") || input.equals("i")) {
                return Action.INVENTORY;
            } else if (input.equals("help")) {
                return Action.HELP;
            } else if (input.equals("quit") || input.equals("q")) {
                return Action.QUIT;
            } else {
                System.out.println(MESSAGE_INVALID_USER_INPUT[r.nextInt(MESSAGE_INVALID_USER_INPUT.length)]);
            }
        }
    }

    public static void moveInDirection(Action a) {
        if (currentRoom.exits.containsKey(a)) {
            currentRoom = allRooms.get(currentRoom.exits.get(a));
            printDesc = true;
        } else {
            System.out.println(MESSAGE_INVALID_MOVE_DIRECTION[r.nextInt(MESSAGE_INVALID_MOVE_DIRECTION.length)]);
        }
    }

    public static void printHelp() {
        System.out.print("Commands you could try: ");
        for (Action a : Action.values()) {
            System.out.print(a.toString().toLowerCase() + ", ");
        }
        System.out.println();
    }

    public enum Action {
        NORTH, SOUTH, EAST, WEST, UP, DOWN, LOOK, EXAMINE, INVENTORY, HELP, QUIT
    }

    public static class DataItem {
        String name;
        String desc;

        public DataItem(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }
    }

    public static class DataRoom {
        String name;
        String desc;

        /**
         * Map from Action(direction) to nickname of room
         */
        Map<Action, String> exits;

        /**
         * List of nicknames of objects in the room
         */
        List<String> objects;

        public DataRoom(String name, String desc) {
            this.name = name;
            this.desc = desc;
            exits = new HashMap<>();
            objects = new ArrayList<>();
        }
    }

}
