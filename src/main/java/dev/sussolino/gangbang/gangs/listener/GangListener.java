package dev.sussolino.gangbang.gangs.listener;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import dev.sussolino.gangbang.api.GangsAPI;
import dev.sussolino.gangbang.gangs.Gang;
import dev.sussolino.gangbang.gangs.GangMap;
import dev.sussolino.gangbang.language.Language;
import dev.sussolino.gangbang.gangs.GangInfo;
import dev.sussolino.gangbang.file.GangYaml;
import dev.sussolino.juicyapi.reflection.annotations.Spartan;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import static dev.sussolino.juicyapi.color.ColorUtils.color;

@Spartan
public class GangListener implements Listener {

    @EventHandler
    public void on(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player damager && e.getEntity() instanceof Player victor)) return;

        if (!GangYaml.hasGang(damager.getName()) || !GangYaml.hasGang(victor.getName())) return;

        String team_damager = GangYaml.getGang(damager.getName());
        String team_victim = GangYaml.getGang(victor.getName());

        if (team_damager == null || team_victim == null) return;

        if (team_damager.equals(team_victim)) {
            damager.sendMessage(color(Language.GANGS_MESSAGES_ATTACK_SAME_GANG.getString()));
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void on(InventoryClickEvent e) {
        if (e.getView().getOriginalTitle().equals(GangInfo.TITLE)) e.setCancelled(true);
    }
    @EventHandler
    public void on(InventoryCloseEvent e) {
        if (!(e.getView().getPlayer() instanceof Player p)) return;
        if (!GangMap.CHEST_OPENED.contains(p)) return;
        if (e.getView().getOriginalTitle().equals("Gang - Chest")) {
            String gangName = GangYaml.getGang(p.getName());

            GangYaml.saveChest(gangName, e.getInventory());
            GangMap.CHEST_OPENED.remove(p);
        }
    }
}
