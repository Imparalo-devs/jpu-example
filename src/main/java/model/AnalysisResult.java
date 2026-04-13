package model;

public class AnalysisResult {

    private String sentiment;
    private double confidence;

    public AnalysisResult(String sentiment, double confidence) {
        this.sentiment = sentiment;
        this.confidence = confidence;
    }

    public String getSentiment() {
        return this.sentiment;
    }

    public double getConfidence() {
        return this.confidence;
    }
}