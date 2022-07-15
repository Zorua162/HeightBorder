package me.zorua162.heightborder.border;

import com.github.yannicklamprecht.worldborder.api.IWorldBorder;
import com.github.yannicklamprecht.worldborder.api.WorldBorderAction;
import me.zorua162.heightborder.HeightBorder;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.abs;

public class WarningManager {
    // TODO Delete save of border reddening when a border is deleted

    BorderManager borderManager;

    // Reddened list: If a player is in the list then they should be reddened
    // if they are not in the list then they shouldn't be reddened
    Map<Player, Map<Border, Boolean>> reddenedMap = new HashMap<>();
    public WarningManager(BorderManager borderManager) {
       this.borderManager = borderManager;
    }

    private ComponentBuilder buildWarningMessage(Player player, Integer distance, Border border) {
        String directionIndicator = "-";
        if (distance < 0) {
            directionIndicator = "↑";
        } else if (distance > 0){
            directionIndicator = "↓";
        }

        ComponentBuilder borderInfo;
        TextComponent colour = distanceColour(player, distance, border);
        if ((player.getGameMode() == GameMode.SPECTATOR) || (player.getGameMode() == GameMode.CREATIVE)) {
            borderInfo = opBorderInfo(distance, border, directionIndicator, colour);
        } else {
            borderInfo = playerBorderInfo(distance, border, directionIndicator, colour);
        }
        // String borderInfo = directionIndicator + abs(distance);
        return borderInfo;
    }

    private String intToHex(Integer integer) {
        String outString;
        String hexValue = Integer.toHexString(integer);
        if (integer < 16) {
            outString = "0" + hexValue;
        } else if (integer > 255) {
            outString = hexValue;
        } else {
            outString = "FF";
        }
        return outString;
    }

    private TextComponent distanceColour(Player player, Integer distance, Border border) {
        // Get the colour of the warning distance from the given border
        TextComponent component = new TextComponent();
        String colourHexString;

        double endHeight = border.getEndHeight();
        double distanceForBorderToGo = endHeight - border.getCurrentHeight();
        // If border has finished moving switch to going by center of the 10 height instead
        if (distanceForBorderToGo == 0) {
            if (border.getDirection().equals("down")) {
                endHeight -= 5;
            } else {
                endHeight += 5;
            }
            distanceForBorderToGo = endHeight - border.getCurrentHeight();
        }
        int rgbNumber = (int) (255 - 512 * (distance / distanceForBorderToGo));

        if (rgbNumber > 255) {
            rgbNumber = 255;
        } else if (rgbNumber < -255) {
            rgbNumber = -255;
        }
        if (border.checkOutsideBorder(player)) {
            // Above border so set to red
            component.setColor(ChatColor.of("#Fc0000"));

        } else {
            if (rgbNumber > 0) {
                colourHexString = String.format("#FC%S00", intToHex(255 - rgbNumber));
            } else {
                // rgbNumber = 255 - abs(rgbNumber);
                colourHexString = String.format("#%SFC00", intToHex(255 - abs(rgbNumber)));
            }

            // Bukkit.getLogger().info(String.valueOf(rgbNumber));
            // Bukkit.getLogger().info(colourHexString);
            component.setColor(ChatColor.of(colourHexString));
            // component.setColor(ChatColor.of("#123456"));
            component.addExtra(colourHexString);
        }
        return component;
    }

    private ComponentBuilder playerBorderInfo(Integer distance, Border border, String directionIndicator,
                                              TextComponent colour) {
        // Colour, arrow
        ComponentBuilder borderInfo = new ComponentBuilder();
        borderInfo.append(colour);
        borderInfo.append(directionIndicator);
        return borderInfo;
    }

    private ComponentBuilder opBorderInfo(Integer distance, Border border, String directionIndicator,
                                          TextComponent colour) {
        // Colour, arrow, distance, y-coord
        ComponentBuilder borderInfo = new ComponentBuilder();
        borderInfo.append(colour);
        borderInfo.append(Integer.toString(abs(distance)));
        borderInfo.append(" ");
        borderInfo.append(directionIndicator);
        int roundCurrentHeight = ((int) border.currentHeight);
        borderInfo.append(" y=");
        borderInfo.append(Integer.toString(roundCurrentHeight));
        return borderInfo;
    }

    private void sendDistanceDisplay(Player player) {
        // Send the current distances to borders to the players
        ComponentBuilder toSend = new ComponentBuilder("Height Border distances| ");
        for (Border border : reddenedMap.get(player).keySet()) {
            int distance = border.getDistance(player);
            toSend.append(buildWarningMessage(player, distance, border).create());
            TextComponent divider = new TextComponent(" | ");
            divider.setColor(ChatColor.WHITE);
            toSend.append(divider);
        }
        // delete unneeded separator

        // toSend.delete(toSend.length()-2, toSend.length()-1);
        // Then send to player as a title
        // player.sendTitle("", toSend.toString(), 0, 20, 0);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, toSend.create());
    }

    private void setPlayerScreen(Player player, Border border, Boolean bool) {
        // Set whether it should be reddened or not
        if (reddenedMap.containsKey(player)) {
            Map<Border, Boolean> playerMap = reddenedMap.get(player);
            playerMap.put(border, bool);
        } else {
            Map<Border, Boolean> playerMap = new HashMap<>();
            playerMap.put(border, bool);
            reddenedMap.put(player, playerMap);
        }
        sendDistanceDisplay(player);
    }

    private boolean checkAllUnreddened(Player player) {
        Map<Border, Boolean> playerMap = reddenedMap.get(player);
        return !playerMap.containsValue(true);
    }
    public void setReddenPlayersScreen(HeightBorder plugin, Player player, Border border) {
        // Set a player to be reddened
        setPlayerScreen(player, border, true);
        // redden the players screen
        reddenPlayersScreen(plugin, player);
    }
    public void setUnReddenPlayersScreen(HeightBorder plugin, Player player, Border border) {
        // Set the player to be unredded by that border
        setPlayerScreen(player, border, false);
        // if no other borders are reddening the players screen then unredden it
        if (checkAllUnreddened(player)) {
            unReddenPlayersScreen(plugin, player);
        }
    }

    private void reddenPlayersScreen(HeightBorder plugin, Player player) {
        IWorldBorder border = plugin.worldBorderApi.getWorldBorder(player);
        border.setWarningDistanceInBlocks((int) border.getSize());
        border.send(player, WorldBorderAction.SET_WARNING_BLOCKS);
    }

    private void unReddenPlayersScreen(HeightBorder plugin, Player player) {
        IWorldBorder border = plugin.worldBorderApi.getWorldBorder(player);
        border.setWarningDistanceInBlocks(0);
        border.send(player, WorldBorderAction.SET_WARNING_BLOCKS);

    }

    public void clearWarnings() {
        reddenedMap.clear();
    }
}
