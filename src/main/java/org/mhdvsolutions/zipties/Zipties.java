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
        if(getConfig().getBoolean("settings.crafting") == true) {
            NamespacedKey zipties = new NamespacedKey(this, "zipties_crafting");
            ShapedRecipe ziptieRecipe = new ShapedRecipe(zipties, api.getZiptieItem());
            NamespacedKey cutters = new NamespacedKey(this, "cutters_crafting");
            ShapedRecipe cuttersRecipe = new ShapedRecipe(cutters, api.getCuttersItem());

            ziptieRecipe.shape(" I ", "ISI", " I ");
            cuttersRecipe.shape(" I ", "IFI", " I ");

            ziptieRecipe.setIngredient('S', Material.STRING);
            ziptieRecipe.setIngredient('I', Material.IRON_INGOT);
            cuttersRecipe.setIngredient('F', Material.FLINT);
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
