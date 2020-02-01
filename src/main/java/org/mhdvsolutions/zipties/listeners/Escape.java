package org.mhdvsolutions.zipties.listeners;

import com.google.common.collect.ImmutableSet;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.mhdvsolutions.zipties.PrisonerManager;
import org.mhdvsolutions.zipties.Zipties;
import org.mhdvsolutions.zipties.api.ReleaseType;
import org.mhdvsolutions.zipties.api.ZiptiesApi;
import org.mhdvsolutions.zipties.utils.Message;
import org.mhdvsolutions.zipties.utils.Msg;

import java.util.HashMap;
import java.util.UUID;

public final class Escape implements Listener {

    private final Zipties plugin;
    private final ZiptiesApi api;
    public HashMap<String, Long> cooldowns = new HashMap<String, Long>();
    public Escape(Zipties plugin) {
        this.plugin = plugin;
        this.api = Zipties.getApi();
    }

    @EventHandler
    public void onPigLeave(VehicleExitEvent event) {
        if (!(event.getVehicle() instanceof Pig) || !(event.getExited() instanceof Player)) {
            return;
        }

        Pig pig = (Pig) event.getVehicle();
        if (!pig.getName().startsWith("Restraint Pig") || pig.isDead()) {
            return;
        }

        Player player = (Player) event.getExited();
        if (api.isRestrained(player)) {
            event.setCancelled(true);
            int cooldownTime = 5; // Get number of seconds from wherever you want
            if(cooldowns.containsKey(player.getName())) {
                long secondsLeft = ((cooldowns.get(player.getName())/1000)+cooldownTime) - (System.currentTimeMillis()/1000);
                if(secondsLeft>0) {
                    // Still cooling down

                    String msg = color(replace(Message.MESSAGES_COOLDOWN.toString(), "%cooldown%", secondsLeft));
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
                    return;
                }
            }
            // No cooldown found or cooldown has expired, save new cooldown
            cooldowns.put(player.getName(), System.currentTimeMillis());
            // Do Command Here
            int random = (int)(Math.random() * 20 + 1);
            if (random > 18) {
                api.release(player, ReleaseType.ESCAPE);
            }
            else {
                Msg.config(player, Message.MESSAGES_ESCAPEFAIL);
            }
        }
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (!(event.getEntered() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntered();
        if (api.isRestrained(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeashBreak(EntityUnleashEvent event) {
        if (!(event.getEntity() instanceof Pig)) {
            return;
        }
        /*
        Pig pig = (Pig) event.getEntity();
        if (!pig.getName().startsWith("Restraint Pig") || pig.isDead()) {
            Player player = (Player) event.getEntity().getPassengers().get(new Integer(0));
            api.release(player, ReleaseType.OTHER);
            return;
        } */
        Player prisoner = (Player) event.getEntity().getPassengers().get(new Integer(0));
        if (api.isRestrained(prisoner.getUniqueId())) {
            api.release(prisoner, ReleaseType.OTHER);
        }
    }

    private static String replace(String string, Object... replacements) {
        if (replacements.length < 2 || replacements.length % 2 != 0) {
            throw new IllegalArgumentException("Invalid Replacements");
        }

        for (int i = 0; i < replacements.length; i += 2) {
            String key = String.valueOf(replacements[i]), value = String.valueOf(replacements[i + 1]);
            if (value == null) value = "";
            string = string.replaceAll(key, value);
        }
        return string;
    }

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
