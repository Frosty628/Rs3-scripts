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

public class FrostyAutoHeater2 extends LoopingScript {


    private BotState botState = BotState.IDLE;
    private boolean useBurialForge = false; // Declare the useBurialForge field
    private boolean someBool = true;
    private Random random = new Random();

    // Getter and Setter for useBurialForge
    public boolean isUseBurialForge() {
        return useBurialForge;
    }

    public void setUseBurialForge(boolean useBurialForge) {
        this.useBurialForge = useBurialForge;
    }
    public enum BurialForgeOption {
        NO("No"),
        YES("Yes");

        private final String label;

        BurialForgeOption(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        // Convert enum values to a list of strings
        public static String[] getAllOptions() {
            String[] options = new String[BurialForgeOption.values().length];
            for (int i = 0; i < BurialForgeOption.values().length; i++) {
                options[i] = BurialForgeOption.values()[i].getLabel();
            }
            return options;
        }
    }



    enum BotState {
        IDLE,
        SKILLING,
        BANKING,
        Smithing,
        Heating,
    }

    public FrostyAutoHeater2(String s, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(s, scriptConfig, scriptDefinition);
        this.sgc = new FrostyAutoHeater2GraphicsContext(getConsole(), this);
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

        // Get the name for the anvil based on whether we're using Burial Forge
        String anvilName = useBurialForge ? "Burial Anvil" : "Anvil";

        long currentTime = System.currentTimeMillis();

// If currently smithing but more than 7s have passed, switch to heating
        if (isSmithing && (currentTime - lastActionTime) > 7000) {
            isSmithing = false; // Switch to heating mode
        }

// If currently smithing and not in smithing animation, interact with anvil
        if (isSmithing && player.getAnimationId() != 32622) {  // Animation ID 32622 for smithing
            SceneObject anvil = SceneObjectQuery.newQuery().name(anvilName).option("Smith").results().nearest();
            if (anvil != null) {
                println("Interacting with " + anvilName + ": " + anvil.interact("Smith"));
                lastActionTime = currentTime; // Reset timer when smithing starts
                return random.nextLong(1500, 3000);
            }
        }

// Interact with the forge if we're in heating mode
        if (!isSmithing) {
            SceneObject forge = SceneObjectQuery.newQuery().name("Forge").option("Heat").results().nearest();
            if (forge != null) {
                println("Interacting with Forge: " + forge.interact("Heat"));
                lastActionTime = currentTime; // Reset timer after using the forge
                isSmithing = true;  // Switch back to smithing after heating
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