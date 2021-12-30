package me.zorua162.heightborder.commands.subcommands;

import me.zorua162.heightborder.HeightBorder;
import me.zorua162.heightborder.commands.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ListCommand extends SubCommand {

    HeightBorder plugin;
    public ListCommand(HeightBorder plugin){
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "list current height borders";
    }

    @Override
    public String getSyntax() {
        return null;
    }

    @Override
    public void perform(Player player, String[] args) {

        //TODO get the list input here

        player.sendMessage("Current border list: " + plugin.borderManager.getBorderList());
    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }


}
