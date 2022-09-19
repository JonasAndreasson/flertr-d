import lift.LiftView;

public class LiftThread extends Thread {
	private LiftMonitor lm;
	private LiftView view;
	private boolean goingDown = false;
	private int NBR_FLOORS, current, next;

	public LiftThread(LiftView view, LiftMonitor lm, int NBR_FLOORS) {
		this.view = view;
		this.lm = lm;
		this.NBR_FLOORS = NBR_FLOORS - 1;
	}

	public void run() {
		lm.updateCurrent(0); //ensure it doesn't ignore passengers if they start on ground floor
		lm.open();
		lm.close();
		while (true) {
			
			current = lm.getCurrent();
			
			lm.updateCurrent(lm.getNext());
			view.moveLift(current, lm.getNext());
			lm.open();
			lm.close();
			current = lm.getCurrent();
			if (current == 0 || current == NBR_FLOORS) {
				goingDown = !goingDown;
			}
			if (goingDown) {
				next = current - 1;
			} else {
				next = current + 1;
			}
			lm.pickDestination(next);
		}
	}
}