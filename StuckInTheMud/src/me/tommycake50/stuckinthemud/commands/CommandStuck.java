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
		}else if(label.equalsIgnoreCase("stuck") && args.length >= 1 && args[0].equalsIgnoreCase("start")){
			inst.gameinst.start();
		}
		return false;
	}

	private void displayHelp(CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + "Usage: /stuck start");
	}
}
