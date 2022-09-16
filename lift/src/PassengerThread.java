import lift.Passenger;

public class PassengerThread extends Thread {
	private Passenger pass;
	private int source, dest;
	private LiftMonitor lm;

	public PassengerThread(Passenger passenger, LiftMonitor lm) {
		this.lm = lm;
		pass = passenger;
		source = pass.getStartFloor();
		dest = pass.getDestinationFloor();
	}

	public void run() {
		pass.begin();
		lm.addPassenger(pass, source);
		lm.removePassenger(pass, dest);
		pass.end();

	}
}
