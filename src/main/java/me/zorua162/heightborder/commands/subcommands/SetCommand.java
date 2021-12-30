package me.zorua162.heightborder.commands.subcommands;

import me.zorua162.heightborder.commands.SubCommand;
import org.bukkit.entity.Player;

import java.util.List;

public class SetCommand extends SubCommand {
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
        return "/hb set <key> <value>";
    }

    @Override
    public void perform(Player player, String[] args) {

    }

    @Override
    public List<String> getSubcommandArguments(Player player, String[] args) {
        return null;
    }
}
