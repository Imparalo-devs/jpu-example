**File:** `src/main/java/com/example/sentiment/ChatDatabase.java`
```java
package com.example.sentiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simulates a simple chat database.
 * <p>
 * Holds ten preset phrases with varying sentiment.  The {@code getNextChat()}
 * method returns the next phrase and removes it from the internal list.
 * When the list is empty {@code null} is returned.
 */
public class ChatDatabase {

    private final List<String> chats;

    public ChatDatabase() {
        List<String> initial = List.of(
                "I am absolutely thrilled with the service!",
                "What a wonderful day, everything is perfect.",
                "I'm feeling pretty good about this.",
                "It's okay, not great but not terrible.",
                "I'm a bit disappointed with the results.",
                "This is quite frustrating, I expected better.",
                "I'm angry about the delay!",
                "I feel terrible after that experience.",
                "I'm hungry and nothing tastes right.",
                "I'm extremely sad and hopeless."
        );
        // Use a mutable list because we need to remove elements.
        this.chats = Collections.synchronizedList(new ArrayList<>(initial));
    }

    /**
     * Retrieves and removes the next chat phrase.
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
```

---

**File:** `src/main/java/com/example/sentiment/SentimentResult.java`
```java
package com.example.sentiment;

/**
 * Simple DTO that holds the sentiment analysis result.
 */
public class SentimentResult {
    private final String sentiment;
    private final double confidence;

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

    @Override
    public String toString() {
        return "SentimentResult{" +
                "sentiment='" + sentiment + '\'' +
                ", confidence=" + confidence +
                '}';
    }
}
```

---

**File:** `src/main/java/com/example/sentiment/SentimentProcessor.java`
```java
package com.example.sentiment;

import vcpu.SentimentIPU;

import java.util.concurrent.Future;

/**
 * Wraps the provided {@link SentimentIPU} (iPU) and exposes a synchronous
 * {@code process} method that returns a {@link SentimentResult}.
 *
 * <p>Usage pattern required by the iPU:
 * <ol>
 *   <li>Instantiate the iPU.</li>
 *   <li>Call {@code writeInput(key, value)} – this triggers the async runner.</li>
 *   <li>Wait for the async completion (via the returned {@link Future}).</li>
 *   <li>Read the outputs with {@code readOutput(key)}.</li>
 * </ol>
 */
public class SentimentProcessor {

    /**
     * Sends a chat string to the SentimentIPU and returns the analysed result.
     *
     * @param chat the raw chat phrase
     * @return a {@link SentimentResult} containing sentiment label and confidence
     * @throws Exception if the async processing fails
     */
    public SentimentResult process(String chat) throws Exception {
        SentimentIPU ipu = new SentimentIPU();

        // 1. Write the input – this automatically starts the async runner.
        // The iPU contract states that writeInput returns a Future that completes
        // when processing is finished.
        Future<?> future = ipu.writeInput("chatInput", chat);

        // 2. Wait for the async task to finish (no busy‑waiting).
        future.get(); // throws ExecutionException / InterruptedException if something goes wrong

        // 3. Read the outputs.
        String sentiment = ipu.readOutput("sentiment");
        String confidenceStr = ipu.readOutput("confidence");

        double confidence = 0.0;
        try {
            confidence = Double.parseDouble(confidenceStr);
        } catch (NumberFormatException e) {
            // If the iPU returns a non‑numeric confidence, keep the default 0.0
        }

        return new SentimentResult(sentiment, confidence);
    }
}
```

---

**File:** `src/main/java/com/example/sentiment/MainApp.java`
```java
package com.example.sentiment;

/**
 * Entry point that drives the whole feature flow:
 * <ol>
 *   <li>START – initialise the database.</li>
 *   <li>LOOP – fetch each chat, process it, and print the result.</li>
 *   <li>END – when the database is exhausted.</li>
 * </ol>
 */
public class MainApp {

    public static void main(String[] args) {
        System.out.println("[system] START: Starting sentiment analysis process");

        ChatDatabase database = new ChatDatabase();
        SentimentProcessor processor = new SentimentProcessor();

        while (true) {
            // STEP 2 – get next chat from the simulated DB
            String chat = database.getNextChat();
            if (chat == null) {
                // No more chats – exit loop
                break;
            }

            System.out.println("[system] DATA: Retrieved chat -> \"" + chat + "\"");

            try {
                // STEP 3 – invoke the iPU via the processor
                SentimentResult result = processor.process(chat);

                // STEP 5 – print the results
                System.out.println("[system] PROCESS: Sentiment = " + result.getSentiment()
                        + ", Confidence = " + result.getConfidence());
            } catch (Exception e) {
                System.err.println("[system] ERROR: Failed to process chat \"" + chat + "\"");
                e.printStackTrace();
            }
        }

        System.out.println("[system] END: All chats processed");
    }
}
```

---

**Notes**

* The code follows standard Java conventions (Effective Java) and is fully **idiomatic**.
* All interactions with the provided `SentimentIPU` respect the mandatory usage pattern:
  * `writeInput` is called once per chat.
  * The returned `Future` is awaited via `Future#get()` – no busy‑waiting loops.
  * Outputs are read **after** the future completes.
* Thread‑safety is ensured for the simulated database by synchronising on the internal list.
* Errors from the async iPU are caught and logged, preventing the whole application from crashing.