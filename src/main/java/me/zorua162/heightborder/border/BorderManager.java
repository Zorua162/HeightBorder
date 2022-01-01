package me.zorua162.heightborder.border;


import me.zorua162.heightborder.HeightBorder;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class BorderManager {

    ArrayList<Border> borderArray = new ArrayList<>();
    HeightBorder plugin;
    int numberOfParticles;


    public BorderManager(HeightBorder plugin){
        this.plugin = plugin;
    }

    public void setup(FileConfiguration config) {
        BukkitTask displayTask;
        BukkitTask moveTask;
        BukkitTask damageTask;
        BukkitTask breakTask;


        // Load the boarder list
        ArrayList<Border> configBorderList = (ArrayList<Border>) plugin.getConfig().get("borderList");
        // Load default number of particles for a border to use to display itself
        numberOfParticles = config.getInt("numberOfParticles");
        if (configBorderList != null){
            borderArray = configBorderList;
        } else {
            Logger logger = plugin.getLogger();
            logger.warning("Could not load borders from save file config.yml");
        }

        // Kick off border display task
        displayTask = plugin.getServer().getScheduler().runTaskTimer(plugin,
                () -> borderArray.forEach(this::displayBorder), 0, 20L);
        moveTask = plugin.getServer().getScheduler().runTaskTimer(plugin,
                () -> borderArray.forEach(this::moveBorder), 0, 1L);
        damageTask = plugin.getServer().getScheduler().runTaskTimer(plugin,
                () -> borderArray.forEach(this::borderDamage), 0, 1L);
        breakTask = plugin.getServer().getScheduler().runTaskTimer(plugin,
                () -> borderArray.forEach(this::borderBreakBlocks), 0, 1L);

    }

    private void borderBreakBlocks(Border border) {
       border.breakBlocks();
    }

    private void borderDamage(Border border) {
        border.doDamage();
    }

    private void moveBorder(Border border) {border.moveBorder();

    }

    private void displayBorder(Border border) {
        border.displayBorder();
    }

    public Border createBorder(Player player, double startHeight, double endHeight, String direction, double velocity,
                               Location flpos, Location brpos, String type){
        // set if the particles are displayed from the config
        boolean displayBorderParticles = plugin.getCurrentConfig().getBoolean("defaultDisplayBorderParticlesSetting");
        boolean damagePlayers;
        boolean breakBlocks;
        if (type.equals("break")) {
            damagePlayers = false;
            breakBlocks = true;
        } else if (type.equals("damage")) {
            damagePlayers = true;
            breakBlocks = false;
        } else {
            StringBuilder errorString = new StringBuilder().append("Could not create border as type: \"" + type);
            errorString.append("\" is not recognised, use \"damage\" or \"break\"");
            player.sendMessage(errorString.toString());
            return null;
        }
        Border border = new Border(startHeight, endHeight, direction, velocity, flpos, brpos, damagePlayers,
                breakBlocks, displayBorderParticles, numberOfParticles);
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
        int intId = Integer.valueOf(id);
       if (intId>borderArray.size()){
           return "index out of range";
       } else if (intId == 0){
          return "Use id 1-n not index";
        }
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
        Border border = borderArray.get(Integer.valueOf(id)-1);
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
            case "damagepause":
                border.setDamagePause(value);
                return "\nSet damage pause to " + value;
            case "numberofparticles":
                border.setNumberOfParticles(value);
                return "\nSet number of particles to display border to " + value;
        }
        return "Something went wrong";
    }
}
