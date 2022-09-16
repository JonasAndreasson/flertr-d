import lift.LiftView;

public class LiftThread extends Thread {
	private LiftMonitor lm;
	private LiftView view;
	private boolean goingDown = false;
	private int NBR_FLOORS;

	public LiftThread(LiftView view, LiftMonitor lm, int NBR_FLOORS) {
		this.view = view;
		this.lm = lm;
		this.NBR_FLOORS = NBR_FLOORS-1;
	}

	public void run() {
		int currentFloor = lm.getCurrentFloor();
		int destination = currentFloor+1;
		while (true) {
			if (goingDown) {
				lm.toggleDoors(view);
				lm.toggleDoors(view);
				lm.setCurrentFloor(destination);
				view.moveLift(currentFloor, destination);
				currentFloor = destination;
				if (currentFloor == 0 || currentFloor == 6) {
					goingDown = !goingDown;
					destination++;
				} else {
					destination--;
				}
			} else {
				lm.toggleDoors(view);
				lm.toggleDoors(view);
				lm.setCurrentFloor(destination);
				view.moveLift(currentFloor, destination);
				currentFloor = destination;
				if (currentFloor == 0 || currentFloor == NBR_FLOORS) {
					goingDown = !goingDown;
					destination--;
				} else {
					destination++;
				}
			}
		}
	}
}
