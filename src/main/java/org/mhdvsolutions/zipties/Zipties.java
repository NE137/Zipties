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
package org.mhdvsolutions.zipties;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.mhdvsolutions.zipties.api.ZiptiesApi;
import org.mhdvsolutions.zipties.commands.ZiptiesCommand;
import org.mhdvsolutions.zipties.listeners.Escape;
import org.mhdvsolutions.zipties.listeners.PlayerInteract;

import java.util.stream.Stream;

public final class Zipties extends JavaPlugin {

    private static Zipties plugin = null;
    private static ZiptiesApi api = null;

    public static Zipties getPlugin() {
        return plugin;
    }

    public static ZiptiesApi getApi() {
        return api;
    }

    @Override
    public void onLoad() {
        plugin = this;
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    @Override
    public void onEnable() {
        api = new PrisonerManager(this);
        Stream.of(new PlayerInteract(this), new Escape(this)).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));
        getCommand("zipties").setExecutor(new ZiptiesCommand(this));
        if(getConfig().getBoolean("crafting") == true) {
            NamespacedKey zipties = new NamespacedKey(this, "zipties_crafting");
            ShapedRecipe ziptieRecipe = new ShapedRecipe(zipties, api.getZiptieItem());
            NamespacedKey cutters = new NamespacedKey(this, "cutters_crafting");
            ShapedRecipe cuttersRecipe = new ShapedRecipe(cutters, api.getCuttersItem());

            ziptieRecipe.shape(" I ", "ISI", " I ");
            cuttersRecipe.shape(" I ", "ISI", " I ");

            ziptieRecipe.setIngredient('S', Material.STRING);
            ziptieRecipe.setIngredient('I', Material.IRON_INGOT);
            cuttersRecipe.setIngredient('S', Material.IRON_SWORD);
            cuttersRecipe.setIngredient('I', Material.IRON_INGOT);

            getServer().addRecipe(ziptieRecipe);
            getServer().addRecipe(cuttersRecipe);
        }
    }

    @Override
    public void onDisable() {
        api.clean();
        api = null;
        plugin = null;
        getServer().resetRecipes();
    }

}
