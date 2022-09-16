import java.util.HashMap;
import java.util.Stack;

import lift.LiftView;
import lift.Passenger;

public class LiftMonitor {
	private int currentFloor, nextFloor, numInLift;
	private boolean openDoor;
	private int[] toEnter, toExit;

	public LiftMonitor() {
		toEnter = new int[7];
		toExit = new int[7];
		numInLift = 0;
		nextFloor = 1;
		openDoor = false;
	}

	public synchronized void isCurrentFloor(int floor) {
		while (currentFloor != floor && !openDoor) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public synchronized void toggleDoors(LiftView v) {
		if(toEnter[currentFloor] == 0 && toExit[currentFloor] == 0) return;
		openDoor = !openDoor;
		if (openDoor) {
			v.openDoors(currentFloor);
			notifyAll();
		} else {
			while (toEnter[currentFloor] != 0 || toExit[currentFloor] != 0){
				try {
					wait();
					System.out.println(numInLift);
					if (numInLift == 4) {
						v.closeDoors();
						return;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			v.closeDoors();
		}

	}

	public synchronized void addPassenger(Passenger p, int floor) {
		toEnter[floor]++;
		isCurrentFloor(floor);
		p.enterLift();
		numInLift++;
		toEnter[floor]--;
		notifyAll();
	}

	public synchronized void removePassenger(Passenger p, int floor) {
		toExit[floor]++;
		isCurrentFloor(floor);
		p.exitLift();
		numInLift--;
		toExit[floor]--;
		notifyAll();
	}

	public void setCurrentFloor(int floor) {
		currentFloor = floor;
		System.out.println(currentFloor);
	}

	public  int getCurrentFloor() {
		return currentFloor;
	}

}
