package me.zorua162.heightborder.border;


import me.zorua162.heightborder.HeightBorder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class BorderManager {

    ArrayList<Border> borderArray = new ArrayList<>();
    HeightBorder plugin;


    public BorderManager(HeightBorder plugin){
        this.plugin = plugin;
    }

    public void setup() {
        BukkitTask displayTask;
        BukkitTask moveTask;
        BukkitTask damageTask;
        BukkitTask breakTask;
        BukkitTask showWarning;
        // Kick off border display task
        displayTask = plugin.getServer().getScheduler().runTaskTimer(plugin,
                () -> borderArray.forEach(this::displayBorder), 0, 20L);
        moveTask = plugin.getServer().getScheduler().runTaskTimer(plugin,
                () -> borderArray.forEach(this::moveBorder), 0, 1L);
        damageTask = plugin.getServer().getScheduler().runTaskTimer(plugin,
                () -> borderArray.forEach(this::borderDamage), 0, 1L);
        breakTask = plugin.getServer().getScheduler().runTaskTimer(plugin,
                () -> borderArray.forEach(this::borderBreakBlocks), 0, 1L);
        showWarning = plugin.getServer().getScheduler().runTaskTimer(plugin,
                () -> borderArray.forEach(this::showBorderWarning), 0, 20L);

    }

    private void showBorderWarning(Border border) {
       border.showWarning(plugin);
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

    public Border createBorder(double startHeight, double endHeight, String direction, double velocity, Location flpos,
                               Location brpos, String type){
        Border border = new Border(startHeight, endHeight, direction, velocity, flpos, brpos, type);
        borderArray.add(border);
        return border;
    }

    public String getBorderList() {
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
       return "Success";
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
            case "type":
                border.setType(value);
                return "\nSet type to " + value;
            case "damagepause":
                border.setDamagePause(value);
                return "\nSet damage pause to " + value;
        }
        return "Something went wrong";
    }
}
