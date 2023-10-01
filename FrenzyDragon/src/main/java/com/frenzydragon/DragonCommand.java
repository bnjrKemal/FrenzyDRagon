package com.frenzydragon;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DragonCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player) {
            if(!sender.isOp()){
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FrenzyDragon.main.getConfig().getString("no-permission")));
                return false;
            }
        }

        try {
            switch (args[0]) {

                case "setlocation":
                    if(!(sender instanceof Player)){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FrenzyDragon.main.getConfig().getString("you-can-not-use-in-the-console")));
                        return false;
                    }

                    FrenzyDragon.main.getConfig().set("location", ((Player) sender).getLocation());
                    FrenzyDragon.location = ((Player) sender).getLocation();
                    FrenzyDragon.main.saveConfig();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FrenzyDragon.main.getConfig().getString("saved-location-for-the-dragon")));
                    return true;

                case "setspawn":
                    if(!(sender instanceof Player)){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FrenzyDragon.main.getConfig().getString("you-can-not-use-in-the-console")));
                        return false;
                    }
                    FrenzyDragon.main.getConfig().set("spawn", ((Player) sender).getLocation());
                    FrenzyDragon.spawn = ((Player) sender).getLocation();
                    FrenzyDragon.main.saveConfig();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FrenzyDragon.main.getConfig().getString("saved-location-for-the-spawn")));
                    return true;

                case "start":
                    if(FrenzyDragon.event){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FrenzyDragon.main.getConfig().getString("already-stopped-event")));
                        return false;
                    }
                    if(FrenzyDragon.createDragon(sender))
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FrenzyDragon.main.getConfig().getString("started-event")));
                    return true;
                case "stop":
                    if(!FrenzyDragon.event){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FrenzyDragon.main.getConfig().getString("already-started-event")));
                        return false;
                    }
                    if(FrenzyDragon.rewards())
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FrenzyDragon.main.getConfig().getString("stopped-event")));
                    return true;
                case "cancel":
                    if(!FrenzyDragon.event){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FrenzyDragon.main.getConfig().getString("already-stopped-event")));
                        return false;
                    }
                    if(FrenzyDragon.cancelDragon())
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FrenzyDragon.main.getConfig().getString("cancelled-event")));
                    return true;
                case "reload":
                    if(FrenzyDragon.event){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FrenzyDragon.main.getConfig().getString("you-must-to-cancel-the-event")));
                        return false;
                    }
                    FrenzyDragon.main.reloadConfig();
                    FrenzyDragon.location = FrenzyDragon.main.getConfig().getLocation("location");
                    FrenzyDragon.spawn = FrenzyDragon.main.getConfig().getLocation("spawn");
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FrenzyDragon.main.getConfig().getString("reload")));
                    return true;
                case "setitems":
                    try {
                        if(!(sender instanceof Player)){
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FrenzyDragon.main.getConfig().getString("you-can-not-use-in-the-console")));
                            return false;
                        }
                        int number = Integer.parseInt(args[1]);
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FrenzyDragon.main.getConfig().getString("saved-items").replace("{number}", number + "")));

                        List<ItemStack> itemStackList = new ArrayList<>();
                        for(ItemStack itemStack : ((Player) sender).getInventory().getContents()){
                            if(itemStack == null) continue;
                            itemStackList.add(itemStack);
                        }

                        FrenzyDragon.main.getConfig().set("rewards." + number + ".items", itemStackList);
                        FrenzyDragon.main.saveConfig();
                        return true;
                    }catch(NumberFormatException | ArrayIndexOutOfBoundsException e){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FrenzyDragon.main.getConfig().getString("invalid-argument")));
                        return true;
                    }
                case "delete":

                    if(FrenzyDragon.event){
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FrenzyDragon.main.getConfig().getString("you-must-to-cancel-the-event")));
                        return false;
                    }

                    FrenzyDragon.file.delete();
                    FrenzyDragon.loadData();
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FrenzyDragon.main.getConfig().getString("deleted-file")));

                    return true;
            }
        }catch(ArrayIndexOutOfBoundsException ignored) {}

        sender.sendMessage(ChatColor.DARK_BLUE + "Help >>" + ChatColor.AQUA + "by bnjrKemal (Discord: " + ChatColor.WHITE +  " bnjrkemal" + ChatColor.AQUA + ")");
        sender.sendMessage(ChatColor.DARK_BLUE + "1) " + ChatColor.WHITE + "/frenzydragon setitems <number>");
        sender.sendMessage(ChatColor.DARK_BLUE + "2) " + ChatColor.WHITE + "/frenzydragon setlocation");
        sender.sendMessage(ChatColor.DARK_BLUE + "3) " + ChatColor.WHITE + "/frenzydragon setspawn");
        sender.sendMessage(ChatColor.DARK_BLUE + "4) " + ChatColor.WHITE + "/frenzydragon start");
        sender.sendMessage(ChatColor.DARK_BLUE + "5) " + ChatColor.WHITE + "/frenzydragon stop");
        sender.sendMessage(ChatColor.DARK_BLUE + "6) " + ChatColor.WHITE + "/frenzydragon cancel");
        sender.sendMessage(ChatColor.DARK_BLUE + "7) " + ChatColor.WHITE + "/frenzydragon reload");
        sender.sendMessage(ChatColor.DARK_BLUE + "7) " + ChatColor.WHITE + "/frenzydragon delete");
        sender.sendMessage(ChatColor.DARK_BLUE + "8) " + ChatColor.WHITE + "/ejderha top");

        return false;
    }
}
