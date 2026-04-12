package com.example.sentiment;

/**
 * Simple POJO that holds the sentiment and confidence values
 * returned by the {@code SentimentIPU}.
 */
public class Result {

    private final String sentiment;
    private final String confidence;

    public Result(String sentiment, String confidence) {
        this.sentiment = sentiment;
        this.confidence = confidence;
    }

    public String getSentiment() {
        return sentiment;
    }

    public String getConfidence() {
        return confidence;
    }

    @Override
    public String toString() {
        return "Result{" +
                "sentiment='" + sentiment + '\'' +
                ", confidence='" + confidence + '\'' +
                '}';
    }
}