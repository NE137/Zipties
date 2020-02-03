package org.mhdvsolutions.zipties.listeners;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.bukkit.ChatColor;
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
    public static HashMap<UUID, Integer> haveRestrained = new HashMap<UUID, Integer>();
    private static final HashBasedTable<UUID, UUID, Integer> loggedWhile = HashBasedTable.create();
    private static final HashBasedTable<UUID, UUID, Integer> currentlySitting = HashBasedTable.create();

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
        }
    }
    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID prisonerId = player.getUniqueId();
        if (api.isRestrained(player)) {
            api.release(player, ReleaseType.OTHER);
            loggedWhile.put(prisonerId, prisonerId, 0);
            return;
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
        if (player.isSneaking()) {
            if(api.isRestrained(player)) {
                Msg.config(player, Message.RESTRAINED_ISALREADYRESTRAINED);
                return;
            }
            if(api.isRestrained(prisoner)) {
                if(api.getRestrainedBy(prisoner.getUniqueId()) == player.getUniqueId()) {
                    UUID prisonerId = prisoner.getUniqueId();
                    if (currentlySitting.containsRow(prisonerId)) {
                        currentlySitting.rowMap().remove(prisonerId);
                        api.standup(player, prisoner);
                        return;
                    } else {
                        currentlySitting.put(prisonerId,prisonerId,0);
                        api.sit(player,prisoner);
                        return;
                    }
                }
                return;
            }
            return;
        }

        if (itemStack.isSimilar(api.getZiptieItem())
                && !prisoner.hasPermission("zipties.bypass")) {
            if(api.isRestrained(player)) {
                Msg.config(player, Message.RESTRAINED_ISALREADYRESTRAINED);
                return;
            }
            if (haveRestrained.containsKey(player.getUniqueId())) {
                Msg.config(player, Message.RESTRAINED_ALREADYHAVE);
                return;
            }
            if(api.isRestrained(prisoner)) {
                Msg.config(player, Message.RESTRAINED_ALREADYOTHER);
                return;
            }
            if (Zipties.getPlugin().getConfig().getBoolean("settings.ziptiesUsePermission") == true) {
                if (!player.hasPermission("zipties.use")) {
                    Msg.config(player, Message.COMMANDS_PERMISSION);
                    return;
                }
            }
            if (Zipties.getPlugin().getConfig().getBoolean("settings.healthNerf") == true) {
                double required = Zipties.getPlugin().getConfig().getDouble("settings.required");
                double health = prisoner.getHealth();
                if (health > required) {
                    Msg.config(player, Message.MESSAGES_HEALTHTOOHIGH, "%prisoner%", prisoner.getName());
                    return;
                }
            }
            haveRestrained.put(player.getUniqueId(), 0);
            double radius = 4D;
            if (!(player.getLocation().distance(prisoner.getLocation()) <= radius)) {
                Msg.config(player, Message.MESSAGES_CLOSER);
                haveRestrained.remove(player.getUniqueId());
                return;
            }

            event.setCancelled(true);
            Msg.config(player, Message.MESSAGES_INPROGRESS, "%prisoner%", prisoner.getName());
            Msg.config(prisoner, Message.MESSAGES_BEGIN, "%restrainer%", player.getName());
            prisoner.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "!", Escape.color(String.valueOf(Message.MESSAGES_BEINGRESTRAINED)), 5, 80,5);
            // Create the task anonymously and schedule to run it once, after x ticks
            new BukkitRunnable() {

                @Override
                public void run() {
                    // What you want to schedule goes here
                    if(api.isRestrained(player.getUniqueId())) {
                        Msg.config(player, Message.MESSAGES_RESTRAINFAIL);
                        return;
                    }
                    if (player.getLocation().distance(prisoner.getLocation()) <= radius) {
                        api.restrain(player, prisoner);
                        if (player.getInventory().getItemInMainHand().isSimilar(api.getZiptieItem()) && Zipties.getPlugin().getConfig().getBoolean("settings.removeOnUse") == true) {
                            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
                        }
                        return;
                    }
                    else {
                        Msg.config(player, Message.MESSAGES_RESTRAINFAIL);
                        haveRestrained.remove(player.getUniqueId());
                        return;
                    }
                }

            }.runTaskLater(this.plugin, plugin.getConfig().getInt("settings.tieTime"));
            return;
        }

        if (itemStack.isSimilar(api.getCuttersItem()) && api.isRestrained(prisoner)) {
            event.setCancelled(true);
            UUID playerId = player.getUniqueId(), prisonerId = prisoner.getUniqueId();
            if (api.isRestrained(playerId)) {
                Msg.config(player, Message.MESSAGES_CANNOTDOTHAT);
                return;
            }
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
                    if (player.getInventory().getItemInMainHand().isSimilar(api.getCuttersItem()) && Zipties.getPlugin().getConfig().getBoolean("settings.removeOnUse") == true) {
                        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
                    }
                } else {
                    breakAttempts.put(prisonerId, attempt);
                    Msg.config(player, Message.ESCAPED_ALMOST,"%progress%", (attempt+"/"+count));
                }
            }
        }
    }

}
