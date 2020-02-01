package org.mhdvsolutions.zipties.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mhdvsolutions.zipties.PrisonerManager;
import org.mhdvsolutions.zipties.Zipties;
import org.mhdvsolutions.zipties.api.ReleaseType;
import org.mhdvsolutions.zipties.api.ZiptiesApi;
import org.mhdvsolutions.zipties.utils.Message;
import org.mhdvsolutions.zipties.utils.Msg;

import java.util.regex.Pattern;

public final class ZiptiesCommand implements CommandExecutor {

    private final Zipties plugin;
    private final ZiptiesApi api;

    public ZiptiesCommand(final Zipties plugin) {
        this.plugin = plugin;
        this.api = Zipties.getApi();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            Msg.msg(sender, "%prefix% &cOnly players may use this command.", "%prefix%", Message.PREFIX);
            return true;
        }

        Player player = (Player) sender;
        if (args.length == 0) {
            Msg.config(player, Message.COMMANDS_HELP, "%cmd%", label);
            return true;
        }

        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].toLowerCase();
        }

        switch (args[0]) {
            case "zipties":
                if (!player.hasPermission("zipties.admin")) {
                    Msg.config(player, Message.COMMANDS_PERMISSION);
                    return true;
                }
                player.getInventory().addItem(api.getZiptieItem());
                Msg.config(player, Message.COMMANDS_ZIPTIES);
                return true;
            case "cutters":
                if (!player.hasPermission("zipties.admin")) {
                    Msg.config(player, Message.COMMANDS_PERMISSION);
                    return true;
                }
                player.getInventory().addItem(api.getCuttersItem());
                Msg.config(player, Message.COMMANDS_CUTTERS);
                return true;
            case "credit":
                String msg = color(("%prefix% &7Credit\n&aThis is a fork of Mishyy's Zipties proof of concept:\n &bhttps://github.com/Mishyy/Zipties\n&aForked & completed by celerry:\n &bhttps://github.com/celerry\n &bhttps://www.spigotmc.org/members/celerry.860890/").replace("%prefix%", Message.PREFIX.toString()));
                NEW_LINE.splitAsStream(msg).forEach(player::sendMessage);
                return true;
            case "release":
                if (!player.hasPermission("zipties.use")) {
                    Msg.config(player, Message.COMMANDS_PERMISSION);
                    return true;
                }

                if (args.length == 2) {
                    Player prisoner = Bukkit.getPlayer(args[1]);
                    if (prisoner == null || !prisoner.isOnline()) {
                        Msg.config(player, Message.COMMANDS_PLAYER, "%name%", args[0]);
                        return true;
                    }

                    if (!player.hasPermission("zipties.admin")) {
                        Msg.config(player, Message.COMMANDS_PERMISSION);
                        return true;
                    }
                    api.release(prisoner, ReleaseType.OTHER);
                    return true;
                } else {
                    Msg.config(player, Message.COMMANDS_HELP, "%cmd%", label);
                    return true;
                }

            default:
                Msg.config(player, Message.COMMANDS_HELP, "%cmd%", label);
                return true;
        }
    }
    private static final Pattern NEW_LINE = Pattern.compile("\n");
    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
