package me.nahu.fairies.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.*;
import me.nahu.fairies.manager.PlayerManager;
import me.nahu.fairies.manager.player.FakePlayer;
import me.nahu.fairies.utils.Messenger;
import org.bukkit.command.CommandSender;

import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
@CommandAlias("angelic|fairies|angels")
public class PlayerCommand extends BaseCommand {
    @Dependency private Messenger messenger;
    @Dependency private PlayerManager playerManager;

    @Default
    @HelpCommand
    @Description("Need any help?")
    @CommandPermission("fairies.help")
    public void sendHelp(CommandSender sender, CommandHelp commandHelp) {
        commandHelp.showHelp();
    }

    @Subcommand("add")
    @CommandPermission("fairies.add")
    public void add(CommandSender sender, String name) {
        FakePlayer fakePlayer;
        try {
            fakePlayer = playerManager.addPlayer(name);
        } catch (IllegalArgumentException ex) {
            messenger.get("error.not-found").replace("%arg", name).send(sender);
            return;
        }
        messenger.get("command.add").replace("%player", fakePlayer.getName()).send(sender);
    }

    @Subcommand("remove")
    @CommandPermission("fairies.remove")
    public void remove(CommandSender sender, String name) {
        java.util.Optional<FakePlayer> fakePlayer = playerManager.getPlayer(name);
        if (!fakePlayer.isPresent()) {
            messenger.get("error.not-found").replace("%arg", name).send(sender);
            return;
        }
        messenger.get("command.remove").replace("%player", fakePlayer.get().getName()).send(sender);
        playerManager.removePlayer(fakePlayer.get());
    }

    @Subcommand("list")
    @CommandPermission("fairies.list")
    public void list(CommandSender sender) {
        String players = playerManager.getPlayerCache().asMap()
                .values()
                .stream()
                .map(FakePlayer::getName)
                .collect(Collectors.joining(", "));
        if (players.isEmpty()) {
            messenger.get("command.list.none").send(sender);
            return;
        }
        messenger.get("command.list.present").replace("%players", players).send(sender);
    }
}
