package me.zorua162.heightborder;

import me.zorua162.heightborder.border.BorderManager;
import me.zorua162.heightborder.commands.CommandManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class HeightBorder extends JavaPlugin {
    // Project specification: Produce a vertical world border
    // Main part: The world border itself
    // Split to three parts:
    // Determine position of World Border
    // y pos, rate of movement, setting up commands
    // Player damage part
    // Player "outside" of border
    // What is outside? Border should be defined as top or bottom
    // World border display part
    // how many particles displayed? Laggy?
    //
    // Alternative implementation as per staff meeting: 06/12
    // remove layers as a way to bring players together
    // Implement with border "type" being either "break" or "damage"
    //
    // TODO list:
    // save to file
    // load from file on enable
    // display: needs improved configuration
    // load from file on reload (if at all possible)
    //
    // Possible later additions:
    // Make particle colour configurable from config
    // Make damage tick config editable
    //
    // Custom death message by checking PlayerDeathEvent
    FileConfiguration config = getConfig();
    public BorderManager borderManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        setupConfig();
        borderManager = new BorderManager(this);
        borderManager.setup();
        getCommand("heightborder").setExecutor(new CommandManager(this));
        // TODO reload from file and with saved wbders
    }

    private void setupConfig() {
        config.addDefault("youAreAwesome", true);
        config.options().copyDefaults(true);
        saveConfig();
        this.getLogger().info("" + config.getBoolean("youAreAwesome"));
        config.set("youAreAwesome", false);
        saveConfig();
    }


    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public BorderManager getBorderManager(){
        return borderManager;
    }
}
