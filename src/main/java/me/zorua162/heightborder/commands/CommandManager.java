package me.zorua162.heightborder.commands;


import me.zorua162.heightborder.HeightBorder;
import me.zorua162.heightborder.commands.subcommands.*;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

// command manager framework credit:
// https://gitlab.com/kodysimpson/updated-command-manager
public class CommandManager implements TabExecutor {

    private final ArrayList<SubCommand> subcommands = new ArrayList<>();
    HeightBorder heightBorder;

    public CommandManager(HeightBorder heightBorder){
        this.heightBorder = heightBorder;
        //Get the subcommands so we can access them in the command manager class(here)
        subcommands.add(new ListCommand(heightBorder));
        subcommands.add(new CreateCommand(heightBorder));
        subcommands.add(new DeleteCommand(heightBorder));
        subcommands.add(new SetCommand(heightBorder));
        subcommands.add(new SetupCommand(heightBorder));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Requiring player for the moment as their current world is used for the create command
        if (sender instanceof Player){
            Player p = (Player) sender;

            if (args.length > 0){
                for (int i = 0; i < getSubCommands().size(); i++){
                    if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName())){
                        getSubCommands().get(i).perform(p, args);
                    }
                }
            }

        } else {
            sender.sendMessage(ChatColor.RED + "[HeightBorder] Command usage from terminal currently disabled");
        }
        return true;
    }

    public ArrayList<SubCommand> getSubCommands(){
        return subcommands;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length == 1){ //heightborder <subcommand> <args>
            ArrayList<String> subcommandsArguments = new ArrayList<>();

            for (int i = 0; i < getSubCommands().size(); i++){
                subcommandsArguments.add(getSubCommands().get(i).getName());
            }

            return subcommandsArguments;
        }else if(args.length >= 2){
            for (int i = 0; i < getSubCommands().size(); i++){
                if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName())){
                    return getSubCommands().get(i).getSubcommandArguments((Player) sender, args);
                }
            }
        }

        return null;
    }
}
