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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import de.superlandnetwork.API.API;
import de.superlandnetwork.API.PlayerAPI.PermEnum;
import de.superlandnetwork.API.PlayerAPI.PlayerAPI;
import de.superlandnetwork.API.ServerAPI.Server;
import de.superlandnetwork.API.ServerAPI.ServerAPI;
import de.superlandnetwork.API.Utils.Nick;
import de.superlandnetwork.API.WorldAPI.WorldAPI;
import de.superlandnetwork.oitc.listener.GameListener;
import de.superlandnetwork.oitc.utils.Methods;

public class Main extends JavaPlugin implements PluginMessageListener {
		
	private static int getAutoStartPlayers = 2;
	private static ArrayList<Location> Spawns = new ArrayList<>();
	private static int RoundOverTime = 300;
	public static int getKillsToWin = 25;
	
	private static GameState state;
	private static int id;
	public static List<UUID> players = new ArrayList<UUID>();
	public static int countdown;  
	private static int endtime;
	private static boolean endtimeOn;
	
	public static RestartScedular RestartScedular;
	
	public List<UUID> NickedPlayers = new ArrayList<>();
	public HashMap<UUID, Nick> NickInstances = new HashMap<>();
	
	public static HashMap<Player, Integer> Kills = new HashMap<>();
	
	static Main instance;
	
	public Server server;
	
	@Override
	public void onEnable() {
		instance = this;
		Scoreboard bord = Bukkit.getScoreboardManager().getMainScoreboard();
		for(Team team : bord.getTeams()){
			team.unregister();
		}
		if (bord.getObjective("aaa") != null)
			bord.getObjective("aaa").unregister();
		getServer().getPluginManager().registerEvents(new GameListener(), this);
		state = GameState.LOBBY;
		RestartScedular = new RestartScedular();
		WorldAPI wAPI = new WorldAPI("OITC_CandyLand");
		wAPI.createCleanWorld();
		Methods.RegisterTeams();
		setSpawns();
		CommandOITC CMD = new CommandOITC();
		getCommand("stats").setExecutor(CMD);
		getCommand("forcemap").setExecutor(CMD);
		getCommand("start").setExecutor(CMD);
		getCommand("nick").setExecutor(CMD);
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
		Bukkit.getWorld("OITC_CandyLand").setAutoSave(false);
		Bukkit.getWorld("OITC_CandyLand").setThundering(false);
		Bukkit.getWorld("OITC_CandyLand").setStorm(false);
		Bukkit.getWorld("OITC_CandyLand").setTime(0L);
		Bukkit.getWorld("OITC_CandyLand").setGameRuleValue("doDaylightCycle", "false");
		Bukkit.getWorld("OITC_CandyLand").setGameRuleValue("announceAdvancements", "false");
		Bukkit.getWorld("OITC_CandyLand").setGameRuleValue("doFireTick", "false");//
		Bukkit.getWorld("OITC_CandyLand").setGameRuleValue("disableElytraMovementCheck", "true");
		Bukkit.getWorld("OITC_CandyLand").setGameRuleValue("doMobSpawning", "false");
		Bukkit.getWorld("OITC_Lobby").setAutoSave(false);
		Bukkit.getWorld("OITC_Lobby").setThundering(false);
		Bukkit.getWorld("OITC_Lobby").setStorm(false);
		Bukkit.getWorld("OITC_Lobby").setTime(0L);
		Bukkit.getWorld("OITC_Lobby").setGameRuleValue("doDaylightCycle", "false");
		Bukkit.getWorld("OITC_Lobby").setGameRuleValue("announceAdvancements", "false");
		Bukkit.getWorld("OITC_Lobby").setGameRuleValue("doFireTick", "false");//
		Bukkit.getWorld("OITC_Lobby").setGameRuleValue("disableElytraMovementCheck", "true");
		Bukkit.getWorld("OITC_Lobby").setGameRuleValue("doMobSpawning", "false");
		server = new Server(4, 1, 1, API.getInstance().ServerID, 1, true, 0, 20);
		new ServerAPI(server).update();
	}
	
	@Override
	public void onDisable() {
		server.setOnline(false);
		new ServerAPI(server).update();
		Scoreboard bord = Bukkit.getScoreboardManager().getMainScoreboard();
		for(Team team : bord.getTeams()){
			team.unregister();
		}
		if (bord.getObjective("aaa") != null)
			bord.getObjective("aaa").unregister();
	}
	
	public static Main getInstance() {
		return instance;
	}
	
