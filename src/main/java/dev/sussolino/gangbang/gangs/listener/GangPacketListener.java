package dev.sussolino.gangbang.gangs.listener;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import dev.sussolino.gangbang.api.GangsAPI;
import dev.sussolino.gangbang.gangs.Gang;
import dev.sussolino.gangbang.gangs.GangMap;
import dev.sussolino.gangbang.language.Language;
import dev.sussolino.juicyapi.player.PlayerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static dev.sussolino.juicyapi.color.ColorUtils.color;

public class GangPacketListener implements PacketListener {

    @Override
    public void onPacketReceive(PacketReceiveEvent e) {
        if (e.getPacketType().equals(PacketType.Play.Client.CHAT_MESSAGE)) {
            if (!(e.getPlayer() instanceof Player p)) return;
            if (!GangMap.CHAT_ENABLED.contains(p)) return;
            Gang gang = GangsAPI.getPlayerGang(p);
            if (gang == null) return;

            WrapperPlayClientChatMessage packet = new WrapperPlayClientChatMessage(e);

            String message =
                    color("{prefix}&f{player} &7» &f{message}"
                            .replace("{prefix}", Language.PREFIX.getString()))
                            .replace("{player}", p.getName())
                            .replace("{message}", packet.getMessage());

            WrapperPlayServerSystemChatMessage sys = new WrapperPlayServerSystemChatMessage(false, Component.text(message));

            gang.getAllMembers().forEach(member -> PacketEvents.getAPI().getPlayerManager().getUser(member).sendPacket(sys));

            e.setCancelled(true);
        }
    }
}
