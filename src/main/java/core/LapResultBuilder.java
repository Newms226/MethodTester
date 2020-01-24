package core;

import java.util.SortedSet;
import java.util.TreeSet;

class LapResultBuilder<T> {

    private SortedSet<Lap<T>> laps;

    private final long runID;

    LapResultBuilder(long runID) {
        this.laps = new TreeSet<>();
        this.runID = runID;
    }

    /**
     * Warning, this method DOES NOT check if the client has already
     * registered a time for the passed Contender. It does validate that
     * elapsed is not negative.
     */
    LapResultBuilder<T> add(Contender<T> contender, long elapsed) {
        if (0 > elapsed)
            throw new RuntimeException("Range cannot be negative");

        laps.add(new Lap<T>(contender, elapsed));
        return this;
    }

    LapResultBuilder<T> add(Contender<T> contender, long start, long end) {
        return add(contender, end - start);
    }

    LapResult<T> build() {
        return new LapResult<T>(laps, runID);
    }
}
