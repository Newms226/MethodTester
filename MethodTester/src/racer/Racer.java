package racer;

public class Racer extends Thread {
		String racerName;
		public StopWatch timer;
		Runnable method, afterRun;
		long startLap, endLap, result;
		
		public Racer(String racerName, int runFor) {
			this(racerName, runFor, null, null);
		}
		
		public Racer(String racerName, int runFor, Runnable method){
			this(racerName, runFor, method, null);
		}
		
		public Racer(String racerName, int runFor, Runnable method, Runnable afterRun){
			this.racerName = racerName;
			this.method = method;
			timer = new StopWatch(racerName, runFor);
			this.afterRun = afterRun;
		}
		
		public void run() {
			startLap = System.nanoTime();
			method.run();
			endLap = System.nanoTime();
			if (afterRun != null) afterRun.run();
			result = timer.lap(startLap, endLap);
		}
		
		public void setResult(long elapsed) {
			this.result = elapsed;
		}
		
		public String toString() {
			return timer.toString();
		}
	}