	private void setSpawns() {
		World w = Bukkit.getWorld("OITC_CandyLand");
		Spawns.add(new Location(w, -23, 45, -70));//1
		Spawns.add(new Location(w, -1, 47, -30));//2
		Spawns.add(new Location(w, 31, 48, -32));//3
		Spawns.add(new Location(w, -27, 48, -163));//4
		Spawns.add(new Location(w, -35, 54, -104));//5
		Spawns.add(new Location(w, -2, 41, -42));//6
		Spawns.add(new Location(w, 11, 41, -58));//7
		Spawns.add(new Location(w, 27, 39, -35));//8
		Spawns.add(new Location(w, -18, 39, -76));//9
		Spawns.add(new Location(w, -18, 44, -86));//10
		Spawns.add(new Location(w, -3, 60, -131));//11
		Spawns.add(new Location(w, -6, 69, -127));//12
		Spawns.add(new Location(w, 19, 46, -159));//13
		Spawns.add(new Location(w, -50, 46, -55));//14
		Spawns.add(new Location(w, -1, 50, -25));//15
		Spawns.add(new Location(w, 28, 48, -30));//16
		Spawns.add(new Location(w, 31, 40, -52));//17
		Spawns.add(new Location(w, 0, 39, -63));//18
		Spawns.add(new Location(w, -36, 44, -120));//19
		Spawns.add(new Location(w, -29, 51, -148));//20
		Spawns.add(new Location(w, 6, 59, -138));//21
		Spawns.add(new Location(w, -51, 49, -76));//22
		Spawns.add(new Location(w, 5, 65, -42));//23
		Spawns.add(new Location(w, 7, 66, -75));//24
		Spawns.add(new Location(w, -5, 41, -140));//25
		Spawns.add(new Location(w, 11, 48, -155));//26
		Spawns.add(new Location(w, 49, 44, -128));//27
		Spawns.add(new Location(w, 36, 52, -100));//28
		Spawns.add(new Location(w, 29, 40, -54));//29
		Spawns.add(new Location(w, -9, 39, -72));//30
	}
	
	public static GameState getState() {
		return state;
	}
	
	public static void setState(GameState state) {
		Main.state = state;
	}
	
	public static Location getRandomSpawn() {
		Random rand = new Random();
		int other = Spawns.size() - 1;
		int num = rand.nextInt(other) + 1;
		
		return getSpawn(num);
	}
	
	public static Location getSpawn(int id) {
		return Spawns.get(id); 
	}
	
	public static void sendHub(Player all) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF("Lobby1");

