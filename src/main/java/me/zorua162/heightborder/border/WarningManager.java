package me.zorua162.heightborder.border;

import com.github.yannicklamprecht.worldborder.api.IWorldBorder;
import com.github.yannicklamprecht.worldborder.api.WorldBorderAction;
import me.zorua162.heightborder.HeightBorder;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class WarningManager {
    // TODO Delete save of border reddening when a border is deleted

    BorderManager borderManager;

    // Reddened list: If a player is in the list then they should be reddened
    // if they are not in the list then they shouldn't be reddened
    Map<Player, Map<Border, Boolean>> reddenedMap = new HashMap<>();
    public WarningManager(BorderManager borderManager) {
       this.borderManager = borderManager;
    }

    private void setPlayerScreen(Player player, Border border, Boolean bool) {
        if (reddenedMap.containsKey(player)) {
            Map<Border, Boolean> playerMap = reddenedMap.get(player);
            playerMap.put(border, bool);
        } else {
            Map<Border, Boolean> playerMap = new HashMap<>();
            playerMap.put(border, bool);
            reddenedMap.put(player, playerMap);
        }
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
}
