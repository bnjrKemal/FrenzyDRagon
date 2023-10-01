package com.frenzydragon;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;

public final class FrenzyDragon extends JavaPlugin {

    static File file;
    static YamlConfiguration yamlConfiguration;

    static boolean event;
    static Location location;
    static Location spawn;
    static HashMap<UUID, Double> attackers;
    static FrenzyDragon main;

    @Override
    public void onEnable() {

        main = this;
        event = false;
        attackers = new HashMap<>();
        getServer().getPluginManager().registerEvents(new InPortal(), this);
        getCommand("frenzydragon").setExecutor(new DragonCommand());
        getCommand("ejderha").setExecutor(new PlayerCommand());
        getConfig().options().copyDefaults(true);
        saveConfig();
        loadData();
        location = getConfig().getLocation("location");
        spawn = getConfig().getLocation("spawn");
    }

    @Override
    public void onDisable(){
        if(event)
            cancelDragon();
    }

    public static void loadData() {
        file = new File(main.getDataFolder(), "data.yml");
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    }



    public static YamlConfiguration getYaml() {
        return yamlConfiguration;
    }

    public static void saveData(){
        for(Map.Entry<UUID, Double> entry : attackers.entrySet()) {
            UUID key = entry.getKey();
            double value = entry.getValue();
            if(getYaml().get(key + "") == null){
                getYaml().set(key + "", value);
            } else if(getYaml().get(key + "") != null) {
                double oldValue = getYaml().getDouble(key + "");
                double result = Double.parseDouble(new DecimalFormat("0.00").format(value + oldValue));
                getYaml().set(key + "", result);
            }
        }
        attackers.clear();
        try {
            getYaml().save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean createDragon(CommandSender sender){

        if(location == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("you-must-set-the-dragon-location")));
            return false;
        }
        if(spawn == null) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("you-must-set-the-spawn-location")));
            return false;
        }

        for(Entity entity : location.getWorld().getEntities()){
            if(entity.getType().equals(EntityType.ENDER_DRAGON)){
                location.getWorld().getEntity(entity.getUniqueId()).remove();
            }
        }

        location.getWorld().spawnEntity(location, EntityType.ENDER_DRAGON);
        for(Player player : Bukkit.getOnlinePlayers())
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("start")));

        event = true;
        return true;
    }

    public static boolean rewards() {

        if(!event) return false;

        for(Entity entity : location.getWorld().getEntities()){
            if(entity.getType().equals(EntityType.ENDER_DRAGON)){
                location.getWorld().getEntity(entity.getUniqueId()).remove();
            }
        }

        for (Player player : Bukkit.getWorld(location.getWorld().getName()).getPlayers())
            player.teleport(main.getConfig().getLocation("spawn"));

        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("finish"))));
        int i = 1;
        for (Player player : list()) {

            //commands
            if(main.getConfig().getStringList("rewards." + i + ".commands") != null){
                for(String cmds : main.getConfig().getStringList("rewards." + i + ".commands")){
                    String cmd = cmds.replace("%player%", player.getName());
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                }
            }

            //items
            if(main.getConfig().isList("rewards." + i + ".items")){
                List<ItemStack> itemStacks = new ArrayList<>();
                for(Object keys : main.getConfig().getList("rewards." + i + ".items")) {
                    ItemStack itemstack = (ItemStack) keys;
                    itemStacks.add(itemstack);
                }
                Map<Integer, ItemStack> map = player.getInventory().addItem(itemStacks.<ItemStack>toArray(new ItemStack[0]));
                if (!map.isEmpty()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("your-chest-is-fully")));
                    for (ItemStack drops : map.values())
                        player.getWorld().dropItemNaturally(player.getLocation(), drops);
                }
            }
            i++;
        }

        event = false;
        saveData();
        return true;
    }

    public static boolean cancelDragon() {
        if(!event) return false;
        for(Entity entity : location.getWorld().getEntities()){
            if(entity.getType().equals(EntityType.ENDER_DRAGON)){
                location.getWorld().getEntity(entity.getUniqueId()).remove();
            }
        }
        for (Player player : Bukkit.getWorld(location.getWorld().getName()).getPlayers())
            player.teleport(main.getConfig().getLocation("spawn"));

        Bukkit.getOnlinePlayers().forEach(player -> player.sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("cancel"))));
        event = false;
        saveData();
        return true;
    }

    public static List<Player> list(){

        int i = 1;
        List<Player> players = new ArrayList<>();
        for(UUID uuid : Util.mapTop(attackers, false)){
            if(main.getConfig().getConfigurationSection("rewards." + i++) == null) break;
            players.add(Bukkit.getPlayer(uuid));
        }
        return players;
    }


























}
