
import lift.LiftView;
import lift.Passenger;

public class OnePersonRidesLift {

	public static void main(String[] args) {

		final int NBR_FLOORS = 7, MAX_PASSENGERS = 4;

		LiftView view = new LiftView(NBR_FLOORS, MAX_PASSENGERS);
		LiftMonitor lm = new LiftMonitor(view);
		for (int i = 0; i < 20; i++) {
			Passenger pass = view.createPassenger();
			PassengerThread pt = new PassengerThread(pass, lm);
			pt.start();
		}
		LiftThread lift = new LiftThread(view, lm, NBR_FLOORS);
		lift.start();
	}
}