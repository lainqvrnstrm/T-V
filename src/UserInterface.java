import java.util.ArrayList;
import java.util.Scanner;

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
            vehicle.gyro.latitude = scanner.nextInt();
            vehicle.gyro.longitude = scanner.nextInt();
            vehicle.speed = scanner.nextInt();
            vehicles.add(vehicle);
            render(vehicles);
        }

        System.out.println("Commands:\nmove -> MoveForward\nchange -> ChangeLane\n");

        while (true) {
            // get the age as an int
            String command = scanner.nextLine();
            System.out.println();

            switch (command) {
                case "move":
                    vehicles.get(0).moveForward();
                    break;
                case "change":
                    vehicles.get(0).changeLane();
                    break;
                default:
                    continue;
            }


            // Move all the other cars forward.
            for (int i = 1; i < vehicles.size(); i++) {
                vehicles.get(i).moveForward();
            }
            render(vehicles);
            updateReadings(vehicles);
        }
    }

    static void render(ArrayList<Vehicle> vehicles) {
        int lanes = 3, range = 20;
        int map[][] = new int[lanes][range+1];

        for (Vehicle vehicle: vehicles) {
            Gyro gyro = vehicle.gyro;
            map[gyro.latitude][gyro.longitude/(100/range)]++;
        }

        for (int i = 0; i < range; i++) {
            System.out.print("####");
        }
        System.out.println();

        // Prints out the cars.
        for (int i = lanes-1; i >= 0; i--) {
            for (int j = 0; j < range; j++) {
                // Only allow one car at one place.
                if (map[i][j] > 1) {
                    System.out.println("Several cars at the same position.");
                    System.exit(1);
                }

                if (map[i][j] == 1) {
                    System.out.print("ō͡≡o");
                } else {
                    System.out.print("        ");
                }
            }
            System.out.print("\n");
        }

        for (int i = 0; i < range; i++) {
            System.out.print("####");
        }

        System.out.println();
    }

    static void updateReadings(ArrayList<Vehicle> vehicles) {
        int map[][] = new int[4][101];

        for (int i = 0; i < 4; i++)
            map[i][100] = 1;
        for (int i = 0; i < 101; i++)
            map[3][i] = 1;

        for (Vehicle vehicle: vehicles) {
            // Draws it on a 2d map.
            for (int i = vehicle.gyro.longitude; i < 100 && i > vehicle.gyro.longitude+3; i++) {
                map[vehicle.gyro.latitude][i] = 1;
            }
        }

        // Sets sensor readings based on map.
        for (Vehicle vehicle: vehicles) {
            vehicle.frontRadar.write(50);
            vehicle.backSideRadar.write(10);
            vehicle.frontSideRadar.write(10);

            // Front detecting radar.
            for (int i = vehicle.gyro.longitude+4; i < 101; i++) {
                if (map[vehicle.gyro.latitude][i] == 1) {
                    vehicle.frontRadar.write(i-vehicle.gyro.longitude);
                    break;
                }
            }

            if (map[vehicle.gyro.latitude +1][vehicle.gyro.longitude] == 1) {
                vehicle.backSideRadar.write(4);
                vehicle.frontSideRadar.write(4);
            }

            System.out.println("Frontradar: " + vehicle.frontRadar.read());
            System.out.println("backSide: " + vehicle.backSideRadar.read());
            System.out.println("FrontSide: " + vehicle.frontSideRadar.read());


        }
    }
}
