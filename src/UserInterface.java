import java.util.ArrayList;
import java.util.Scanner;

/**
 * @author Pontus Laestadius
 */
public class UserInterface {

    public static void main(String[] args) {

        // create a scanner so we can read the command-line input
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
                "\nwait -> perform no action");

        while (true) {
            Vehicle car = vehicles.get(0);
            // Emulates sensor readings.
            vehicles = updateReadings(vehicles);

            // get the age as an int
            String command = scanner.nextLine();
            System.out.println();
            boolean result = false;

            switch (command) {
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
                case "print":
                    print(vehicles);
                default:
                    continue;
            }

            System.out.println("Method output: " + result);

            // Move all the other cars forward.
            for (int i = 1; i < vehicles.size(); i++)
                vehicles.get(i).moveForward();

            render(vehicles);
        }
    }

    private static void print(ArrayList<Vehicle> vehicles) {
        int i = 0;
        for (Vehicle vehicle: vehicles)
            System.out.println("Car #" + i++
                            + "\nBackSideRadar: " + vehicle.getBackSideRadar().read()
                            + "\nFrontSideRadar: " + vehicle.getFrontSideRadar().read()
                            + "\nFrontRadar: " + vehicle.getFrontRadar().read()
            );
    }

    private static void render(ArrayList<Vehicle> vehicles) {
        int lanes = 3, range = 20;
        int map[][] = new int[lanes][range+1];

        for (Vehicle vehicle: vehicles) {
            Gyro gyro = vehicle.getGyro();
            map[gyro.getLatitude()][gyro.getLongitude()/(101/range)]++;
        }

        for (int i = 0; i < range; i++) {
            System.out.print("||||");
        }

        System.out.println();

        // Prints out the cars.
        for (int i = lanes-1; i >= 0; i--) {
            for (int j = 0; j < range; j++) {
                // Only allow one car at one place.
                switch (map[i][j]) {
                    case 1:
                        System.out.print("ō͡≡o");
                        break;
                    case 2-Integer.MAX_VALUE:
                        System.out.print("Erro");
                        break;
                    default:
                        System.out.print("----");
                }
            }
            System.out.println(); // new lane.
        }

        for (int i = 0; i < range; i++) {
            System.out.print("||||");
        }

        System.out.println();
    }

    private static ArrayList<Vehicle> updateReadings(ArrayList<Vehicle> vehicles) {

        int map[][] = generateMap(vehicles);

        // Sets sensor readings based on map.
        for (Vehicle vehicle: vehicles) {
            vehicle.getFrontRadar().write(50);
            vehicle.getBackSideRadar().write(10);
            vehicle.getFrontSideRadar().write(10);
            vehicle.getLidar().writeIndex(45, 11);

            // Front detecting radar.
            for (int i = vehicle.getGyro().getLongitude()+4;
                 i < (vehicle.getGyro().getLongitude()+10 > 100 ?100:vehicle.getGyro().getLongitude()+10); i++) {
                if (map[vehicle.getGyro().getLatitude()][i] == 1) {
                    vehicle.getFrontRadar().write(i-vehicle.getGyro().getLongitude());
                    break;
                }
            }

            for (int i = 0; i < 3; i++) { // Sets side radars.

                if (map[vehicle.getGyro().getLatitude() +1][(vehicle.getGyro().getLongitude() -i) < 0 ? 0: vehicle.getGyro().getLongitude() -i] == 1) {
                    vehicle.getBackSideRadar().write(4);
                    vehicle.getLidar().writeIndex(45, 5);
                }

                if (map[vehicle.getGyro().getLatitude() +1][(vehicle.getGyro().getLongitude() +i) > 100?100:vehicle.getGyro().getLongitude() +i] == 1) {
                    vehicle.getFrontSideRadar().write(4);
                    vehicle.getLidar().writeIndex(45, 5);
                }
            }
        }

        return vehicles;
    }

    private static int[][] generateMap(ArrayList<Vehicle> vehicles) {
        int map[][] = new int[4][101];

        for (int i = 0; i < 4; i++) // Set end of road border.
            map[i][100] = 1;
        for (int i = 0; i < 101; i++) // Set top side border.
            map[3][i] = 1;

        for (Vehicle vehicle: vehicles)            // Draws it on a 2d map.
            for (int i = vehicle.getGyro().getLongitude(); i < 100 && i < vehicle.getGyro().getLongitude()+3; i++)
                map[vehicle.getGyro().getLatitude()][i] = 1;

        return map;
    }
}
