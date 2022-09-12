package factory.simulation;

import java.util.LinkedList;
import java.util.Stack;

import factory.model.Conveyor;
import factory.model.Tool;
import factory.model.Widget;
import train.model.Segment;

public class FactoryMonitor {
	private Tool press;
	private Tool paint;
	private Conveyor conveyor;
	private Stack<Integer> stack;

	public FactoryMonitor(Factory f) {
		conveyor = f.getConveyor();

		press = f.getPressTool();
		paint = f.getPaintTool();
		stack = new Stack<Integer>();
	}

	public void paint() {

		paint.waitFor(Widget.ORANGE_MARBLE);
		conveyorOff();
		paint.performAction();
		conveyorOn();

	}

	public void press() {

		press.waitFor(Widget.GREEN_BLOB);
		conveyorOff();
		press.performAction();
		conveyorOn();
	}

	private synchronized void conveyorOn() {
		stack.pop();
		if (stack.isEmpty()) {
			conveyor.on();
		}
	}
	private synchronized void conveyorOff() {
		stack.push(1);
		conveyor.off();
	}

}
