package me.zorua162.heightborder;

import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import me.zorua162.heightborder.border.Border;
import me.zorua162.heightborder.border.BorderManager;
import me.zorua162.heightborder.commands.CommandManager;
import me.zorua162.heightborder.commands.ToggleDisplay;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.RegisteredServiceProvider;
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
    //
    //
    // TODO list:
    // Allow applicable commands to be called from command prompt such as set, list, delete
    // Allow for the block warning distance (and time) to be set for screen reddening
    // save to file:
    //      moving colour
    //      stopped colour
    //
    // Possible later additions:
    // Show break block animation on blocks before breaking them
    // Make particle colour configurable from config
    // Tick times of all tasks editable, either via config and pre gen or similar solution to damagepause

    FileConfiguration config;
    public BorderManager borderManager;
    public WorldBorderApi worldBorderApi;

    @Override
    public void onEnable() {
        // Plugin startup logic
        setupConfig();
        borderManager = new BorderManager(this);
        borderManager.setup(config);
        setupWorldBorderAPI();
        getCommand("heightborder").setExecutor(new CommandManager(this));
        getCommand("toggledisplay").setExecutor(new ToggleDisplay(this));
        // TODO reload from file and with saved wbders
    }

    private void setupConfig() {
        ConfigurationSerialization.registerClass(Border.class);
        config = getConfig();
        config.addDefault("numberOfParticles", 100);
        config.addDefault("defaultDisplayBorderParticlesSetting", true);
        config.addDefault("damageWait", 20);
        config.addDefault("breakWait", 1);
        config.addDefault("displayWait", 20);
        config.addDefault("moveWait", 20);
        config.options().copyDefaults(true);
        saveConfig();
    }

    private void setupWorldBorderAPI(){
        RegisteredServiceProvider<WorldBorderApi> worldBorderApiRegisteredServiceProvider = getServer().getServicesManager().getRegistration(WorldBorderApi.class);

        if (worldBorderApiRegisteredServiceProvider == null) {
            getLogger().info("API not found");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        worldBorderApi = worldBorderApiRegisteredServiceProvider.getProvider();
    }

    public FileConfiguration getCurrentConfig() {
        return config;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        // Save all current world border states so they don't reset to start positions
        borderManager.saveBorders();

    }

    public BorderManager getBorderManager(){
        return borderManager;
    }
}
