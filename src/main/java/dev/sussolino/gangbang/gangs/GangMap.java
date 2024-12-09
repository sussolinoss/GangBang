package dev.sussolino.gangbang.gangs;

import dev.sussolino.gangbang.GangBang;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class GangMap {

    private static final Map<String, List<String>> GANG_INVITES = Collections.synchronizedMap(new HashMap<>());
    public static final Set<Player> CHAT_ENABLED = Collections.synchronizedSet(new HashSet<>());
    public static final Set<Player> CHEST_OPENED = Collections.synchronizedSet(new HashSet<>());

    public static List<String> getInvites(String gang) {
        return GANG_INVITES.getOrDefault(gang, Collections.synchronizedList(new ArrayList<>()));
    }

    public static void invite(String gang, String player) {
        synchronized (GANG_INVITES) {
            List<String> invites = getInvites(gang);
            if (!invites.contains(player)) {
                invites.add(player);
                GANG_INVITES.put(gang, invites);
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                removeInvite(gang, player);
            }
        }.runTaskLater(GangBang.INSTANCE, 20 * 60);
    }

    public static void removeInvite(String gang, String player) {
        synchronized (GANG_INVITES) {
            List<String> invites = getInvites(gang);
            invites.remove(player);

            if (invites.isEmpty()) GANG_INVITES.remove(gang);
            else GANG_INVITES.put(gang, invites);

        }
    }

    public static boolean isInvited(String gang, String player) {
        synchronized (GANG_INVITES) {
            return getInvites(gang).contains(player);
        }
    }

    public static boolean toggleChat(Player p) {
        synchronized (CHAT_ENABLED) {
            if (!CHAT_ENABLED.contains(p)) CHAT_ENABLED.add(p);
            else CHAT_ENABLED.remove(p);

        }
        return CHAT_ENABLED.contains(p);
    }
}