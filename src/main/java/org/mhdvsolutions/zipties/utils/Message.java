package org.mhdvsolutions.zipties.utils;

import org.mhdvsolutions.zipties.Zipties;

public enum Message {

    PREFIX {
        @Override
        public String toString() {
            return Zipties.getPlugin().getConfig().getString("messages.prefix");
        }
    },
    COMMANDS_HELP,
    COMMANDS_ZIPTIES,
    COMMANDS_CUTTERS,
    COMMANDS_PLAYER,
    COMMANDS_PERMISSION,

    MESSAGES_COOLDOWN,
    MESSAGES_ESCAPEFAIL,
    MESSAGES_CLOSER,
    MESSAGES_RESTRAINFAIL,
    MESSAGES_INPROGRESS,
    MESSAGES_CANNOTDOTHAT,
    MESSAGES_BEGIN,

    RESTRAINED_ALREADYOTHER,
    RESTRAINED_SELF,
    RESTRAINED_OTHER,
    RESTRAINED_ISALREADYRESTRAINED,
    RESTRAINED_LEFT,
    RESTRAINED_NOWSITTING,
    RESTRAINED_NOTSITTING,
    RESTRAINED_NOTONGROUND,
    RESTRAINED_ALREADYHAVE,


    RELEASED_RESTRAINER,
    RELEASED_PRISONER,

    ESCAPED_RESTRAINER,
    ESCAPED_PRISONER,
    ESCAPED_FREE,
    ESCAPED_ALMOST,
    ;

    private final String path;

    Message() {
        this.path = name().toLowerCase().replace("_", ".");
    }

    @Override
    public String toString() {
        return Zipties.getPlugin().getConfig().getString("messages." + path).replace("%prefix%", PREFIX.toString());
    }

}
