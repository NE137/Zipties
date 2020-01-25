/*
 * Zipties - Player restraint system.
 * Copyright (c) 2018, Mitchell Cook <https://github.com/Mishyy>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.mhdvsolutions.zipties.listeners;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;
import org.bukkit.event.player.PlayerUnleashEntityEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.mhdvsolutions.zipties.Zipties;
import org.mhdvsolutions.zipties.api.ReleaseType;
import org.mhdvsolutions.zipties.api.ZiptiesApi;
import org.mhdvsolutions.zipties.utils.Message;
import org.mhdvsolutions.zipties.utils.Msg;

import java.util.HashMap;

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

        Player player = (Player) event.getEntity().getPassengers().get(new Integer(0));
        if (api.isRestrained(player)) {
            api.release(player, ReleaseType.OTHER);
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
