package me.zorua162.heightborder.border;


import me.zorua162.heightborder.HeightBorder;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class BorderManager {

    ArrayList<Border> borderArray = new ArrayList<>();
    HeightBorder plugin;
    int numberOfParticles;
    int damageWait;
    int breakWait;
    int displayWait;
    int moveWait;
    // Filter for validating which parameters cannot be zero.
    List<String> zeroNotAllowed;
    // Manager for handling the red "warning" that displays on player's screens
    WarningManager warningManager;

    public BorderManager(HeightBorder plugin){
        this.plugin = plugin;
    }

    public void setup(FileConfiguration config) {
        BukkitTask borderTaskTimer;

        // Load the boarder list
        ArrayList<Border> configBorderList = (ArrayList<Border>) plugin.getConfig().get("borderList");
        // Load default number of particles for a border to use to display itself
        numberOfParticles = config.getInt("numberOfParticles");
        if (configBorderList != null){
            borderArray = configBorderList;
            // remove null borders
            borderArray.removeAll(Collections.singleton(null));
            saveBorders();

        } else {
            Logger logger = plugin.getLogger();
            logger.warning("Could not load borders from save file config.yml");
        }

        warningManager = new WarningManager(this);

        for (Border border : borderArray) {
            border.setManager(warningManager);
        }

        // Kick off border tasks
        borderTaskTimer = plugin.getServer().getScheduler().runTaskTimer(plugin,
                () -> borderArray.forEach(this::runBorderTasks), 0, 1L);
        // For stopping user from setting value to 0 as division error would occur
        zeroNotAllowed = Arrays.asList("damagewait", "breakwait", "movewait", "displaywait");


    }

    private void runBorderTasks(Border border) {border.runTasks(plugin);}

    public Border createBorder(Player player, double startHeight, double endHeight, String direction, double velocity,
                               Location flpos, Location brpos, String type){
        // set if the particles are displayed from the config
        FileConfiguration config = plugin.getCurrentConfig();
        boolean displayBorderParticles = config.getBoolean("defaultDisplayBorderParticlesSetting");
        // set task periodic wait times from config
        damageWait = config.getInt("damageWait");
        breakWait = config.getInt("breakWait");
        displayWait = config.getInt("displayWait");
        moveWait = config.getInt("moveWait");
        boolean damagePlayers;
        boolean breakBlocks;
        if (type.equals("break")) {
            damagePlayers = false;
            breakBlocks = true;
        } else if (type.equals("damage")) {
            damagePlayers = true;
            breakBlocks = false;
        } else {
            StringBuilder errorString = new StringBuilder("Could not create border as type: \"" + type);
            errorString.append("\" is not recognised, use \"damage\" or \"break\"");
            player.sendMessage(errorString.toString());
            return null;
        }
        Border border = new Border(startHeight, endHeight, direction, velocity, flpos, brpos, damagePlayers,
                breakBlocks, displayBorderParticles, numberOfParticles, damageWait, breakWait, displayWait, moveWait);
        border.setManager(warningManager);
        borderArray.add(border);
        saveBorders();
        return border;
    }

    public String getBorderListString() {
        String borderList = "";
        int i = 0;
        for (Border border: borderArray) {
            i = i + 1;
            borderList = borderList + "\n-----\nBorder Id: " + i + "\n";
            borderList = borderList + border.getListInfo() + "\n----\n";
        }
        return borderList;
    }
    public String deleteBorder(String id) {
        if (id.equalsIgnoreCase("all")) {
            //remove all borders
            borderArray.clear();
            saveBorders();
            warningManager.clearWarnings();
            return "Success";
        }
        int intId = Integer.parseInt(id);
        if (intId>borderArray.size()){
            return "index out of range";
        } else if (intId == 0){
          return "Use id 1-n not index";
        }
        warningManager.clearWarnings();
       borderArray.remove(intId-1);
       saveBorders();
       return "Success";
    }

    public void saveBorders() {
        FileConfiguration config = plugin.getConfig();
        config.set("borderList", borderArray);
        plugin.saveConfig();
    }

    public List<String> getBorderIdList() {
        ArrayList<String> idList = new ArrayList<>();
        int end = borderArray.size();
        for (int i=1; i<=end; i++){
            idList.add(String.valueOf(i));
        }
        return idList;
    }

    public String setParameter(String id, String parameter, String value) {
        Border border = borderArray.get(Integer.parseInt(id)-1);
        if ((zeroNotAllowed.contains(parameter) && Integer.parseInt(value) == 0)) {
            return "\nFailed to set " + parameter + " 0 is not valid for this parameter";
        }


        switch (parameter){
            case "currentheight":
                border.setCurrentHeight(Double.parseDouble(value));
                return "\nSet current height to " + value;
            case "endheight":
                border.setEndHeight(Double.parseDouble(value));
                return "\nSet end height to " + value;
            case "direction":
                if (Arrays.asList("up", "down").contains(value)) {
                    border.setDirection(value);
                    return "\nSet direction to " + value;
                } else {
                   return "\nDirection not recognised only up or down accepted " + value;
                }
            case "velocity":
                border.setVelocity(Double.parseDouble(value));
                return "\nSet velocity to " + value;
            case "pos1x":
                border.setPos("1x", value);
                return "\nSet pos1x to " + value;
            case "pos1z":
                border.setPos("1z", value);
                return "\nSet pos1z to " + value;
            case "pos2x":
                border.setPos("2x", value);
                return "\nSet pos2x to " + value;
            case "pos2z":
                border.setPos("2z", value);
                return "\nSet pos2z to " + value;
            case "damagePlayers":
                border.setDamagePlayers(value);
                return "\nSet damage players to " + value;
            case "breakBlocks":
                border.setBreakBlocks(value);
                return "\nSet break blocks to " + value;
            case "displayBorderParticles":
                border.setDisplayBorderParticles(value);
                return "\nSet display border particles to " + value;
            case "damagewait":
                border.setDamageWait(value);
                return "\nSet damage wait to " + value;
            case "breakwait":
                border.setBreakWait(value);
                return "\nSet break wait to " + value;
            case "displaywait":
                border.setDisplayWait(value);
                return "\nSet display wait to " + value;
            case "movewait":
                border.setMoveWait(value);
                return "\nSet move wait to " + value;
            case "numberofparticles":
                border.setNumberOfParticles(value);
                return "\nSet number of particles to display border to " + value;
        }
        return "Something went wrong";
    }
    public WarningManager getWarningManager() {return this.warningManager;}
    public void setupBorders(Player player, String[] args) {
        Double timeToFinish;
        if (args.length == 1) {
            player.sendMessage("The center height of the end box needs to be specified");
            return;
        }
        if (args.length == 3) {
            timeToFinish = Double.parseDouble(args[2]);
        } else {
            timeToFinish = 20.0;
        }
        int topStartHeight = 320;
        int bottomStartHeight = -64;

        int endCenter =  Integer.parseInt(args[1]);

        int topEndHeight = endCenter + 5;
        int bottomEndHeight = endCenter - 5;

        int topBorderVelocity = (int) ((topStartHeight - topEndHeight)/timeToFinish);
        int bottomBorderVelocity = (int) ((bottomEndHeight - bottomStartHeight )/timeToFinish);

        Location flpos = new Location(player.getWorld(), -5, topStartHeight, 5);
        Location brpos = new Location(player.getWorld(), 5, bottomStartHeight, -5);

        createBorder(player, topStartHeight, topEndHeight, "down", topBorderVelocity, flpos,
                brpos, "damage");
        createBorder(player, bottomStartHeight, bottomEndHeight, "up", bottomBorderVelocity, flpos,
                brpos, "damage");
        player.sendMessage("Creating borders");
    }
}

