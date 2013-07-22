package me.tommycake50.stuckinthemud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

public class GameManager implements Listener {
	public ArrayList<String> stuckers;
	public HashMap<String, Boolean> stuckees;
	StuckInTheMud inst;
	int x;
	int z;
	int y;
	ArrayList<String> three = new ArrayList<String>();
	Random r;
	String addedplayer;
	int checktask;
	int timetask;
	int xptask;
	public ArrayList<String> donors = new ArrayList<String>();
	public boolean graceperiod = true;
	public boolean isingame;
	
	public GameManager(StuckInTheMud inst){
		this.inst = inst;
		r = new Random();
		stuckees = new HashMap<String, Boolean>();
		stuckers = new ArrayList<String>();
	}
	
	public void start(){
		doScheduleTasks();
		loadRandomArena();
		teleportplayers();
		setOneInThreeStuckeesAndStuckers();
		givePlayersxp();
		isingame = true;
		checkMultiples();
		for(String s : stuckers){
			inst.getServer().getPlayer(s).sendMessage(ChatColor.DARK_BLUE + "You are a stucker, Stick all the stuckees!");
		}
		for(String s : stuckees.keySet()){
			inst.getServer().getPlayer(s).sendMessage(ChatColor.DARK_BLUE + "You are a stuckee, Unstick all your fellow stuckees and avoid getting stuck!");
		}
	}
	
	private void checkMultiples() {
		for(Player p : inst.getServer().getOnlinePlayers()){
			if(stuckers.contains(p.getName()) && stuckees.containsKey(p.getName())){
				stuckees.remove(p.getName());
			}
		}
	}

	private void givePlayersxp() {
		for(Player p : inst.getServer().getOnlinePlayers()){
			p.setLevel(inst.config.getInt("matchlength"));;
		}
	}

	private void clearUp() {
		donors.clear();
		stuckers.clear();
		stuckees.clear();
		inst.getServer().getScheduler().cancelTask(xptask);
		inst.getServer().getScheduler().cancelTask(timetask);
		inst.getServer().getScheduler().cancelTask(checktask);
		graceperiod = true;
	}

	private void setOneInThreeStuckeesAndStuckers() {
		if(((inst.getServer().getOnlinePlayers().length - donors.size()) % 3) == 0){
			for(Player p : inst.getServer().getOnlinePlayers()){
				if(!stuckers.contains(p.getName()) && !stuckees.containsKey(p.getName())){
					three.add(p.getName());
				}
				if(three.size() == 3){
					for(int i = 0; i < 2; i++){
						stuckees.put(three.get(r.nextInt(3)), false);
					}
					addedplayer = three.get(r.nextInt(3));
					stuckers.add(addedplayer);
					three.remove(addedplayer);
					for(String s : three){
						stuckees.put(s, false);
					}
					three.clear();
				}
			}	
		}else{
			for(Player p : inst.getServer().getOnlinePlayers()){
				if(r.nextInt(3) == 0 && !donors.contains(p.getName())){
					stuckers.add(p.getName());
				}else{
					stuckees.put(p.getName(), false);
				}
			}
		}
	}

	private void teleportplayers() {
		for(Player p : inst.getServer().getOnlinePlayers()){
			p.teleport(new Location(p.getWorld(), x, y, z));
		}
	}

	private void loadRandomArena() {
		int amt = inst.config.getInt("amountofarenas");
		ConfigurationSection cs = inst.config.getConfigurationSection("arenas").getConfigurationSection((r.nextInt(amt) + 1) + "");
		x = cs.getInt("x");
		y = cs.getInt("y");
		z = cs.getInt("z");
	}

	private void doScheduleTasks(){
		timetask = inst.getServer().getScheduler().scheduleSyncDelayedTask(inst, new Runnable(){@Override public void run(){stop("stuckees"); inst.getServer().getScheduler().cancelTask(checktask);}}, inst.config.getInt("matchlength") * 20);
		checktask = inst.getServer().getScheduler().scheduleSyncRepeatingTask(inst, new Runnable(){@Override public void run(){if(hasended()){stop("stuckers"); inst.getServer().getScheduler().cancelTask(timetask);}}}, inst.config.getInt("matchcheck") * 20, inst.config.getInt("matchcheck") * 20);
		xptask = inst.getServer().getScheduler().scheduleSyncRepeatingTask(inst, new Runnable(){@Override public void run(){for(Player p : inst.getServer().getOnlinePlayers())p.setLevel(p.getLevel() - 1);}}, 20, 20);
		inst.getServer().getScheduler().scheduleSyncDelayedTask(inst, new Runnable(){@Override public void run(){graceperiod = false;}}, 5*20);
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
		clearUp();
		isingame = false;
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
	
	@EventHandler
	public void onPlayerDamageByPlayerEvent(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
			Player victim = (Player)e.getEntity();
			e.setDamage((double)0);
			if(stuckees.containsKey(victim.getName()) && stuckers.contains(((Player)e.getDamager()).getName()) && !graceperiod && !stuckees.get(victim.getName())){
				stuckees.put(victim.getName(), true);
			}else if(stuckees.containsKey(victim.getName()) && stuckees.containsKey(((Player)e.getDamager()).getName())){
					stuckees.put(victim.getName(), false);
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPLayerMoveEvent(PlayerMoveEvent e){
		if(stuckees.get(e.getPlayer().getName())){
			e.setCancelled(true);
			e.setTo(e.getFrom());
		}
	}
	@EventHandler
	public void velocity(PlayerVelocityEvent e){
		if(stuckees.get(e.getPlayer().getName())){
			e.setVelocity(new Vector(0, 0, 0));
			e.setCancelled(true);
		}
	}
}
