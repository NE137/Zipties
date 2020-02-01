package org.mhdvsolutions.zipties.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class Msg {

    private static final Pattern NEW_LINE = Pattern.compile("\n");

    public static void config(Player player, Message message) {
        msg(player, message.toString());
    }

    public static void config(Player player, Message message, Object... replacements) {
        msg(player, message.toString(), replacements);
    }

    public static void msg(CommandSender sender, String message) {
        NEW_LINE.splitAsStream(color(message)).forEach(sender::sendMessage);
    }

    public static void msg(CommandSender sender, String message, Object... replacements) {
        String msg = color(replace(message, replacements));
        NEW_LINE.splitAsStream(msg).forEach(sender::sendMessage);
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

    public static List<String> color(List<String> strings) {
        return strings.stream().map(Msg::color).collect(Collectors.toList());
    }

}
