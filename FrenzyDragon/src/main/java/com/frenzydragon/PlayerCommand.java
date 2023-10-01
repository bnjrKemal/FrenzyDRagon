package com.frenzydragon;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static com.frenzydragon.FrenzyDragon.*;

public class PlayerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        String topFormat = main.getConfig().getString("top-format");

        int topList = main.getConfig().getInt("top-list");

        List<String> stringList = new ArrayList<>();

        int i = 1;
        for(UUID uuid : allTopList()){
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            double score = getYaml().getDouble(player.getUniqueId() + "") + attackers.getOrDefault(player.getUniqueId(), 0.0);
            int killCount = 0;
            if(getYaml().get("kill-count." + player.getUniqueId()) != null){
                killCount = getYaml().getInt("kill-count." + player.getUniqueId());
            }
            String str = topFormat
                    .replace("{top-index}", i + "")
                    .replace("{top-key}", player.getName())
                    .replace("{top-value}", String.format("%.2f", score))
                    .replace("{kill-count}", killCount + "");
            stringList.add(str);
            if(i++ == topList) break;
        }

        for(String str : main.getConfig().getStringList("top")){
            if(str.contains("{top-format}")) {
                stringList.forEach(str1 -> sender.sendMessage(ChatColor.translateAlternateColorCodes('&', str1)));
                continue;
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', str));
        }

        return false;
    }

    private List<UUID> allTopList(){

        HashMap<UUID, Double> hashMap = new HashMap<>();

        for(String uuid : getYaml().getKeys(false)){
            if(uuid.equals("kill-count")) continue;
            hashMap.put(UUID.fromString(uuid), getYaml().getDouble(uuid));
        }

        for(Map.Entry<UUID, Double> entry : attackers.entrySet()){
            hashMap.put(entry.getKey(), hashMap.getOrDefault(entry.getKey(), 0.0) + entry.getValue());
        }

        return Util.mapTop(hashMap, false);
    }

}
