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
package org.mhdvsolutions.zipties.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
                String msg = color(("%prefix% &7Developers:\n&8* &aMishyy - &b&nhttps://github.com/Mishyy\n&8* &acelerry - &b&nhttps://github.com/celerry").replace("%prefix%", Message.PREFIX.toString()));
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
