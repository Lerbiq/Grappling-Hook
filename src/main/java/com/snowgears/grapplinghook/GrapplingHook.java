package com.snowgears.grapplinghook;

import com.snowgears.grapplinghook.api.HookAPI;
import com.snowgears.grapplinghook.utils.Metrics;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GrapplingHook extends JavaPlugin{
	
	public final GrapplingListener alisten = new GrapplingListener(this);
	public static GrapplingHook plugin;
	protected FileConfiguration config;  

	public static boolean usePerms = false;
	public static boolean sendMessages = true;
	public static boolean teleportHooked = false;
	public static boolean fallDamage = false;
	public static boolean disableCrafting = false;
	public static int woodUses = 0;
	public static int stoneUses = 0;
	public static int ironUses = 0;
	public static int goldUses = 0;
	public static int diamondUses = 0;
	public static int netheriteUses = 0;
	public static int timeBetweenUses = 0;
	public static double hookSpeedMultiplier = 0;

	public static String hookedByMessage = "&You have been hooked by %player%!";
	public static String hookedPlayerMessage = "&eYou have hooked %player%!";
	public static String hookedEntityMessage = "&eYou have hooked a %entity%!";
	public static String hookedItemMessage = "&eYou have hooked %amount% of %item%!";
	public static String cooldownMessage = "&cYou cannot do that yet.";
	public static String noHookMessage = "&eYou cannot pull %player%.";
	public static String notEnoughPermission = "&cYou are not authorized to do that!";
	public static String playerGiveGrappling = "&7%player% has given you a grappling hook with %uses% uses!";
	public static String serverGiveGrappling = "&7You have been given a grappling hook with %uses% uses by the server!";
	public static String incorrectArguments = "&Incorrect arguments!";
	public static String playerNotFound = "&cThat player could not be found.";
	public static String disabledWorld = "&cYou cannot use grappling hook in this world.";
	public static String configReloaded = "&cSuccessfully reloaded the config.";

	public static List<String> allowedWorlds = new ArrayList<>();

	public void onEnable(){
		plugin = this;
		getServer().getPluginManager().registerEvents(alisten, this);
		
		 try {
		     Metrics metrics = new Metrics(this);
		     metrics.start();
		 } catch (IOException e) {
		     // Failed to submit the stats
		 }
		
		File configFile = new File(this.getDataFolder() + "/config.yml");

		if(!configFile.exists())
		{
		  this.saveDefaultConfig();
		}

		loadConfigOptions();

		if(!disableCrafting){

			Integer i = 1;


			for(Material plankMaterial : Tag.PLANKS.getValues()) {
				NamespacedKey keyWood = new NamespacedKey(plugin, plugin.getDescription().getName() + "wood" + i);
				i++;
				ShapedRecipe woodRecipe = new ShapedRecipe(keyWood, HookAPI.createGrapplingHook(woodUses))
						.shape(" **", " &*", "   ")
						.setIngredient('*', plankMaterial)
						.setIngredient('&', Material.FISHING_ROD);
				getServer().addRecipe(woodRecipe);
			}

			NamespacedKey keyStone = new NamespacedKey(plugin, plugin.getDescription().getName() + "stone");
			ShapedRecipe stoneRecipe = new ShapedRecipe(keyStone, HookAPI.createGrapplingHook(stoneUses))
					.shape(" **", " &*", "   ")
					.setIngredient('*', Material.COBBLESTONE)
					.setIngredient('&', Material.FISHING_ROD);
			getServer().addRecipe(stoneRecipe);

			NamespacedKey keyIron = new NamespacedKey(plugin, plugin.getDescription().getName() + "Iron");
			ShapedRecipe ironRecipe = new ShapedRecipe(keyIron, HookAPI.createGrapplingHook(ironUses))
					.shape(" **", " &*", "   ")
					.setIngredient('*', Material.IRON_INGOT)
					.setIngredient('&', Material.FISHING_ROD);
			getServer().addRecipe(ironRecipe);

			NamespacedKey keyGold = new NamespacedKey(plugin, plugin.getDescription().getName() + "gold");
			ShapedRecipe goldRecipe = new ShapedRecipe(keyGold, HookAPI.createGrapplingHook(goldUses))
					.shape(" **", " &*", "   ")
					.setIngredient('*', Material.GOLD_INGOT)
					.setIngredient('&', Material.FISHING_ROD);
			getServer().addRecipe(goldRecipe);

			NamespacedKey keyDiamond = new NamespacedKey(plugin, plugin.getDescription().getName() + "diamond");
			ShapedRecipe diamondRecipe = new ShapedRecipe(keyDiamond, HookAPI.createGrapplingHook(diamondUses))
					.shape(" **", " &*", "   ")
					.setIngredient('*', Material.DIAMOND)
					.setIngredient('&', Material.FISHING_ROD);
			getServer().addRecipe(diamondRecipe);

			if(Bukkit.getVersion().contains("1.16")){
				getLogger().info("Detected 1.16.x version. Registering netherite recipe.");
				NamespacedKey keyNetherite = new NamespacedKey(plugin, plugin.getDescription().getName() + "netherite");
				ShapedRecipe netheriteRecipe = new ShapedRecipe(keyNetherite, HookAPI.createGrapplingHook(netheriteUses))
						.shape(" **", " &*", "   ")
						.setIngredient('*', Material.NETHERITE_INGOT)
						.setIngredient('&', Material.FISHING_ROD);
				getServer().addRecipe(netheriteRecipe);
			}
		}
	}

	public void loadConfigOptions(){
		usePerms = getConfig().getBoolean("usePermissions");
		sendMessages = getConfig().getBoolean("sendMessages");
		teleportHooked = getConfig().getBoolean("teleportToHook");
		fallDamage = getConfig().getBoolean("fallDamageWithHook");
		disableCrafting = getConfig().getBoolean("disableCrafting");

		woodUses = getConfig().getConfigurationSection("Uses").getInt("wood");
		stoneUses = getConfig().getConfigurationSection("Uses").getInt("stone");
		ironUses = getConfig().getConfigurationSection("Uses").getInt("iron");
		goldUses = getConfig().getConfigurationSection("Uses").getInt("gold");
		diamondUses = getConfig().getConfigurationSection("Uses").getInt("diamond");
		netheriteUses = getConfig().getConfigurationSection("Uses").getInt("netherite");

		timeBetweenUses = getConfig().getInt("timeBetweenGrapples");

		hookSpeedMultiplier = getConfig().getDouble("hookSpeedMultiplier");

		hookedByMessage = getConfig().getConfigurationSection("Messages").getString("hookedByMessage");
		hookedPlayerMessage = getConfig().getConfigurationSection("Messages").getString("hookedPlayerMessage");
		hookedEntityMessage = getConfig().getConfigurationSection("Messages").getString("hookedEntityMessage");
		hookedItemMessage = getConfig().getConfigurationSection("Messages").getString("hookedItemMessage");
		cooldownMessage = getConfig().getConfigurationSection("Messages").getString("cooldownMessage");
		noHookMessage = getConfig().getConfigurationSection("Messages").getString("noHookMessage");
		notEnoughPermission = getConfig().getConfigurationSection("Messages").getString("notEnoughPermission");
		disabledWorld = getConfig().getConfigurationSection("Messages").getString("disabledWorld");
		configReloaded = getConfig().getConfigurationSection("Messages").getString("configReloaded");

		allowedWorlds = getConfig().getStringList("allowedWorlds");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 1){
			if(sender instanceof Player){
				Player player = (Player)sender;
				if ((cmd.getName().equalsIgnoreCase("gh") && args[0].equalsIgnoreCase("give"))) {
					if(player.hasPermission("grapplinghook.command.give"))
						player.getInventory().setItemInMainHand(HookAPI.createGrapplingHook(50));
					else
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', notEnoughPermission));
				}else if((cmd.getName().equalsIgnoreCase("gh")) && args[0].equalsIgnoreCase("reload")){
					if(player.hasPermission("grapplinghook.command.reload")){
						reloadConfig();
						loadConfigOptions();
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', configReloaded));
					}else{
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', notEnoughPermission));
					}
				}
			}else{
				if ((cmd.getName().equalsIgnoreCase("gh") && args[0].equalsIgnoreCase("give"))) {
					sender.sendMessage("You need to be a player to do this command.");
				}else if((cmd.getName().equalsIgnoreCase("gh")) && args[0].equalsIgnoreCase("reload")){
					reloadConfig();
					loadConfigOptions();
				}
			}
			return true;
		}
		else if(args.length == 2){
			if(sender instanceof Player){
				Player player = (Player)sender;
				if ((cmd.getName().equalsIgnoreCase("gh") && args[0].equalsIgnoreCase("give") && args[1].length() > 0)) {
					if(player.hasPermission("grapplinghook.command.give")){
						if(isInteger(args[1]))
							player.getInventory().setItemInMainHand(HookAPI.createGrapplingHook(Integer.parseInt(args[1])));
						else if(Bukkit.getPlayer(args[1]) != null){
							Bukkit.getPlayer(args[1]).getInventory().addItem(HookAPI.createGrapplingHook(50));
							//Bukkit.getPlayer(args[1]).sendMessage(ChatColor.GRAY+player.getName()+" has given you a grappling hook with 50 uses!");
							Bukkit.getPlayer(args[1]).sendMessage(ChatColor.translateAlternateColorCodes('&', playerGiveGrappling
									.replaceAll("%player%", player.getName()).replaceAll("%player_display_name%", player.getDisplayName()).replaceAll("%uses%", "50")));

						}
						else
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', incorrectArguments + " '/gh give <player>'"));
							//player.sendMessage(ChatColor.RED+"Incorrect arguments. '/gh give <player>'.");
					}
					else
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', notEnoughPermission));
				}
			}
			else{ //sender from console
				if ((cmd.getName().equalsIgnoreCase("gh") && args[0].equalsIgnoreCase("give") && args[1].length() > 0)) {
						if(Bukkit.getPlayer(args[1]) != null){
							Bukkit.getPlayer(args[1]).getInventory().addItem(HookAPI.createGrapplingHook(50));
							//Bukkit.getPlayer(args[1]).sendMessage(ChatColor.GRAY+" You have been given a grappling hook with 50 uses by the server!");
							Bukkit.getPlayer(args[1]).sendMessage(ChatColor.translateAlternateColorCodes('&', serverGiveGrappling
									.replaceAll("%uses%", "50")));
						}
						else
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', incorrectArguments + " '/gh give <player>'"));
				}
			}
			return true;
		}
		else if(args.length == 3){
			if(sender instanceof Player){
				Player player = (Player)sender;
				if (cmd.getName().equalsIgnoreCase("gh") && args[0].equalsIgnoreCase("give")) {
					if(player.hasPermission("grapplinghook.command.give")){
						if(isInteger(args[2])){
							if(Bukkit.getPlayer(args[1]) != null){
								int uses = Integer.parseInt(args[2]);
								Bukkit.getPlayer(args[1]).getInventory().addItem(HookAPI.createGrapplingHook(uses));
								Bukkit.getPlayer(args[1]).sendMessage(ChatColor.GRAY+player.getName()+" has given you a grappling hook with "+uses+" uses!");
							}
							else
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', playerNotFound + " '/gh give <player> <#>'"));
								//player.sendMessage(ChatColor.RED+"That player could not be found. '/gh give <player> <#>'.");
								
						}
						else
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', incorrectArguments + " '/gh give <player> <#>'"));
					}
					else
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', notEnoughPermission));
				}
			}
			else{ //sending from console
				if (cmd.getName().equalsIgnoreCase("gh") && args[0].equalsIgnoreCase("give")) {
						if(isInteger(args[2])){
							if(Bukkit.getPlayer(args[1]) != null){
								int uses = Integer.parseInt(args[2]);
								Bukkit.getPlayer(args[1]).getInventory().addItem(HookAPI.createGrapplingHook(uses));
								Bukkit.getPlayer(args[1]).sendMessage(ChatColor.translateAlternateColorCodes('&', serverGiveGrappling.replaceAll("%uses%", Integer.toString(uses))));
								//Bukkit.getPlayer(args[1]).sendMessage(ChatColor.GRAY+"You have been given a grappling hook with "+uses+" uses by the server!");
							}
							else
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', playerNotFound + " '/gh give <player> <#>'"));
								//sender.sendMessage(ChatColor.RED+"That player could not be found. '/gh give <player> <#>'.");
								
						}
						else
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', incorrectArguments + " '/gh give <player> <#>'"));
							//sender.sendMessage(ChatColor.RED+"Incorrect arguments. '/gh give <player> <#>'.");
				}
			}
			return true;
		}
        return false;
    }
	
    private static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
}