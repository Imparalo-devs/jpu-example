package database;

import java.util.ArrayList;
import java.util.List;

public class ChatDatabase {
    private List<String> chats = new ArrayList<>();

    public ChatDatabase() {
        chats.add("I'm so happy today!");
        chats.add("I'm feeling sad and lonely.");
        chats.add("This pizza is amazing!");
        chats.add("I'm so hungry I could eat a whole elephant.");
        chats.add("I love playing with my dog.");
        chats.add("I'm feeling anxious and stressed.");
        chats.add("This coffee is so good!");
        chats.add("I'm so tired I could sleep all day.");
        chats.add("I love watching movies.");
        chats.add("I'm feeling angry and frustrated.");
    }

    public String getNextChat() {
        if (chats.isEmpty()) {
            return null;
        }
        String chat = chats.remove(0);
        return chat;
    }
}

// src/main/java/model/SentimentResult.java
package model;

public class SentimentResult {
    private String sentiment;
    private double confidence;

    public SentimentResult(String sentiment, double confidence) {
        this.sentiment = sentiment;
        this.confidence = confidence;
    }

    public String getSentiment() {
        return sentiment;
    }

    public double getConfidence() {
        return confidence;
    }
}

// src/main/java/vcpu/SentimentIPU.java
package vcpu;

// This file is already existing on staging, so we're not implementing it here.

// src/main/java/vcpu/SentimentProcessor.java
package vcpu;

import java.util.concurrent.Future;

public class SentimentProcessor {
    private SentimentIPU sentimentIPU;

    public SentimentProcessor(SentimentIPU sentimentIPU) {
        this.sentimentIPU = sentimentIPU;
    }

    public SentimentResult processChat(String chat) throws Exception {
        sentimentIPU.writeInput("chatInput", chat);
        Future<?> future = sentimentIPU.runner();
        future.get(); // Wait for the async operation to complete
        String sentiment = sentimentIPU.readOutput("sentiment");
        double confidence = sentimentIPU.readOutput("confidence");
        return new SentimentResult(sentiment, confidence);
    }
}

// src/main/java/app/Main.java
package app;

import database.ChatDatabase;
import model.SentimentResult;
import vcpu.SentimentIPU;
import vcpu.SentimentProcessor;

public class Main {
    public static void main(String[] args) throws Exception {
        ChatDatabase chatDatabase = new ChatDatabase();
        SentimentIPU sentimentIPU = new SentimentIPU(); // Instantiate the SentimentIPU
        SentimentProcessor sentimentProcessor = new SentimentProcessor(sentimentIPU);

        while (true) {
            String currentChat = chatDatabase.getNextChat();
            if (currentChat == null) {
                break;
            }

            SentimentResult result = sentimentProcessor.processChat(currentChat);
            System.out.println("Sentiment: " + result.getSentiment());
            System.out.println("Confidence: " + result.getConfidence());
        }
    }
}