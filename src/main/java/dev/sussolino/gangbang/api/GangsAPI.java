package dev.sussolino.gangbang.api;

import dev.sussolino.gangbang.file.GangYaml;
import dev.sussolino.gangbang.gangs.Gang;
import org.bukkit.entity.Player;

public class GangsAPI {

    /*
     *
     *
     *     GANG CLASS
     *
     *
     */

    public static Gang getGang(String gangName) {
        return new Gang(gangName);
    }
    public static Gang getPlayerGang(Player player) {
        String gangName = GangYaml.getGang(player.getName());

        return gangName != null ? new Gang(gangName) : null;
    }

    /*
     *
     *
     *      HEADER
     *
     *
     */

    public static void create(String owner, String gangName) {
        GangYaml.createGang(owner, gangName);
    }
    public static void disband(String gangName) {
        GangYaml.deleteGang(gangName);
    }

    /*
     *
     *
     *       FORCED - ADD && SET
     *
     *
     */

    public static void addMember(String gangName, String memberName) {
        GangYaml.addMember(gangName, memberName);
    }
    public static void addAdmin(String gangName, String adminName) {
        GangYaml.addAdmin(gangName, adminName);
    }
    public static void setOwner(String gangName, String ownerName) {
        Gang gang = getGang(gangName);
        addMember(gangName, gang.getOwner().getName());
        GangYaml.setOwner(gangName, ownerName);
    }

    /*
     *
     *
     *       FORCED - REMOVE
     *
     *
     */


    public static void removeMember(String gangName, String memberName) {
        GangYaml.removeMember(gangName, memberName);
    }
    public static void removeAdmin(String gangName, String adminName) {
        GangYaml.removeAdmin(gangName, adminName);
    }

    /*
     *
     *
     *       FORCED - PEX
     *
     *
     */

    public static void pex(String gangName, String memberName) {
        removeMember(gangName, memberName);
        addAdmin(gangName, memberName);
    }
}
