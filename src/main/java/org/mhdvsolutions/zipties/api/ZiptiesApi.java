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
     * Releases/breaks the ziptie restraining the player.
     */
    void release(Player player, ReleaseType type);

    /**
     * Handle plugin shutdown.
     */
    default void clean() {
    }

}
