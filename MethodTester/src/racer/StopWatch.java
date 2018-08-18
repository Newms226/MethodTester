package racer;

import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import tools.FileTools;
import tools.NumberTools;
import tools.StringTools;

@Preamble(
		author = "Michael Newman",
		date = "Thursday, May 24th, 2018",
		description = 
		          "This program acts a stopwatch. It has a method to call a lap (.lap) "
				+ "which is used to time the run time of a partiuclar method. This class"
				+ "handles all of the necessary overhead to analyize a method. It calculates"
				+ "the average run time of a method, the total time, and the standard deviation."
				+ "This methods indended use is to time a method over a certian amount of runs"
				+ "(runs) within the Race object (which enables a user to compare the run time"
				+ "of multiple methods). Note that this method is thread-safe, as the .lap method"
				+ "is synchronized. The typical method call is documented in the main method of "
				+ "this class. Note that this class is designed to function with System.nanoseconds"
				+ "but will perform the same functions with System.currentTimeMills."
		)
public class StopWatch implements Comparable<StopWatch> {
	
	
	private long totalTime;     // Total time of all the laps in the method
	private int runs,           // Amount of laps the stop watch will contain
	            runFeildLength; // Length of the maximum run. Used to form a formated table
	private double averageTime, // Average time over all of the runs
	               variance,    // The variance (squared) of all of the runs
	               standardDev; // Standard deviation
	private double[][] results; // lazy implementation. [start][end][elapsed]

	ArrayList<Lap> laps;        // Collection of all the laps present
	private String title,       // Name of the Stopwatch
				   summary;     // Summary of the title, total time, average time, & standard deviation
	
	@NestedClassPreamble(description = "Nested class which represents a singlular lap")
	class Lap implements Comparable<Lap> {
		final long START,       // Time started (meaningless if in nanotime)
		                     END,         // Time ended (meaningless if in nanotime)
		                     ELAPSED;     // Time the method took to run
		private double actualVariance,    // The actual difference from the average
		               percentDifference; // Percent difference from the average
		
		@MethodPreamble(
				description = "Default constructor. Note that the parameters must satisify start < end",
				parameters = {"'start' is the beginning time of the lap",
						       "'end' is the end of the lap"} 
				)
		private Lap(long start, long end) {
			this.START = start;
			this.END = end;
			ELAPSED = end - start;
		}
		
		@MethodPreamble(
				description = "Overrides the Object.toString method",
				returns = "Returns a String representing the eleapsed time of this lap")
		@Override
		public String toString() {
			return nanosecondsToString(ELAPSED);
		}
		
		@MethodPreamble(
				description = "Calculates the variance from the average in both actual form"
							+ "and as a percent. This method should only be called once the"
							+ "average time has been calculated. Will produce inaccurate"
							+ "results if this is not adhered to.")
		private void calculateVariance() {
			actualVariance = ELAPSED - averageTime;
			percentDifference = NumberTools.percentDifference(ELAPSED, averageTime);
		}
		
		@MethodPreamble(
				description = "Method to enable the Comparable interface",
				returns = "-1 if this < that"
						+ "1 if this > that"
						+ "0 if this = that")
		@Override
		public int compareTo(Lap that) {
			if (this.ELAPSED < that.ELAPSED) return -1;
			if (this.ELAPSED > that.ELAPSED) return 1;
			return 0;
		}
	} // end Lap class
	
	@MethodPreamble(
			description = "Default constructor",
			parameters = {"'title' is the name of this stopwatch object",
						  "'runs' is the count of laps this stopwatch will contain"}
			)
	public StopWatch(String title, int runs){
		this.title = title;
		this.runs = runs;
		laps = new ArrayList<>(runs);
		
		// Used for formating the results String:
		String temp = runs + "";
		runFeildLength = temp.length();
		temp = null;
	}
	
	@MethodPreamble(
			description = "Returns the average time of this stopwatch. Method should only be called"
						+ "once the stop watch has finished executing. If this is not adhered to,"
						+ "the method will return 0",
			returns = "the average time as a double")
	public double getAverage() {
		return averageTime;
	}
	
	@MethodPreamble(
			description = "Returns the title of this stop watch object",
			returns = "A String representing the title")
	public String getTitle() {
		return title;
	}
	
	@MethodPreamble(
			description = ".lap() is the primary mechanisim of the stop watch class. The method"
						+ "is thread safe. Method should be called after the method to time has"
						+ "been executed. Note that for this object to adequetaly time the execution"
						+ "of a method, the ONLY thing that should be between startLap and endLap "
						+ "is the methods core function. It should be absent of any initalization or"
						+ "any other background functions."
						+ ""
						+ "Example form:"
						+ ""
						+ "long startLap = System.nanoTime();"
						+ "// method runs here"
						+ "long endLap = System.nanoTime();"
						+ "stopwatchObject.lap(startLap, endLap);",
			parameters = {"'startLap' is the beginning of the lap",
						  "'endLap' is the end of the lap"},
			throwsExceptions = "Throws an Error if startLap >= endLap")
	public synchronized long lap(long startLap, long endLap) {
		if (startLap >= endLap) throw new Error("Invalid time: " + startLap + " - " + endLap);
		Lap lap = new Lap(startLap, endLap);
		laps.add(lap);
		return lap.ELAPSED;
	}
	
