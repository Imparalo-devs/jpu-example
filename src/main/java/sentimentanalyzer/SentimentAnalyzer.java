import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import vcpu.SentimentIPU;

public class SentimentAnalyzer {

    public static void main(String[] args) {
        ChatDatabase chatDatabase = new ChatDatabase();
        List<String> chats = chatDatabase.getChats();

        while (chats != null && !chats.isEmpty()) {
            String currentChat = chats.remove(0);
            SentimentIPU sentimentIPU = new SentimentIPU();

            // Process sentiment
            sentimentIPU.writeInput("chatInput", currentChat);
            Future<Void> future = sentimentIPU.runner();
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                System.err.println("Error processing sentiment: " + e.getMessage());
                return;
            }

            String resultSentiment = sentimentIPU.readOutput("sentiment");
            double resultConfidence = sentimentIPU.readOutput("confidence");

            System.out.println("Chat: " + currentChat);
            System.out.println("Sentiment: " + resultSentiment);
            System.out.println("Confidence: " + resultConfidence);
        }
    }
}

// src/main/java/sentimentanalyzer/ChatDatabase.java

import java.util.ArrayList;
import java.util.List;

public class ChatDatabase {

    private List<String> chats = new ArrayList<>();

    public ChatDatabase() {
        chats.add("I love this product!");
        chats.add("This product is amazing!");
        chats.add("I'm so happy with this purchase!");
        chats.add("I'm feeling very hungry.");
        chats.add("I'm so excited for the weekend!");
        chats.add("This product is okay, I guess.");
        chats.add("I'm feeling a bit sad today.");
        chats.add("I love playing video games!");
        chats.add("I'm so tired today.");
        chats.add("I'm feeling very anxious.");
    }

    public List<String> getChats() {
        return chats;
    }
}