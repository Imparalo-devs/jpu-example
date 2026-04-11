import java.util.ArrayList;
import java.util.List;

public class SentimentAnalyzer {
    private ChatDatabase chatDatabase;
    private SentimentIPU sentimentIPU;

    public SentimentAnalyzer(ChatDatabase chatDatabase) {
        this.chatDatabase = chatDatabase;
        this.sentimentIPU = new SentimentIPU();
    }

    public void analyzeSentiment() {
        String chat = this.chatDatabase.getChat();
        while (chat != null) {
            SentimentIPU.SentimentResult result = this.sentimentIPU.processSentiment(chat);
            System.out.println("Sentiment: " + result.getSentiment() + ", Confidence: " + result.getConfidence());
            chat = this.chatDatabase.getChat();
        }
    }
}