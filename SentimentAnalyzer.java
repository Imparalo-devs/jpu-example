import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

public class SentimentAnalyzer {
    private ChatDatabase chatDatabase;
    private SentimentIPU sentimentIPU;

    public SentimentAnalyzer() {
        this.chatDatabase = new ChatDatabase();
        this.sentimentIPU = new SentimentIPU();
    }

    public void start() throws InterruptedException, ExecutionException {
        while (true) {
            String currentChat = chatDatabase.getChat();
            if (currentChat == null) {
                break;
            }

            sentimentIPU.writeInput("chatInput", currentChat);
            Future<String> sentimentFuture = sentimentIPU.readOutput("sentiment");
            Future<String> confidenceFuture = sentimentIPU.readOutput("confidence");

            String resultSentiment = sentimentFuture.get();
            String resultConfidence = confidenceFuture.get();

            System.out.println("Result Sentiment: " + resultSentiment);
            System.out.println("Result Confidence: " + resultConfidence);
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
        sentimentAnalyzer.start();
    }
}