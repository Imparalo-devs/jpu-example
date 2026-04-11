import java.util.ArrayList;

public class ChatDatabase {
    private ArrayList<String> chats;

    public ChatDatabase() {
        this.chats = new ArrayList<>();
        this.chats.add("I'm feeling very happy today!");
        this.chats.add("I'm so hungry, I could eat a whole pizza by myself!");
        this.chats.add("I'm feeling sad and depressed.");
        this.chats.add("I'm excited for my upcoming vacation!");
        this.chats.add("I'm feeling anxious about my upcoming exam.");
        this.chats.add("I'm feeling grateful for my wonderful family.");
        this.chats.add("I'm feeling angry about the current state of politics.");
        this.chats.add("I'm feeling happy and content with my life.");
        this.chats.add("I'm feeling frustrated with my current job.");
    }

    public String getChat() {
        if (chats.isEmpty()) {
            return null;
        }
        String chat = chats.get(0);
        chats.remove(0);
        return chat;
    }
}