package train.simulation;
import java.util.HashSet;
import java.util.Set;

import train.model.Segment;
public class TrainMonitor {
	private Set<Segment> busySegments;
	public TrainMonitor() {
		busySegments = new HashSet<>();
	}
	public void addBusySegment(Segment s) {
		
		busySegments.add(s);
		s.enter();
	}
	public synchronized void removeBusySegment(Segment s) {
		busySegments.remove(s);
		notifyAll();
	}
	public synchronized boolean isSegmentFree(Segment s) {
		while (busySegments.contains(s)){
			try {
				wait();
			} catch (InterruptedException e) {
				 //TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		addBusySegment(s);
		return true;
	}
}
