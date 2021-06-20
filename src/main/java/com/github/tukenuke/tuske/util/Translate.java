package com.github.tukenuke.tuske.util;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;


public class Translate{
	
	
	
	private static Method CraftItemStack_asNMSCopy;
	private static Method ItemStack_getItem;
	private static Method Item_getName;
	private static final Map<String, String> methodNames = ImmutableMap.copyOf(ImmutableList.of(new SimpleEntry<>("v1_8", "e_"), new SimpleEntry<>("v1_9", "f_"),
			new SimpleEntry<>("v1_10", "f_"), new SimpleEntry<>("v1_11", "a"), new SimpleEntry<>("v1_12", "a"), new SimpleEntry<>("v1_13", "h"),
			new SimpleEntry<>("v1_14", "f"), new SimpleEntry<>("v1_15", "f"), new SimpleEntry<>("v1_16", "f"), new SimpleEntry<>("v1_17", "j")));
	
	
	static {
		String version = Bukkit.getServer().getClass().getName().split("\\.")[3];
		try {
			CraftItemStack_asNMSCopy = Class.forName(String.format("org.bukkit.craftbukkit.%s.inventory.CraftItemStack", version)).getMethod("asNMSCopy", ItemStack.class);
			ItemStack_getItem = CraftItemStack_asNMSCopy.getReturnType().getMethod("getItem");
			String methodName = methodNames.get(version.split("_")[1]);
			if (methodName != null) Item_getName = ItemStack_getItem.getReturnType().getMethod(methodName);
			else Item_getName = Stream.of(ItemStack_getItem.getReturnType().getMethods()).filter(m -> String.class.equals(m.getReturnType()))
					.filter(m -> Arrays.equals(m.getParameterTypes(), new Class<?>[] {CraftItemStack_asNMSCopy.getReturnType()})).findAny().orElseThrow();
		} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		// e_ f_ f_ a a h f f f j 
	}
	
	
	
		public static String getIDTranslate(Enchantment e){
			String r = "none";
			String n = e.getName();
			if (e != null){
				if (n.startsWith("DAMAGE_"))
					r =  e.getName().toLowerCase().replaceAll("_", ".");
				else if (n.startsWith("ARROW_") || n.startsWith("FROST_") || n.endsWith("WORKER")) 
					r = n.toLowerCase().split("_")[0] + WordUtils.capitalize(n.toLowerCase().split("_")[1]);
				else if (n.equals("DIG_SPEED"))
					r = "digging";
				else if (n.equals("FIRE_ASPECT"))
					r = "fire";
				else if (n.equals("LUCK"))
					r = "fishingSpeed";
				else if (n.equals("LURE"))
					r = "lootBonusFishing";
				else if (n.equals("LOOT_BONUS_BLOCKS"))
					r = "lootBonusDigger";
				else if (n.equals("LOOT_BONUS_MOBS"))
					r = "lootBonus";
				else if (n.startsWith("PROTECTION_"))
					r = "protect." + ((!n.endsWith("ENVIRONMENTAL")) ? (n.endsWith("EXPLOSIONS") ? "explosion" : n.toLowerCase().split("_")[1]) : "all");
				else if (n.equals("SILK_TOUCH"))
					r = "untouching";
				else if (n.endsWith("_STRIDER"))
					r = "waterWalker";
				else
					r = n.toLowerCase();
			}
			
			return "enchantment."+ r;
		}
		public static String getIDTranslate(EntityType e){
			String r = "generic";
			String n = e.name();
			if (e != null){
				switch (e){
				case MINECART_COMMAND: return "item.minecartCommandBlock.name";
				case MINECART_CHEST:
				case MINECART_FURNACE:
				case MINECART_HOPPER:
				case MINECART_TNT:
					return "item.minecart" + WordUtils.capitalize(n.toLowerCase().split("_")[1]) + ".name";
				case MINECART_MOB_SPAWNER: return "tile.mobSpawner.name";
				case ITEM_FRAME: return "item.frame.name";
				case ENDER_CRYSTAL: return "item.end_crystal.name";
				case WITHER_SKULL: return "item.skull.wither.name";
				case LEASH_HITCH: return "item.leash.name";
				case ENDER_PEARL: return "item.enderPearl.name";
				case FALLING_BLOCK: r = "FallingSand"; break;
				case HORSE: r =	"EntityHorse"; break;
				case DROPPED_ITEM: r = "Item"; break;
				case OCELOT: r = "Ozelot"; break;
				case MAGMA_CUBE: r = "LavaSlime"; break;
				case SNOWMAN: r = "SnowMan"; break;
				case IRON_GOLEM: r = "VillagerGolem"; break;
				case WITHER: r = "WitherBoss"; break;
				case EXPERIENCE_ORB: r = "XPOrb"; break;
					
				
				default: r = WordUtils.capitalize(n.toLowerCase().replaceAll("_", " ")).replaceAll(" ", "");
				}
			}
			return "entity." + r + ".name";
			
		}
		@SuppressWarnings("deprecation")
		public static String getIDTranslate (Block b){
			ItemStack i = new ItemStack(b.getType(), 1);
			i.setDurability(b.getData());
			if (i.getType().equals(Material.ANVIL))
				i.setDurability((short) ((i.getDurability() < 4) ? 0 : (i.getDurability() < 8) ?  1 : 2));
			return getIDTranslate(i);
		}
		
		public static String getIDTranslate(ItemStack i){
			
			try {
				Object nms = CraftItemStack_asNMSCopy.invoke(null, i);
				return (String) Item_getName.invoke(ItemStack_getItem.invoke(nms));
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}
}
