package database;

import java.util.ArrayList;
import java.util.List;

public class ChatDatabase {

    private List<String> chats;

    public ChatDatabase() {
        this.chats = new ArrayList<>();
    }

    public void addChat(String chat) {
        this.chats.add(chat);
    }

    public String getNextChat() {
        if (this.chats.isEmpty()) {
            return null;
        }
        String chat = this.chats.get(0);
        this.chats.remove(0);
        return chat;
    }
}