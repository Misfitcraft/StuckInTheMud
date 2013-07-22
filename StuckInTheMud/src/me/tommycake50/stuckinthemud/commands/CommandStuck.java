package me.tommycake50.stuckinthemud.commands;

import me.tommycake50.stuckinthemud.StuckInTheMud;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandStuck implements CommandExecutor {
	StuckInTheMud inst;
	
	public CommandStuck(StuckInTheMud inst){
		this.inst = inst;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("stuck") && args.length == 0){
			displayHelp(sender);
		}else if(label.equalsIgnoreCase("stuck") && args.length >= 1 && args[0].equalsIgnoreCase("start") && sender.hasPermission("stuck.startgame")){
			if(inst.getServer().getOnlinePlayers().length >= 3){
				if(inst.gameinst.isingame){
					sender.sendMessage("Game is already on!");
				}else{
					inst.gameinst.start();
				}
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "You need at least three players online to start a game of stuck!");
			}
		}
		return true;
	}

	private void displayHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "Usage: /stuck start");
	}
}
