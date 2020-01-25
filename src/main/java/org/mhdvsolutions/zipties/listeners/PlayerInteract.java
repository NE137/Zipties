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

import com.google.common.collect.HashBasedTable;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.mhdvsolutions.zipties.Zipties;
import org.mhdvsolutions.zipties.api.ReleaseType;
import org.mhdvsolutions.zipties.api.ZiptiesApi;
import org.mhdvsolutions.zipties.utils.Message;
import org.mhdvsolutions.zipties.utils.Msg;

import java.util.HashMap;
import java.util.UUID;

public final class PlayerInteract implements Listener {
    
    private static final HashMap<UUID, Integer> breakAttempts = new HashMap<UUID, Integer>();
    private static final HashBasedTable<UUID, UUID, Integer> loggedWhile = HashBasedTable.create();

    private final Zipties plugin;
    private final ZiptiesApi api;

    public PlayerInteract(Zipties plugin) {
        this.plugin = plugin;
        this.api = Zipties.getApi();
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID prisonerId = player.getUniqueId();
        if (api.isRestrained(player)) {
            api.release(player, ReleaseType.OTHER);
            loggedWhile.put(prisonerId, prisonerId, 0);
        }
    }
    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID prisonerId = player.getUniqueId();
        if (api.isRestrained(player)) {
            api.release(player, ReleaseType.OTHER);
            loggedWhile.put(prisonerId, prisonerId, 0);
        }
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID prisonerId = player.getUniqueId();
        if (loggedWhile.containsRow(prisonerId)) {
            Msg.config(player, Message.RESTRAINED_LEFT);
            player.setHealth(0);
            loggedWhile.rowMap().remove(prisonerId);
        }
    }
    @EventHandler
    public void onRestrain(PlayerInteractAtEntityEvent event) throws InterruptedException {
        if (event.getHand() != EquipmentSlot.HAND || !(event.getRightClicked() instanceof Player)) {
            return;
        }

        Player player = event.getPlayer(), prisoner = (Player) event.getRightClicked();
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return;
        }

        if (itemStack.isSimilar(api.getZiptieItem())
                && player.hasPermission("zipties.use")
                && !prisoner.hasPermission("zipties.bypass")) {
            if(api.isRestrained(player)) {
                Msg.config(player, Message.RESTRAINED_ISALREADY);
                return;
            }
            if(api.isRestrained(prisoner)) {
                Msg.config(player, Message.RESTRAINED_ALREADY);
                return;
            }
            double radius = 4D; // whatever you want really
            if (!(player.getLocation().distance(prisoner.getLocation()) <= radius)) {
                Msg.config(player, Message.MESSAGES_CLOSER);
                return;
            }
            event.setCancelled(true);
            Msg.config(player, Message.MESSAGES_INPROGRESS, "%prisoner%", prisoner.getName());
            // Create the task anonymously and schedule to run it once, after 20 ticks
            new BukkitRunnable() {

                @Override
                public void run() {
                    // What you want to schedule goes here
                    if (player.getLocation().distance(prisoner.getLocation()) <= radius) {
                        api.restrain(player, prisoner);
                        return;
                    }
                    else {
                        Msg.config(player, Message.MESSAGES_RESTRAINFAIL);
                        return;
                    }
                }

            }.runTaskLater(this.plugin, plugin.getConfig().getInt("tieTime"));
            return;
        }

        if (itemStack.isSimilar(api.getCuttersItem()) && api.isRestrained(prisoner)) {
            event.setCancelled(true);
            UUID playerId = player.getUniqueId(), prisonerId = prisoner.getUniqueId();

            if (!breakAttempts.containsKey(prisonerId) && !breakAttempts.containsKey(playerId)) {
                breakAttempts.put(prisonerId, 0);
            }

            long count = plugin.getConfig().getInt("cutters.count");
            int attempt = breakAttempts.get(prisonerId);
            if (attempt < count) {
                if (++attempt == count) {
                    Msg.config(player, Message.ESCAPED_FREE, "%prisoner%", prisoner.getName());
                    api.release(prisoner, ReleaseType.OTHER);
                    breakAttempts.remove(prisonerId);
                } else {
                    breakAttempts.put(prisonerId, attempt);
                    Msg.config(player, Message.ESCAPED_ALMOST,"%progress%", (attempt+"/"+count));
                }
            }
        }
    }

}
