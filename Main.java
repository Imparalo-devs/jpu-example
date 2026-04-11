import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ChatDatabase chatDatabase = new ChatDatabase();
        SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer();
        String chat;
        while ((chat = chatDatabase.getChat()) != null) {
            sentimentAnalyzer.analyzeSentiment(chat);
        }
    }
}