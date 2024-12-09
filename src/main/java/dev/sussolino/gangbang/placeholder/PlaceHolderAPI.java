package dev.sussolino.gangbang.placeholder;

import dev.sussolino.gangbang.api.GangsAPI;
import dev.sussolino.gangbang.file.GangYaml;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class PlaceHolderAPI extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "gangbang";
    }

    @Override
    public @NotNull String getAuthor() {
        return "sussolino";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.7-DROWNIN";
    }

    @Nullable
    public String onPlaceholderRequest(Player p, @NotNull String arg) {
        if (p == null) return "";

        if (arg.equals("name")) return GangYaml.getGang(p.getName());

        return "none";
    }
}
