import java.util.HashMap;
import java.util.Stack;

import lift.LiftView;
import lift.Passenger;

public class LiftMonitor {
	private int currentFloor, nextFloor, numInLift;
	private int[] toEnter, toExit;
	private boolean doorOpen, goingDown;
	private LiftView lv;

	public LiftMonitor(LiftView lv) {
		this.lv = lv;
		toEnter = new int[7];
		toExit = new int[7];
		numInLift = 0;
		currentFloor = 0;
		nextFloor = 1;
	}

	public synchronized void toEnter(int floor) {
		toEnter[floor]++;
		notifyAll();
	}

	public synchronized void toLeave(int floor) {
		toExit[floor]++;
		
	}

	public synchronized boolean waitToEnter(int floor, Passenger pass) {
		while (floor != currentFloor || !doorOpen || numInLift == 4) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		notifyAll();
		return true;
	}

	public synchronized boolean waitToLeave(int floor, Passenger pass) {
		while (floor != currentFloor || !doorOpen) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		notifyAll();
		return true;
	}

	public synchronized void hasEntered(int floor) {
		toEnter[floor]--;
		numInLift++;
		notifyAll();
	}

	public synchronized void hasExited(int floor) {
		toExit[floor]--;
		numInLift--;
		notifyAll();
	}

	public synchronized int getCurrent() {
		return currentFloor;
	}

	public synchronized int getNext() {
		
		return nextFloor;
	}

	public synchronized void open() {
		if (toExit[currentFloor] == 0 && toEnter[currentFloor] == 0) return;

		lv.openDoors(currentFloor);
		notifyAll();
		doorOpen = true;
	}

	public synchronized void close() {
		if (toExit[currentFloor] == 0 && toEnter[currentFloor] == 0) return;
		while (toExit[currentFloor] != 0 || (toEnter[currentFloor] != 0 && numInLift != 4)) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		doorOpen = false;
		lv.closeDoors();
	}

	public synchronized void updateCurrent(int current) {
        while (true) {
            for (int i : toEnter) {
                for (int j : toExit) {
                    if (i != 0 || j != 0) {
                        currentFloor = current;
                        return;
                    }
                }
            }
            try {
                wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

	public synchronized void pickDestination(int next) {
		
		nextFloor = next;
	}

}