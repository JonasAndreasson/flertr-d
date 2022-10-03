package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

import static wash.control.WashingMessage.Order.*;

public class WashingProgram1 extends ActorThread<WashingMessage> {

    private WashingIO io;
    private ActorThread<WashingMessage> temp;
    private ActorThread<WashingMessage> water;
    private ActorThread<WashingMessage> spin;
    
    public WashingProgram1(WashingIO io,
                           ActorThread<WashingMessage> temp,
                           ActorThread<WashingMessage> water,
                           ActorThread<WashingMessage> spin) 
    {
        this.io = io;
        this.temp = temp;
        this.water = water;
        this.spin = spin;
    }
    @Override
    public void run() {
    	
    	try {
    		
    		io.lock(true);

    		water.send(new WashingMessage(this, WATER_FILL)); //FILL
    		WashingMessage ack = receive();
    		temp.send(new WashingMessage(this, TEMP_SET_40)); //40C
    		ack = receive();
    		
    		spin.send(new WashingMessage(this, SPIN_SLOW));
    		ack = receive();
    		
    		Thread.sleep(30 * 60000 / Settings.SPEEDUP); // WAIT 30 MINUTES
    		
    		temp.send(new WashingMessage(this, TEMP_IDLE));
    		water.send(new WashingMessage(this, WATER_DRAIN));
    		spin.send(new WashingMessage(this, SPIN_OFF));
    		ack = receive();
    		ack = receive();
    		ack = receive();
    		
    		for(int i = 0; i < 5; i++) {
    			water.send(new WashingMessage(this, WATER_FILL));
    			ack = receive();
    			System.out.println(ack.toString());
    			spin.send(new WashingMessage(this, SPIN_SLOW));
    			ack = receive();
    			System.out.println(ack.toString());
    			Thread.sleep(2*60000/Settings.SPEEDUP);
    			
    			spin.send(new WashingMessage(this, SPIN_OFF));
    			ack = receive();
    			System.out.println(ack.toString());
    			water.send(new WashingMessage(this, WATER_DRAIN));
    			ack = receive();
    			System.out.println(ack.toString());
    			
    		}	
    			water.send(new WashingMessage(this, WATER_IDLE));
    			ack = receive();
    			spin.send(new WashingMessage(this, SPIN_FAST));
    			ack = receive();
    			Thread.sleep(5*60000/Settings.SPEEDUP);
    			
    			spin.send(new WashingMessage(this, SPIN_OFF));
    			
    			ack = receive();
    		io.lock(false); //UNLOCK

    		
    	}catch(InterruptedException e) {
    		System.out.println(e);
    	}
    }
    
}
