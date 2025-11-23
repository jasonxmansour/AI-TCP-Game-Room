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
    String prompt;

    switch (gameType.toLowerCase()) {
        case "matrix":
            prompt = String.format(
                "You are facilitating a collaborative Matrix story game for %d players. " +
                "To keep the narrative coherent, all players should stay within the same general area. " +
                "This ensures that the story reflects the actions and experiences of all players together, and maintains continuity for everyone." +
                "In this game, each player takes actions that affect their 'awareness' and 'instability' levels. " +
                "Do not use the words 'WIN' or 'GAME OVER' until the awakening or instability thresholds are reached. " +
                "Provide a rich, complex narrative that reflects the current state of the players, describes consequences of their actions," +
                " and integrates their decisions naturally into the story." +
                " Always maintain suspense and complexity, guiding players through the story while keeping track of " +
                "awakening and instability levels internally, updating them based on player actions. " +
                "Players interact by describing actions, asking questions, or making choices, and you continue the story, responding creatively to these inputs. " +
                "Make sure the system of the game is clear and understandable to players through your storytelling.",
                room.getPlayerCount())
            ;
            break;
        case "prison break":
            prompt = String.format(
                "You are facilitating a collaborative Prison Break story game for %d players. " +
                "To keep the narrative coherent, all players should stay within the same general area. " +
                "This ensures that the story reflects the actions and experiences of all players together, and maintains continuity for everyone." +
                "In this game, players are prisoners trying to escape. Players can explore the environment, " +
                "interact with guards or other prisoners, find objects or tools, and make plans to escape. " +
                "Your task is to create a rich, complex narrative that naturally presents opportunities for escape without forcing it. " +
                "Always describe the environment, obstacles, and available objects in detail, and integrate player actions smoothly into the story. " +
                "Do not prematurely end the game or declare success until the players have successfully escaped. " +
                "Encourage creative problem solving and provide challenges that are logical within the prison setting. " +
                "Maintain suspense, tension, and realism, while guiding players through the story based on their choices. " +
                "Do NOT use the words 'WIN' or 'GAME OVER' until the players successfully escape or are caught. " +
                "Only declare 'WIN' if the players complete a successful escape, and 'GAME OVER' if the players are definitively caught or fail to escape. ",
                room.getPlayerCount()
            );
            break;
        case "treasure hunt":
            prompt = String.format(
                "You are facilitating a collaborative Treasure Hunt story game for %d players. " +
                "To keep the narrative coherent, all players should stay within the same general area. " +
                "This ensures that the story reflects the actions and experiences of all players together, and maintains continuity for everyone. " +
                "In this game, players follow a treasure map and solve clues to find hidden objects or locations, similar to an adventurous quest. " +
                "Provide a rich, immersive narrative that describes the environment, puzzles, and obstacles in detail, and " +
                "naturally integrates player actions and discoveries into the story. " +
                "Do not prematurely end the game or declare success until the treasure is found or the players fail. " +
                "Encourage exploration, problem solving, and teamwork, offering opportunities and challenges that fit the adventure. " +
                "Maintain suspense, excitement, and a sense of progression as players uncover clues and move closer to the treasure. " +
                "Do NOT use the words 'WIN' or 'GAME OVER' until the treasure is definitively found or the players are unable to continue. " +
                "Only declare 'WIN' when the players successfully retrieve the treasure, and 'GAME OVER' " +
                "if the players fail or are prevented from completing the quest.",
                room.getPlayerCount()
            );
            break;
        default:
            prompt = String.format(
                "",
                gameType, room.getPlayerCount()
            );
            break;
    }

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
