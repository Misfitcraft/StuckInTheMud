package me.tommycake50.stuckinthemud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Builder;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

public class GameManager implements Listener {
	public ArrayList<String> stuckers;
	public HashMap<String, Boolean> stuckees;
	StuckInTheMud inst;
	int x;
	int z;
	int y;
	int stuckx;
	int stucky;
	int stuckz;
	ArrayList<String> three = new ArrayList<String>();
	Random r;
	String addedplayer;
	int checktask;
	int timetask;
	int xptask;
	public ArrayList<String> donors = new ArrayList<String>();
	public boolean graceperiod = true;
	public boolean isingame;
	ScoreboardManager s;
	public Team stuckers1;
	public Team stuckees1;
	private ArrayList<Color> colorlist;
	
	public GameManager(StuckInTheMud inst){
		for(Team t : inst.getServer().getScoreboardManager().getMainScoreboard().getTeams()){
			t.unregister();
		}
		isingame = false;
		this.inst = inst;
		r = new Random();
		stuckees = new HashMap<String, Boolean>();
		stuckers = new ArrayList<String>();
		s = inst.getServer().getScoreboardManager();
		stuckers1 = s.getMainScoreboard().registerNewTeam("stuckers");
		stuckees1 = s.getMainScoreboard().registerNewTeam("stuckees");
		colorlist = new ArrayList<Color>();
		colorlist.add(Color.AQUA);
		colorlist.add(Color.BLACK);
		colorlist.add(Color.BLUE);
		colorlist.add(Color.FUCHSIA);
		colorlist.add(Color.GRAY);
		colorlist.add(Color.GREEN);
		colorlist.add(Color.LIME);
		colorlist.add(Color.MAROON);
		colorlist.add(Color.NAVY);
		colorlist.add(Color.OLIVE);
		colorlist.add(Color.ORANGE);
		colorlist.add(Color.ORANGE);
		colorlist.add(Color.PURPLE);
		colorlist.add(Color.RED);
		colorlist.add(Color.SILVER);
		colorlist.add(Color.TEAL);
		colorlist.add(Color.WHITE);
		colorlist.add(Color.YELLOW);
	}
	
	public void start(){
		stuckers1.setAllowFriendlyFire(true);
		stuckees1.setAllowFriendlyFire(true);
		stuckers1.setPrefix(ChatColor.valueOf(inst.config.getString("stuckercolor").toUpperCase()) + "");
		stuckees1.setPrefix(ChatColor.valueOf(inst.config.getString("stuckeecolor").toUpperCase()) + "");
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
		graceperiod = true;
		inst.getServer().broadcastMessage(ChatColor.GOLD + "Grace period lasts for 10 seconds, RUN!!!");
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
		for(OfflinePlayer op : stuckers1.getPlayers()){
			stuckers1.removePlayer(op);
		}
		for(OfflinePlayer op : stuckees1.getPlayers()){
			stuckees1.removePlayer(op);
		}
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
		if(nooneIsStucker()){
			shiftStuckeesToStuckers();
		}
		for(String s : stuckees.keySet()){
			stuckees1.addPlayer(inst.getServer().getOfflinePlayer(s));
		}
		for(String s : stuckers){
			stuckers1.addPlayer(inst.getServer().getOfflinePlayer(s));
		}
	}

	private boolean nooneIsStucker() {
		if(stuckers.size() == 0){
			return true;
		}
		return false;
	}

	private void shiftStuckeesToStuckers() {
		Random r = new Random();
		Iterator<String> i = stuckees.keySet().iterator();
		String currplayer = null;
		for(int i1 = 0; i1 < r.nextInt(stuckees.keySet().size()); i1++){
			currplayer = i.next();
		}
		stuckees.keySet().remove(currplayer);
		stuckers.add(currplayer);
	}

	private void teleportplayers() {
		for(Player p : inst.getServer().getOnlinePlayers()){
			p.teleport(new Location(p.getWorld(), x, y, z));
		}
	}

	private void loadRandomArena() {
		int amt = inst.config.getInt("amountofarenas");
		ConfigurationSection cs = inst.config.getConfigurationSection("arenas").getConfigurationSection((r.nextInt(amt) + 1) + "");
		if(cs != null){
			x = cs.getInt("x");
			y = cs.getInt("y");
			z = cs.getInt("z");
			stuckx = cs.getInt("stuckx");
			stucky = cs.getInt("stucky");
			stuckz = cs.getInt("stuckz");
		}
	}

