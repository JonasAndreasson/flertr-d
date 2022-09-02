import java.util.concurrent.Semaphore;

import clock.io.ClockInput;
import clock.io.ClockOutput;

public class OutputThread extends Thread {
	private ClockOutput out;
	private ClockInput in;
	private SharedData sd;
	private Semaphore sem;

	public OutputThread(ClockInput in, ClockOutput out, SharedData sd) {
		super();
		this.out = out;
		this.in = in;
		this.sd = sd;
		sem = sd.getSemaphore();
		sd.getHour();
		sd.getMinute();
		sd.getSecond();
	}

	public void run() {
		int h, m, s;
		while (true) {
			try {
				sem.acquire();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			long t0 = System.currentTimeMillis();
			h = sd.getHour();
			m = sd.getMinute();
			s = sd.getSecond();
			if (h == sd.getAlarmHour() && m == sd.getAlarmMinute() && s >= sd.getAlarmSecond()
					&& s <= sd.getAlarmSecond() + 20 && sd.getAlarm()) {
				out.alarm();
			}
			out.displayTime(h, m, s);
			s++;
			if (s == 60) {
				s = 0;
				m++;
				if (m == 60) {
					m = 0;
					h++;
					if (h == 24) {
						h = 0;
					}
				}
			}
			sd.setTime(h, m, s);
			sem.release();
			try {
				long now = System.currentTimeMillis();
				long t = now - t0;
				this.sleep(1000 - t);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
