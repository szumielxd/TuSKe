package com.github.tukenuke.tuske.listeners;

import com.github.tukenuke.tuske.TuSKe;
import com.github.tukenuke.tuske.manager.gui.v2.SkriptGUIEventV2;
import com.github.tukenuke.tuske.util.InventoryUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tuke_Nuke on 27/05/2017
 */
public abstract class GUIListener {

	private @NotNull Inventory gui;
	@Getter
	private boolean started = false;

	protected GUIListener(@NotNull Inventory gui) {
		this.gui = gui;
	}

	public abstract void onClick(@NotNull InventoryClickEvent e, int slot);
	public abstract void onClose(@NotNull InventoryCloseEvent e);
	public abstract void onDrag(@NotNull InventoryDragEvent e, int slot);

	public boolean onEvent(@NotNull Event event) {
		if (event instanceof InventoryClickEvent && !((InventoryClickEvent) event).isCancelled()) {
			return processInventoryClickEvent((InventoryClickEvent) event);
		} else if (event instanceof InventoryCloseEvent) {
			return processInventoryCloseEvent((InventoryCloseEvent) event);
		} else if (event instanceof InventoryDragEvent) {
			return processInventoryDragEvent((InventoryDragEvent) event);
		}
		return false;
	}

	public void stop() {
		if (isStarted() && gui.getViewers().isEmpty()) { //In Global GUIs, someone can try to open a gui really fast or other player can, so let's make sure first
			SkriptGUIEventV2.getInstance().removeListener(this);
			started = false;
		}
	}
	public void start() {
		if (!isStarted()) {
			started = true;
			SkriptGUIEventV2.getInstance().addListener(this);
		}
	}
	public void setInventory(@NotNull Inventory inv) {
		this.gui = inv;
	}

	private boolean processInventoryClickEvent(@NotNull InventoryClickEvent event) {
		if (!isAllowedType(event.getClick())) {
			return false;
		}
		Inventory click = InventoryUtils.getClickedInventory(event);
		Inventory op = InventoryUtils.getOpositiveInventory(event.getView(), click); // null click gives null
		if (op == null || !click.equals(gui) && !op.equals(gui)) {
			return false;
		}
		int slot = event.getSlot();
		switch (event.getAction()) {
			case MOVE_TO_OTHER_INVENTORY:
				if (gui.equals(op)) {
					click = op;
					slot = InventoryUtils.getSlotTo(op, event.getCurrentItem());
				}
				break;
			case COLLECT_TO_CURSOR:
				click = gui;
				slot = InventoryUtils.getSlotTo(click, event.getCursor());
				break;
			case HOTBAR_SWAP:
			case HOTBAR_MOVE_AND_READD:
				if (gui.getType().equals(InventoryType.PLAYER)) {
					slot = event.getHotbarButton();
					click = gui;
				}
				break;
			default:
				break;
		}
		if (!click.equals(gui)) {
			return false;
		}
		onClick(event, slot);
		return true;
	}

	private boolean processInventoryCloseEvent(@NotNull InventoryCloseEvent event) {
		if (!event.getInventory().equals(gui)){
			return false;
		}
		if (event.getViewers().size() == 1) { //Only stop listener when the last one close.
			Bukkit.getScheduler().runTaskLater(TuSKe.getInstance(), this::stop, 1L);
		}
		onClose(event);
		return true;
	}

	private boolean processInventoryDragEvent(@NotNull InventoryDragEvent event) {
		if (!event.getInventory().equals(gui)) {
			return false;
		}
		for (int slot : event.getRawSlots()) {
			if (slot < event.getInventory().getSize()) {
				slot = event.getView().convertSlot(slot);
				onDrag(event, slot);
				if (event.isCancelled()) {
					break;
				}
			}
		}
		return true;
	}

	private boolean isAllowedType(@NotNull ClickType ct){
		switch(ct) {
			case UNKNOWN:
			case WINDOW_BORDER_RIGHT:
			case WINDOW_BORDER_LEFT:
			case CREATIVE:
				return false;
			default:
				return true;
		}
	}

	public void finalizeGUI() {
		gui.clear();
	}
}
