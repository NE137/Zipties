package org.mhdvsolutions.zipties.api;

import com.google.common.collect.ImmutableSet;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public interface ZiptiesApi {

    /**
     * Item used to represent zipties.
     *
     * @return {@code ItemStack} representing the zipties
     */
    ItemStack getZiptieItem();

    /**
     * Item used to represent the ziptie cutters.
     *
     * @return {@code ItemStack} representing the ziptie cutters.
     */
    ItemStack getCuttersItem();

    default ImmutableSet<UUID> getRestrained(Player player) {
        return getRestrained(player.getUniqueId());
    }

    /**
     * @param uuid restraining other users
     * @return collection of users being restrained by the user
     */
    ImmutableSet<UUID> getRestrained(UUID uuid);

    default boolean isRestrained(Player player) {
        return isRestrained(player.getUniqueId());
    }

    /**
     * @param uuid user to check
     * @return if the user is restrained or not
     */
    boolean isRestrained(UUID uuid);

    default UUID getRestrainedBy(Player player) {
        return getRestrainedBy(player.getUniqueId());
    }


    /**
     * @param uuid restrained
     * @return uuid that restrained the user
     */
    UUID getRestrainedBy(UUID uuid);

    /**
     * Restrains/zipties the user to another player.
     *
     * @param restrainer player restraining
     * @param prisoner   player being restrained
     */
    void restrain(Player restrainer, Player prisoner);

    /**
     * Restrains/zipties the user to another player.
     *
     * @param restrainer player restraining
     * @param prisoner   player being restrained
     */
    void sit(Player restrainer, Player prisoner);

    /**
     * Restrains/zipties the user to another player.
     *
     * @param restrainer player restraining
     * @param prisoner   player being restrained
     */
    void standup(Player restrainer, Player prisoner);

    /**
     * Releases/breaks the ziptie restraining the player.
     */
    void release(Player player, ReleaseType type);

    /**
     * Handle plugin shutdown.
     */
    default void clean() {
    }

}
