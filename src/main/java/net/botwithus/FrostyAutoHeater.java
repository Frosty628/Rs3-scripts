package net.botwithus;

import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.game.Client;
import net.botwithus.rs3.game.hud.interfaces.Interfaces;
import net.botwithus.rs3.game.queries.builders.objects.SceneObjectQuery;
import net.botwithus.rs3.game.scene.entities.characters.player.LocalPlayer;
import net.botwithus.rs3.game.scene.entities.object.SceneObject;
import net.botwithus.rs3.script.Execution;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;

import java.util.Random;

public class FrostyAutoHeater extends LoopingScript {

    private BotState botState = BotState.IDLE;
    private boolean someBool = true;
    private Random random = new Random();

    enum BotState {
        //define your own states here
        IDLE,
        SKILLING,
        BANKING,
        Smithing,
        Heating,
        //...
    }

    public FrostyAutoHeater(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
        this.sgc = new FrostyAutoHeaterGraphicsContext(getConsole(), this);
    }

    @Override
    public void onLoop() {
        LocalPlayer player = Client.getLocalPlayer();
        if (player == null || Client.getGameState() != Client.GameState.LOGGED_IN || botState == BotState.IDLE) {
            Execution.delay(random.nextLong(3000, 7000));
            return;
        }

        switch (botState) {
            case IDLE:
                println("We're idle!");
                Execution.delay(random.nextLong(1000, 3000));
                break;

            case SKILLING:
                Execution.delay(handleSkilling(player));
                break;

            case BANKING:
                // Handle banking logic here
                break;

            case Smithing:
                break;

            case Heating:
                break;
        }
    }

    // Declare class-level variables at the top of your script
    private long lastActionTime = System.currentTimeMillis(); // Tracks last action time
    private boolean isSmithing = true; // Tracks whether we're smithing or heating
    private long lastAnimationTime = 0;  // To track the last time the animation was -1

    private long handleSkilling(LocalPlayer player) {
        // If the skilling progress interface is open, wait before proceeding
        if (Interfaces.isOpen(1251)) {
            return random.nextLong(250, 1500);
        }

        // If inventory is full, switch to BANKING state
        if (Backpack.isFull()) {
            println("Going to banking state!");
            botState = BotState.BANKING;
            return random.nextLong(250, 1500);
        }

        // Get the current time
        long currentTime = System.currentTimeMillis();

        // If the animation is -1, track the time it has been -1
        int currentAnimation = player.getAnimationId();
        if (currentAnimation == -1) {
            // If the animation is -1, check how long it has been in this state
            if (lastAnimationTime == 0) {
                lastAnimationTime = currentTime; // First time seeing -1, record the timestamp
            } else if (currentTime - lastAnimationTime >= 300000) { // 5 minutes = 300000 ms
                println("Player animation is -1 for 5 minutes. Switching to idle state.");
                botState = BotState.IDLE; // Set the bot state to IDLE to stop skilling
                return 0; // Immediate return, stopping execution (bot is now idle)
            }
        } else {
            // If the animation is not -1, reset the lastAnimationTime
            lastAnimationTime = 0;
        }

        // Randomize the time between 15s (15000ms) and 30s (30000ms)
        long randomInterval = random.nextLong(15000, 30000); // Random interval between 15s and 30s

        // If the random interval time has passed, toggle the states
        if ((currentTime - lastActionTime) >= randomInterval) {
            isSmithing = !isSmithing; // Toggle between smithing and heating
            lastActionTime = currentTime; // Update last action timestamp
        }

        // If currently smithing and not in smithing animation, interact with anvil
        if (isSmithing && currentAnimation != 32622) {  // Animation ID 32622 for smithing
            SceneObject anvil = SceneObjectQuery.newQuery().name("Anvil").option("Smith").results().nearest();
            if (anvil != null) {
                println("Interacting with Anvil: " + anvil.interact("Smith"));
                return random.nextLong(1500, 3000);
            }
        }

        // Interact with the forge if we're in heating mode
        if (!isSmithing) {
            SceneObject forge = SceneObjectQuery.newQuery().name("Forge").option("Heat").results().nearest();
            if (forge != null) {
                println("Interacting with Forge: " + forge.interact("Heat"));
                lastActionTime = currentTime; // Reset timer after using the forge
                isSmithing = true;  // Force the bot to switch back to smithing after 2 seconds
                return random.nextLong(2000, 3000); // Interact with forge for 2 seconds
            }
        }

        // Default return delay if no interaction occurs
        return random.nextLong(1500, 3000);
    }

    public BotState getBotState() {
        return botState;
    }

    public void setBotState(BotState botState) {
        this.botState = botState;
    }

    public boolean isSomeBool() {
        return someBool;
    }

    public void setSomeBool(boolean someBool) {
        this.someBool = someBool;
    }
}
