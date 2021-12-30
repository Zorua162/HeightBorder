package me.zorua162.heightborder.commands.subcommands;

import me.zorua162.heightborder.HeightBorder;
import me.zorua162.heightborder.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SetCommand extends SubCommand {

    HeightBorder plugin;
    // Possible parameters for this command
    List<String> possibleParams = Arrays.asList("currentheight",
            "endheight",
            "direction",
            "velocity",
            "pos1x",
            "pos1z",
            "pos2x",
            "pos2z",
            "type",
            "damagepause");

    public SetCommand(HeightBorder plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "Set a value of a border";
    }

    @Override
    public String getSyntax() {
        return "/hb set <border id> <value to set> <value>";
    }

    @Override
    public void perform(Player player, String[] args) {
        StringBuilder outString = new StringBuilder();
        // String out = plugin.borderManager.deleteBorder(args[1]);
        int end;
        // Should be an even number of parameters arguments, dump the last argument if there is an extra one
        if (args.length % 2 == 1){
           end = args.length - 1;
           outString.append("Incorrect number of arguments, dropped last as no value was found");
        } else {
            end = args.length;
        }
        for (int i = 2; i<end; i = i + 2) {
            if (possibleParams.contains(args[i])){
                outString.append(plugin.borderManager.setParameter(args[1], args[i], args[i + 1]) + ", ");
            } else {
               outString.append("Unknown parameter: " + args[i]);
            }
        }
        player.sendMessage(outString.toString());
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        if (args.length == 2){
            // border selection from ids
            return plugin.borderManager.getBorderIdList();
        }
        if (args.length % 2 == 1){
            // border properties that can be set
            return possibleParams;
        } else {
            switch (args[args.length-2]) {
                case "currentheight":
                    return Arrays.asList("256", "128", "64", "0");
                case "endheight":
                    return Arrays.asList("0", "64", "128", "256");
                case "direction":
                    return Arrays.asList("up", "down");
                case "velocity":
                    return Arrays.asList("60", "7");
                case "pos1x":
                case "pos1z":
                    return Arrays.asList("5", "50");
                case "pos2x":
                case "pos2z":
                    return Arrays.asList("-5", "50");
                case "particlecolour":
                    return Collections.singletonList("255");
                case "type":
                    return Arrays.asList("break", "damage");
                case "damagepause":
                    return Collections.singletonList("20");
            }
            return Collections.singletonList("unknown");
        }
    }
}
