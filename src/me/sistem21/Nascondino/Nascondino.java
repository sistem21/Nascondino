package me.sistem21.Nascondino;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class Nascondino extends JavaPlugin implements Listener{
	
	private Map<UUID, Long> cooldown;
	ItemStack abilitato,disabilitato;
	
	public void onEnable(){
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		
	    abilitato = new ItemStack(Material.INK_SACK, 1, (short) 10);
	    ItemMeta abilitatoM = abilitato.getItemMeta();
		abilitatoM.setDisplayName("§bGiocatori §7- §aAbilitati");
		abilitatoM.setLore(Arrays.asList("§7Abilita i giocatori"));
		abilitato.setItemMeta(abilitatoM);
		
		disabilitato = new ItemStack(Material.INK_SACK, 1, (short) 8);
		ItemMeta disabilitatoM = disabilitato.getItemMeta();
		disabilitatoM.setDisplayName("§bGiocatori §7- §aDisabilitati");
		disabilitatoM.setLore(Arrays.asList("§7Disabilita i giocatori"));
		disabilitato.setItemMeta(disabilitatoM);
		
		this.cooldown = new HashMap<>();
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		e.getPlayer().getInventory().setItem(7, abilitato);
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e){
		e.getPlayer().getInventory().setItem(7, abilitato);
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent e){
		Player p = e.getPlayer();
		ItemStack item = p.getItemInHand();
		
		if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(item.hasItemMeta()){
				if(item.getItemMeta().hasDisplayName()){
					if(item.getItemMeta().getDisplayName().equals("§bGiocatori §7- §aAbilitati")){
						if(puòUsarlo(p, cooldown, 7)){
							for(Player pl : Bukkit.getOnlinePlayers()){
								if(p.canSee(pl)){
									p.hidePlayer(pl);
								}
							}
							p.getInventory().removeItem(abilitato);
							p.setItemInHand(disabilitato);
							p.sendMessage("§9§lHub §8§l» §7Hai §cnascosto §7gli altri giocatori");
						}
					}else if(item.getItemMeta().getDisplayName().equals("§bGiocatori §7- §aDisabilitati")){
						if(puòUsarlo(p, cooldown, 7)){
							for(Player pl : Bukkit.getOnlinePlayers()){
								if(p.canSee(pl)){
									e.setCancelled(true);
								}else{
									p.showPlayer(pl);
								}
							}
							p.getInventory().remove(disabilitato);
							p.setItemInHand(abilitato);
							p.sendMessage("§9§lHub §8§l» §7Ora puoi §avedere §7gli altri giocatori.");
						}
					}
				}
			}
		}
		
	}
	
	 @EventHandler
	  public void onDropEvent(PlayerDropItemEvent event) {
	    Player player = event.getPlayer();
	    if ((!player.isOp())) {
	      event.setCancelled(true);
	      player.sendMessage("§9§lHub §8§l» §cNon puoi buttare item a terra!");
	    }
	  }
	
	public boolean puòUsarlo(Player p, Map<UUID, Long> map, int secondi){
		UUID uuid = p.getUniqueId();
		
		if(map.containsKey(uuid)){
			long differenza = (System.currentTimeMillis() - map.get(uuid)) / 1000;
			if(differenza < secondi){
				if(secondi - differenza == 1){
					p.sendMessage("§9§lHub §8§l» §7Aspetta §9" + (secondi - differenza) + " secondo §7prima di riprovare.");
				}else{
					p.sendMessage("§9§lHub §8§l» §7Aspetta §9" + (secondi - differenza) + " secondi §7prima di riprovare.");
					return false;
				}
				
			}else{
				map.remove(uuid);
			}
			
		}else{
			map.put(uuid, System.currentTimeMillis());
		}
		return true;
	}
	

}
