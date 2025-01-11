package com.github.tukenuke.tuske.util;

import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Tuke_Nuke on 10/04/2017
 */
@UtilityClass
public class InventoryUtils {

	private static final boolean HAS_CLICK_METHOD = ReflectionUtils.hasMethod(InventoryClickEvent.class, "getClickedInventory");

	/**
	 * KCaldron and other jar's doesn't have this method. Just a Util to check that.
	 * @param event - The {@link InventoryClickEvent}
	 * @return The clicked inventory.
	 */
	@Nullable
	public static Inventory getClickedInventory(@NotNull InventoryClickEvent event){
		if (HAS_CLICK_METHOD) {
			return event.getClickedInventory();
		} else if (event.getRawSlot() < 0) {
			return null;
		} else if ((event.getView().getTopInventory() != null) && (event.getRawSlot() < event.getView().getTopInventory().getSize())) {
			return event.getView().getTopInventory();
		} else {
			return event.getView().getBottomInventory();
		}
	}
	@Contract("null, _ -> null; _, null -> null")
	@Nullable
	public static Inventory getOpositiveInventory(@Nullable InventoryView view, @Nullable Inventory inv) {
		if (view == null || inv == null) {
			return null;
		}
		if (view.getTopInventory().equals(inv)) {
			return view.getBottomInventory();
		}
		if (view.getBottomInventory().equals(inv)) {
			return view.getTopInventory();
		}
		return null;
	}

	public static int getSlotTo(@NotNull Inventory invTo, @Nullable ItemStack i){
		if (i != null && invTo.first(i.getType()) >= 0) {
			for (int x = invTo.first(i.getType()); x < invTo.getSize(); x++) {
				if (invTo.getItem(x) != null && invTo.getItem(x).getData().equals(i.getData()) && invTo.getItem(x).getAmount() < invTo.getItem(x).getMaxStackSize()) {
					return x;
				}
			}
		}

		return invTo.firstEmpty();
	}

	@SuppressWarnings("deprecation")
	public static int getInvertedSlotTo(@NotNull Inventory invTo, @NotNull ItemStack i){
		for (int x = 8; x >= 0; x--)
			if ((invTo.getItem(x) == null) || (invTo.getItem(x) != null && invTo.getItem(x).getDurability() == i.getDurability() && invTo.getItem(x).getAmount() < invTo.getItem(x).getMaxStackSize()))
				return x;
		for (int x = invTo.getSize() -1; x > 8; x--)
			if ((invTo.getItem(x) == null) || (invTo.getItem(x) != null && invTo.getItem(x).getDurability() == i.getDurability() && invTo.getItem(x).getAmount() < invTo.getItem(x).getMaxStackSize()))
				return x;
		return -1;
	}

	@Nullable
	public static Inventory newInventory(@NotNull InventoryType type, @Nullable Integer size, @Nullable String name) {
		if (size == null)
			size = type.getDefaultSize();
		else
			size *= 9;
		if (name == null)
			name = type.getDefaultTitle();
		else if (name.length() > 32)
			name = name.substring(0, 32);
		switch (type) {
			case BEACON:
			case MERCHANT:
			case CRAFTING:
			case CREATIVE:
				return null;
			case CHEST:
				return Bukkit.getServer().createInventory(null, size, name);
			case DROPPER:
				return Bukkit.getServer().createInventory(null, InventoryType.DISPENSER, name);
			default:
				return Bukkit.getServer().createInventory(null, type, name);
		}
	}
}
