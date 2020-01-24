package core;

interface RaceResult<T> extends Comparable<RaceResult<T>> {

    long elapsed();

    Contender<T> contender();

    static <T> RaceResult<T> from(long elapsed, Contender<T> contender) {
        return new RaceResult<T>() {
            @Override
            public long elapsed() {
                return elapsed;
            }

            @Override
            public Contender<T> contender() {
                return contender;
            }
        };
    }

    @Override
    default int compareTo(RaceResult<T> o) {
        return Long.compare(elapsed(), o.elapsed());
    }
}