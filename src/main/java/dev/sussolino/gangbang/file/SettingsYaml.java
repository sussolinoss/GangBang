package dev.sussolino.gangbang.file;

import dev.sussolino.gangbang.GangBang;
import dev.sussolino.juicyapi.player.PlayerUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SettingsYaml {

    private static File file;
    @Getter
    private static FileConfiguration config;

    @SneakyThrows
    public static void save() {
        config.save(file);
    }

    public static void reload() {
        save();
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static void init() {
        file = new File(GangBang.INSTANCE.getDataFolder(), "settings.yml");
        if (!file.exists()) {
            GangBang.INSTANCE.saveResource("settings.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    private static List<String> groups() {
        return config.getStringList("groups");
    }
    public static boolean gangHasChest(String owner) {
        String luckGroup = PlayerUtil.getGroup(owner);

        AtomicBoolean hasChest = new AtomicBoolean(false);

        groups().stream().filter(group -> group.startsWith(luckGroup)).forEach(group -> {
            var split = group.split("!");
            hasChest.set(split.length == 2 && split[1].startsWith("CHEST_"));
        });

        return hasChest.get();
    }

    public static int gangChestSize(String owner) {
        AtomicInteger chestSize = new AtomicInteger(-1);
        String luckGroup = PlayerUtil.getGroup(owner);

        if (!gangHasChest(owner)) return chestSize.get();

        groups().stream().filter(group -> group.startsWith(luckGroup)).map(group -> group.split("!")).filter(split -> split.length == 2).map(split -> split[1].split("_")).filter(sizeSplit -> sizeSplit.length == 2 && sizeSplit[0].equalsIgnoreCase("CHEST")).forEach(sizeSplit -> {
            chestSize.set(Integer.parseInt(sizeSplit[1]));
        });

        return chestSize.get();
    }

    public static int gangMaxMembers(String owner) {
        AtomicInteger maxMembers = new AtomicInteger(-1);
        String luckGroup = PlayerUtil.getGroup(owner);

        groups().stream().filter(group -> group.startsWith(luckGroup)).forEach(group -> {
            var split = group.split(":");
            if (split.length == 2) {
                maxMembers.set(Integer.parseInt(split[1].split("!")[0]));
            }
        });

        return maxMembers.get();
    }
}