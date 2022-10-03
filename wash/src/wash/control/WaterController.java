package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;
import wash.control.WashingMessage.Order;
import wash.control.WashingMessage.Order.*;

public class WaterController extends ActorThread<WashingMessage> {

	// TODO: add attributes
	WashingIO io;

	public WaterController(WashingIO io) {
		// TODO
		this.io = io;

	}

	@Override
	public void run() {
		// TODO
		while (true) {
			try {
				WashingMessage op = receive();
				switch (op.getOrder()) {
				case WATER_DRAIN:
					io.fill(false);
					while (io.getWaterLevel() > 0) {
						io.drain(true);
					}
					io.drain(false);
					op.getSender().send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
					break;
				case WATER_IDLE:
					io.drain(true);
					io.fill(false);
					op.getSender().send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
					break;
				case WATER_FILL:
					io.drain(false);
					while (io.getWaterLevel() < 10) {
						io.fill(true);
					}
					io.fill(false);
					op.getSender().send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
					break;
				default:

				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
