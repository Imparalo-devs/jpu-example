import java.util.concurrent.Future;

public class SentimentIPUWrapper {
    private SentimentIPU sentimentIPU;

    public SentimentIPUWrapper() {
        sentimentIPU = new SentimentIPU();
    }

    public void writeInput(String key, String value) {
        sentimentIPU.writeInput(key, value);
    }

    public Future<SentimentResult> getFuture() {
        return sentimentIPU.getFuture();
    }

    public SentimentResult readOutput(String key) {
        return sentimentIPU.readOutput(key);
    }
}