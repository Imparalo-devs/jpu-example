public class SentimentProcessor {
    public SentimentResult process(String chat) {
        // Simple sentiment analysis algorithm
        if (chat.contains("happy") || chat.contains("love")) {
            return new SentimentResult("Positive", 0.8);
        } else if (chat.contains("sad") || chat.contains("lonely")) {
            return new SentimentResult("Negative", 0.7);
        } else if (chat.contains("angry")) {
            return new SentimentResult("Negative", 0.9);
        } else if (chat.contains("grateful")) {
            return new SentimentResult("Positive", 0.6);
        } else if (chat.contains("bored")) {
            return new SentimentResult("Neutral", 0.5);
        } else if (chat.contains("excited")) {
            return new SentimentResult("Positive", 0.9);
        } else if (chat.contains("anxious")) {
            return new SentimentResult("Negative", 0.8);
        } else {
            return new SentimentResult("Neutral", 0.5);
        }
    }
}