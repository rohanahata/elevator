import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Elevator {
    static final int STILL = 0;
    static final int UP = 1;
    static final int DOWN = -1;
    static final int UNDEFINED = Integer.MIN_VALUE;

    private final int elevatorID;
    private int currentFloor;
    private int currentDestination;
    private int direction; // 0 if still. 1 to go up. -1 to go down.

    public Map<Integer, Request> getRequestMap() {
        return requestMap;
    }

    Map<Integer, Request> requestMap; // This is map from requestId to the Request. Used instead of a list so that deletion of requests is constant time.

    public Elevator(int elevatorID) {
        this.elevatorID = elevatorID;
        this.currentFloor = 0;
        this.currentDestination = UNDEFINED;
        this.direction = STILL;
        this.requestMap = new HashMap<Integer, Request>();
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public int getCurrentDestination() {
        return currentDestination;
    }

    public int getDirection() {
        return direction;
    }

    void addInternalRequest(Request request) {
        requestMap.put(request.getRequestId(), request);
        resolve();
    }

    void addExternalRequest(Request request) {
        requestMap.put(request.getRequestId(), request);
        reroute();
    }

    void reroute() {
        int flag = -1;
        Set<Map.Entry<Integer, Request>> set = new HashSet<Map.Entry<Integer, Request>>(requestMap.entrySet());
        for (Map.Entry<Integer, Request> entry : set) {
            Request request = entry.getValue();
            if (currentDestination == UNDEFINED && direction == STILL) {
                currentDestination = request.getTargetFloor();
                if (currentDestination == currentFloor) {
                    currentDestination = UNDEFINED;
                    direction = request.getDirection();
                    System.out.println("Resolved request id : " + request.getRequestId());
                    requestMap.remove(entry.getKey());
                } else {
                    direction = (currentDestination - currentFloor) / Math.abs(currentDestination - currentFloor);
                }
            } else if (currentDestination == UNDEFINED) {
                if (direction == UP && request.getDirection() == UP && request.getTargetFloor() - currentFloor > 0) {
                    currentDestination = request.getTargetFloor();
                }
                if (direction == DOWN && request.getDirection() == DOWN && request.getTargetFloor() - currentFloor < 0) {
                    currentDestination = request.getTargetFloor();
                }
            } else {
                if (direction == UP && (request.getDirection() == UP || request.getDirection() == UNDEFINED)) {
                    if (currentDestination > request.getTargetFloor()) {
                        currentDestination = request.getTargetFloor();
                        flag = entry.getKey();
                    }
                } else if (direction == DOWN && (request.getDirection() == DOWN || request.getDirection() == UNDEFINED)) {
                    if (currentDestination < request.getTargetFloor()) {
                        currentDestination = request.getTargetFloor();
                        flag = entry.getKey();
                    }
                }
            }
        }
        if (currentDestination == currentFloor && flag != -1) {
            System.out.println("Resolved request id : " + flag);
            currentDestination = UNDEFINED;
            if (!requestMap.isEmpty()) {
                reroute();
            } else {
                direction = requestMap.get(flag).getDirection();
            }
            requestMap.remove(flag);
        }
    }

    void move() {
        if (direction == STILL) {
            System.out.printf("Elevator %d is standing still.\n", elevatorID);
            return;
        }
        if (currentDestination != UNDEFINED && direction == UP) {
            currentFloor ++;
        } else if (currentDestination != UNDEFINED && direction == DOWN) {
            currentFloor --;
        }
        resolve();
        reroute();
    }

    void status() {
        System.out.printf("Elevator: %d is moving in dir %d and is on floor %d and is moving toward %d. There are %d requests in the map.\n", elevatorID, direction, currentFloor, currentDestination, requestMap.size());
    }

    // For in-elevator requests
    void resolve() {
        Map<Integer, Request> copyMap = new HashMap<Integer, Request>(requestMap);
        Set<Map.Entry<Integer, Request>> set = copyMap.entrySet();
        for (Map.Entry<Integer, Request> entry : set) {
            Request request = entry.getValue();
            if (request.getTargetFloor() == currentFloor) {
                System.out.println("Resolved request id : " + request.getRequestId());
                requestMap.remove(request.getRequestId());
            }
        }
        if (currentFloor == currentDestination) {
            currentDestination = UNDEFINED;
        }
        determineWhereNext();
    }

    void determineWhereNext() {
        if (requestMap.isEmpty() && currentDestination == UNDEFINED) {
            direction = STILL;
        } else if (currentDestination == UNDEFINED) {
            Request getBest = getOptimum();
            if (getBest == null) {
                System.out.println("Nothing in same dir apparently");
                return;
            }
            currentDestination = getBest.getTargetFloor();
            if (currentDestination > currentFloor) {
                direction = UP;
            } else if (currentDestination < currentFloor) {
                direction = DOWN;
            } else {
                System.out.println("You have been banished to the fiery depths of hell.");
            }
        }
    }

    Request getOptimum() {
        int minDiff = Integer.MAX_VALUE;
        int minDiffReqId = 0;
        for (Map.Entry<Integer, Request> entry : requestMap.entrySet()) {
            Request request = entry.getValue();
            if ((direction == UP || direction == STILL) && request.getTargetFloor() > currentFloor) {
                if ((request.getTargetFloor() - currentFloor) < minDiff) {
                    minDiff = request.getTargetFloor() - currentFloor;
                    minDiffReqId = request.getRequestId();
                }
            }
            if ((direction == DOWN || direction == STILL) && request.getTargetFloor() < currentFloor) {
                if ((currentFloor - request.getTargetFloor()) < minDiff) {
                    minDiff = currentFloor - request.getTargetFloor();
                    minDiffReqId = request.getRequestId();
                }
            }
        }
        if (minDiff == Integer.MAX_VALUE) {
            return null;
        }
        return requestMap.get(minDiffReqId);
    }
}
