package racer;

import java.util.ArrayList;
import java.util.Arrays;

import tools.FileTools;
import tools.NumberTools;

public class Judge {
		Result[] lapWinners;
		StringBuffer insightBuffer;
		int racerCount, runs;
		boolean DEEP_INSIGHT;
		String title;
		public ArrayList<Racer> racers;
		
		public Judge(String title, ArrayList<Racer> racers, int runFor, boolean deepInsight) {
			this.title = title;
			this.racers = new ArrayList<>(racers);
			this.racerCount = racers.size();
			this.DEEP_INSIGHT = deepInsight;
			this.runs = runFor;
			lapWinners = new Result[racerCount];
			if (DEEP_INSIGHT) insightBuffer = new StringBuffer();
			for (int i = 0; i < racerCount; i++) lapWinners[i] = new Result(i);
		}
		
		public void decideLapWinner(Result[] lapResults) {
			Arrays.sort(lapResults);
			lapWinners[lapResults[0].ID].outcome++;
			if (DEEP_INSIGHT) { 
				insightBuffer.append(
					racers.get(lapResults[0].ID).racerName
					+ " by " + NumberTools.percentDifferenceAsString(lapResults[0].outcome,
																    lapResults[1].outcome) 
					+ "\n   ");
				for (int r = 0; r < racerCount; r++) {
					 Racer temp = racers.get(lapResults[r].ID);
					 insightBuffer.append(temp.racerName + ": " + StopWatch.nanosecondsToString(temp.result));
					 if (r != racerCount - 1) insightBuffer.append(" / ");
				}
				insightBuffer.append("\n");
			}
		} 
		
		public void decideTotalWinner() {
			Arrays.sort(lapWinners);
		}
		
		public String getWinnerByLap() {
			if (!DEEP_INSIGHT) throw new UnsupportedOperationException("Deep insight is not enabled");
			return new StringBuffer(this.toString() + FileTools.LINE_BREAK + insightBuffer.toString()).toString();
		}
		
		public String toString() {
			StringBuffer buffer = new StringBuffer(title + FileTools.LINE_BREAK);
			for (int r = 0; r < racerCount; r++) {
				buffer.append(racers.get(lapWinners[r].ID).racerName
						+ "\n  Won: " 
							+ NumberTools.format((int)lapWinners[r].outcome) 
							+  "/" + NumberTools.format((int) runs) 
						+ " (" + NumberTools.formatPercent(
								((double) lapWinners[r].outcome)/ 
								((double) runs)) + ")\n");
			}
			return buffer.toString();
		}
		
	}