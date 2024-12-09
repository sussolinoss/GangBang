package dev.sussolino.gangbang.file;

import dev.sussolino.gangbang.GangBang;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class GangYaml {

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
        file = new File(GangBang.INSTANCE.getDataFolder(), "gangs.yml");
        if (!file.exists()) {
            GangBang.INSTANCE.saveResource("gangs.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);
        if (config.getConfigurationSection("gangs") == null) config.createSection("gangs");
    }

    public static void setChat(Player p , boolean chat) {
        config.set(p.getName() , chat);
    }

   /* public static void saveChest(String team) {
        getItemStackList("teams." + team + ".chest");
    }

    public static void openChest(Player p, String team) {
        Inventory inv = Bukkit.createInventory(p, 56);
        getItemStackList("teams." + team + ".chest").forEach(i -> inv.addItem(i));
        p.openInventory(inv);
    }

    */


    public static void deleteGang(String teamName) {
        ConfigurationSection section = GangYaml.config.getConfigurationSection("gangs");
        assert section != null;
        section.set(teamName, null);
        reload();
    }

    public static void createGang(String owner, String gangName) {
        ConfigurationSection section = GangYaml.config.getConfigurationSection("gangs");
        section.set(gangName, gangName);
        section.set(gangName + ".owner", owner);
        section.createSection(gangName + ".members");
        reload();
    }



    public static void addMember(String gang, String member) {
        List<String> users = config.getStringList("gangs." + gang + ".members");
        users.add(member);
        config.set("gangs." + gang + ".members", users);
        reload();
    }

    public static void addAdmin(String gang, String member) {
        List<String> users = config.getStringList("gangs." + gang + ".admin");
        users.add(member);
        config.set("gangs." + gang + ".admins", users);
        reload();
    }




    public static void removeAdmin(String gang, String admin) {
        List<String> users = config.getStringList("gangs." + gang + ".members");
        users.remove(admin);
        config.set("gangs." + gang + ".admins", users);
        reload();
    }

    public static void removeMember(String gang, String member) {
        List<String> users = config.getStringList("gangs." + gang + ".members");
        users.remove(member);
        config.set("gangs." + gang + ".members", users);
        reload();
    }


    public static void setOwner(String team, String newOwner) {
        config.set("gangs." + team + ".owner" , newOwner);
        reload();
    }




    public static String getOwner(String gang) {
        String owner = config.getString("gangs." + gang + ".owner");
        return owner != null ? owner : "";
    }

    public static @NotNull List<String> getMembers(String gang) {
        return config.getStringList("gangs." + gang + ".members");
    }

    public static @NotNull List<String> getAdmins(String gang) {
        return config.getStringList("gangs." + gang + ".admins");
    }

    public static boolean getChat(Player p) {
       return config.getBoolean(p.getName());
    }






    public static boolean inGang(String team , String player) {
        String path = "gangs." + team;
        if (config.getStringList(path + ".members").contains(player)) return true;
        if (config.getStringList(path + ".admins").contains(player)) return true;
        return config.getString(path + ".owner").equals(player);
    }


    public static boolean isAdmin(String team, String member) {
        return config.getStringList("gangs." + team + ".admins").contains(member);
    }

    public static boolean hasGang(String player) {
        Set<String> TIM = Objects.requireNonNull(config.getConfigurationSection("gangs")).getKeys(false);
        for (String VODAFONE : TIM) {
            if (config.getString("gangs." + VODAFONE + ".owner").equals(player)) return true;
            if (config.getStringList("gangs." + VODAFONE + ".admins").contains(player)) return true;
            if (config.getStringList("gangs." + VODAFONE + ".members").contains(player)) return true;
        }
        return false;
    }

    public static boolean gangExist(String team) {
        Set<String> TIM = config.getConfigurationSection("gangs").getKeys(false);
        for (String VODAFONE : TIM) return VODAFONE.equals(team);
        return false;
    }

    public static String getGang(String player) {
        Set<String> TIM = config.getConfigurationSection("gangs").getKeys(false);
        for (String VODAFONE : TIM) {
            if (config.getString("gangs." + VODAFONE + ".owner").equals(player)) return VODAFONE;
            if (config.getStringList("gangs." + VODAFONE + ".admins").contains(player)) return VODAFONE;
            if (config.getStringList("gangs." + VODAFONE + ".members").contains(player)) return VODAFONE;
        }
        return null;
    }

    public static void saveChest(String gangName, Inventory inv) {
        List<ItemStack> contents = new ArrayList<>();
        Arrays.stream(inv.getContents()).forEach(itemStack -> {
            if (itemStack == null) itemStack = new ItemStack(Material.AIR);

            contents.add(itemStack);
        });

        config.set("gangs." + gangName + ".chest-items", contents);
        config.set("gangs." + gangName + ".chest-size", inv.getSize());
        reload();
    }

    public static Inventory getChest(String gangName) {
        if (config.getInt("gangs." + gangName + ".chest-size") == 0) return null;
        List<ItemStack> items = getItemStackList("gangs." + gangName + ".chest-items");
        int size = config.getInt("gangs." + gangName + ".chest-size");
        Inventory inv = Bukkit.createInventory(null, size, "Gang - Chest");

        items.forEach(inv::addItem);

        return inv;
    }

    public static @NotNull List<ItemStack> getItemStackList(@NotNull String path) {
        if (config.contains(path)) {
            List<?> list = config.getList(path);
            if (list != null) {
                ArrayList<ItemStack> result = new ArrayList<>();
                for (Object object : list) {
                    if (object instanceof ItemStack) {
                        result.add((ItemStack) object);
                    }
                }
                return result;
            }
        }
        return new ArrayList<>();
    }

}