// src/GameController.java
import java.util.*;
import java.io.*;
import java.net.URI;
import java.net.http.*;
import org.json.*;

public class GameController {
    private gameRoom room;
    private String gameType;
    private StringBuilder storyContext;
    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY"); 
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    
    public GameController(gameRoom room, String gameType) {
        this.room = room;
        this.gameType = gameType;
        this.storyContext = new StringBuilder();
        initializeGame();
    }
    
    private void initializeGame() {
        String prompt = String.format(
            "",
            gameType, room.getPlayerCount()
        );
        
        String intro = callOpenAI(prompt);
        room.broadcast("\n" + intro + "\n");
    }
    
    public void processPlayerAction(String playerName, String action) {
        String prompt = String.format(
            "Game: %s\nPrevious context: %s\n\n%s says: '%s'\n\n" +
            "",
            gameType, storyContext.toString(), playerName, action
        );
        
        String response = callOpenAI(prompt);
        storyContext.append("\n").append(playerName).append(": ").append(action).append("\n").append(response);
        
        room.broadcast("\n" + response + "\n");
        
        if (response.contains("GAME OVER")) {
            endGame(response.contains("WIN"));
        }
    }
    
    private String callOpenAI(String prompt) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            
            String requestBody = String.format(
                "{\"model\":\"gpt-4\",\"messages\":[{\"role\":\"system\",\"content\":\"%s\"}],\"max_tokens\":300}",
                prompt.replace("\"", "\\\"").replace("\n", "\\n")
            );
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());
            return json.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content"); 
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    
    private void endGame(boolean won) {
        String message = won ? 
            "\nVICTORY! \n" : 
            "\nDEFEAT! \n";
        room.broadcast(message);
    }
}
