package me.tommycake50.stuckinthemud.listeners;

import me.tommycake50.stuckinthemud.StuckInTheMud;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener implements Listener {
	StuckInTheMud inst;
	
	public SignListener(StuckInTheMud inst){
		this.inst = inst;
	}
	@EventHandler
	public void onInteractEvent(PlayerInteractEvent e){
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getClickedBlock().getType().equals(Material.WALL_SIGN)){
			Sign s = (Sign)e.getClickedBlock().getState();
			if(s.getLine(1).equalsIgnoreCase("[join]") && e.getPlayer().hasPermission("stuck.chooseteam")){
				switch(s.getLine(2)){
					case "stuckers":
						inst.gameinst.stuckers.add(e.getPlayer().getName());
					break;
					case "stuckees":
						inst.gameinst.stuckees.put(e.getPlayer().getName(), false);
					break;
				}
			}
		}
	}
}
