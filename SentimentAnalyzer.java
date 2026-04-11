import vcpu.SentimentIPU;

public class SentimentAnalyzer {
    private SentimentIPU sentimentIPU;

    public SentimentAnalyzer() {
        this.sentimentIPU = new SentimentIPU();
    }

    public void analyzeSentiment(String chat) {
        sentimentIPU.writeInput("chatInput", chat);
        sentimentIPU.runner();
        while (!sentimentIPU.getStatus().equals("OK")) {
            // wait for the sentimentIPU to finish processing
        }
        String sentiment = sentimentIPU.readOutput("sentiment");
        String confidence = sentimentIPU.readOutput("confidence");
        System.out.println("Sentiment: " + sentiment);
        System.out.println("Confidence: " + confidence);
    }
}