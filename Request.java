public class Request {

    private static int REQUEST_COUNTER = 0;
    private final int requestId;
    private final int direction; // Can only be 1 for up or -1 for down, -int.max for undefined
    private final int targetFloor; // Can only be exercised if and only if the request came from inside the elevator

    public Request(int direction, int targetFloor) {
        this.requestId = REQUEST_COUNTER ++;
        this.direction = direction;
        this.targetFloor = targetFloor;
    }

    public int getRequestId() {
        return requestId;
    }

    public int getDirection() {
        return direction;
    }

    public int getTargetFloor() {
        return targetFloor;
    }
}
