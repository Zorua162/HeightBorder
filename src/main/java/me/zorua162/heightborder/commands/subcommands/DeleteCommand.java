package me.zorua162.heightborder.commands.subcommands;

import me.zorua162.heightborder.HeightBorder;
import me.zorua162.heightborder.commands.SubCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DeleteCommand extends SubCommand {

    HeightBorder plugin;

    public DeleteCommand(HeightBorder plugin) {this.plugin = plugin;}
    @Override
    public String getName() {
        return "delete";
    }

    @Override
    public String getDescription() {
        return "delete a given height border by number";
    }

    @Override
    public String getSyntax() {
        return "<number> of border to be deleted given by /hb list";
    }

    @Override
    public void perform(Player player, String[] args) {
        String out = plugin.borderManager.deleteBorder(args[1]);
        if (out == "Success"){
            player.sendMessage("Successfully removed border id: " + args[1]);
        } else {
            player.sendMessage("Could not remove border id: " + args[1] +
            "\nError message: " + out);
        }

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return plugin.borderManager.getBorderIdList();
    }
}
