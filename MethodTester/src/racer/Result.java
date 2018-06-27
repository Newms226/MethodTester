package racer;

public class Result implements Comparable<Result> {
	int ID;
	long outcome;
	
	public Result(int ID) {
		this.ID = ID;
		outcome = 0;
	}
	
	public Result(int ID, long time){
		this.ID = ID;
		this.outcome = time;
	}

	@Override
	public int compareTo(Result that) {
		if (this.outcome < that.outcome) return -1;
		if (this.outcome > that.outcome) return 1;
		return 0;
	}
}