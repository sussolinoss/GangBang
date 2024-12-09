package dev.sussolino.gangbang.gangs;

import dev.sussolino.gangbang.language.Language;
import dev.sussolino.gangbang.file.GangYaml;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

import static dev.sussolino.gangbang.file.GangYaml.*;
import static dev.sussolino.juicyapi.color.ColorUtils.color;

@Getter
public class GangInfo {

    private final String gang;
    private final List<Object> members;

    private final Player player;
    public static String TITLE;

    public GangInfo(Player p) {
        this.player = p;
        this.gang = GangYaml.getGang(p.getName());
        this.members = new ArrayList<>();
        this.members.addAll(GangYaml.getMembers(gang));
        this.members.addAll(getAdmins(gang));
        this.members.add(getOwner(gang));
        TITLE = color(Language.GANGS_GUI.getString());
    }

    @SuppressWarnings("all")
    public void open() {
        Inventory UI = Bukkit.createInventory(player, 54, color(TITLE));

        for (Object member : members) {
            Player m = Bukkit.getPlayerExact((String) member);
            
            if (m == null) continue;

            ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);

            SkullMeta skull = (SkullMeta) head.getItemMeta();

            skull.setOwner(m.getName());
            head.setItemMeta(skull);

            skull.setDisplayName((String) member);

            List<String> lore = new ArrayList<>();

            if (getOwner(gang).equals(member)) {
                lore.add("owner");
            }
            if (isAdmin(gang, (String) member)) {
                lore.add("admin");
            }
            if (GangYaml.getMembers(gang).contains(member)) {
                lore.add("membro");
            }

            skull.setLore(lore);
            head.setItemMeta(skull);

            UI.addItem(head);
        }
        player.openInventory(UI);
    }
}