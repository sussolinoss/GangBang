package dev.sussolino.gangbang;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import dev.sussolino.gangbang.file.GangYaml;
import dev.sussolino.gangbang.file.LanguageYaml;
import dev.sussolino.gangbang.file.SettingsYaml;
import dev.sussolino.gangbang.gangs.command.GangCommand;
import dev.sussolino.gangbang.gangs.listener.GangListener;
import dev.sussolino.gangbang.gangs.listener.GangPacketListener;
import dev.sussolino.gangbang.placeholder.PlaceHolderAPI;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class GangBang extends JavaPlugin {

    public static GangBang INSTANCE;

    @Override
    public void onLoad(){
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings().reEncodeByDefault(false)
                .checkForUpdates(true)
                .bStats(true);
        PacketEvents.getAPI().load();
    }


    @Override
    public void onEnable() {
        INSTANCE = this;

        PacketEvents.getAPI().init();

        GangYaml.init();
        LanguageYaml.init();
        SettingsYaml.init();

        Bukkit.getPluginManager().registerEvents(new GangListener(), this);

        getCommand("gang").setExecutor(new GangCommand());
        getCommand("gang").setTabCompleter(new GangCommand());

        PacketEvents.getAPI().getEventManager().registerListener(new GangPacketListener(), PacketListenerPriority.LOW);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceHolderAPI().register();
            getLogger().info("PlaceholderAPI found!");
        }
        else getLogger().warning("PlaceholderAPI not found...");
    }


    @Override
    public void onDisable() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) new PlaceHolderAPI().unregister();

        PacketEvents.getAPI().terminate();

        INSTANCE = null;
    }
}
