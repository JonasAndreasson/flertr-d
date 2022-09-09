package train.simulation;

import java.util.LinkedList;

import train.model.Route;
import train.model.Segment;
import train.view.TrainView;

public class TrainSimulation {

    public static void main(String[] args) {

        TrainView view = new TrainView();
        TrainMonitor tm = new TrainMonitor();
        TrainThread tt;
        for (int i = 0; i < 20; i++) {
        	Route route = view.loadRoute();
        	 tt = new TrainThread(route, tm);
        	 tt.start();
        }
    }
    private static class TrainThread extends Thread {
    	private LinkedList<Segment> queue;
    	private Route route;
    	private Segment head,tail;
    	private TrainMonitor tm;
    	private TrainThread(Route route,TrainMonitor tm) {
            this.route = route;
            this.queue = new LinkedList<>(); //FIFO queue kinda
            this.tm = tm;
    	}
    	public void run() {
            for (int i = 0; i < 3; i++) {
            	Segment seg = route.next();
            	queue.add(seg);
            	tm.addBusySegment(seg);
            }
    		while(true) {
    				head = route.next();
            	if(tm.isSegmentFree(head)) {
            		queue.addLast(head);
            		tail = queue.poll();
            		tail.exit();
            		tm.removeBusySegment(tail);
            	}
            }
    	}
    }
}
