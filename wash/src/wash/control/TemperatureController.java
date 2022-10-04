package wash.control;

import actor.ActorThread;
import wash.control.WashingMessage.Order;
import wash.io.WashingIO;

public class TemperatureController extends ActorThread<WashingMessage> {

	private WashingIO io;
	private Order order = null;
	private ActorThread<WashingMessage> sender;
	private double mu10 = 0.5;
	private double ml100 = 0.1;
	private double ml200 = 0.2;
	private double sensorVar = 0.2;
	private int timer = 0;
	private int ACK = 0;

	public TemperatureController(WashingIO io) {
		this.io = io;
	}

	@Override
	public void run() {
		try {
			while (true) {
				WashingMessage m = receiveWithTimeout(10000 / Settings.SPEEDUP);
				if (m != null) {
					order = m.getOrder();
					sender = m.getSender();
					switch (order) {
					case TEMP_IDLE:
						io.heat(false);
						m.getSender().send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
						break;
					case TEMP_SET_40:
						if (io.getTemperature() < 40 - mu10 && io.getWaterLevel() > 0) {
							io.heat(true);
							timer = 200;
							ACK = 0;
						}
						break;
					case TEMP_SET_60:
						if (io.getTemperature() < 60 - mu10 && io.getWaterLevel() > 0) {
							io.heat(true);
							timer = 100;
							ACK = 0;
						}						
						break;
							default:
						break;

					}
				} else if (order != null) {
					switch (order) {
					case TEMP_IDLE:
						io.heat(false);
						break;
					case TEMP_SET_40:
						if (io.getTemperature()+sensorVar + mu10 < 40 && (io.getWaterLevel() > 0 && timer >= 200)) {
							io.heat(true);
							timer = 0;
						} else if (io.getTemperature()-(ml200+sensorVar)<38 && (io.getWaterLevel() > 0 && timer >= 200)) {
							io.heat(true);
							timer = 0;
						} else if (io.getTemperature()+sensorVar + mu10 >= 40) {
							if(ACK == 0) {
								sender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
								ACK++;
							}
							io.heat(false);
						} else {
							timer+=10;
						}
						break;
					case TEMP_SET_60:
						if (io.getTemperature()+sensorVar + mu10 < 60 && (io.getWaterLevel() > 0 && timer >= 100)) {
							io.heat(true);
							timer = 0;
						} else if (io.getTemperature()-(ml100+sensorVar)<58 && (io.getWaterLevel() > 0 && timer >= 100)) {
							io.heat(true);
							timer = 0;
						} else if (io.getTemperature()+sensorVar + mu10 >= 60) {
							if(ACK == 0) {
								sender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
								ACK++;
							}
							io.heat(false);
						} else {
							timer+=10;
						}
						break;
					default:
						if (io.getWaterLevel() < 9) {
							io.heat(false);
						}
						break;

					}
				} else {
					if (io.getWaterLevel() < 9) {
						io.heat(false);
					}
				}
			}
		} catch (InterruptedException e) {

		}
	}
}
