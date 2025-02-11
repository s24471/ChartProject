public class RateInfo {
        double high;
        double vol;
        double low;
        long ctm;
        String ctmString;
        double close;
        double open;

    public RateInfo(double high, double vol, double low, long ctm, String ctmString, double close, double open) {
        this.high = high+open;
        this.vol = vol;
        this.low = low+open;
        this.ctm = ctm;
        this.ctmString = ctmString;
        this.close = close+open;
        this.open = open;
    }

}

