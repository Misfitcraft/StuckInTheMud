package me.tommycake50.stuckinthemud;

import me.tommycake50.stuckinthemud.commands.CommandStuck;
import me.tommycake50.stuckinthemud.listeners.SignListener;

import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class StuckInTheMud extends JavaPlugin {
	public FileConfiguration config;
	public GameManager gameinst;

	@Override
	public void onEnable() {
		gameinst = new GameManager(this);
		getServer().getPluginManager().registerEvents(gameinst, this);
		getServer().getPluginManager().registerEvents(new SignListener(this), this);
		getCommand("Stuck").setExecutor(new CommandStuck(this));
		getServer().setDefaultGameMode(GameMode.ADVENTURE);
		config = getConfig();
	}
}
