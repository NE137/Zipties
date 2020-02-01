package org.mhdvsolutions.zipties;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.mhdvsolutions.zipties.api.ReleaseType;
import org.mhdvsolutions.zipties.api.ZiptiesApi;
import org.mhdvsolutions.zipties.api.events.PrisonerReleaseEvent;
import org.mhdvsolutions.zipties.api.events.PrisonerRestrainEvent;
import org.mhdvsolutions.zipties.listeners.PlayerInteract;
import org.mhdvsolutions.zipties.utils.Message;
import org.mhdvsolutions.zipties.utils.Msg;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class PrisonerManager implements ZiptiesApi {

    private static final PotionEffect INVIS_EFFECT = new PotionEffect(
            PotionEffectType.INVISIBILITY,
            Integer.MAX_VALUE, 0,
            false, false
    );
    private static final PotionEffect WEAKNESS_EFFECT = new PotionEffect(
            PotionEffectType.WEAKNESS,
            Integer.MAX_VALUE, 10,
            false, false
    );
    private final Zipties plugin;
    private final SetMultimap<UUID, UUID> prisoners = HashMultimap.create();
    private final SetMultimap<UUID, UUID> satDownPrisoners = HashMultimap.create();
    private final SetMultimap<UUID, UUID> beingSatDownPrisoners = HashMultimap.create();
    private static final Map<UUID, Pig> pigs = new HashMap<>();

    PrisonerManager(Zipties plugin) {
        this.plugin = plugin;
    }

    @Override
    public ItemStack getZiptieItem() {
        FileConfiguration configuration = plugin.getConfig();
        String type = configuration.getString("items.zipties.type");
        int data = configuration.getInt("items.zipties.data");

        ItemStack stack = new ItemStack(Material.valueOf(type), 1, (short) data);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Msg.color(configuration.getString("items.zipties.name")));
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        meta.setUnbreakable(true);

        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public ItemStack getCuttersItem() {
        FileConfiguration configuration = plugin.getConfig();
        String type = configuration.getString("items.cutters.type");
        int data = configuration.getInt("items.cutters.data");

        ItemStack stack = new ItemStack(Material.valueOf(type), 1, (short) data);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Msg.color(configuration.getString("items.cutters.name")));
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        meta.setUnbreakable(true);
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public ImmutableSet<UUID> getRestrained(UUID uuid) {
        return ImmutableSet.copyOf(prisoners.get(uuid));
    }

    @Override
    public boolean isRestrained(UUID uuid) {
        return prisoners.values().stream().anyMatch(uuid::equals);
    }
    @Override
    public UUID getRestrainedBy(UUID uuid) {
        if (!isRestrained(uuid)) {
            return null;
        }

        return prisoners.entries().stream()
                .filter(entry -> entry.getValue().equals(uuid))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void restrain(Player restrainer, Player prisoner) {
        PrisonerRestrainEvent event = new PrisonerRestrainEvent(prisoner, restrainer);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        if (isRestrained(prisoner)) {
            Msg.config(restrainer, Message.RESTRAINED_ALREADYOTHER, "%prisoner%", prisoner.getName());
            return;
        }

        Pig pig = (Pig) prisoner.getWorld().spawnEntity(prisoner.getLocation(), EntityType.PIG);
        pig.setCustomName("Restraint Pig-" + ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE));
        pig.addPotionEffect(INVIS_EFFECT, true);
        pig.setCustomNameVisible(false);
        pig.setLeashHolder(restrainer);
        pig.addPassenger(prisoner);
        pig.setInvulnerable(true);
        pig.setSilent(true);
        pig.setAI(true);
        pig.setBaby();
        pigs.put(prisoner.getUniqueId(), pig);
        prisoner.addPotionEffect(WEAKNESS_EFFECT);
        prisoners.put(restrainer.getUniqueId(), prisoner.getUniqueId());
        Msg.config(restrainer, Message.RESTRAINED_SELF, "%prisoner%", prisoner.getName());
        Msg.config(prisoner, Message.RESTRAINED_OTHER, "%restrainer%", restrainer.getName());
    }
    @Override
    public void sit(Player restrainer, Player player) {
        PrisonerManager.getPig(player).ifPresent(pig -> {
            if(!pig.isOnGround()) {
                Msg.config(restrainer, Message.RESTRAINED_NOTONGROUND, "%prisoner%", player.getName());
                return;
            }
            pig.setAI(false);
            pig.setLeashHolder(null);
            Msg.config(restrainer, Message.RESTRAINED_NOWSITTING, "%prisoner%", player.getName());
        });
    }
    @Override
    public void standup(Player restrainer, Player player) {
        PrisonerManager.getPig(player).ifPresent(pig -> {
            pig.setLeashHolder(restrainer);
            pig.setAI(true);
            Msg.config(restrainer, Message.RESTRAINED_NOTSITTING, "%prisoner%", player.getName());
        });
    }
    @Override
    public void release(Player player, ReleaseType type) {
        if (!isRestrained(player)) {
            return;
        }
        PlayerInteract.haveRestrained.remove(getRestrainedBy(player.getUniqueId()));
        UUID restrainerUuid = getRestrainedBy(player);
        Player restrainer = Bukkit.getPlayer(restrainerUuid);

        PrisonerReleaseEvent event = new PrisonerReleaseEvent(player, restrainer, type);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        getPig(player).ifPresent(pig -> {
            pig.remove();
            pigs.remove(player.getUniqueId());
        });
        prisoners.removeAll(restrainerUuid);
        player.removePotionEffect(PotionEffectType.WEAKNESS);

        boolean escaped = type == ReleaseType.ESCAPE;
        if (restrainer != null && restrainer.isOnline()) {
            Msg.config(restrainer, escaped ? Message.ESCAPED_RESTRAINER : Message.RELEASED_RESTRAINER, "%prisoner%", player.getName());
        }
        Msg.config(player, escaped ? Message.ESCAPED_PRISONER : Message.RELEASED_PRISONER);
    }
    @Override
    public void clean() {
        prisoners.entries().forEach(entry -> {
            Player prisoner = Bukkit.getPlayer(entry.getValue());
            if (prisoner != null) {
                release(prisoner, ReleaseType.OTHER);
            }
        });
        prisoners.clear();
        pigs.values().forEach(Entity::remove);
        pigs.clear();
    }

    public static Optional<Pig> getPig(Player player) {
        return Optional.ofNullable(PrisonerManager.pigs.get(player.getUniqueId()));
    }

}
