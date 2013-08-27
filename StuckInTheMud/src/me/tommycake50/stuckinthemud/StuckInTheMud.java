package me.tommycake50.stuckinthemud;

import me.tommycake50.stuckinthemud.listeners.FallDamageListener;
import me.tommycake50.stuckinthemud.listeners.SignListener;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

public class StuckInTheMud extends JavaPlugin {
	public FileConfiguration config;
	public GameManager gameinst;

	@Override
	public void onEnable(){
		saveDefaultConfig();
		gameinst = new GameManager(this);
		getServer().getPluginManager().registerEvents(gameinst, this);
		getServer().getPluginManager().registerEvents(new SignListener(this), this);
		getServer().getPluginManager().registerEvents(new FallDamageListener(), this);
		getServer().setDefaultGameMode(GameMode.ADVENTURE);
		config = getConfig();
	}
	
	@Override
	public void onDisable() {
		for(Team t :getServer().getScoreboardManager().getMainScoreboard().getTeams()){
			t.unregister();
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(label.equalsIgnoreCase("start") && args.length == 0){
			if(getServer().getOnlinePlayers().length >= 3){
				if(gameinst != null && gameinst.isingame){
					sender.sendMessage("Game is already on!");
				}else{
					if(gameinst != null){
						gameinst.start();
					}else{
						sender.sendMessage("Game manager is null!");
					}
				}
			}else{
				sender.sendMessage(ChatColor.DARK_RED + "You need at least three players online to start a game of stuck!");
			}
		}
		return true;
	}
}
