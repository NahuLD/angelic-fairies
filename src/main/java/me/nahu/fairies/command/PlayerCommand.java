package me.nahu.fairies.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.HelpCommand;
import org.bukkit.command.CommandSender;

@CommandAlias("angelic|fairies|angels")
public class PlayerCommand extends BaseCommand {
    @Default
    @HelpCommand
    @Description("Need any help?")
    public void sendHelp(CommandSender sender, CommandHelp commandHelp) {
        commandHelp.showHelp();
    }
}
