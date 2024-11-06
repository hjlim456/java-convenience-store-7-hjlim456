package store.domain;

import java.time.LocalDate;

public class Promotion {
    private final static String LINE_SPLIT_SEPARATOR = ",";

    private final String name;
    private final int buy;
    private final int free;
    private final LocalDate startDate;
    private final LocalDate endDate;

    public Promotion(final String name, final int buy, final int free,
                     final LocalDate startDate, final LocalDate endDate) {
        this.name = name;
        this.buy = buy;
        this.free = free;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getName() {
        return name;
    }

    public int getBuy() {
        return buy;
    }

    public int getFree() {
        return free;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public static Promotion create(String line) {
        String[] parts = line.split(LINE_SPLIT_SEPARATOR);
        String name = parts[0];
        int buy = Integer.parseInt(parts[1]);
        int free = Integer.parseInt(parts[2]);
        LocalDate startDate = LocalDate.parse(parts[3]);
        LocalDate endDate = LocalDate.parse(parts[4]);

        return new Promotion(name, buy, free, startDate, endDate);
    }
}
