package dev.sussolino.gangbang.gangs;

import dev.sussolino.gangbang.file.GangYaml;
import dev.sussolino.gangbang.api.GangsAPI;
import dev.sussolino.gangbang.file.SettingsYaml;
import dev.sussolino.gangbang.language.Language;
import dev.sussolino.postepay.api.PostePayAPI;
import dev.sussolino.postepay.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

import static dev.sussolino.juicyapi.color.ColorUtils.color;


public class Gang {

    private final String gangName;

    private final String owner;

    private final List<String> admins;
    private final List<String> members;

    public Gang(String gangName) {
        this.gangName = gangName;

        this.owner = GangYaml.getOwner(gangName);
        this.admins = GangYaml.getAdmins(gangName);
        this.members = GangYaml.getMembers(gangName);
    }


    public double getBalance() {
        return GangYaml.getConfig().getDouble("gangs." + gangName + ".balance");
    }
    public void addBalance(double amount) {
        GangYaml.getConfig().set("gangs." + gangName + ".balance", getBalance() + amount);
        GangYaml.reload();
    }
    public void removeBalance(double amount) {
        GangYaml.getConfig().set("gangs." + gangName + ".balance", getBalance() - amount);
        GangYaml.reload();
    }


    /**
     * BOOLEANS - CASUAL
     */

    public boolean hasPerms(Player p) {
        return getAdminsName().contains(p.getName()) || owner.equals(p.getName());
    }

    public boolean inGang(Player t) {
        return getAllMembersName().contains(t.getName());
    }

    public boolean isInvited(Player p) {
        return GangMap.isInvited(gangName, p.getName());
    }

    /**
     * BOOLEANS - DEFINED
     */


    public boolean isAdmin(Player p) {
        return admins.contains(p.getName());
    }

    public boolean isMember(Player p) {
        return members.contains(p.getName());
    }

    public boolean isOwner(Player p) {
        return owner.equals(p.getName());
    }

    /**
     * GETTER LIST - STRING
     */

    public List<String> getMembersName() {
        return members;
    }

    public List<String> getAdminsName() {
        return admins;
    }

    public List<String> getAllMembersName() {
        List<String> list = new ArrayList<>();
        list.addAll(members);
        list.addAll(admins);
        list.add(owner);
        return list;
    }

    /**
     * GETTER LIST - PLAYER
     */

    public List<Player> getMembers() {
        List<Player> members = new ArrayList<>();

        for (String member : this.members) {
            Player p = Bukkit.getPlayerExact(member);

            if (p != null && p.isOnline()) members.add(p);
        }

        return members;
    }


    public List<Player> getAdmins() {
        List<Player> members = new ArrayList<>();

        for (String member : this.admins) {
            Player p = Bukkit.getPlayerExact(member);

            if (p != null && p.isOnline()) members.add(p);
        }
        return members;
    }

    public List<Player> getAllMembers() {
        List<Player> members = new ArrayList<>();
        for (String member : this.getAllMembersName()) {
            Player p = Bukkit.getPlayerExact(member);
            if (p != null && p.isOnline()) members.add(p);
        }
        return members;
    }

    public Player getOwner() {
        return Bukkit.getPlayerExact(owner) != null && Bukkit.getPlayerExact(owner).isOnline() ? Bukkit.getPlayer(owner) : null;
    }

    /**
     * DISBAND
     */

    public void disband(Player p) {
        if (!hasPerms(p)) {
            p.sendMessage(Language.GANGS_NO__PERM.getString());
            return;
        }

        getAllMembers().forEach(member -> member.sendMessage(Language.GANGS_DISBAND.getString()));

        GangsAPI.disband(gangName);
    }

    /**
     * KICK
     */

    public void kick(Player p, Player t) {
        if (!inGang(t)) {
            p.sendMessage(color(Language.GANGS_TARGET__NOT__IN__GANG.getString("{player}", t.getName())));
            return;
        }

        if (!hasPerms(p)) {
            p.sendMessage(color(Language.GANGS_NO__PERM.getString("{player}", t.getName())));
            return;
        }
        if (hasPerms(t) && !owner.equals(p.getName())) {
            p.sendMessage(Language.GANGS_SAME__PERMS.getString("{player}", t.getName()));
            return;
        }

        if (members.contains(t.getName())) GangsAPI.removeMember(gangName, t.getName());
        else GangsAPI.removeAdmin(gangName, t.getName());

        p.sendMessage(Language.GANGS_KICK.getString());

        String all = Language.GANGS_KICK__ANNOUNCE.getString("{player}", p.getName()).replace("{target}", t.getName());

        getAdmins().forEach(admins -> admins.sendMessage(Language.GANGS_KICK.getString("{player}", t.getName())));
        getMembers().forEach(members -> members.sendMessage(all));
    }

    /**
     * INVITE
     */

