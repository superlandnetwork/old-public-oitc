//   ___    ___   _____    ___ 
//  / _ \  |_ _| |_   _|  / __|
// | (_) |  | |    | |   | (__ 
//  \___/  |___|   |_|    \___|
//
// Copyright (C) Filli-IT (Einzelunternehmen) & Ursin Filli - All Rights Reserverd
// Unauthorized copying of the this file, via any medium is strictly prohibited
// Proprietary and confidential
// Written by Ursin Filli <ursin.filli@Filli-IT.ch>

package de.superlandnetwork.oitc.listener;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import de.superlandnetwork.API.PlayerAPI.PermEnum;
import de.superlandnetwork.API.PlayerAPI.PlayerAPI;
import de.superlandnetwork.API.ServerAPI.ServerAPI;
import de.superlandnetwork.API.StatsAPI.StatsAPI;
import de.superlandnetwork.API.StatsAPI.StatsEnum;
import de.superlandnetwork.API.Utils.Nick;
import de.superlandnetwork.API.Utils.Tablist;
import de.superlandnetwork.oitc.GameState;
import de.superlandnetwork.oitc.Main;
import de.superlandnetwork.oitc.utils.Methods;
import net.minecraft.server.v1_12_R1.PacketPlayInClientCommand;
import net.minecraft.server.v1_12_R1.PacketPlayInClientCommand.EnumClientCommand;

