package dev.sussolino.gangbang.language;

import dev.sussolino.gangbang.file.LanguageYaml;
import dev.sussolino.juicyapi.color.ColorUtils;

import java.util.Objects;

public enum Language {

    PREFIX,
    ERROR_PREFIX,
    ERROR_COMMAND,
    ERROR_OFFLINE,
    ERROR_YOU,
    ERROR_DOUBLE,

    GANGS_BALANCE,

    GANGS_GUI,

    GANGS_HELP,
    GANGS_CHAT,

    GANGS_CREATE,
    GANGS_DISBAND,

    GANGS_NO__ENOUGH__MONEY,
    GANGS_NOT__OWNER,
    GANGS_TARGET__NOT__IN__GANG,
    GANGS_DISBAND__INSTEAD__LEAVE,
    GANGS_NO__PERM,
    GANGS_SAME__PERMS,
    GANGS_NOT__INVITED,
    GANGS_NOT__EXIST,
    GANGS_SAME__GANG,
    GANGS_ALREADY__INVITED,

    GANGS_SET__OWNER,
    GANGS_DEMOTED__OWNER,

    GANGS_JOIN,
    GANGS_JOIN__ANNOUNCE,

    GANGS_LEAVE,
    GANGS_LEAVE__ANNOUNCE,

    GANGS_TAKE,
    GANGS_DEPOSIT,

    GANGS_ALREADY__PEXED,
    GANGS_ALREADY__IN__GANG,
    GANGS_HAS__ANOTHER__GANG,
    GANGS_NOT__IN__GANG,

    GANGS_INVITE__RECEIVER,
    GANGS_INVITE__SENDER,

    GANGS_KICK,
    GANGS_KICK__ANNOUNCE,

    GANGS_PEX__RECEIVER,
    GANGS_PEX__SENDER,

    GANGS_DEPEX__RECEIVER,
    GANGS_DEPEX__SENDER,

    GANGS_MESSAGES_ATTACK_SAME_GANG;

    private final String path;

    Language() {
        this.path = name().toLowerCase().replace("__", "-").replace("_", ".");
    }
    public String getString() {
        return ColorUtils.color(LanguageYaml.getConfig().getString(path)
                .replace("{error}", Objects.requireNonNull(LanguageYaml.getConfig().getString(ERROR_PREFIX.path))
                ).replace("{prefix}", Objects.requireNonNull(LanguageYaml.getConfig().getString(PREFIX.path))
                ));
    }
    public String getString(String replacer, String replace) {
        return getString().replace(replacer, replace);
    }
}
