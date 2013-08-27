package me.tommycake50.stuckinthemud.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import static org.bukkit.event.EventPriority.*;

public class FallDamageListener implements Listener{
	
	@EventHandler(priority=HIGHEST)
	public void onPlayerTakeFallDamageEvent(EntityDamageEvent e){
		if(e.getCause().equals(DamageCause.FALL)){
			e.setCancelled(true);
		}
	}
}
