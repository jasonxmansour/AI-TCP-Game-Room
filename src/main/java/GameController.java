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
    
    private String[] currentChoices = new String[4];
    private int[] votes = new int[4];
    private Set<String> votedPlayers = new HashSet<>();
    private Timer voteTimer;
    private boolean votingInProgress = false;
    
    public GameController(gameRoom room, String gameType) {
        this.room = room;
        this.gameType = gameType;
        this.storyContext = new StringBuilder();
        initializeGame();
    }
    
    private void initializeGame() {
        String systemPrompt = getSystemPrompt();
        
        String prompt = String.format(
            "%s\n\nYou have %d players. Start the game with a brief situation (1-2 sentences max) and give 4 numbered action choices. Keep choices under 40 characters each.",
            systemPrompt, room.getPlayerCount()
        );
        
        String intro = callOpenAI(prompt);
        room.broadcast("\n" + intro + "\n");
        parseAndDisplayChoices(intro);
        startVoting();
    }
    
    private String getSystemPrompt() {
        switch (gameType) {
            case "Matrix":
                return "You're running a Matrix escape game. Players are trapped in the simulation and must either reach consciousness (wake up in the real world) or destabilize the Matrix system itself. Keep narration brief and action-focused. Each turn: describe what happens in 1-2 sentences, then give 4 tactical choices. Track their progress toward either goal.";
            
            case "Prison Break":
                return "You're running a prison escape game. Players are inmates trying to break out before guards catch them. Keep narration brief and tense. Each turn: describe what happens in 1-2 sentences, then give 4 escape-focused choices. Track their progress toward freedom.";
            
            case "Treasure Hunt":
                return "You're running a treasure hunt race. Players are searching for treasure while a rival pirate crew is also hunting it. Keep narration brief and urgent. Each turn: describe what happens in 1-2 sentences, then give 4 action choices. Track both crews' progress - if the rivals find it first, players lose.";
            
            default:
                return "Brief narration, 4 choices per turn.";
        }
    }
    
    private void parseAndDisplayChoices(String response) {
        // openai api responses
        String[] lines = response.split("\n");
        int choiceIndex = 0;
        for (String line : lines) {
            if (line.matches("^[1-4]\\..*") && choiceIndex < 4) {
                currentChoices[choiceIndex] = line.substring(2).trim();
                choiceIndex++;
            }
        }
        
        for (int i = choiceIndex; i < 4; i++) {
            currentChoices[i] = "Continue forward";
        }
    }
    
    private void displayVotingUI() {
        StringBuilder ui = new StringBuilder();
        
        ui.append("\n\n");
        ui.append("╔══════════════════════════════════════════════════════════════════════════════╗\n");
        ui.append("║                           VOTING IN PROGRESS                                 ║\n");
        ui.append("║                   What should the group do next?                             ║\n");
        ui.append("╚══════════════════════════════════════════════════════════════════════════════╝\n");
        ui.append("\n");
        
        // Displaying each choice in a box
        for (int i = 0; i < 4; i++) {
            ui.append("┌────────────────────────────────────────────────────────────────────────────┐\n");
            ui.append(String.format("│ Choice %d: %-67s│\n", i + 1, currentChoices[i]));
            ui.append("│                                                                            │\n");
            
            // Progress bar 
            int percentage = (room.getPlayerCount() > 0) ? (votes[i] * 100) / room.getPlayerCount() : 0;
            String progressBar = createProgressBar(percentage, 60);
            ui.append(String.format("│ %s %d%%│\n", progressBar, percentage));
            ui.append(String.format("│ Votes: %d/%d                                                               │\n", 
                votes[i], room.getPlayerCount()));
            ui.append("└────────────────────────────────────────────────────────────────────────────┘\n");
            ui.append("\n");
        }
        
        ui.append("════════════════════════════════════════════════════════════════════════════════\n");
        ui.append("Type /vote 1, /vote 2, /vote 3, or /vote 4 to cast your vote!\n");
        ui.append(String.format("Votes cast: %d/%d | Time remaining: 30 seconds\n", 
            votedPlayers.size(), room.getPlayerCount()));
        ui.append("════════════════════════════════════════════════════════════════════════════════\n");
        
        room.broadcast(ui.toString());
    }
    
    private String createProgressBar(int percentage, int width) {
        int filled = (percentage * width) / 100;
        int empty = width - filled;
        
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < filled; i++) {
            bar.append("█");
        }
        for (int i = 0; i < empty; i++) {
            bar.append("░");
        }
        bar.append("]");
        
        return bar.toString();
    }
    
    private void startVoting() {
        votes = new int[4];
        votedPlayers.clear();
        votingInProgress = true;
    
        displayVotingUI();
    
        // timeout timer for 30 seconds maybe change later 
        voteTimer = new Timer();
        voteTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                votingInProgress = false;
                tallyVotes();
            }
        }, 30000);
    }
    
    public void handleVote(String playerName, int choice) {
        if (!votingInProgress) {
            return;
        }
        
        if (choice < 1 || choice > 4) {
            room.sendToPlayer(playerName, "Invalid choice! Please vote 1-4.");
            return;
        }
        
        if (votedPlayers.contains(playerName)) {
            room.sendToPlayer(playerName, "You already voted!");
            return;
        }
        
        votes[choice - 1]++;
        votedPlayers.add(playerName);

        displayVotingUI();
        
        room.broadcast(String.format("\n✓ %s voted for Choice %d!", playerName, choice));
        
        if (votedPlayers.size() == room.getPlayerCount()) {
            voteTimer.cancel();
            votingInProgress = false;
            tallyVotes();
        }
    }
    
    private void tallyVotes() {
        int maxVotes = 0;
        int winningChoice = 0;
        
        for (int i = 0; i < 4; i++) {
            if (votes[i] > maxVotes) {
                maxVotes = votes[i];
                winningChoice = i;
            }
        }
        
        StringBuilder results = new StringBuilder();
        results.append("\n\n");
        results.append("╔══════════════════════════════════════════════════════════════════════════════╗\n");
        results.append("║                            VOTING COMPLETE!                                  ║\n");
        results.append("╚══════════════════════════════════════════════════════════════════════════════╝\n");
        results.append("\n");
        
        for (int i = 0; i < 4; i++) {
            String marker = (i == winningChoice) ? "✓ WINNER" : "";
            results.append(String.format("Choice %d: %s - %d votes %s\n", 
                i + 1, currentChoices[i], votes[i], marker));
        }
        
        results.append("\n");
        results.append("════════════════════════════════════════════════════════════════════════════════\n");
        results.append(String.format("The group has chosen: %s\n", currentChoices[winningChoice]));
        results.append("════════════════════════════════════════════════════════════════════════════════\n");
        
        room.broadcast(results.toString());
        
        // Wait 3 seconds before continuing
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        processChosenAction(currentChoices[winningChoice]);
    }
    
    private void processChosenAction(String action) {
        String systemPrompt = getSystemPrompt();
        
        String prompt = String.format(
            "%s\n\nStory so far: %s\n\nPlayers chose: '%s'\n\nWhat happens next? (1-2 sentences only). Then give 4 new numbered choices (under 40 characters each). If they win or lose, end with 'GAME OVER - WIN' or 'GAME OVER - LOSE'.",
            systemPrompt, storyContext.toString(), action
        );
        
        String response = callOpenAI(prompt);
        storyContext.append("\nAction: ").append(action).append("\n").append(response);
        room.broadcast("\n" + response + "\n");
        
        if (response.contains("GAME OVER")) {
            endGame(response.contains("WIN"));
        } else {
            parseAndDisplayChoices(response);
            startVoting();
        }
    }
    
    private String callOpenAI(String prompt) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            
            String requestBody = String.format(
                "{\"model\":\"gpt-4o-mini\",\"messages\":[{\"role\":\"system\",\"content\":\"%s\"}],\"max_tokens\":300}",
                prompt.replace("\"", "\\\"").replace("\n", "\\n")
            );
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + OPENAI_API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("API Response: " + response.body());
            
            JSONObject json = new JSONObject(response.body());
            
            if (json.has("error")) {
                System.err.println("OpenAI Error: " + json.getJSONObject("error").getString("message"));
                return "The AI narrator is currently unavailable. Please try again.";
            }
            
            return json.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content"); 
        } catch (Exception e) {
            e.printStackTrace();
            return "Error connecting to AI narrator.";
        }
    }
    
    private void endGame(boolean won) {
        votingInProgress = false;
        if (voteTimer != null) voteTimer.cancel();
        
        StringBuilder endScreen = new StringBuilder();
        endScreen.append("\n\n");
        endScreen.append("╔══════════════════════════════════════════════════════════════════════════════╗\n");
        if (won) {
            endScreen.append("║                             VICTORY!                                         ║\n");
            endScreen.append("║                     The group completed the mission!                         ║\n");
        } else {
            endScreen.append("║                             DEFEAT!                                          ║\n");
            endScreen.append("║                        The group has failed...                               ║\n");
        }
        endScreen.append("╚══════════════════════════════════════════════════════════════════════════════╝\n");
        
        room.broadcast(endScreen.toString());
    }
}