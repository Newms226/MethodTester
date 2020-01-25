package core;

public class Lap<T> implements Comparable<Lap<T>> {

    final Contender<T> contender;

    final long elapsed;

    Lap(Contender<T> contender, long elapsed) {
        this.contender = contender;
        this.elapsed = elapsed;
    }

    long getElapsed() { return elapsed; }

    @Override
    public int compareTo(Lap<T> o) {
        return Long.compareUnsigned(this.elapsed, o.elapsed);
    }

    @Override
    public String toString() {
        return contender + " took " + elapsed;
    }
}
