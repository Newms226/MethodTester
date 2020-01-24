package core;

import java.util.*;

class LapResult<T> {

    public final SortedSet< Lap<T> > results;

    public final long runID;

    protected LapResult(SortedSet< Lap<T> > results, long runID) {
        this.results = Collections.unmodifiableSortedSet(results);
        this.runID = runID;
    }

    Contender<T> getWinner() {
        return results.first().contender;
    }

}