import org.junit.Assert;
import org.junit.Test;

import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Simulator {
    public static void main(String [] args) {
        Scanner sn = new Scanner(new InputStreamReader(System.in));
        System.out.println("Enter the number of elevators : ");
        int number = sn.nextInt();
        Elevator[] elevators = new Elevator[number];
        for (int i = 0; i < number; i++) {
            elevators[i] = new Elevator(i);
        }
        ElevatorControlSystem elevatorControlSystem = new ElevatorControlSystem(elevators);
        String cmd = "";
        do {
            System.out.println("Please enter your command");
            cmd = sn.nextLine().toLowerCase();
            if (cmd.equals("step")) {
                elevatorControlSystem.step();
            } else if (cmd.equals("status")) {
                elevatorControlSystem.status();
            } else if (cmd.matches("go\\s\\d+\\s\\d+")) { // Simulates someone inside an elevator who requests to go to a specific floor. Input Format : "go 1 5" indicates someone in elevator 1 wants to go to the fifth floor
                Pattern p = Pattern.compile("\\d+");
                Matcher m = p.matcher(cmd);
                m.find();
                int elevatorId = Integer.parseInt(m.group());
                m.find();
                int floor = Integer.parseInt(m.group());
                System.out.printf("Request is to go on elev %d to floor %d\n", elevatorId, floor);
                elevatorControlSystem.addInternalRequest(elevatorId, floor);
            } else if (cmd.matches("pick\\s\\d+\\s(up|down)")) { // Simulates someone outside an elevator who requests to go up or down. Input Format : "pick 4 up" indicates someone wants to go up from the fourth floor
                Pattern p = Pattern.compile("\\d+");
                Matcher m = p.matcher(cmd);
                m.find();
                int requestFloor = Integer.parseInt(m.group());
                p = Pattern.compile("(up|down)");
                m = p.matcher(cmd);
                m.find();
                int dir = m.group().equals("up") ? Elevator.UP : Elevator.DOWN;
                System.out.printf("Request is to pick from %d to go in dir %d\n", requestFloor, dir);
                elevatorControlSystem.addExternalRequest(dir, requestFloor);
            }
        } while(!cmd.equals("exit"));
    }

    @Test
    public void testTwoElevators() {
        Elevator [] elevators = new Elevator[2];
        elevators[0] = new Elevator(0);
        elevators[1] = new Elevator(1);
        ElevatorControlSystem elevatorControlSystem = new ElevatorControlSystem(elevators);
        elevatorControlSystem.addExternalRequest(1, 5);
        elevatorControlSystem.step();
        elevatorControlSystem.step();
        elevatorControlSystem.addExternalRequest(-1, 3);
        Assert.assertEquals(1, elevators[1].getRequestMap().size());
    }

    @Test
    public void testTwoElevatorsComplex() {
        Elevator [] elevators = new Elevator[2];
        elevators[0] = new Elevator(0);
        elevators[1] = new Elevator(1);
        ElevatorControlSystem elevatorControlSystem = new ElevatorControlSystem(elevators);
        elevatorControlSystem.addExternalRequest(1, 5);
        elevatorControlSystem.step();
        elevatorControlSystem.step();
        elevatorControlSystem.addExternalRequest(1, 4);
        elevatorControlSystem.step();
        elevatorControlSystem.addExternalRequest(-1, 3);
        Assert.assertEquals(4, elevators[0].getCurrentDestination());
        Assert.assertEquals(2, elevators[0].getRequestMap().size());
    }

    @Test
    public void testUnassignedRequest() {
        Elevator [] elevators = new Elevator[2];
        elevators[0] = new Elevator(0);
        elevators[1] = new Elevator(1);
        ElevatorControlSystem elevatorControlSystem = new ElevatorControlSystem(elevators);
        elevatorControlSystem.addExternalRequest(1, 1);
        elevatorControlSystem.step();
        elevatorControlSystem.addInternalRequest(1, 10);
        elevatorControlSystem.step();
        elevatorControlSystem.step();
        elevatorControlSystem.step();
        elevatorControlSystem.step();
        elevatorControlSystem.addExternalRequest(-1, 5);
        elevatorControlSystem.step();
        elevatorControlSystem.step();
        elevatorControlSystem.addExternalRequest(-1, 2);
        // Unassigned Request
        Assert.assertEquals(1, elevatorControlSystem.getRequestMap().size());
        elevatorControlSystem.step();
        elevatorControlSystem.step();
        Assert.assertEquals(2, elevators[0].getCurrentDestination());
    }
}
