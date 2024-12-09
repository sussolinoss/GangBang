package dev.sussolino.gangbang.gangs.command;

import dev.sussolino.gangbang.gangs.Gang;
import dev.sussolino.gangbang.gangs.GangInfo;
import dev.sussolino.gangbang.api.GangsAPI;
import dev.sussolino.gangbang.language.Language;
import dev.sussolino.juicyapi.reflection.abstracts.PlayerCommand;
import dev.sussolino.juicyapi.reflection.annotations.AntiSocial;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@AntiSocial
public class GangCommand extends PlayerCommand implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        List<String> COMMANDS;

        if (args.length == 1) {
            COMMANDS = Arrays.asList("withdraw","balance","deposit", "info","chest", "create", "invite", "join", "leave", "kick", "pex", "disband", "set-owner", "chat");

            return COMMANDS.stream().filter(cmd -> cmd.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }
        else {
            COMMANDS = new ArrayList<>();
            if (args.length == 2) {
                Bukkit.getOnlinePlayers().forEach(p -> COMMANDS.add(p.getName()));
                return COMMANDS;
            }
        }

        return null;
    }



    @Override
    protected void execute(Player p, String[] args) {
        if (args.length < 1) {
            p.sendMessage(Language.GANGS_HELP.getString());
            return;
        }

        Gang gang = GangsAPI.getPlayerGang(p);

        String cmd = args[0].toLowerCase();

        if (gang == null && !cmd.equalsIgnoreCase("join") && !cmd.equalsIgnoreCase("create")) {
            p.sendMessage(Language.GANGS_NOT__IN__GANG.getString());
            return;
        }

        if (args.length == 1) {
            switch (cmd) {
                case "balance" -> p.sendMessage(Language.GANGS_BALANCE.getString("{amount}", String.valueOf(gang.getBalance())));
                case "chest" -> gang.openChest(p);
                case "chat" -> {
                    assert gang != null;
                    gang.chat(p);
                }
                case "info" -> new GangInfo(p).open();
                case "leave" -> gang.leave(p);
                case "disband" -> gang.disband(p);
                default -> p.sendMessage(Language.ERROR_COMMAND.getString());
            }
        }
        else if (args.length == 2) {

            String arg1 = args[1];

            if (arg1.equals(p.getName()) && !cmd.equalsIgnoreCase("join") && !cmd.equalsIgnoreCase("create")) {
                p.sendMessage(Language.ERROR_YOU.getString());
                return;
            }

            switch (cmd) {
                /*
                 *
                 *
                 *   PLAYER AS ARG1
                 *
                 *
                 */
                case "invite" -> {
                    Player target = Bukkit.getPlayerExact(arg1);

                    if (target == null) {
                        Language.ERROR_OFFLINE.getString();
                        return;
                    }

                    assert gang != null;
                    gang.invite(p, target);
                }
                case "set-owner" -> {
                    Player target = Bukkit.getPlayerExact(arg1);
                    if (target == null) {
                        p.sendMessage(Language.ERROR_OFFLINE.getString());
                        return;
                    }
                    gang.setOwner(p, target);
                }
                case "kick" -> {
                    Player target = Bukkit.getPlayerExact(arg1);

                    if (target == null) {
                        Language.ERROR_OFFLINE.getString();
                        return;
                    }
                    assert gang != null;

                    gang.kick(p, target);
                }
                case "pex" -> {
                    Player target = Bukkit.getPlayerExact(arg1);

                    if (target == null) {
                        Language.ERROR_OFFLINE.getString();
                        return;
                    }

                    gang.pex(p, target);
                }
                /*
                 *
                 *
                 *   GANG AS ARG1
                 *
                 *
                 */
                case "create" -> {
                    if (gang != null) {
                        p.sendMessage(Language.GANGS_ALREADY__IN__GANG.getString());
                        return;
                    }

                    GangsAPI.create(p.getName(), arg1);

                    p.sendMessage(Language.GANGS_CREATE.getString("{gang}", arg1));
                }
                case "join" -> new Gang(arg1).join(p);

                /*
                 *
                 *
                 *   MONEY AS ARG1
                 *
                 *
                 */

                case "deposit" -> {
                    if (!isValid(arg1, p)) {
                        p.sendMessage(Language.ERROR_DOUBLE.getString());
                        return;
                    }
                    double amount = Double.parseDouble(arg1);

                    gang.deposit(p, amount);
                }
                case "withdraw" -> {
                    if (!isValid(arg1, p)) {
                        p.sendMessage(Language.ERROR_DOUBLE.getString());
                        return;
                    }
                    double amount = Double.parseDouble(arg1);

                    gang.take(p, amount);
                }
            }
        }
    }
    private boolean isValid(String arg1, Player p) {
        double amount;
        try {
            amount = Double.parseDouble(arg1);
        }
        catch (NumberFormatException e) {
            p.sendMessage("You need to specify a valid number");
            return false;
        }
        return amount > 0;
    }

}