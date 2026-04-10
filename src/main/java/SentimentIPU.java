public class SentimentIPU {
    public String processSentiment(String text) {
        // Simulate sentiment analysis
        if (text.contains("happy") || text.contains("love")) {
            return "Positive";
        } else if (text.contains("sad") || text.contains("hate")) {
            return "Negative";
        } else {
            return "Neutral";
        }
    }

    public double getConfidence() {
        // Simulate confidence level
        return Math.random();
    }
}