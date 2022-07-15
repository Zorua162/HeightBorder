package me.zorua162.heightborder.commands;

import me.zorua162.heightborder.HeightBorder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.List;

public class ToggleDisplay implements CommandExecutor {

    HeightBorder heightBorder;

    public ToggleDisplay(HeightBorder heightBorder) {
        this.heightBorder = heightBorder;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player p = (Player) sender;
            Boolean displayed;
            displayed = heightBorder.getBorderManager().getWarningManager().toggleDisplay(p);
            String msg;
            if (displayed) {
                msg = ChatColor.GREEN + "Enabled border distance display, it will show when borders are present";
            } else {
                msg = ChatColor.RED + "Disabled border distance display, it will not show when borders are present";
            }
            p.sendMessage(msg);

        } else {
            sender.sendMessage(ChatColor.RED + "[HeightBorder] Command usage from terminal currently disabled");
        }
        return true;
    }

}
