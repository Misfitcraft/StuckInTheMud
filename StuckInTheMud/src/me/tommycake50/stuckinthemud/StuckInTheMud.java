package me.tommycake50.stuckinthemud;

import me.tommycake50.stuckinthemud.listeners.SignListener;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class StuckInTheMud extends JavaPlugin {
	public FileConfiguration config;
	public GameManager gameinst;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		gameinst = new GameManager(this);
		getServer().getPluginManager().registerEvents(gameinst, this);
		getServer().getPluginManager().registerEvents(new SignListener(this), this);
		getServer().setDefaultGameMode(GameMode.ADVENTURE);
		config = getConfig();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("stuck") && args.length == 0){
			displayHelp(sender);
		}else if(label.equalsIgnoreCase("stuck") && args.length >= 1 && args[0].equalsIgnoreCase("start") && sender.hasPermission("stuck.startgame")){
			if(getServer().getOnlinePlayers().length >= 3){
				if(gameinst.isingame){
					sender.sendMessage("Game is already on!");
				}else{
					gameinst.start();
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