public class GameListener implements Listener{
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent e){
		final Player player = e.getPlayer();
		e.setCancelled(true);
			
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable(){

			public void run() {
				player.updateInventory();
			}
				
		}, 1L);
			
	}
	
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent e) {
		if (((e.getEntity() instanceof Player)) && ((e.getDamager() instanceof Arrow))) {
			Arrow arrow = (Arrow)e.getDamager();
			if ((arrow.getShooter() instanceof Player)) {
				Player attacker = (Player)arrow.getShooter();
				Player player = (Player)e.getEntity();
				if (!player.getName().equalsIgnoreCase(attacker.getName())) {
					e.setDamage(100.0D);
				} else {
					e.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onProjHit(ProjectileHitEvent e){
		if(e.getEntity() instanceof Arrow){
			Arrow arrow = (Arrow) e.getEntity();
			if(arrow.getShooter() instanceof Player){
				arrow.remove();
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onRespawn(PlayerRespawnEvent e){
		Player player = e.getPlayer();
		e.setRespawnLocation(Main.getRandomSpawn());
		Methods.setDefaultGameInventory(player);
		player.updateInventory();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeath(PlayerDeathEvent e) {
		final Player player = e.getEntity();
		e.getDrops().clear();
		e.setDeathMessage("");
		e.setDroppedExp(0);
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
			public void run() {
				PacketPlayInClientCommand packet = new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN);
				((CraftPlayer)player).getHandle().playerConnection.a(packet);
			}
		}, 1);
				
		if(player.getKiller() != null) {
			Player killer = player.getKiller();
			onPlayerKill(killer, player);		
		}
	}
	
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e){
		Player player = e.getPlayer();
		player.getInventory().clear();
		Main.removePlayer(player);
		e.setQuitMessage(null);
		Main.getInstance().server.setPlayers_Online(Bukkit.getOnlinePlayers().size()-1);
		new ServerAPI(Main.getInstance().server).update();
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		p.teleport(Methods.getLobby());
		p.getInventory().clear();
		if(Main.getInstance().NickedPlayers.contains(p.getUniqueId())) {
			p.sendMessage("§7[§5NICK§7] §4Du spielst als §e" + Main.getInstance().NickInstances.get(p.getUniqueId()).getNick());
			p.sendMessage(" ");
		}
		Main.addPlayer(p);
		Main.setScoreboard(p, false);
		e.setJoinMessage(null);
		new Tablist(p, "§eSuperLandNetwork.de Netzwerk §7- §aOITC", "§7Teamspeak: §eTs.SuperLandNetwork.de \n §7Shop: §eShop.SuperLandNetwork.de");
		p.setGameMode(GameMode.ADVENTURE);
		p.setFoodLevel(20);
		p.setHealth(20.0D);
		Methods.setPrefix(p);
		StatsAPI sAPI = new StatsAPI(p.getUniqueId());
		sAPI.InsertUserInDB(StatsEnum.OITC);
		Main.getInstance().server.setPlayers_Online(Bukkit.getOnlinePlayers().size());
		new ServerAPI(Main.getInstance().server).update();
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		UUID UUID = p.getUniqueId();
		e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
		PlayerAPI api = new PlayerAPI(UUID);
		if(Main.getInstance().NickedPlayers.contains(UUID))
			e.setFormat(PermEnum.SPIELER.getPrefix() + p.getName() + "§f" + " §7: §f" + e.getMessage());
		else
			e.setFormat(api.getChatPrefix() + " §7: §f" + e.getMessage());
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		PlayerAPI api = new PlayerAPI(e.getPlayer().getUniqueId());
		if(Main.getState() == GameState.RESTART) {
			e.disallow(Result.KICK_WHITELIST, "Server is Restarting...");
			return;
		}
		if(Main.getState() == GameState.INGAME) {
			e.disallow(Result.KICK_WHITELIST, "Game is Running...");
			return;
		}
		if(e.getResult() == Result.KICK_FULL) {
			if(!api.IsPlayerInGroup(1)) {
				if(kickPlayer())
					e.allow();
			}
		}
		if(api.AutoNick()) {
			Nick nick = new Nick(e.getPlayer());
			if(nick.nick()) {
				//OK
				Main.getInstance().NickedPlayers.add(e.getPlayer().getUniqueId());
				Main.getInstance().NickInstances.put(e.getPlayer().getUniqueId(), nick);
			} else {
				//Nick Failded 1/3
				if(nick.nick()) {
					//OK
					Main.getInstance().NickedPlayers.add(e.getPlayer().getUniqueId());
					Main.getInstance().NickInstances.put(e.getPlayer().getUniqueId(), nick);
				} else {
					//Nick Failded 2/3
					if(nick.nick()) {
						//OK
						Main.getInstance().NickedPlayers.add(e.getPlayer().getUniqueId());
						Main.getInstance().NickInstances.put(e.getPlayer().getUniqueId(), nick);
					} else {
						//Nick Failed 3/3
					}
				}
			}
		}
	}
	
	private boolean kickPlayer() {
		for (Player all : Bukkit.getOnlinePlayers()) {
			PlayerAPI api = new PlayerAPI(all.getUniqueId());
			if (api.IsPlayerInGroup(1)) {
				all.sendMessage("");//TODO: Message (Player Kick) | OITC - SLN
				Main.sendHub(all);
				return true;
			}
		}
		return false;
	}


	public void onPlayerKill(Player killer, Player player) {
		PlayerAPI pAPI = new PlayerAPI(player.getUniqueId());
		PlayerAPI kAPI = new PlayerAPI(killer.getUniqueId());
		StatsAPI pSapi = new StatsAPI(player.getUniqueId());
		StatsAPI kSapi = new StatsAPI(killer.getUniqueId());
		if(Main.getInstance().NickedPlayers.contains(killer.getUniqueId())) {
			player.sendMessage("§7[§dOnInTheCamber§7] §7Du §7wurdest §7von " + PermEnum.SPIELER.getTabList() + killer.getName() + " §7getötet.");
		} else {
			player.sendMessage("§7[§dOnInTheCamber§7] §7Du §7wurdest §7von " + kAPI.getTabPrefix() + " §7getötet.");
		}
		if(Main.getInstance().NickedPlayers.contains(player.getUniqueId())) {
			killer.sendMessage("§7[§dOnInTheCamber§7] §7Du §7hast " + PermEnum.SPIELER.getTabList() + player.getName() + " §7getötet.");
		} else {
			killer.sendMessage("§7[§dOnInTheCamber§7] §7Du §7hast " + pAPI.getTabPrefix() + " §7getötet.");
		}
		
		Methods.addArrow(killer);
		
		for(Player all : Bukkit.getOnlinePlayers()) {
			Main.setScoreboard(all, true);
		}
		if (Main.Kills.containsKey(killer)) {
			Main.Kills.replace(killer, Main.Kills.get(killer)+1);
		} else {
			Main.Kills.put(killer, 1);
		}
		
		kSapi.setStates(StatsEnum.OITC_KILLS, kSapi.getStates(StatsEnum.OITC_KILLS) + 1);
		pSapi.setStates(StatsEnum.OITC_DEATHS, pSapi.getStates(StatsEnum.OITC_DEATHS) + 1);
		
		if(Main.Kills.get(killer) >= Main.getKillsToWin){
			kSapi.setStates(StatsEnum.OITC_RoundWin, kSapi.getStates(StatsEnum.OITC_RoundWin) + 1);
			for(UUID uuid : Main.players) {
				if(uuid == killer.getUniqueId()) continue;
				StatsAPI sapi = new StatsAPI(uuid);
				sapi.setStates(StatsEnum.OITC_RoundLose, sapi.getStates(StatsEnum.OITC_RoundLose) + 1);
			}
			
			Bukkit.broadcastMessage("");
			Bukkit.broadcastMessage("");
			Bukkit.broadcastMessage("");
			Bukkit.broadcastMessage("");
			Bukkit.broadcastMessage("");
			Bukkit.broadcastMessage("");
			Bukkit.broadcastMessage("");
			Bukkit.broadcastMessage("");
			Bukkit.broadcastMessage("");
			
			Bukkit.broadcastMessage(ChatColor.GREEN +"================" + ChatColor.GRAY + "[" + ChatColor.AQUA + "OITC" + ChatColor.GRAY + "]" +ChatColor.GREEN +  "================");
			
			Bukkit.broadcastMessage(ChatColor.RED + killer.getName() + ChatColor.GRAY +
					" Hat das ziel ereicht " + ChatColor.GOLD + Main.getKillsToWin + ChatColor.GRAY + 
					" und hat das Spiel Gewonen");
			Bukkit.broadcastMessage(ChatColor.GREEN +"================" + ChatColor.GRAY + "[" + ChatColor.AQUA + "OITC" + ChatColor.GRAY + "]" +ChatColor.GREEN +  "================");
			//TODO: Messages (Game Win - Kills) | OITC - SLN
			Bukkit.broadcastMessage("");
			Bukkit.broadcastMessage("");
			Main.stop();
		}
		
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e){
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
			if(e.getClickedBlock().getType() == Material.CHEST){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onDamageByEntitiy(EntityDamageByEntityEvent e) {
		if(Main.getState() != GameState.INGAME)
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(Main.getState() != GameState.INGAME)
			e.setCancelled(true);
	}
	
}

