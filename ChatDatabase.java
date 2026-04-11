import java.util.ArrayList;

public class ChatDatabase {
    private ArrayList<String> chats;

    public ChatDatabase() {
        this.chats = new ArrayList<>();
        this.chats.add("I'm feeling very happy today!");
        this.chats.add("I'm so hungry, I need to eat something.");
        this.chats.add("This is the best day of my life!");
        this.chats.add("I'm feeling sad and depressed.");
        this.chats.add("I'm excited for the weekend!");
        this.chats.add("I'm feeling anxious about the future.");
        this.chats.add("I'm grateful for my friends and family.");
        this.chats.add("I'm feeling angry and frustrated.");
        this.chats.add("I'm feeling calm and peaceful.");
        this.chats.add("I'm feeling bored and uninterested.");
    }

    public String getChat() {
        if (chats.isEmpty()) {
            return null;
        }
        return chats.remove(0);
    }
}