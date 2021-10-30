package com.github.tukenuke.tuske.manager.gui.v2;

import ch.njol.skript.SkriptEventHandler;
import ch.njol.skript.lang.*;
import ch.njol.util.NonNullPair;

import com.github.tukenuke.tuske.TuSKe;
import com.github.tukenuke.tuske.util.ReflectionUtils;
import com.github.tukenuke.tuske.listeners.GUIListener;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * @author Tuke_Nuke on 15/03/2017
 */
public class SkriptGUIEvent extends SkriptEvent {

	private static SkriptGUIEvent instance;
	public static SkriptGUIEvent getInstance() {
		if (instance == null)
			instance = new SkriptGUIEvent();
		return instance;
	}

	private final Map<Class<?>, List<Trigger>> triggersMap;
	private final List<NonNullPair<Class<? extends Event>, Trigger>> triggersList;
	private final List<GUIListener> listeners = new ArrayList<>();
	private boolean registered = false;
	@SuppressWarnings("unchecked")
	private SkriptGUIEvent() {
		Object obj = ReflectionUtils.getField(SkriptEventHandler.class, null, "triggers");
		if (obj instanceof Map) {
			// executed for legacy Skript versions
			this.triggersMap = (Map<Class<?>, List<Trigger>>) obj;
			this.triggersList = null;
		} else {
			// valid for Skript 2.6 and probably above
			this.triggersMap = null;
			this.triggersList = (List<NonNullPair<Class<? extends Event>, Trigger>>) obj;
		}
		register();
	}

	@Override
	public boolean check(Event event) {
		List<GUIListener> current = new ArrayList<>(listeners);
		for (GUIListener gui : current) {
			gui.onEvent(event);
			if (event instanceof Cancellable && ((Cancellable) event).isCancelled())
				break; //A matching gui was already found, so let's stop it
		}
		return false; // It needs to be false to not call Trigger#execute(e).
	}
	public void register() {
		if (!registered) {
			//Make sure it only execute this once when necessary
			registered = true;
			// A listener to know when Skript will remove all listeners
			new TriggerUnregisterListener().register();
			// This is a safe Trigger. Even using null values, it won't cause any issue.
			// It will be used to load as "SkriptListener" instead of Bukkit one,
			// So, when cancelling this event, it will still calling all scripts events too.
			// It will basically be like parsing this:
			// on inventory click:
			//     #TuSKe check here if it is a proper GUI.
			//     stop
			Trigger t = new Trigger(null, "gui inventory click", this, new ArrayList<>());
			//Those will be added before all triggers to cancel it before them.
			addTrigger(t, 0 , InventoryClickEvent.class, InventoryDragEvent.class);
			//It will add for the last one
			addTrigger(t, 1 , InventoryCloseEvent.class);
			ReflectionUtils.invokeMethod(SkriptEventHandler.class, "registerBukkitEvents", null);
		}
	}
	public void register(GUIListener gui) {
		//Just in case it didn't enabled the listener before opening a gui
		register();
		listeners.add(gui);
	}
	public void unregister(GUIListener gui) {
		listeners.remove(gui);
	}
	/**
	 * Removes all current open GUIInventory
	 */
	public void unregisterAll(){
		listeners.forEach(GUIListener::finalize);
		listeners.clear();
		//When running /skript reload all, it remove this object from Skript's event listener
		registered = false;
	}
	private void addTrigger(Trigger t, int priority, Class<? extends Event>... clzz) {
		if (priority == 0) {
			for (Class<? extends Event> clz : clzz) {
				//It will add a new array in case it doesn't have the event.
				//It will put this trigger at first index
				//This little workaround needed just to not
				//have conflicts between different objects.
				if (this.triggersList != null) {
					this.triggersList.add(0, new NonNullPair<>(clz, t)); // executed for Skript 2.6 and probably above
				} else {
					this.triggersMap.computeIfAbsent(clz, (c) -> new ArrayList<>()).add(0, t); // executed for legacy versions
				}
			}
		} else {
			Method m = ReflectionUtils.getMethod(SkriptEventHandler.class, "addTrigger", clzz.getClass(), Trigger.class);
			ReflectionUtils.invokeMethod(m, null, clzz, t);
		}
	}
	@Override
	public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
		return true;
	}
	@Override
	public String toString(Event event, boolean b) {
		return event != null ? "gui event: " + event.getEventName() : "gui event";
	}
}
