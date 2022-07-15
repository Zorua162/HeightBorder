package me.zorua162.heightborder.commands.subcommands;

import me.zorua162.heightborder.HeightBorder;
import me.zorua162.heightborder.commands.SubCommand;
import org.bukkit.entity.Player;

import java.util.List;

public class StartCommand extends SubCommand {

    HeightBorder plugin;
    public StartCommand(HeightBorder plugin){
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Start all stopped borders";
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public void perform(Player player, String[] args) {

        plugin.borderManager.startBorders();
        player.sendMessage("Started the borders moving");
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }


}
