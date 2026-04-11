import java.util.ArrayList;
import java.util.List;

public class ChatDatabase {
    private List<String> chats;

    public ChatDatabase() {
        this.chats = new ArrayList<>();
        this.chats.add("I'm feeling very happy today!");
        this.chats.add("I'm so hungry, I could eat a whole pizza by myself.");
        this.chats.add("I'm feeling sad and depressed.");
        this.chats.add("I'm excited for my upcoming vacation!");
        this.chats.add("I'm feeling anxious about my upcoming exam.");
        this.chats.add("I'm feeling grateful for my wonderful friends.");
        this.chats.add("I'm feeling frustrated with this difficult project.");
        this.chats.add("I'm feeling happy and content with my life.");
        this.chats.add("I'm feeling bored and need something to do.");
    }

    public String getChat() {
        if (this.chats.isEmpty()) {
            return null;
        }
        String chat = this.chats.get(0);
        this.chats.remove(0);
        return chat;
    }
}