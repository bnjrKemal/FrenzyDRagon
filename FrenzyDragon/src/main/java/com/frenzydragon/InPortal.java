package com.frenzydragon;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class InPortal implements Listener {

    HashMap<UUID, Long> countdown = new HashMap<>();

    @EventHandler
    public void onPortal(PlayerPortalEvent e){
        if(FrenzyDragon.location == null || FrenzyDragon.spawn == null) return;
        if(!e.getTo().getWorld().equals(FrenzyDragon.location.getWorld())) return;
        if(!FrenzyDragon.event) {
            e.setCancelled(true);
            if(countdown.get(e.getPlayer().getUniqueId()) != null){
                if(System.currentTimeMillis() - countdown.get(e.getPlayer().getUniqueId()) < 1000 * 1)
                    return;
                countdown.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', FrenzyDragon.main.getConfig().getString("no-enter")));
                String cmd = FrenzyDragon.main.getConfig().getString("portal-command-in-err");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", e.getPlayer().getName()));
            }
            countdown.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
        }

    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e){
        if(!FrenzyDragon.event) return;
        if(FrenzyDragon.location == null || FrenzyDragon.spawn == null) return;
        if(!(e.getDamager() instanceof Player)) return;
        if(!(e.getEntity() instanceof EnderDragon)) return;
        if(!e.getEntity().getWorld().getName().equals(FrenzyDragon.location.getWorld().getName())) return;
        UUID uuid = e.getDamager().getUniqueId();
        FrenzyDragon.attackers.put(uuid, FrenzyDragon.attackers.getOrDefault(uuid, 0.0) + e.getDamage());
        String text = ChatColor.translateAlternateColorCodes('&', FrenzyDragon.main.getConfig().getString("action-bar"));
        double damage = FrenzyDragon.attackers.get(uuid).doubleValue();
        double maxhealth = ((EnderDragon) e.getEntity()).getHealth();
        e.getDamager().sendMessage(text.replace("{damage}", String.format("%.2f", damage)).replace("{maxhealth}", String.format("%.2f", maxhealth)));

    }

    @EventHandler
    public void onKill(EntityDeathEvent e){
        if(!FrenzyDragon.event) return;
        if(FrenzyDragon.location == null || FrenzyDragon.spawn == null) return;
        if(!e.getEntity().getWorld().getName().equals(FrenzyDragon.location.getWorld().getName())) return;
        if(!(e.getEntity() instanceof EnderDragon)) return;
        int killCount = 0;
        if(FrenzyDragon.getYaml().get("kill-count." + e.getEntity().getKiller().getUniqueId()) != null){
            killCount = FrenzyDragon.getYaml().getInt("kill-count." + e.getEntity().getKiller().getUniqueId());
        }
        FrenzyDragon.getYaml().set("kill-count." + e.getEntity().getKiller().getUniqueId(), killCount + 1);
        FrenzyDragon.rewards();
    }

}
