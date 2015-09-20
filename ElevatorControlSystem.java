import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ElevatorControlSystem {
    // If this were a map from elevatorId to Elevator, you could have elevators go out of service without disrupting the system
    static final int UNNECESSARY = Integer.MIN_VALUE;

    private final Elevator[] elevators;

    public Map<Integer, Request> getRequestMap() {
        return requestMap;
    }

    private final Map<Integer, Request> requestMap;

    public ElevatorControlSystem(Elevator[] elevators) {
        this.elevators = elevators;
        this.requestMap = new HashMap<Integer, Request>();
    }

    public void step() {
        for (int i = 0; i < elevators.length; i ++) {
            elevators[i].move();
        }
        if (!requestMap.isEmpty()) {
            delegate();
        }
    }

    public void status() {
        System.out.printf("There are %d unassigned requests.\n", requestMap.size());
        for (int i = 0; i < elevators.length; i ++) {
            elevators[i].status();
        }
    }

    void addInternalRequest(int elevatorId, int targetFloor) {
        elevators[elevatorId].addInternalRequest(new Request(Elevator.UNDEFINED, targetFloor));
    }

    void addExternalRequest(int direction, int requestFloor) {
        Request request = new Request(direction, requestFloor);
        requestMap.put(request.getRequestId(), request);
        delegate();
    }

    void delegate() {
        Set<Map.Entry<Integer, Request>> set = new HashMap<Integer, Request>(requestMap).entrySet();
        for (Map.Entry<Integer, Request> entry : set) {
            Request request = entry.getValue();
            boolean flag = false;
            int i;
            int diff = Integer.MAX_VALUE;
            int diffReqId = UNNECESSARY;
            for (i = 0; i < elevators.length; i ++) {
                if (request.getTargetFloor() == elevators[i].getCurrentFloor() && elevators[i].getDirection() == Elevator.STILL) {
                    System.out.printf("Request %d has been added to elevator %d.\n", entry.getKey(), i);
                    elevators[i].addExternalRequest(request);
                    flag = true;
                    requestMap.remove(request.getRequestId());
                }
                if (flag) {
                    break;
                }
                if (((request.getTargetFloor() - elevators[i].getCurrentFloor()) >= 0 && (request.getTargetFloor() - elevators[i].getCurrentFloor()) < diff &&
                        (elevators[i].getDirection() == Elevator.STILL ||
                                (elevators[i].getDirection() == request.getDirection() && elevators[i].getDirection() == Elevator.UP))) ||
                        ((elevators[i].getCurrentFloor() - request.getTargetFloor()) >= 0 && (elevators[i].getCurrentFloor() - request.getTargetFloor()) < diff &&
                            (elevators[i].getDirection() == Elevator.STILL ||
                                    (elevators[i].getDirection() == request.getDirection() && elevators[i].getDirection() == Elevator.DOWN)))) {
                    diff = Math.abs(request.getTargetFloor() - elevators[i].getCurrentFloor());
                    diffReqId = i;
                }
            }
            if (flag) {
                continue;
            }
            if (diffReqId == UNNECESSARY) {
                System.out.println("Everyone is in the other direction. Please wait till we find a suitable match.");
                // Do nothing
            } else {
                elevators[diffReqId].addExternalRequest(request);
                requestMap.remove(request.getRequestId());
            }
        }
    }
}
