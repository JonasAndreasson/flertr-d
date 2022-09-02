import java.util.concurrent.Semaphore;

public class SharedData {
	private int h,m,s, alarmH,alarmM,alarmS;
	private Semaphore sem;
	private boolean alarm;
	public SharedData(int h, int m, int s) {
		this.h = h;
		this.m = m;
		this.s = s;
		sem = new Semaphore(1);
		alarm = false;
	}
	public int getHour() {
		return h;
	}
	public int getMinute() {
		return m;
	}
	public int getSecond() {
		return s;
	}
	public void setTime(int h, int m, int s) {
		this.h = h;
		this.m = m;
		this.s = s;
	}
	public boolean getAlarm() {
		return alarm;
	}
	public void toggleAlarm() {
		alarm = !alarm;
	}
	public void setAlarm(int h,int m, int s) {
		alarmH = h;
		alarmM = m;
		alarmS = s;
	}
	public int getAlarmHour(){
		return alarmH;
	}
	public int getAlarmMinute(){
		return alarmM;
	}
	public int getAlarmSecond(){
		return alarmS;
	}
	public Semaphore getSemaphore() {
		return sem;
	}
}
