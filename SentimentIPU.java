public class SentimentIPU {
    public SentimentResult processSentiment(String text) {
        // Simulate sentiment analysis
        if (text.contains("happy") || text.contains("excited")) {
            return new SentimentResult("Positive", 0.8);
        } else if (text.contains("sad") || text.contains("depressed")) {
            return new SentimentResult("Negative", 0.8);
        } else if (text.contains("hungry")) {
            return new SentimentResult("Neutral", 0.5);
        } else {
            return new SentimentResult("Unknown", 0.0);
        }
    }

    public static class SentimentResult {
        private String sentiment;
        private double confidence;

        public SentimentResult(String sentiment, double confidence) {
            this.sentiment = sentiment;
            this.confidence = confidence;
        }

        public String getSentiment() {
            return sentiment;
        }

        public double getConfidence() {
            return confidence;
        }
    }
}