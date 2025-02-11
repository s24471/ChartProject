public class TradeRecord {
    private final String symbol;
    private final double volume;
    private final double openPrice;
    private final long order;

    public TradeRecord(String symbol, double volume, double openPrice, long order) {
        this.symbol = symbol;
        this.volume = volume;
        this.openPrice = openPrice;
        this.order = order;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getVolume() {
        return volume;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public long getOrder() {
        return order;
    }
}
