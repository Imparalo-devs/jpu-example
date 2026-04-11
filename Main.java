import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ChatDatabase chatDatabase = new ChatDatabase();
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();

        while (true) {
            String chat = chatDatabase.getChat();
            if (chat == null) {
                break;
            }

            sentimentAnalyzer.analyzeSentiment(chat);
        }
    }
}