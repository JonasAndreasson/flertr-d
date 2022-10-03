package actor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
public class ActorThread<M> extends Thread {
	private LinkedBlockingQueue<M> list;
	public ActorThread(){
		list = new LinkedBlockingQueue<>();
	}
	
	
    /** Called by another thread, to send a message to this thread. */
    public void send(M message) {
        try {
			list.put(message);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    /** Returns the first message in the queue, or blocks if none available. */
    protected M receive() throws InterruptedException {
        return list.take();
    }
    
    /** Returns the first message in the queue, or blocks up to 'timeout'
        milliseconds if none available. Returns null if no message is obtained
        within 'timeout' milliseconds. */
    protected M receiveWithTimeout(long timeout) throws InterruptedException {
    	return list.poll(timeout, TimeUnit.MILLISECONDS);
    }
}