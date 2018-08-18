

package racer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;


import tools.CollectionTools;
import tools.FileTools;
import tools.NumberTools;

public class ThreadedRace {
	private final boolean DEEP_INSIGHT = true;
	
	private ArrayList<Racer> racers;
	private String title;
	private int runFor, racerCount, run;
	private Runnable start, afterRun;
	private Judge judge;
	private Result[] lapResults;
	
	
	
	public ThreadedRace(String title, int runFor) {
		this (title, runFor, null, null);
	}
	
	public ThreadedRace(String title, int runFor, Runnable start, Runnable afterRun) {
		this.title = title;
		this.runFor = runFor;
		racers = new ArrayList<>();
		this.start = start;
		this.afterRun = afterRun;
	}
	
	public void addMethod(String title, Runnable method) {
		racers.add(new Racer(title, runFor, method, afterRun));
	}
	
	private void setup() {
		racers.trimToSize();
		racerCount = racers.size();
		lapResults = new Result[racerCount];
		judge = new Judge(title, racers, runFor, DEEP_INSIGHT);
	}
	
	public void begin() {
		setup();
		for (run = 0; run < runFor; run++) {
			System.out.println("Run #" + (run + 1));
			if (start != null) start.run();
			
			racers.parallelStream().forEach(r -> r.run());
			
			racers
				.stream()
				.forEach(t -> {
					try {
						t.join();
					} catch (InterruptedException e) {
						throw new Error(e.getMessage());
					}
				});
			
			for(int r = 0; r < racerCount; r++) {
				lapResults[r] = new Result(r, racers.get(r).result);
			}
			
			judge.decideLapWinner(lapResults);
		}
		
		racers
			.stream()
			.forEach(r -> r.timer.end());
		
		judge.decideTotalWinner();
	}
	
	public String toString() {
		return CollectionTools.collectionPrinter(
				// Header:
				title + "\n\n"
//				+ "\n  Winner: " + winner.title 
//					+ " by " + NumberTools.formatPercent(winner.wonBy) 
//					+ " (" + NumberTools.formatPercent(((double)winner.timesWon) / ((double)runFor)) 
//					+ " over " + NumberTools.format(runFor) + " runs)"
					+ judge.toString()
					+ FileTools.LINE_BREAK,
				// Collection:
				racers,
				// numbered:
				false);
	}
	
	public String getFullResults() {
		StringBuffer buffer = new StringBuffer(this.toString() + FileTools.LINE_BREAK);
		racers.stream().forEach(r -> buffer.append(r.timer.resultsAsString()));
		return buffer.toString();
	}
	
	
	
//	private Winner getWinner() {
//		System.out.println("Building results array...");
//		double[][] results = new double[runFor][racers.size()];
//		for (int i = 0; i < runFor; i++) {
//			for (int r = 0; r < racers.size(); r++) {
//				results[i][r] = racers.get(r).timer.laps.get(i).ELAPSED;
//			}
//		}
//		
//		System.out.println("Determining winner...");
//		int[] won = new int[racers.size()];
//		for (int i = 0; i < runFor; i++) {
////			int winner = 0;
////			for (int r = 1; r < racers.size(); r++) {
////				if (results[i][r] < results[i][winner]) {
////					winner = r;
////				}
////			}
//			won[0]++;
//		}
//		
//		for (int r = 0; r < racers.size(); r++) {
//			racers.get(r).setWonCount(won[r]);
//		}
//		
//		Collections.sort(racers);
//		return new Winner(racers.get(0), racers.get(1));
//		return null;
//	}
	
	public static void main(String[] args) {
//		int size, runFor;
//		if (args.length == 0) {
//			size = 100;
//			runFor = 1000;
//		} 
////			else if (args.length > 2) {
////			System.out.println("Too many command line inputs. Max 2! 0: size 1: runFor. You provided: " + args.length);
////			size = 1000000;
////			runFor = 50;
////		} else {
////			size = Integer.parseInt(args[0]);
////			runFor = Integer.parseInt(args[1]);
////		}
//		
//		Integer[] data = new Integer[size];
//		Integer[] toSort = new Integer[size];
//		Integer[] toSort2 = new Integer[size];
//		ArrayList<Integer> list = new ArrayList<>(size);
//		Runnable start = () -> {
//			Random random = new Random();
//			list.clear();
//			for (int i = 0; i < size; i++) {
//				data[i] = random.nextInt(1000000);
//				toSort[i] = toSort2[i] = data[i];
//				list.add(data[i]);
//			}
//		};
//		Runnable test1 = () -> {
//			Arrays.sort(toSort);
//		};
//		Runnable test2 = () -> {
//			Arrays.parallelSort(toSort2);
//		};
//		Runnable test3 = () -> {
//			Collections.sort(list);
//		};
//		
////		for (int i = 0; i < 10000; i++) {
//			Race test = new Race("Test", 1000, start, null);
//			test.addMethod("Regular", test1);
//			test.addMethod("Parallel", test2);
//			test.addMethod("Collection", test3);
//			test.begin();
////		}
////		System.out.println("Done.");
//		System.out.println(test.judge.getWinnerByLap());
	}
	
}