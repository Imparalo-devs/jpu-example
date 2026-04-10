import java.util.ArrayList;
import java.util.List;

public class ChatDatabase {
    private List<String> chats;

    public ChatDatabase() {
        chats = new ArrayList<>();
        chats.add("I'm feeling very happy today!");
        chats.add("I'm so hungry, I could eat a whole pizza by myself.");
        chats.add("This is the worst day ever.");
        chats.add("I'm feeling quite neutral about this.");
        chats.add("I love this new restaurant, the food is amazing!");
        chats.add("I'm so tired, I just want to sleep all day.");
        chats.add("This is the best day of my life!");
        chats.add("I'm feeling a bit anxious about this.");
        chats.add("I'm so excited for the weekend!");
        chats.add("I'm feeling really sad today.");
    }

    public String getChat() {
        if (chats.isEmpty()) {
            return null;
        }
        return chats.remove(0);
    }
}