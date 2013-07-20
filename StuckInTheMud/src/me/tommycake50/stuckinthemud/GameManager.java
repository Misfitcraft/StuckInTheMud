package me.tommycake50.stuckinthemud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class GameManager implements Listener {
	ArrayList<String> stuckers;
	HashMap<String, Boolean> stuckees;
	StuckInTheMud inst;
	int x;
	int z;
	int y;
	ArrayList<String> three = new ArrayList<String>();
	Random r;
	int added1;
	int added2;
	int checktask;
	int timetask;
	
	public GameManager(StuckInTheMud inst){
		this.inst = inst;
		r = new Random();
	}
	
	public void start(){
		doScheduleTasks();
		loadRandomArena();
		teleportplayers();
		setOneInThreeStuckeesAndStuckers();
	}
	
	private void setOneInThreeStuckeesAndStuckers() {
		for(Player p : inst.getServer().getOnlinePlayers()){
			if(!stuckers.contains(p.getName()) && !stuckees.containsKey(p.getName())){
				three.add(p.getName());
			}
			if(three.size() == 3){
				for(int i = 0; i < 2; i++){
					stuckees.put(three.get(r.nextInt(3)), false);
				}
				int currentrandom = 1;
				while(currentrandom != added1 && currentrandom != added2){
					currentrandom = r.nextInt(3);
				}
				stuckers.add(three.get(currentrandom));
			}
		}
	}

	private void teleportplayers() {
		for(Player p : inst.getServer().getOnlinePlayers()){
			p.teleport(new Location(p.getWorld(), x, y, z));
		}
	}

	private void loadRandomArena() {
		int amt = inst.config.getConfigurationSection("arenas").getValues(false).size();
		ConfigurationSection cs = inst.config.getConfigurationSection("arenas").getConfigurationSection((r.nextInt(amt) + 1) + "");
		x = cs.getInt("x");
		y = cs.getInt("y");
		z = cs.getInt("z");
	}

	private void doScheduleTasks(){
		timetask = inst.getServer().getScheduler().scheduleSyncDelayedTask(inst, new Runnable(){@Override public void run(){stop("stuckees"); inst.getServer().getScheduler().cancelTask(checktask);}}, inst.config.getInt("matchlength") * 20);
		checktask = inst.getServer().getScheduler().scheduleSyncDelayedTask(inst, new Runnable(){@Override public void run(){if(hasended()){stop("stuckers"); inst.getServer().getScheduler().cancelTask(timetask);}}}, inst.config.getInt("matchcheck") * 20);
	}
	
	protected boolean hasended() {
		boolean returns = true;
		for(String s : stuckees.keySet()){
			if(!stuckees.get(s)){
				returns = false;
			}
		}
		return returns;
	}

	protected void stop(String string) {
		for(Player p : inst.getServer().getOnlinePlayers()){
			p.teleport(p.getWorld().getSpawnLocation());
		}
		inst.getServer().broadcastMessage("Team:" + string + " Were the winning team!");
	}

	@EventHandler
	public void onPlayerLeaveEvent(PlayerQuitEvent e){
		if(stuckers.contains(e.getPlayer())){
			stuckers.remove(e.getPlayer().getName());
		}
		if(stuckees.containsKey(e.getPlayer())){
			stuckees.remove(e.getPlayer().getName());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerMoveEvent(PlayerMoveEvent e){
		if(stuckees.get(e.getPlayer().getName()) == true){
			e.setCancelled(true);
			e.setFrom(e.getFrom());
			e.setTo(e.getFrom());
			e.getPlayer().setVelocity(new Vector(0, 0 ,0));
		}
	}
	
	@EventHandler
	public void onPlayerDamageByPlayerEvent(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
			Player victim = (Player)e.getEntity();
			e.setDamage((double)0);
			if(stuckees.containsKey(victim.getName()) && stuckers.contains(((Player)e.getDamager()).getName())){
				victim.addPotionEffect(PotionEffectType.SLOW.createEffect(999999999, 999999999));
			}else if(stuckees.containsKey(victim.getName()) && stuckees.containsKey(((Player)e.getDamager()).getName())){
				if(victim.hasPotionEffect(PotionEffectType.SLOW)){
					victim.removePotionEffect(PotionEffectType.SLOW);
				}
			}
		}
	}
}
