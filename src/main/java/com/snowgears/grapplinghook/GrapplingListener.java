package com.snowgears.grapplinghook;

import com.snowgears.grapplinghook.api.HookAPI;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;
import org.bukkit.inventory.meta.Damageable;

import java.util.HashMap;


public class GrapplingListener implements Listener{

	public GrapplingHook plugin;

	public HashMap<Integer, Integer> noFallEntities = new HashMap<>(); //entity id, delayed task id
	public HashMap<String, Integer> noGrapplePlayers = new HashMap<>(); //name, delayed task id
	
	public GrapplingListener(GrapplingHook instance) {
		plugin = instance;
	}
	
	@EventHandler
	public void onPreCraft(CraftItemEvent event){
		if(!GrapplingHook.usePerms)
			return;
		if(event.getView().getPlayer() instanceof Player){
			Player player = (Player)event.getView().getPlayer();
			if(HookAPI.isGrapplingHook(event.getInventory().getResult())){
				for(ItemStack is : event.getInventory().getContents()){
					if(Tag.PLANKS.isTagged(is.getType())){
						if(!player.hasPermission("grapplinghook.craft.wood")) {
							event.setCancelled(true);
							return;
						}
					}
				}
				if(event.getInventory().contains(Material.COBBLESTONE)){
					if(!player.hasPermission("grapplinghook.craft.stone"))
						event.setCancelled(true);
				}
				else if(event.getInventory().contains(Material.IRON_INGOT)){
					if(!player.hasPermission("grapplinghook.craft.iron"))
						event.setCancelled(true);
				}
				else if(event.getInventory().contains(Material.GOLD_INGOT)){
					if(!player.hasPermission("grapplinghook.craft.gold"))
						event.setCancelled(true);
				}
				else if(event.getInventory().contains(Material.DIAMOND)){
					if(!player.hasPermission("grapplinghook.craft.diamond"))
						event.setCancelled(true);
				}else if(Bukkit.getVersion().contains("1.16")){
					if(event.getInventory().contains(Material.NETHERITE_INGOT)){
						if(!player.hasPermission("grapplinghook.craft.netherite")){
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

    /*@EventHandler //Isn't working as it should. Event doesnt get even called when using grappling hook.
    public void onAttack(EntityDamageByEntityEvent event){
		Bukkit.getLogger().info("EntityDamageByEntityEvent triggered.");
		if(event.isCancelled()){
			Bukkit.getLogger().info("Event canceled.");
		}
    	
    		if(event.getDamager() instanceof FishHook){
    		FishHook hook = (FishHook)event.getDamager();
    		if( ! (hook.getShooter() instanceof Player))
    			return;
    		Player player = (Player)hook.getShooter();

    		if(!HookAPI.isGrapplingHook(player.getInventory().getItemInMainHand()))
    			return;
    		
    		if(!GrapplingHook.usePerms || player.hasPermission("grapplinghook.pull.players")){
	    		
	    		if(event.getEntity() instanceof Player){
		    		Player hooked = (Player)event.getEntity();
		    		if(hooked.hasPermission("grapplinghook.player.nopull")){
		    			event.setCancelled(true);
		    		}
		    		else if(GrapplingHook.sendMessages){
		    			hooked.sendMessage(
		    					ChatColor.translateAlternateColorCodes('&', GrapplingHook.hookedByMessage
										.replaceAll("%player%", player.getName()).replaceAll("%player_display_name%", player.getDisplayName()))
						);
		    			//hooked.sendMessage(ChatColor.YELLOW+"You have been hooked by "+ ChatColor.RESET+player.getName()+ChatColor.YELLOW+"!");
						player.sendMessage(
								ChatColor.translateAlternateColorCodes('&', GrapplingHook.hookedPlayerMessage
										.replaceAll("%player%", hooked.getName()).replaceAll("%player_display_name%", hooked.getDisplayName())));
		    			//player.sendMessage(ChatColor.GOLD+"You have hooked "+ChatColor.RESET+hooked.getName()+ChatColor.YELLOW+"!");
		    		}
	    		}
	    		else{
	    			if(GrapplingHook.sendMessages){
						String entityName = event.getEntityType().toString().replace("_", " ").toLowerCase();
						String entityCustomName = event.getEntity().getCustomName();
						if(entityCustomName == null){
							entityCustomName = entityName;
						}
						player.sendMessage(
								ChatColor.translateAlternateColorCodes('&', GrapplingHook.hookedEntityMessage
										.replaceAll("%entity%", entityName).replaceAll("%entity_name%", entityCustomName)));
						//player.sendMessage(ChatColor.GOLD+"You have hooked a "+entityName+"!");
					}
	    		}
	    	}
    		}
	}*/
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageEvent(EntityDamageEvent event) {
        if (event.getCause() == DamageCause.FALL) {
        	if(GrapplingHook.fallDamage){
        		return;
        	}
        	if(noFallEntities.containsKey(event.getEntity().getEntityId()))
        		event.setCancelled(true);
        }
    }

    @EventHandler
	public void onProjectileShoot(ProjectileLaunchEvent e){
		final double multiplier = GrapplingHook.hookSpeedMultiplier;
		if (e.getEntityType().equals((Object)EntityType.FISHING_HOOK)) {
			e.getEntity().setVelocity(e.getEntity().getVelocity().multiply(multiplier));
		}
	}

	/*@EventHandler
	public void onLeftClickWithHook(PlayerInteractEvent e){
		if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK){
			if(HookAPI.isGrapplingHook(e.getItem())){

			}
		}
	}*/

    @EventHandler (priority = EventPriority.MONITOR)
    public void onGrapple(PlayerGrappleEvent event){
    	if(event.isCancelled())
    		return;
		final Player player = event.getPlayer();
    	if(!GrapplingHook.allowedWorlds.contains(player.getLocation().getWorld().getName())){
    		if(!player.hasPermission("grapplinghook.worlds.bypass")){
				player.sendMessage(ChatColor.translateAlternateColorCodes('&',GrapplingHook.disabledWorld));
				event.setCancelled(true);
				return;
			}
		}


		Damageable meta = (Damageable) event.getHookItem().getItemMeta();
		meta.setDamage((short)-10);
		event.getHookItem().setItemMeta((ItemMeta) meta);
    	
    	if(noGrapplePlayers.containsKey(player.getName())){
    		if(!player.hasPermission("grapplinghook.player.nocooldown")){
    			player.sendMessage(ChatColor.translateAlternateColorCodes('&', GrapplingHook.cooldownMessage));
    			return;
    		}
    	}
    	
    	Entity e = event.getPulledEntity();
    	Location loc = event.getPullLocation();
    	
    	if(player.equals(e)){ //the player is pulling themself to a location
	    	if(GrapplingHook.teleportHooked){
	    		loc.setPitch(player.getLocation().getPitch());
	    		loc.setYaw(player.getLocation().getYaw());
	        	player.teleport(loc);
	    	}
	    	else{
	    		if(player.getLocation().distance(loc) < 6) //hook is too close to player
	    			pullPlayerSlightly(player, loc);
	    		else
	    			pullEntityToLocation(player, loc);
	    	}
    	}
    	else{ //the player is pulling an entity to them
    		if(GrapplingHook.teleportHooked)
	        	e.teleport(loc);
	    	else{
	    		pullEntityToLocation(e, loc);
	    		if(e instanceof Item){
	    			if(GrapplingHook.sendMessages){
						ItemStack is = ((Item)e).getItemStack();
						String itemName = is.getType().toString().replace("_", " ").toLowerCase();
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', GrapplingHook.hookedItemMessage
								.replaceAll("%amount%", Integer.toString(is.getAmount())).replaceAll("%item%", itemName)));
					}
	    		}else if(e instanceof Player){
					Player hooked = (Player)e;
					if(hooked.hasPermission("grapplinghook.player.nopull")){
						event.setCancelled(true);
					}
					else if(GrapplingHook.sendMessages){
						hooked.sendMessage(
								ChatColor.translateAlternateColorCodes('&', GrapplingHook.hookedByMessage
										.replaceAll("%player%", player.getName()).replaceAll("%player_display_name%", player.getDisplayName()))
						);
						player.sendMessage(
								ChatColor.translateAlternateColorCodes('&', GrapplingHook.hookedPlayerMessage
										.replaceAll("%player%", hooked.getName()).replaceAll("%player_display_name%", hooked.getDisplayName())));
					}
				}
				else{
					if(GrapplingHook.sendMessages){
						String entityName = e.getName().toString().replace("_", " ").toLowerCase();
						String entityCustomName = e.getCustomName();
						if(entityCustomName == null){
							entityCustomName = entityName;
						}
						player.sendMessage(
								ChatColor.translateAlternateColorCodes('&', GrapplingHook.hookedEntityMessage
										.replaceAll("%entity%", entityName).replaceAll("%entity_name%", entityCustomName)));
						//player.sendMessage(ChatColor.GOLD+"You have hooked a "+entityName+"!");
					}
				}
	    	}
    	}

    	if(HookAPI.addUse(player, event.getHookItem()))
    		HookAPI.playGrappleSound(player.getLocation());
    	
    	if(GrapplingHook.timeBetweenUses != 0)
    		HookAPI.addPlayerCooldown(player, GrapplingHook.timeBetweenUses);
    }
    
    @EventHandler (priority = EventPriority.HIGHEST)
    public void fishEvent(PlayerFishEvent event) //called before projectileLaunchEvent
    {
        Player player = event.getPlayer();


        if(!HookAPI.isGrapplingHook(player.getInventory().getItemInMainHand()))
        	return;

		if(event.getState() == org.bukkit.event.player.PlayerFishEvent.State.IN_GROUND  || event.getState() == org.bukkit.event.player.PlayerFishEvent.State.FAILED_ATTEMPT){

        	Location loc = event.getHook().getLocation();

			if(!GrapplingHook.usePerms || player.hasPermission("grapplinghook.pull.items")){
	        	for(Entity ent : event.getHook().getNearbyEntities(1.5, 1, 1.5)){
	        		if(ent instanceof Item){
	        			PlayerGrappleEvent e = new PlayerGrappleEvent(player, ent, player.getLocation());
	                	plugin.getServer().getPluginManager().callEvent(e);
	        			return;
	        		}
	        	}
			}
        	
			if(!GrapplingHook.usePerms || player.hasPermission("grapplinghook.pull.self")){
				PlayerGrappleEvent e = new PlayerGrappleEvent(player, player, loc);
				plugin.getServer().getPluginManager().callEvent(e);
			}
        }
        else if(event.getState() == org.bukkit.event.player.PlayerFishEvent.State.CAUGHT_ENTITY){
        	event.setCancelled(true);
        	if(event.getCaught() instanceof Player){
        		Player hooked = (Player)event.getCaught();
        		if(hooked.hasPermission("grapplinghook.player.nopull")){
	    			event.setCancelled(true);
	    			player.sendMessage(ChatColor.translateAlternateColorCodes('&', GrapplingHook.noHookMessage
							.replaceAll("%player%", hooked.getName()).replaceAll("%hooked_display_name%", hooked.getDisplayName())));
	    		}
        		else if(!GrapplingHook.usePerms || player.hasPermission("grapplinghook.pull.players")){
            		PlayerGrappleEvent e = new PlayerGrappleEvent(player, hooked, player.getLocation());
                	plugin.getServer().getPluginManager().callEvent(e);
    			}
        	}
        	else if(!GrapplingHook.usePerms || player.hasPermission("grapplinghook.pull.mobs")){
        		PlayerGrappleEvent e = new PlayerGrappleEvent(player, event.getCaught(), player.getLocation());
            	plugin.getServer().getPluginManager().callEvent(e);
			}
        }
        else if(event.getState() == org.bukkit.event.player.PlayerFishEvent.State.CAUGHT_FISH){
        	event.setCancelled(true);
        }
    }

//	//FOR HOOKING AN ITEM AND PULLING TOWARD YOU
//	public void pullItemToLocation(Item i, Location loc){
//		ItemStack is = i.getItemStack();
//		i.getWorld().dropItemNaturally(loc, is);
//		i.remove();
//	}
//	
//	//FOR HOOKING AN ITEM AND PULLING TOWARD YOU
//	public void pullItemToLocation(Entity e, Location loc){
//		Location oLoc = e.getLocation().add(0, 1, 0);
//		Location pLoc = loc;
//	
//		// Velocity from Minecraft Source. 
//		double d1 = pLoc.getX() - oLoc.getX();
//		double d3 = pLoc.getY() - oLoc.getY();
//		double d5 = pLoc.getZ() - oLoc.getZ();
//		double d7 = ((float) Math
//				.sqrt((d1 * d1 + d3 * d3 + d5 * d5)));
//		double d9 = 0.10000000000000001D;
//		double motionX = d1 * d9;
//		double motionY = d3 * d9 + (double) ((float) Math.sqrt(d7))
//				* 0.080000000000000002D;
//		double motionZ = d5 * d9;
//		e.setVelocity(new Vector(motionX, motionY, motionZ));
//	}
	
//	//FOR HOOKING AN ENTITY AND PULLING TOWARD YOU
//	private void pullEntityToLocation(Entity e, Location loc){
//		Location entityLoc = e.getLocation();
//		
//		double dX = entityLoc.getX() - loc.getX();
//		double dY = entityLoc.getY() - loc.getY();
//		double dZ = entityLoc.getZ() - loc.getZ();
//		
//		double yaw = Math.atan2(dZ, dX);
//		double pitch = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY) + Math.PI;
//		
//		double X = Math.sin(pitch) * Math.cos(yaw);
//		double Y = Math.sin(pitch) * Math.sin(yaw);
//		double Z = Math.cos(pitch);
//		 
//		Vector vector = new Vector(X, Z, Y);
//		e.setVelocity(vector.multiply(8));
//	}
	
	//For pulling a player slightly
	private void pullPlayerSlightly(Player p, Location loc){
		if(loc.getY() > p.getLocation().getY()){
			p.setVelocity(new Vector(0,0.25,0));
			return;
		}
		
		Location playerLoc = p.getLocation();
		
		Vector vector = loc.toVector().subtract(playerLoc.toVector());
		p.setVelocity(vector);
	}
	
//	//Code from r306 roll the dice
//	private void pullEntityToLocation(final Entity e, Location loc){
//		Location entityLoc = e.getLocation();
//			
//		final Vector velocity = loc.toVector().subtract(entityLoc.subtract(0, 1, 0).toVector()).normalize().multiply(new Vector(2, 2, 2));
//		
//		if (Math.abs(loc.getBlockY() - entityLoc.getBlockY()) < 2 && loc.distance(entityLoc) > 4)
//		{
//
//			e.setVelocity(velocity.multiply(new Vector(1, 1, 1)));
//
//			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
//			{
//				@Override
//				public void run() 
//				{
//					e.setVelocity(velocity.multiply(new Vector(1, 1, 1)));
//					//player.setVelocity(location.toVector().subtract(player.getLocation().subtract(0, 1, 0).toVector().normalize().multiply(2)));
//
//				}
//
//			}, 1L);
//		}
//		else
//		{
//			e.setVelocity(velocity);
//
//			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
//			{
//				@Override
//				public void run() 
//				{
//					e.setVelocity(velocity.multiply(new Vector(1, 1, 1)));
//					//player.setVelocity(location.toVector().subtract(player.getLocation().subtract(0, 1, 0).toVector().normalize().multiply(0.5)));
//				}
//			}, 0L);
//		}
//		addNoFall(e, 100);
//	}
	
	//better method for pulling
	private void pullEntityToLocation(final Entity e, Location loc){
		Location entityLoc = e.getLocation();

		entityLoc.setY(entityLoc.getY()+0.5);
		e.teleport(entityLoc);
		
		double g = -0.08;
		double d = loc.distance(entityLoc);
		double t = d;
		double v_x = (1.0+0.07*t) * (loc.getX()-entityLoc.getX())/t;
		double v_y = (1.0+0.03*t) * (loc.getY()-entityLoc.getY())/t -0.5*g*t;
		double v_z = (1.0+0.07*t) * (loc.getZ()-entityLoc.getZ())/t;
		
		Vector v = e.getVelocity();
		v.setX(v_x);
		v.setY(v_y);
		v.setZ(v_z);
		e.setVelocity(v);
		
		addNoFall(e, 100);
	}
	
	public void addNoFall(final Entity e, int ticks) {
		if(noFallEntities.containsKey(e.getEntityId()))
			Bukkit.getServer().getScheduler().cancelTask(noFallEntities.get(e.getEntityId()));
		
		int taskId = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,new Runnable() {
			 @Override
			public void run(){
				  if(noFallEntities.containsKey(e.getEntityId()))
					 noFallEntities.remove(e.getEntityId());
			  }
	  	}, ticks);
		
		noFallEntities.put(e.getEntityId(), taskId);
	}
}