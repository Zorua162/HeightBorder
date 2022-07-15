package me.zorua162.heightborder.commands.subcommands;

import me.zorua162.heightborder.HeightBorder;
import me.zorua162.heightborder.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SetupCommand extends SubCommand {

    HeightBorder plugin;
    // Possible parameters for this command
    List<String> possibleParams = Arrays.asList("80", "90", "100");

    public SetupCommand(HeightBorder plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "setup";
    }

    @Override
    public String getDescription() {
        return "Sets up two vertical borders which will meet 10 blocks apart";
    }

    @Override
    public String getSyntax() {
        return "/hb setup <end center> [time to finish]";
    }

    @Override
    public void perform(Player player, String[] args) {
        StringBuilder outString = new StringBuilder();
        // Should be an even number of parameters arguments, dump the last argument if there is an extra one
        if (args.length == 1){
           outString.append("No end height given, please specify the y coordinate of where the end box should be");
        }

        plugin.borderManager.setupBorders(player, args);

        player.sendMessage(outString.toString());
        plugin.borderManager.saveBorders();
        for(Player msgAll: player.getWorld().getPlayers()) {
            msgAll.sendMessage(ChatColor.RED + "Height borders have started to close, get to the surface!");
        }
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        if (args.length == 1){
            // border selection from ids
            return possibleParams;
        }
        return Collections.singletonList("end y center");
        }
}
