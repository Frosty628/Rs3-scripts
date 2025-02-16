package net.botwithus;

import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;

public class FrostyAutoHeater2GraphicsContext extends ScriptGraphicsContext {

    private FrostyAutoHeater2 script;
    private boolean useBurialForge = false; // Checkbox state

    public FrostyAutoHeater2GraphicsContext(ScriptConsole scriptConsole, FrostyAutoHeater2 script) {
        super(scriptConsole);
        this.script = script;
    }

    public boolean isUseBurialForge() {
        return useBurialForge;
    }

    public void setUseBurialForge(boolean useBurialForge) {
        this.useBurialForge = useBurialForge;
    }

    @Override
    public void drawSettings() {
        if (ImGui.Begin("Frosty Auto Heater", ImGuiWindowFlag.None.getValue())) {
            if (ImGui.BeginTabBar("My bar", ImGuiWindowFlag.None.getValue())) {
                if (ImGui.BeginTabItem("Settings", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Frosty Simple Auto Heater!");
                    ImGui.Text("Instructions - Start with unfinished smithing items.");
                    ImGui.Text("My script's state is: " + script.getBotState());

                    // Start and Stop button
                    if (ImGui.Button(script.getBotState() == FrostyAutoHeater2.BotState.IDLE ? "Start" : "Stop")) {
                        script.setBotState(script.getBotState() == FrostyAutoHeater2.BotState.IDLE ?
                                FrostyAutoHeater2.BotState.SKILLING :
                                FrostyAutoHeater2.BotState.IDLE);
                    }

                    // Combo Box for selecting "Use Burial Forge"
                    String[] options = FrostyAutoHeater2.BurialForgeOption.getAllOptions();

                    // Use a single integer to store the index
                    int currentIndex = script.isUseBurialForge() ?
                            FrostyAutoHeater2.BurialForgeOption.YES.ordinal() :
                            FrostyAutoHeater2.BurialForgeOption.NO.ordinal();

                    // Display combo box and update the state if the selection changes
                    currentIndex = ImGui.Combo("Use Burial Forge", currentIndex, options);

                    // Set the useBurialForge value based on the selected index
                    script.setUseBurialForge(currentIndex == FrostyAutoHeater2.BurialForgeOption.YES.ordinal());

                    ImGui.EndTabItem();
                }
                ImGui.EndTabBar();
            }
            ImGui.End();
        }
    }
}