	public synchronized void end() {
		laps.trimToSize();
		averageTimes();
		generateVariance();
		summary = title
				+ "\n  Total Time: " + nanosecondsToString(totalTime)
				+ "\n  Average Time: " + nanosecondsToString(averageTime)
				+ "\n  Standard Deviation: " + nanosecondsToString(standardDev);
	}
	
	private void averageTimes() {
		sum();
		averageTime = ((double)totalTime) / ((double)runs);
	}
	
	private void sum() {
		totalTime = (long) laps
				.stream()
				.mapToDouble(l -> l.ELAPSED)
				.sum();
	}
	
	class VarianceCalculator {
		private double total = 0;
		private double count = 0;
		
		public void accept(Lap lap) {
			lap.calculateVariance();
			total += lap.actualVariance * lap.actualVariance;
			count++;
		}
		
		public void combine(VarianceCalculator that) {
			total += that.total;
			count += that.count;
		}
		
		public double getAverageActualVariance() {
			return count > 0 ? total/count : 0;
		}
		
	}
	
	private void generateVariance() {
		variance = laps
			.parallelStream()
			.collect(VarianceCalculator::new, 
					 VarianceCalculator::accept, 
					 VarianceCalculator::combine)
			.getAverageActualVariance();
		standardDev = Math.sqrt(variance);
	}
	
	public double[][] getResults(){
		if (results == null) {
			// only generate results once
			generateResults();
		}
		return results;
	}
	
	private void generateResults() {
		results = new double[runs][3];
		Lap temp;
		for (int i = 0; i < runs; i++) {
			temp = laps.get(i);
			results[i][0] = temp.START;
			results[i][1] = temp.END;
			results[i][2] = temp.ELAPSED;
		}
	}
	
	public double[] extractElapsedTimes() {
		return laps
			.parallelStream()
			.mapToDouble(l -> l.ELAPSED)
			.toArray();
	}
	
	public String toString() {
		return summary;
	}
	
	public String resultsAsString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(FileTools.LINE_BREAK
				  + "\n" + summary
				  + FileTools.LINE_BREAK);
		Lap temp;
		for (int i = 0; i < runs; i++) {
			temp = laps.get(i);
			buffer.append("\n" + StringTools.padOnLeft(runFeildLength, i + 1) + ": "
					+ temp.toString() + " Variance: "
					+ NumberTools.formatPercent(temp.percentDifference));
		}
		buffer.append("\n");
		return buffer.toString();
	}
	
	@Override
	public int compareTo(StopWatch that) {
		if (this.getAverage() < that.getAverage()) return -1;
		if (this.getAverage() > that.getAverage()) return 1;
		return 0;
	}
	
	public static String nanosecondsToString(long nanoseconds) {
		return nanosecondsToString((double) nanoseconds);
	}
	
	// copies Judy's Code
	public static String nanosecondsToString(double nanoseconds) {
		StringBuffer buffer = new StringBuffer();
		long seconds = (long) ((nanoseconds/nanoConversionRate)             % 60),
		     minutes = (long) ((nanoseconds/(nanoConversionRate * 60))      % 60),
			 hours   = (long) ((nanoseconds/(nanoConversionRate * 60 * 60)) % 24);
		if (seconds == 0 && minutes == 0 && hours == 0) {
			// nanoseconds
//			if (nanoseconds >= 100000000) {
//				return secondsFormat.format(nanoseconds/nanoConversionRate) + " seconds";
//			}
			buffer.append(nanosecondsFormat.format(nanoseconds) + " nanosecond");
			if (nanoseconds != 1) buffer.append("s");
		}
		else if (minutes == 0 && hours == 0) {
			// seconds
			buffer.append(secondsFormat.format(nanoseconds/nanoConversionRate) + " second");
			if (seconds != 1) buffer.append("s");
		}
		else if (hours == 0) {
			// minutes
			buffer.append(minutes + " minute");
			if (minutes != 1) buffer.append("s");
			buffer.append(" " + secondsFormat.format((nanoseconds/nanoConversionRate) - (60 * minutes)) + " second");
			if (seconds != 1) buffer.append("s");
		}
		else {
			// hours
			buffer.append(hours + " hour");
			if (hours != 0) buffer.append("s");
			buffer.append(" " + minutes + " minute");
			if (minutes != 1) buffer.append("s");
			buffer.append(" " + secondsFormat.format((nanoseconds/nanoConversionRate) - (60 * hours * minutes)) + " second");
			if (seconds != 1) buffer.append("s");
		}
		return buffer.toString();
	}
	
	public static void main(String[] args) throws InterruptedException, FileNotFoundException {
		int run = 5;
		long start, end;
		StopWatch test = new StopWatch("Test", run);
		Random random = new Random();
		for (int i = 0; i < run; i++) {
			start = System.nanoTime();
			Thread.sleep(900);
			end = System.nanoTime();
			test.lap(start, end);
		}
		test.end();
		System.out.println(test.resultsAsString());
//		
		
	}           
}
