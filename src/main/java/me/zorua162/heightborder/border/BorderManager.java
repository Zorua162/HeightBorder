package me.zorua162.heightborder.border;


import me.zorua162.heightborder.HeightBorder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.logging.Logger;

public class BorderManager {

    ArrayList<Border> borderArray = new ArrayList<Border>();
    private Logger log;
    HeightBorder plugin;


    public BorderManager(HeightBorder plugin){
        this.log = Bukkit.getLogger();
        this.plugin = plugin;
    }

    public void setup() {
        BukkitTask displayTask;
        BukkitTask moveTask;
        BukkitTask damageTask;
        BukkitTask breakTask;
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
    public int getBorderListSize() {
        return borderArray.size();
    }
}