		all.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
	}
	  
	public static Location getLobbySpawn() {
		return Methods.getLobby();
	}
	
	public static void healAll() {
		for (UUID s : players) {
			if (Bukkit.getPlayer(s) != null) {
				Bukkit.getPlayer(s).setHealth(20.0D);
				Bukkit.getPlayer(s).setFoodLevel(20);
			}
		}
	}
	  
	private static void setInventories() {
		for(UUID s : players) {
			if(Bukkit.getPlayer(s) != null) {
				  Methods.setDefaultGameInventory(Bukkit.getPlayer(s));
			}
		}
	}
	

	public static void setScoreboard(Player p, boolean inGame) {
		Scoreboard bord = Bukkit.getScoreboardManager().getMainScoreboard();
		if(bord.getObjective("aaa") != null) {
			bord.getObjective("aaa").unregister();
		}
		Objective score = bord.registerNewObjective("aaa", "dumy");
		score.setDisplaySlot(DisplaySlot.SIDEBAR);
		if(!Kills.containsKey(p))
			Kills.put(p, 0);
		if (inGame) {
			score.setDisplayName("§eSuperLandNetwork§7.§bde");
			score.getScore("   ").setScore(9);
			score.getScore("§6Kills:").setScore(8);
			score.getScore("§c" + Kills.get(p)).setScore(7);
			score.getScore("  ").setScore(6);
			score.getScore("§6Map:").setScore(5);
			score.getScore("§aCandyLand").setScore(4);
			score.getScore(" ").setScore(3);
			score.getScore("§61. Platz:").setScore(2);
			score.getScore("§e" + getMostKills()).setScore(1);
		} else {
			score.setDisplayName("§dOneInTheChamber");
			score.getScore("  ").setScore(6);
			score.getScore("§6Spielgrösse:").setScore(5);
			score.getScore("§c20x1").setScore(4);
			score.getScore(" ").setScore(3);
			score.getScore("§6Map:").setScore(2);
			score.getScore("§aCandyLand").setScore(1);
		}
		p.setScoreboard(bord);	
	}
	
	private static String getMostKills() {
		int i = 0;
		String name = "";
		for(Player all : Kills.keySet()) {
			if(Kills.get(all) > i) {
				i = Kills.get(all);
				name = all.getName();
			}
		}
		return name;
	}

	private static void spawnPlayers() {
		for (UUID s : players) {
			if (Bukkit.getPlayer(s) != null) {
				Player player = Bukkit.getPlayer(s);
				Location loc = getRandomSpawn();
				if(loc != null) {
					player.teleport(loc);
				}
			}
		}
	}
		
	public static void start() {
		if(getState() == GameState.INGAME || getState() == GameState.STARTING){
			return;
		}
		  
		countdown = 60;
		
		id = Bukkit.getScheduler().scheduleSyncRepeatingTask(instance, new Runnable() {
			public void run() {
				if (countdown > 0) {
					setState(GameState.STARTING);
					if (countdown == 1) {
						Bukkit.broadcastMessage("§7[§dOnInTheCamber§7] §7Das Spiel beginnt in §e" + countdown + " §7Sekunde.");
					} else if (countdown == 60 || countdown == 45 || countdown == 30 || countdown == 15 || countdown == 10 || countdown <= 5) {
						Bukkit.broadcastMessage("§7[§dOnInTheCamber§7] §7Das Spiel beginnt in §e" + countdown + " §7Sekunden.");
					}
					countdown--;
				} else {
					Bukkit.getScheduler().cancelAllTasks();
					setState(GameState.INGAME);
					Main.getInstance().server.setStatusID(2);
					new ServerAPI(Main.getInstance().server).update();
					startGameTimer();
					healAll();
					
					for(Player all : Bukkit.getOnlinePlayers()) {
						setScoreboard(all, true);
					}
					
					spawnPlayers();
					setInventories();
				}
			}
		}, 0L, 20L);
		
	}
	
	public static void stop() {
		if(getState() == GameState.STARTING) {
			Bukkit.getScheduler().cancelTask(id);
			setState(GameState.LOBBY);
		}
		
		healAll();
		
		if (endtimeOn) {
			Bukkit.getScheduler().cancelTask(endtime);
		}
		for (UUID s : players) {
			if (Bukkit.getPlayer(s) != null) {
				Player player = Bukkit.getPlayer(s);
				player.teleport(Methods.getLobby());
			}
		}
		
		if(getState() == GameState.INGAME) {
			RestartScedular.StartScedular();
		}
		players.clear();
	 }
	 	
	public static void startGameTimer() {
		endtimeOn = true;
		endtime = Bukkit.getScheduler().scheduleSyncDelayedTask(instance, new Runnable() {
			public void run() {
				Bukkit.broadcastMessage("§7[§dOnInTheCamber§7] §eDas §eZeit §eLimlit §ewurde §eereicht!");
				stop();
			}
		}, RoundOverTime * 20);
	 }
	
	public boolean hasPlayer(Player player) {
		if (players.contains(player.getUniqueId())) {
			return true;
		}
		return false;
	} 
	public static void addPlayer(Player player) {
		if (!players.contains(player.getUniqueId())) {
			players.add(player.getUniqueId());
			PlayerAPI api = new PlayerAPI(player.getUniqueId());
			if(Main.getInstance().NickedPlayers.contains(player.getUniqueId()))
				Bukkit.broadcastMessage("§a» " + PermEnum.SPIELER.getTabList() + player.getName() + " §7hat §7den §7Server §7betreten.");
			else
				Bukkit.broadcastMessage("§a» " + api.getTabPrefix() + " §7hat §7den §7Server §7betreten.");
			
			player.teleport(getLobbySpawn());
		
			if(canStart()){
				start();
			}
		}
	}
 	
	public static void removePlayer(Player player) {
		if (players.contains(player.getUniqueId())) {
			players.remove(player.getUniqueId());
		}
		
		PlayerAPI api = new PlayerAPI(player.getUniqueId());
		if(Main.getInstance().NickedPlayers.contains(player.getUniqueId()))
			Bukkit.broadcastMessage("§c« " + PermEnum.SPIELER.getTabList() + player.getName() + " §7hat §7den §7Server §7verlassen.");
		else
			Bukkit.broadcastMessage("§c« " + api.getTabPrefix() + " §7hat §7den §7Server §7verlassen.");
				
		if (player.isInsideVehicle()) {
			player.getVehicle().eject();
		}
		
		player.teleport(Methods.getLobby());
		
		if (getState() == GameState.INGAME || getState() == GameState.STARTING) {
			if(players.size() < getAutoStartPlayers) {
				stop();
			}
		}
	}
	
	public static boolean canStart()  {
		if (getState() != GameState.INGAME && getState() != GameState.STARTING) {
			if (players.size() >= getAutoStartPlayers) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public void onPluginMessageReceived(String arg0, Player arg1, byte[] arg2) {
		return;
	}

}

