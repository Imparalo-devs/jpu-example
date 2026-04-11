import java.util.ArrayList;
import vcpu.SentimentIPU;
import java.util.List;

public class SentimentAnalyzer {
    private ChatDatabase chatDatabase;

    public SentimentAnalyzer() {
        this.chatDatabase = new ChatDatabase();
    }

    public void analyzeSentiments() {
        String currentChat;
        while ((currentChat = chatDatabase.getChat()) != null) {
            SentimentIPU sentimentIPU = new SentimentIPU();
            sentimentIPU.writeInput("chatInput", currentChat);
            sentimentIPU.runner();
            while (!sentimentIPU.getStatus().equals("OK")) {
                // wait for results
            }
            String resultSentiment = sentimentIPU.readOutput("sentiment");
            String resultConfidence = sentimentIPU.readOutput("confidence");
            System.out.println("Result Sentiment: " + resultSentiment);
            System.out.println("Result Confidence: " + resultConfidence);
        }
    }

    public static void main(String[] args) {
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
        sentimentAnalyzer.analyzeSentiments();
    }
}