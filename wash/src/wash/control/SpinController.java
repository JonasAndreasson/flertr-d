package wash.control;

import actor.ActorThread;
import wash.control.WashingMessage.Order;
import wash.io.WashingIO;

public class SpinController extends ActorThread<WashingMessage> {
	
    private WashingIO io;
    int SPIN_IDLE          = 1;    // barrel not rotating
    int SPIN_LEFT          = 2;    // barrel rotating slowly, left
    int SPIN_RIGHT         = 3;    // barrel rotating slowly, right
    int SPIN_FAST          = 4;    // barrel rotating fast
    int current = 0;

    public SpinController(WashingIO io) {
        this.io = io;
    }

    @Override
    public void run() {
        try {

            while (true) {
                // wait for up to a (simulated) minute for a WashingMessage
                WashingMessage m = receiveWithTimeout(60000 / Settings.SPEEDUP);
                // if m is null, it means a minute passed and no message was received
                if (m != null) {
                    
                    
                    switch(m.getOrder()) {
                    	case SPIN_SLOW:
                    		io.setSpinMode(SPIN_LEFT);
                    		current = SPIN_LEFT;
                    		m.getSender().send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
                    		break;
                    	case SPIN_FAST:
                    		io.setSpinMode(SPIN_FAST);
                    		m.getSender().send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
                    		current = SPIN_FAST;
                    		break;
                    	case SPIN_OFF:
                    		io.setSpinMode(SPIN_IDLE);
                    		m.getSender().send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
                    		current = SPIN_IDLE;
                    		break;
                    	default:
						break;
                    	}
                } else {
                	if(current == SPIN_RIGHT) {
                		io.setSpinMode(SPIN_LEFT);
                		current = SPIN_LEFT;
                	}
                	else if (current == SPIN_LEFT) {
                		io.setSpinMode(SPIN_RIGHT);
                		current = SPIN_RIGHT;
                	} else {
                		//DO NOTHING
                	}
                }
                
            }
        } catch (InterruptedException unexpected) {
            // we don't expect this thread to be interrupted,
            // so throw an error if it happens anyway
        	io.setSpinMode(SPIN_IDLE);
            throw new Error(unexpected);
        }
    }
}
