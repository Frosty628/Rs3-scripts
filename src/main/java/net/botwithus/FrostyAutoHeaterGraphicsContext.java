package net.botwithus;

import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.imgui.ImGuiWindowFlag;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;

public class FrostyAutoHeaterGraphicsContext extends ScriptGraphicsContext {

    private FrostyAutoHeater script;

    // Declare the `isSkilling` variable at the class level
    private boolean isSkilling = false; // Default state is idle

    public FrostyAutoHeaterGraphicsContext(ScriptConsole scriptConsole, FrostyAutoHeater script) {
        super(scriptConsole);
        this.script = script;
    }

    @Override
    public void drawSettings() {
        if (ImGui.Begin("Frosty Auto Heater", ImGuiWindowFlag.None.getValue())) {
            if (ImGui.BeginTabBar("My bar", ImGuiWindowFlag.None.getValue())) {
                if (ImGui.BeginTabItem("Settings", ImGuiWindowFlag.None.getValue())) {
                    ImGui.Text("Frosty simple auto heater!");
                    ImGui.Text( "Instructions - Start with unfinished smithing items, bot heats and smiths");
                    ImGui.Text("My script's state is: " + script.getBotState());

                    // Display the Start and Stop buttons on the same line
                    ImGui.SameLine();  // Makes the next element appear on the same line
                    if (ImGui.Button(script.getBotState() == FrostyAutoHeater.BotState.IDLE ? "Start" : "Stop")) {
                        if (script.getBotState() == FrostyAutoHeater.BotState.IDLE) {
                            script.setBotState(FrostyAutoHeater.BotState.SKILLING); // Start the script
                        } else {
                            script.setBotState(FrostyAutoHeater.BotState.IDLE); // Stop the script
                        }
                    }

                    ImGui.EndTabItem();
                }
                ImGui.EndTabBar();
            }
            ImGui.End();
        }
    }
}

