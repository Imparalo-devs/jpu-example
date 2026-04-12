package com.example.sentiment;

/**
 * Entry point that follows the exact feature process flow:
 * <ol>
 *   <li>Start the process.</li>
 *   <li>Repeatedly fetch a chat from {@link ChatDatabase}.</li>
 *   <li>Process the chat with {@link SentimentProcessor}.</li>
 *   <li>Print the resulting sentiment and confidence.</li>
 *   <li>End when the database is empty.</li>
 * </ol>
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("[system] START: Starting sentiment analysis process");

        ChatDatabase db = new ChatDatabase();
        SentimentProcessor processor = new SentimentProcessor();

        while (true) {
            // STEP 2 – fetch next chat
            String chat = db.getNextChat();
            if (chat == null) {
                // No more chats – exit loop
                break;
            }

            System.out.println("[system] DATA: Retrieved chat -> \"" + chat + "\"");

            // STEP 3 – process sentiment
            Result result = processor.process(chat);

            // STEP 5 – print results
            System.out.println("[system] PROCESS: Sentiment = " + result.getSentiment()
                    + ", Confidence = " + result.getConfidence());
        }

        System.out.println("[system] END: All chats processed");
    }
}