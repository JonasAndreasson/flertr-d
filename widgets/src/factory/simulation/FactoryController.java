package factory.simulation;

import factory.model.Conveyor;
import factory.model.Tool;
import factory.model.Widget;

public class FactoryController {

	public static void main(String[] args) {
		Factory factory = new Factory();
		FactoryMonitor fm = new FactoryMonitor(factory);
		Thread t1 = new FactoryThread(fm);
		t1.start();
		while (true) {
		
			fm.press();
		}
	}

	private static class FactoryThread extends Thread {
		private FactoryMonitor fm;

		private FactoryThread(FactoryMonitor fm) {
			this.fm = fm;
		}

		public void run() {
			while(true) {
				fm.paint();
			}
		}
	}
}
