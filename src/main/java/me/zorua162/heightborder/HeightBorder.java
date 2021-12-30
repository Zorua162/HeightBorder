package me.zorua162.heightborder;

import com.github.yannicklamprecht.worldborder.api.WorldBorderApi;
import me.zorua162.heightborder.border.BorderManager;
import me.zorua162.heightborder.commands.CommandManager;
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
    // how many particles displayed? Laggy?
    //
    // Alternative implementation as per staff meeting: 06/12
    // remove layers as a way to bring players together
    // Implement with border "type" being either "break" or "damage"
    //
    // TODO list:
    // reorder todo list
    // Player world border warning indicator thingy
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

    public BorderManager borderManager;
    public WorldBorderApi worldBorderApi;

    @Override
    public void onEnable() {
        // Plugin startup logic
        borderManager = new BorderManager(this);
        borderManager.setup();
        getCommand("heightborder").setExecutor(new CommandManager(this));
        // TODO reload from file and with saved wbders
        registerWorldBorderAPI();
    }

    private void registerWorldBorderAPI() {
        RegisteredServiceProvider<WorldBorderApi> worldBorderApiRegisteredServiceProvider = getServer().getServicesManager().getRegistration(WorldBorderApi.class);

        if (worldBorderApiRegisteredServiceProvider == null) {
            getLogger().info("API not found");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        worldBorderApi = worldBorderApiRegisteredServiceProvider.getProvider();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public BorderManager getBorderManager(){
        return borderManager;
    }
}
