package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;
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
					io.drain(true);

				case WATER_IDLE:
					io.drain(false);
					io.fill(false);

				case WATER_FILL:
					io.drain(false);
					io.fill(true);

				default:

				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
