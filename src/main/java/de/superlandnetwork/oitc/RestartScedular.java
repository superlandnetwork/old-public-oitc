//   ___    ___   _____    ___ 
//  / _ \  |_ _| |_   _|  / __|
// | (_) |  | |    | |   | (__ 
//  \___/  |___|   |_|    \___|
//
// Copyright (C) Filli-IT (Einzelunternehmen) & Ursin Filli - All Rights Reserverd
// Unauthorized copying of the this file, via any medium is strictly prohibited
// Proprietary and confidential
// Written by Ursin Filli <ursin.filli@Filli-IT.ch>

package de.superlandnetwork.oitc;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;

import de.superlandnetwork.API.API;
import de.superlandnetwork.oitc.utils.Methods;

public class RestartScedular {

	public int CD;
	public int countdown = 15;
	
	public void StartScedular() {
		
		countdown = 15;
		
		CD = Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getInstance(), new Runnable() {

			@Override
			public void run() {
				
				if(countdown == 15) {
					reset();
					Main.setState(GameState.RESTART_LOBBY);
					Main.getInstance().server.setStatusID(3);
				}
				
				int countdownb = countdown-5;
				
				if (countdown == 15 || countdown == 14 || countdown == 13 || countdown == 12 || countdown == 11
						|| countdown == 10 || countdown == 9 || countdown == 8 || countdown == 7 || countdown == 6) {
					if (countdown != 6)
						API.getInstance().sendMessageToAllPlayers("§7[§dOnInTheCamber§7] §7Der Server startet in §e" + countdownb + " §7Sekunden neu.");
					else
						API.getInstance().sendMessageToAllPlayers("§7[§dOnInTheCamber§7] §7Der Server startet in §e" + countdownb + " §7Sekunde neu.");
				}
				if(countdown == 5) {
					for(Player all : Bukkit.getOnlinePlayers()) {
						Main.sendHub(all);
					}
					
					Main.setState(GameState.RESTART);
				}
				
				if(countdown == 0) {
					Bukkit.spigot().restart();
					Bukkit.getScheduler().cancelAllTasks();
				}
				countdown--;
			}
			
		}, 0L, 20L);
		
	}
	
	private void reset() {
		Scoreboard b = Bukkit.getScoreboardManager().getMainScoreboard();
		for(Player all : Bukkit.getOnlinePlayers()) {
			for(Player all2 : Bukkit.getOnlinePlayers()) {
				all.showPlayer(all2);
			}
			all.teleport(Methods.getLobby());
			all.setGameMode(GameMode.ADVENTURE);
			if(all.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
				all.removePotionEffect(PotionEffectType.INVISIBILITY);
			}
			all.getInventory().clear();
			all.getInventory().setArmorContents(null);
			if(b.getTeam("spec") != null) {
				b.getTeam("spec").unregister();
			}
			if(Main.getInstance().NickedPlayers.contains(all.getUniqueId())) {
				//unnick(all);
			}
		}
	}
	
	@SuppressWarnings("unused")
	private void unnick(Player all) {
		Main.getInstance().NickInstances.get(all.getUniqueId()).setName((CraftPlayer)all, all.getDisplayName());
		Main.getInstance().NickInstances.get(all.getUniqueId()).setSkin((CraftPlayer)all, all.getUniqueId());
		
		Main.getInstance().NickedPlayers.remove(all.getUniqueId());
		
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				for(Player all2 : Bukkit.getOnlinePlayers()) {
					all2.hidePlayer(all);	
				}
			}
		}, 1L);
		
		Bukkit.getScheduler().runTaskLater(Main.getInstance(), new Runnable() {
			
			@Override
			public void run() {
				for(Player all2 : Bukkit.getOnlinePlayers()) {
					all2.showPlayer(all);	
				}
			}
		}, 5L);
		
	}
	
}
