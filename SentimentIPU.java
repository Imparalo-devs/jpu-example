public class SentimentIPU {
    public String processSentiment(String text) {
        // Simulate sentiment analysis
        if (text.contains("happy") || text.contains("love")) {
            return "Positive";
        } else if (text.contains("hungry") || text.contains("tired")) {
            return "Neutral";
        } else if (text.contains("worst") || text.contains("sad")) {
            return "Negative";
        } else {
            return "Unknown";
        }
    }

    public double getResultConfidence() {
        // Simulate confidence level
        return Math.random();
    }
}