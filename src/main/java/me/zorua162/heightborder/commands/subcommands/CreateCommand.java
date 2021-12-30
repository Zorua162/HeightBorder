package me.zorua162.heightborder.commands.subcommands;

import me.zorua162.heightborder.HeightBorder;
import me.zorua162.heightborder.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class CreateCommand extends SubCommand {
    HeightBorder plugin;
    double startHeight;
    double endHeight;
    String direction;
    double velocity;
    double x1;
    double z1;
    double x2;
    double z2;
    String type;

    public CreateCommand(HeightBorder plugin) {
        this.plugin = plugin;
    }
    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getDescription() {
        return "Create a height world border";
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public void perform(Player player, String[] args) {
        createBorder(player, args);
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        switch (args.length) {
            case 2:
                // startHeight completion
                return Arrays.asList("256", "128", "64", "0");
            case 3:
                // endHeight completion
                return Arrays.asList("0", "5", "64", "128", "256");
            case 4:
                // direction
                return Arrays.asList("up", "down");
            case 5:
                // Velocity
                return Arrays.asList("7", "60");
            case 6:
            case 7:
                // z1
                // x1
                return Arrays.asList("5", "50");
            case 8:
            case 9:
                // z2
                // x2
                return Arrays.asList("-5", "50");
            case 10:
                // Type
                return Arrays.asList("damage", "break");
        }
        return null;
    }
    public void createBorder(Player player, String[] args) {

        if (!(player.hasPermission("world-border.commands.create-world-border"))) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command");
        }
        if (args.length <= 9) {
            player.sendMessage("Required arguments: startHeight, endHeight, <up|down>,"+
                    "velocity (in blocks per minute), x1, z1, x2, z2, Type");
            return;
        }
        if (!args[3].equals("up") && !args[3].equals("down")) {
            player.sendMessage("Must give direction for border to travel up or down " + args[3] + " given.");
            return;
        }

        startHeight = Double.parseDouble(args[1]);
        endHeight = Double.parseDouble(args[2]);
        direction = args[3];
        velocity = Double.parseDouble(args[4]);
        x1 = Double.parseDouble(args[5]);
        z1 = Double.parseDouble(args[6]);
        x2 = Double.parseDouble(args[7]);
        z2 = Double.parseDouble(args[8]);
        type = args[9];

        Location flpos = new Location(player.getWorld(), x1, startHeight, z1);
        Location brpos = new Location(player.getWorld(), x2, startHeight, z2);
        // Create the border
        plugin.borderManager.createBorder(startHeight, endHeight, direction, velocity, flpos, brpos, type);
        player.sendMessage("Successfully created world border");
    }
}
