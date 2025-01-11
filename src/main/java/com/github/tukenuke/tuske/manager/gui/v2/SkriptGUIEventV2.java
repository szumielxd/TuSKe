package com.github.tukenuke.tuske.manager.gui.v2;

import ch.njol.skript.SkriptEventHandler;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import com.github.tukenuke.tuske.TuSKe;
import com.github.tukenuke.tuske.listeners.GUIListener;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

public class SkriptGUIEventV2 extends SkriptEvent {

	@Getter
	private static final SkriptGUIEventV2 instance = new SkriptGUIEventV2();

	private final List<GUIListener> listeners = new LinkedList<>();
	private boolean registered = false;

	private SkriptGUIEventV2() {
		this.eventPriority = EventPriority.NORMAL;
		this.trigger = new Trigger(null, "gui inventory click", this, Collections.singletonList(new SkriptDummyEffect()));
	}

	private void register() {
		if (!registered) {
			Stream.of(InventoryClickEvent.class, InventoryDragEvent.class, InventoryCloseEvent.class)
					.forEach(e -> SkriptEventHandler.registerBukkitEvent(trigger, e));
			registered = true;
		}
	}

	private void unregister() {
		if (registered) {
			SkriptEventHandler.unregisterBukkitEvents(trigger);
			registered = false;
		}
	}

	public void ensureRegistered() {
		if (!registered) {
			register();
		}
	}

	public void addListener(GUIListener gui) {
		register();
		listeners.add(gui);
	}

	public void removeListener(GUIListener gui) {
		listeners.remove(gui);
		if (listeners.isEmpty()) {
			unregister();
		}
	}

	@Override
	public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parseResult) {
		return false;
	}

	@Override
	public boolean check(@NotNull Event event) {
		return event instanceof InventoryEvent && TuSKe.getGUIManager().hasGUI(((InventoryEvent) event).getInventory());
	}

	@Override
	public @NotNull String toString(@Nullable Event event, boolean debug) {
		return event != null ? "gui event: " + event.getEventName() : "gui event";
	}

	private class SkriptDummyEffect extends Effect {

		@Override
		protected void execute(@NotNull Event event) {
			for (GUIListener gui : new ArrayList<>(listeners)) {
				if (gui.onEvent(event)) {
					return; // already found matching gui
				}
			}
		}

		@Override
		public String toString(@Nullable Event event, boolean debug) {
			return event != null ? "gui event effect: " + event.getEventName() : "gui event";
		}

		@Override
		public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
			return true;
		}
	}

}
