package com.example.sentiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simulates a simple chat database using an {@link ArrayList}.
 * Each call to {@link #getNextChat()} returns the next chat string
 * and removes it from the internal list. When the list is empty,
 * {@code null} is returned.
 */
public class ChatDatabase {

    /** Thread‑safe list of chat messages. */
    private final List<String> chats = Collections.synchronizedList(new ArrayList<>());

    /** Initialise the database with ten varied sentiment strings. */
    public ChatDatabase() {
        chats.add("I am absolutely thrilled with the service!");
        chats.add("What a terrible experience, I'm very upset.");
        chats.add("I'm feeling okay, nothing special.");
        chats.add("The product is amazing, I love it!");
        chats.add("I'm disappointed, it didn't meet my expectations.");
        chats.add("Just had a decent day, nothing to complain about.");
        chats.add("Wow, this is the best thing ever!");
        chats.add("I'm angry about the delay in delivery.");
        chats.add("It was fine, could be better but not bad.");
        chats.add("I'm ecstatic! This exceeded all my hopes.");
    }

    /**
     * Retrieves and removes the next chat from the database.
     *
     * @return the next chat string, or {@code null} if the database is empty
     */
    public String getNextChat() {
        synchronized (chats) {
            if (chats.isEmpty()) {
                return null;
            }
            return chats.remove(0);
        }
    }
}