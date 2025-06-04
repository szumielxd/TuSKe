package com.github.tukenuke.tuske.nms;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.logging.Level;

import com.github.tukenuke.tuske.util.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.mojang.authlib.GameProfile;

import com.github.tukenuke.tuske.TuSKe;
import org.jetbrains.annotations.NotNull;

public class ReflectionNMS implements NMS {


	private final @NotNull Class<?> CRAFTITEM_CLASS = Objects.requireNonNull(ReflectionUtils.getCraftClass("inventory.CraftItemStack"));
	private final @NotNull Method CRAFTITEM_ASNMSCOPY_METHOD = Objects.requireNonNull(ReflectionUtils.getMethod(CRAFTITEM_CLASS,"asNMSCopy", ItemStack.class));
	private final @NotNull Class<?> NMSITEM_CLASS = Objects.requireNonNull(CRAFTITEM_ASNMSCOPY_METHOD.getReturnType());

	private final @NotNull Class<?> CRAFTHUMAN_CLASS = Objects.requireNonNull(ReflectionUtils.getCraftClass("entity.CraftHumanEntity"));
	private final @NotNull Method CRAFTHUMAN_GETHANDLE_METHOD = Objects.requireNonNull(ReflectionUtils.getMethod(CRAFTHUMAN_CLASS, "getHandle"));
	private final @NotNull Class<?> NMSHUMAN_CLASS = Objects.requireNonNull(CRAFTHUMAN_GETHANDLE_METHOD.getReturnType());
	private final @NotNull Method NMSHUMAN_DROP_METHOD = Objects.requireNonNull(ReflectionUtils.getMethod(NMSHUMAN_CLASS, "getHandle", NMSITEM_CLASS, boolean.class));

	private final @NotNull Class<?> CRAFTPLAYER_CLASS = Objects.requireNonNull(ReflectionUtils.getCraftClass("entity.CraftPlayer"));
	private final @NotNull Method CRAFTPLAYER_GETHANDLE_METHOD = Objects.requireNonNull(ReflectionUtils.getMethod(CRAFTPLAYER_CLASS, "getHandle"));
	private final @NotNull Class<?> NMSPLAYER_CLASS = Objects.requireNonNull(CRAFTPLAYER_GETHANDLE_METHOD.getReturnType());

	private final @NotNull Class<?> CRAFTSERVER_CLASS = Objects.requireNonNull(ReflectionUtils.getCraftClass("CraftServer"));
	private final @NotNull Method CRAFTSERVER_GETSERVER_METHOD = Objects.requireNonNull(ReflectionUtils.getMethod(CRAFTSERVER_CLASS, "getServer"));


	public ReflectionNMS(){
	}

	@Override
	public Player getToPlayer(OfflinePlayer p) {
		if (!p.isOnline() && p.hasPlayedBefore()){
			try {
				throw new UnsupportedOperationException("No more easy NMS");
				/*Object sv = CRAFTSERVER_GETSERVER_METHOD.invoke(Bukkit.getServer());
				Class<?> nms = Class.forName("net.minecraft.server.v"+version+".MinecraftServer");
				Object worldServer = nms.getDeclaredMethod("getWorldServer", int.class).invoke(sv, 0);
				Object playerInteractManager = Class.forName("net.minecraft.server.v" + version + ".PlayerInteractManager").getConstructors()[0].newInstance(worldServer);
				GameProfile profile = new GameProfile(p.getUniqueId(), p.getName());
				Constructor<?> constructor = NMSPLAYER_CLASS.getConstructors()[0];
				Object newPlayer = constructor.newInstance(sv, worldServer, profile, playerInteractManager);
				Player player = (Player) newPlayer.getClass().getDeclaredMethod("getBukkitEntity").invoke(newPlayer);
				if (player != null){
					player.loadData();
					return player;
				}*/
			} catch (Exception e){
				TuSKe.log(Level.WARNING,
					"An error occured with expression to get player data. It is because your server version isn't supported yet.",
					"So, report it somewhere, in Spigot or GitHub, to the developer with following details:",
					"Running version: v" + Bukkit.getVersion(),
					"Error details:");
				e.printStackTrace();
			}
			
		}
		return null;
	}

	@Override
	public void makeDrop(Player p, ItemStack i) {
		if (p != null && i != null){
			try {
				Object item = CRAFTITEM_ASNMSCOPY_METHOD.invoke(null, i);
				Object entity = CRAFTPLAYER_GETHANDLE_METHOD.invoke(p);
				NMSHUMAN_DROP_METHOD.invoke(entity, item, true);
			} catch (Exception e){
				TuSKe.log(Level.WARNING,
					"An error occured with effect to force a player to drop a item. It is because your server version isn't supported yet.",
					"So, report it somewhere, in Spigot or GitHub, to the developer with following details:",
					"Running version: v" + Bukkit.getVersion(),
					"Error details:");
				e.printStackTrace();
				
			}
		}
		
	}

	@Override
	public void setFastBlock(World world, int x, int y, int z, Material type, byte data) {		
	}

	@Override
	public void updateChunk(Chunk c) {
	}

}
