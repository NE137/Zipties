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
