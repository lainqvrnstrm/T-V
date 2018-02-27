import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Pontus Laestadius
 * Draws out a simulation in the terminal.
 */
public class UserInterface {

    private static String[] commands = {"move", "change", "wait", "move"};
    private static int car_length = 4;

    public static void main(String[] args) {

        // create all the vehicles for the simulation.
        ArrayList<Vehicle> vehicles = createVehicles();
        Vehicle car = vehicles.get(0); // The car we are controlling.

        Scanner scanner = new Scanner(System.in);
        boolean automated = false;
        boolean result = false;
        String command;

        while (true) {
            // Emulates sensor readings.
            vehicles = updateReadings(vehicles);

            if (!automated) {
                command = scanner.nextLine();
                System.out.println();
            } else {
                command = commands[(int)(System.currentTimeMillis() % commands.length)];
                try {
                    Thread.sleep(500);
                }catch (Exception e) {
                    System.exit(1);
                }
            }

            switch (command) {
                case "auto":
                    automated = true;
                case "move":
                    result = car.moveForward();
                    break;
                case "change":
                    result = car.changeLane();
                    break;
                case "quit":
                    System.exit(0);
                case "wait":
                    // Perform no action.
                    break;
                case "save":
                    System.out.println("--------------------------");
                    System.out.print(vehicles.size() + " ");
                    for (Vehicle vehicle: vehicles) {
                        System.out.print(vehicle.getGyro().getLatitude() + " ");
                        System.out.print(vehicle.getGyro().getLongitude() + " ");
                        System.out.println(vehicle.getSpeed());
                    }
                    System.out.println("--------------------------");
                case "print":
                    print(vehicles);
                default:
                    continue;
            }

            System.out.println("(" + command + ") " + "Method output: " + result);

            // Move all the other cars forward.
            for (int i = 1; i < vehicles.size(); i++)
                vehicles.get(i).moveForward();

            render(vehicles);
        }
    }

    /**
     * Will create a set of vehicles based on user given input.
     * @return a list of vehicles in their indicated position.
     */
    private static ArrayList<Vehicle> createVehicles() {
        Scanner scanner = new Scanner(System.in);

        ArrayList<Vehicle> vehicles = new ArrayList<>();

        System.out.println("Specify number of cars:");
        int cars = scanner.nextInt();

        System.out.println("Specify lane (0-2) and distance (0-100) and speed (0-100)");
        for (int i = 0; i < cars; i++) {
            Vehicle vehicle = new Vehicle();
            System.out.println("Set Car #" + (i+1) + "'s position and speed");
            vehicle.getGyro().setLatitude(scanner.nextInt());
            vehicle.getGyro().setLongitude(scanner.nextInt());
            vehicle.setSpeed(scanner.nextInt());
            vehicles.add(vehicle);
            render(vehicles);
        }

        System.out.println("Commands:" +
                "\nmove -> MoveForward" +
                "\nchange -> ChangeLane" +
                "\nprint -> Print sensor data" +
                "\nwait -> perform no action" +
                "\nauto -> runs simulation automated" +
                "\nsave -> prints out input for re-entering"
        );

        return vehicles;
    }

    /**
     * Prints out all the sensor readings of given input.
     * @param vehicles sensor data that will be printed
     */
    private static void print(ArrayList<Vehicle> vehicles) {
        int i = 0;
        for (Vehicle vehicle: vehicles)
            System.out.println("Car #" + i++
                    + "\nBackSideRadar: " + vehicle.getBackSideRadar().read()
                    + "\nFrontSideRadar: " + vehicle.getFrontSideRadar().read()
                    + "\nFrontRadar: " + vehicle.getFrontRadar().read()
                    + "\nLatitude:" + vehicle.getGyro().getLatitude()
                    + "\nLongitude:" + vehicle.getGyro().getLongitude()
                    + "\nSpeed:" + vehicle.getSpeed()
            );
    }

    /**
     * Given a list of vehicles, will render a image to print to stdout.
     * @param vehicles to render in stdout
     */
    private static void render(ArrayList<Vehicle> vehicles) {
        int lanes = 3, range = 101;
        int map[][] = generateMap(vehicles);
        for (int i = 0; i < range; i++)
            System.out.print("|");
        System.out.println();

        for (int i = lanes-1; i >= 0; i--) { // Prints out the cars.
            for (int j = 0; j < range; j++) {
                if (j >= 100) {
                    System.out.print("|");
                    continue;
                }
                switch (map[i][j]) { // Only allow one car at one place.
                    case 1:
                        System.out.print("ō͡≡o");
                        j += car_length;
                        break;
                    default:
                        System.out.print("-");
                }
            }
            System.out.println(); // new lane.
        }

        for (int i = 0; i < range; i++)
            System.out.print("|");

        System.out.println();
    }

    /**
     * Updates all vehicles sensors based on their positions.
     * @param vehicles to set their readings and read from.
     * @return a new list of vehicles which can be utilized.
     */
    private static ArrayList<Vehicle> updateReadings(ArrayList<Vehicle> vehicles) {

        int map[][] = generateMap(vehicles);

        // Sets sensor readings based on map.
        for (Vehicle vehicle: vehicles) {
            vehicle.getFrontRadar().write(50);
            vehicle.getBackSideRadar().write(10);
            vehicle.getFrontSideRadar().write(10);
            vehicle.getLidar().writeIndex(45, 11);

            int longi = vehicle.getGyro().getLongitude();
            int lati = vehicle.getGyro().getLatitude();
            int max = (longi+50 > 101 ?101:longi+50);

            for (int i = longi+car_length; i < max; i++) { // Front detecting radar.
                if (map[lati][i] == 1) {
                    vehicle.getFrontRadar().write(i-longi);
                    break;
                }
            }

            for (int i = 0; i < 3; i++) { // Sets side radars.
                if (map[lati +1][(longi -i) < 0 ? 0: longi -i] == 1)
                    vehicle.getBackSideRadar().write(4);
                if (map[lati +1][(longi +i) > 100?100:longi +i] == 1)
                    vehicle.getFrontSideRadar().write(4);
            }

            if (vehicle.getBackSideRadar().read() == 4 || vehicle.getFrontSideRadar().read() == 4)
                vehicle.getLidar().writeIndex(45, 5);

        }

        return vehicles;
    }

    /**
     * @param vehicles gyro position used to render positions.
     * @return a top-down view which indicates which positions are occupied and which are free.
     */
    private static int[][] generateMap(ArrayList<Vehicle> vehicles) {
        int map[][] = new int[4][101]; // Size of map.
        for (int i = 0; i < 4; i++) // Set end of road border.
            map[i][100] = 1;
        for (int i = 0; i < 101; i++) // Set top side border.
            map[3][i] = 1;
        for (Vehicle vehicle: vehicles)            // Draws it on a 2d map.
            for (int i = vehicle.getGyro().getLongitude(); i < 100 && i < vehicle.getGyro().getLongitude()+car_length; i++)
                map[vehicle.getGyro().getLatitude()][i] = 1;
        return map;
    }
}
