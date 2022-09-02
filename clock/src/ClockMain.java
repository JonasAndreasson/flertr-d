import java.util.concurrent.Semaphore;

import clock.AlarmClockEmulator;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;

public class ClockMain {

	public static void main(String[] args) throws InterruptedException {
		AlarmClockEmulator emulator = new AlarmClockEmulator();

		ClockInput in = emulator.getInput();
		ClockOutput out = emulator.getOutput();
		Semaphore sem = in.getSemaphore();
		SharedData sd = new SharedData(0, 0, 0); // arbitrary time: just an example
		Semaphore sdSem = sd.getSemaphore();
		OutputThread t1 = new OutputThread(in, out, sd);
		t1.start();
		while (true) {
			sem.acquire();
			UserInput userInput = in.getUserInput();
			int choice = userInput.getChoice();
			int h = userInput.getHours();
			int m = userInput.getMinutes();
			int s = userInput.getSeconds();
			System.out.println("choice=" + choice + " h=" + h + " m=" + m + " s=" + s);
			switch (choice) {
			case 1:
				sdSem.acquire();
				sd.setTime(h,m,s);
				sdSem.release();
				break;
			case 2:
				sd.setAlarm(h,m,s);
				break;
			case 3:
				sd.toggleAlarm();
				out.setAlarmIndicator(sd.getAlarm());
				break;
			default:
				System.out.println("This shouldn't be happening?");
			}
		}
	}
}
