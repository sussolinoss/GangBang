package dev.sussolino.gangbang.file;

import dev.sussolino.gangbang.GangBang;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class LanguageYaml {

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
        file = new File(GangBang.INSTANCE.getDataFolder(), "language.yml");
        if (!file.exists()) {
            GangBang.INSTANCE.saveResource("language.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
        save();
    }
}