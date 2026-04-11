import vcpu.sentiment_ipu;

public class SentimentAnalyzer {
    public void analyzeSentiment(String chat) {
        sentiment_ipu sentimentIPU = new sentiment_ipu();
        sentimentIPU.writeInput("chatInput", chat);
        sentimentIPU.runner();
        while (!sentimentIPU.getStatus().equals("OK")) {
            // wait for the sentimentIPU to finish processing
        }
        String resultSentiment = sentimentIPU.readOutput("sentiment");
        String resultConfidence = sentimentIPU.readOutput("confidence");
        System.out.println("Result Sentiment: " + resultSentiment);
        System.out.println("Result Confidence: " + resultConfidence);
    }
}