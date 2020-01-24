package core;

public class PreparedRace<T> {

    final RaceConfig<T> config;

    volatile T sampleData;

    PreparedRace(RaceConfig<T> config, T sampleData) {
        this.sampleData = sampleData;
        this.config = config;
    }
}