    public void invite(Player p, Player t) {
        if (GangYaml.hasGang(t.getName())) {
            p.sendMessage(color(Language.GANGS_HAS__ANOTHER__GANG.getString()));
            return;
        }

        if (!hasPerms(p)) {
            p.sendMessage(color(Language.GANGS_NO__PERM.getString()));
            return;
        }

        if (isInvited(t)) {
            p.sendMessage(Language.GANGS_ALREADY__INVITED.getString());
            return;
        }

        GangMap.invite(gangName, t.getName());

        p.sendMessage(Language.GANGS_INVITE__SENDER.getString("{player}", t.getName()));
        t.sendMessage(Language.GANGS_INVITE__RECEIVER.getString("{gang}", gangName));
    }

    /**
     * LEAVE
     */

    public void leave(Player p) {
        if (isOwner(p)) {
            p.sendMessage(Language.GANGS_DISBAND__INSTEAD__LEAVE.getString());
            return;
        }

        if (!isAdmin(p)) GangsAPI.removeMember(gangName, p.getName());
        else GangsAPI.removeAdmin(gangName, p.getName());

        getAllMembers().forEach(member -> member.sendMessage(Language.GANGS_LEAVE__ANNOUNCE.getString("{player}", p.getName())));
    }

    /**
     * JOIN
     */

    public void join(Player p) {
        if (GangYaml.hasGang(p.getName())) {
            p.sendMessage(Language.GANGS_HAS__ANOTHER__GANG.getString());
            return;
        }
        if (!isInvited(p)) {
            p.sendMessage(Language.GANGS_NOT__INVITED.getString());
            return;
        }

        GangMap.removeInvite(gangName, p.getName());
        GangsAPI.addMember(gangName, p.getName());

        p.sendMessage(Language.GANGS_JOIN.getString());
        getAllMembers().forEach(member -> member.sendMessage(Language.GANGS_JOIN__ANNOUNCE.getString("{player}", p.getName())));
    }

    /**
     * PEX
     */

    public void pex(Player sender, Player newAdmin) {
        if (!inGang(newAdmin)) {
            sender.sendMessage(Language.GANGS_TARGET__NOT__IN__GANG.getString("{player}", newAdmin.getName()));
            return;
        }
        if (hasPerms(newAdmin)) {
            sender.sendMessage(Language.GANGS_ALREADY__PEXED.getString());
            return;
        }
        if (!hasPerms(sender)) {
            sender.sendMessage(Language.GANGS_NO__PERM.getString());
            return;
        }

        GangsAPI.pex(gangName, newAdmin.getName());

        sender.sendMessage(Language.GANGS_PEX__SENDER.getString("{player}", newAdmin.getName()));
        newAdmin.sendMessage(Language.GANGS_PEX__RECEIVER.getString());
    }

    /**
     * SET OWNER
     */

    public void setOwner(Player owner, Player newOwner) {
        if (!isOwner(owner)) {
            owner.sendMessage(Language.GANGS_NOT__OWNER.getString());
            return;
        }
        if (!inGang(newOwner)) {
            owner.sendMessage(Language.GANGS_TARGET__NOT__IN__GANG.getString());
            return;
        }

        GangsAPI.setOwner(gangName, newOwner.getName());

        owner.sendMessage(Language.GANGS_DEMOTED__OWNER.getString());
        newOwner.sendMessage(Language.GANGS_SET__OWNER.getString());
    }

    /**
     *  CHAT TOGGLE
     */

    public void chat(Player sender) {
        String replace = GangMap.toggleChat(sender) ? "&aenabled" : "&cdisabled";

        sender.sendMessage(color(Language.GANGS_CHAT.getString("{state}", replace)));
    }

    /**
     *  CHEST
     */

    public void openChest(Player p) {
        if (!SettingsYaml.gangHasChest(owner)) {
            //TODO : no chest access for this gang
            p.sendMessage("No chest access for this gang");
            return;
        }
        int prevSize = SettingsYaml.gangChestSize(owner);

        Inventory inv = GangYaml.getChest(gangName) == null ? Bukkit.createInventory(null, prevSize, "Gang - Chest") : GangYaml.getChest(gangName);

        GangMap.CHEST_OPENED.add(p);

        p.openInventory(inv);
    }

    /**
     *  DEPOSIT
     */
    public void deposit(Player p, double amount) {
        Profile profile = PostePayAPI.getProfile(p.getName(), "coins");

        if (profile.getBalance() < amount) {
            p.sendMessage(Language.GANGS_NO__ENOUGH__MONEY.getString());
            return;
        }
        profile.take(amount);

        addBalance(amount);

        p.sendMessage(Language.GANGS_DEPOSIT.getString("{amount}", String.valueOf(amount)));
    }

    public void take(Player p, double amount) {
        if (!hasPerms(p)) {
            p.sendMessage(Language.GANGS_NO__PERM.getString());
            return;
        }
        if (amount > getBalance()) {
            p.sendMessage(Language.GANGS_NO__ENOUGH__MONEY.getString());
            return;
        }
        Profile profile = PostePayAPI.getProfile(p.getName(), "coins");
        profile.setBalance(profile.getBalance() + amount);

        removeBalance(amount);

        p.sendMessage(Language.GANGS_TAKE.getString("{amount}", String.valueOf(amount)));
    }
}