	private void doScheduleTasks(){
		timetask = inst.getServer().getScheduler().scheduleSyncDelayedTask(inst, new Runnable(){@Override public void run(){stop("stuckees"); inst.getServer().getScheduler().cancelTask(checktask);}}, inst.config.getInt("matchlength") * 20);
		checktask = inst.getServer().getScheduler().scheduleSyncRepeatingTask(inst, new Runnable(){@Override public void run(){if(hasended()){stop("stuckers"); inst.getServer().getScheduler().cancelTask(timetask);}}}, inst.config.getInt("matchcheck") * 20, inst.config.getInt("matchcheck") * 20);
		xptask = inst.getServer().getScheduler().scheduleSyncRepeatingTask(inst, new Runnable(){@Override public void run(){for(Player p : inst.getServer().getOnlinePlayers())p.setLevel(p.getLevel() - 1);}}, 20, 20);
		inst.getServer().getScheduler().scheduleSyncDelayedTask(inst, new Runnable(){@Override public void run(){graceperiod = false; inst.getServer().broadcastMessage(ChatColor.GREEN + "Grace period has ended! RUN SOME MORE!!!");}}, 10*20);
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
		inst.getServer().broadcastMessage(ChatColor.GOLD + "Team: " + string + " Were the winning team!");
		clearUp();
		isingame = false;
		for(Player p : inst.getServer().getOnlinePlayers()){
			p.setLevel(0);
			p.setExp(0);
		}
		startFireworks();
		clearEffects();
		scheduleStopServer();
	}
	
	private void clearEffects(){
		for(Player p : inst.getServer().getOnlinePlayers()){
			for(PotionEffect p1 : p.getActivePotionEffects()){
				p.removePotionEffect(p1.getType());
			}
		}
	}

	private void startFireworks() {
		final Random r = new Random();
		inst.getServer().getScheduler().scheduleSyncRepeatingTask(inst, new Runnable(){
			public void run(){
				Location currfirelockcopy = inst.getServer().getWorld("world").getSpawnLocation();
				currfirelockcopy.add(new Vector(r.nextInt(3),r.nextInt(3), r.nextInt(3)));
				spawnRandom(currfirelockcopy);
			}
		}, 10, 10);
	}

	private void spawnRandom(Location currfirelock) {
		Random r = new Random();
		Firework f = (Firework) inst.getServer().getWorld("world").spawnEntity(currfirelock, EntityType.FIREWORK);
		FireworkMeta fm = f.getFireworkMeta();
		Builder b = FireworkEffect.builder();
		for(int i = 0; i < r.nextInt(20); i++){
			b.withColor(colorlist.get(r.nextInt(colorlist.size())));
		}
		b.flicker(r.nextBoolean());
		for(int i = 0; i < r.nextInt(20); i++){
			b.withFade(colorlist.get(r.nextInt(colorlist.size())));
		}
		fm.addEffect(b.build());
		f.setFireworkMeta(fm);
	}

	protected void scheduleStopServer() {
		inst.getServer().broadcastMessage("[" + ChatColor.RED + "Stuck" + ChatColor.WHITE + "]" + ChatColor.GREEN + "Server Restarting in 10 Seconds....");
		inst.getServer().getScheduler().scheduleSyncDelayedTask(inst, new Runnable(){public void run(){reset();}}, 10*20);
	}
	
	void reset(){
		inst.getServer().shutdown();
	}
	
	@EventHandler
	public void onPlayerLeaveEvent(PlayerQuitEvent e){
		if(stuckers.contains(e.getPlayer().getName())){
			stuckers.remove(e.getPlayer().getName());
		}
		if(stuckees.containsKey(e.getPlayer().getName())){
			stuckees.remove(e.getPlayer().getName());
		}
	}
	
	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent e){
		if(stuckers.contains(((Player)e.getEntity()).getName())){
			stuckers.remove(((Player)e.getEntity()).getName());
		}
		if(stuckees.containsKey(((Player)e.getEntity()).getName())){
			stuckees.remove(((Player)e.getEntity()).getName());
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDamageByPlayerEvent(EntityDamageByEntityEvent e){
		if(e.getDamager() instanceof Player && e.getEntity() instanceof Player){
			Player victim = (Player)e.getEntity();
			e.setDamage((double)0);
			if(stuckees.containsKey(victim.getName()) && stuckers.contains(((Player)e.getDamager()).getName()) && !graceperiod && !stuckees.get(victim.getName())){
				stuckees.put(victim.getName(), true);
				victim.addPotionEffect(PotionEffectType.SLOW.createEffect(100000000, 255));
				victim.addPotionEffect(PotionEffectType.JUMP.createEffect(100000000, 128));
				inst.getServer().broadcastMessage(ChatColor.RED + victim.getDisplayName() + "Was stuck by: " + ((Player)e.getDamager()).getDisplayName() + "!");
			}else if(stuckees.containsKey(victim.getName()) && stuckees.containsKey(((Player)e.getDamager()).getName()) && !stuckees.get(((Player)e.getDamager()).getName()) && stuckees.get(victim.getName())){
					stuckees.put(victim.getName(), false);
					for(PotionEffect p : victim.getActivePotionEffects()){
						victim.removePotionEffect(p.getType());
					}
					inst.getServer().broadcastMessage(ChatColor.RED + victim.getDisplayName() + "Was unstuck by: " + ((Player)e.getDamager()).getDisplayName() + "!");
			}
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
