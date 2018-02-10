//   ___    ___   _____    ___ 
//  / _ \  |_ _| |_   _|  / __|
// | (_) |  | |    | |   | (__ 
//  \___/  |___|   |_|    \___|
//
// Copyright (C) Filli-IT (Einzelunternehmen) & Ursin Filli - All Rights Reserverd
// Unauthorized copying of the this file, via any medium is strictly prohibited
// Proprietary and confidential
// Written by Ursin Filli <ursin.filli@Filli-IT.ch>

package de.superlandnetwork.oitc.utils;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import de.superlandnetwork.API.PlayerAPI.PermEnum;
import de.superlandnetwork.API.PlayerAPI.PlayerAPI;
import de.superlandnetwork.oitc.Main;

public class Methods {
	
	public static void addArrow(Player player) {
		ItemStack arrow = new ItemStack(Material.ARROW, 1);
		player.getInventory().addItem(new ItemStack[] { arrow });
	}
	
	public static void setDefaultGameInventory(Player player){ 
		ItemStack bow = new ItemStack(Material.BOW, 1);
		ItemStack arrow = new ItemStack(Material.ARROW, 1);
		ItemStack sword = new ItemStack(Material.WOOD_SWORD,1);
		player.getInventory().clear();
		
		player.getInventory().addItem(sword);
		player.getInventory().addItem(bow);
		player.getInventory().addItem(arrow);
		
		player.updateInventory();
	}
	
	public static ItemStack createColorArmor(ItemStack i, Color c)  {
		LeatherArmorMeta meta = (LeatherArmorMeta)i.getItemMeta();
		meta.setColor(c);
		i.setItemMeta(meta);
		return i;
	}
	
	/**
	 * @return
	 */
	public static Location getLobby() {
		return new Location(Bukkit.getWorld("OITC_Lobby"), 1.500D, 22D, 0.500D);
	}
	
	public static void RegisterTeams() {
		Scoreboard bord = Bukkit.getScoreboardManager().getMainScoreboard();
		bord.registerNewTeam("0012Spieler").setPrefix(PermEnum.SPIELER.getTabList());
		bord.registerNewTeam("0011Premium").setPrefix(PermEnum.PREMIUM.getTabList());
		bord.registerNewTeam("0010PremiumPlus").setPrefix(PermEnum.PREMIUMPLUS.getTabList());
		bord.registerNewTeam("009YouTube").setPrefix(PermEnum.YOUTUBER.getTabList());
		bord.registerNewTeam("0008Builder").setPrefix(PermEnum.BUILDER.getTabList());
		bord.registerNewTeam("0008Builderin").setPrefix(PermEnum.BUILDERIN.getTabList());
//		bord.registerNewTeam("0007HeadBuilder").setPrefix(PermEnum.HEADBUILDER.getTabList());
//		bord.registerNewTeam("0007HeadBuildin").setPrefix(PermEnum.HEADBUILDERIN.getTabList());
		bord.registerNewTeam("0006Supporter").setPrefix(PermEnum.SUPPORTER.getTabList());
		bord.registerNewTeam("0006Supporterin").setPrefix(PermEnum.SUPPORTERIN.getTabList());
		bord.registerNewTeam("0005Moderator").setPrefix(PermEnum.MODERATOR.getTabList());
		bord.registerNewTeam("0005Moderatorin").setPrefix(PermEnum.MODERATORIN.getTabList());
		bord.registerNewTeam("0004SrModerator").setPrefix(PermEnum.SRMODERATOR.getTabList());
		bord.registerNewTeam("0004SrModeratin").setPrefix(PermEnum.SRMODERATORIN.getTabList());
		bord.registerNewTeam("0003Devloper").setPrefix(PermEnum.DEVELOPER.getTabList());
		bord.registerNewTeam("0003Devloperin").setPrefix(PermEnum.DEVELOPERIN.getTabList());
		bord.registerNewTeam("0002Admin").setPrefix(PermEnum.ADMINISTRATOR.getTabList());
		bord.registerNewTeam("0002Adminin").setPrefix(PermEnum.ADMINISTRATORIN.getTabList());
		AntiCollision();
	}
		 
	private static void AntiCollision() {
		Scoreboard bord = Bukkit.getScoreboardManager().getMainScoreboard();
		for(Team t : bord.getTeams()) {
			t.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
		}
	}
	 
	@SuppressWarnings("deprecation")
	public static void setPrefix(Player player) {
		Scoreboard bord = player.getScoreboard();
		for(Player all : Bukkit.getOnlinePlayers()) {
			String team = "0012Spieler";
			UUID UUID = all.getUniqueId();
			PlayerAPI api = new PlayerAPI(UUID);
			if(api.IsPlayerInGroup(PermEnum.PREMIUM.getId())) {
				team = "0011Premium";
			} else if(api.IsPlayerInGroup(PermEnum.PREMIUMPLUS.getId())) {
				team = "0010PremiumPlus";
			} else if(api.IsPlayerInGroup(PermEnum.YOUTUBER.getId())) {
				team = "0009YouTube";
			} else if(api.IsPlayerInGroup(PermEnum.SUPPORTER.getId())) {
				team = "0008Supporter";
			} else if(api.IsPlayerInGroup(PermEnum.SUPPORTERIN.getId())) {
				team = "0008Supporterin";
			} else if(api.IsPlayerInGroup(PermEnum.MODERATOR.getId())) {
				team = "0007Moderator";
			} else if(api.IsPlayerInGroup(PermEnum.MODERATORIN.getId())) {
				team = "0007Moderatorin";
			} else if(api.IsPlayerInGroup(PermEnum.SRMODERATOR.getId())) {
				team = "0006SrModerator";
			} else if(api.IsPlayerInGroup(PermEnum.SRMODERATORIN.getId())) {
				team = "0006SrModeratin";
			} else if(api.IsPlayerInGroup(PermEnum.BUILDER.getId())) {
				team = "0005Builder";
			} else if(api.IsPlayerInGroup(PermEnum.BUILDERIN.getId())) {
				team = "0005Builderin";
//			} else if(api.IsPlayerInGroup(PermEnum.HEADBUILDER.getId())) {
//				team = "0004HeadBuilder";
//			} else if(api.IsPlayerInGroup(PermEnum.HEADBUILDERIN.getId())) {
//				team = "0004HeadBuildin";
			} else if(api.IsPlayerInGroup(PermEnum.DEVELOPER.getId())) {
				team = "0003Devloper";
			} else if(api.IsPlayerInGroup(PermEnum.DEVELOPERIN.getId())) {
				team = "0003Devloperin";
			} else if(api.IsPlayerInGroup(PermEnum.ADMINISTRATOR.getId())) {
				team = "0002Admin";
			} else if(api.IsPlayerInGroup(PermEnum.ADMINISTRATORIN.getId())) {
				team = "0002Adminin";
			}
			if(Main.getInstance().NickedPlayers.contains(UUID))
				team = "0012Spieler";
			bord.getTeam(team).addPlayer(all);
		}
		player.setScoreboard(bord);
	}
	
}

