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
            System.out.println("Set Car #" + (i+1) + "'s position");
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
            //updateReadings(vehicles);
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

            System.out.println("#");
        }


        for (int i = 0; i < range; i++) {
            System.out.print("####");
        }
        System.out.println();
    }

    /*
    static void updateReadings(ArrayList<Vehicle> vehicles) {
        for (int i = 0; i < vehicles.size(); i++) {
            Gyro gyro = vehicles.get(i).gyro;
            for (Vehicle vehicle2: vehicles) {
                if (vehicle2.gyro.longitude > gyro) {

                }
            }
        }
    }
    */
}
