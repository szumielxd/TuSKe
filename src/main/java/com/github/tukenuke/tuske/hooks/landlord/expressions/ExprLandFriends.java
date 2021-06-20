package com.github.tukenuke.tuske.hooks.landlord.expressions;

import com.github.tukenuke.tuske.util.Registry;

import biz.princeps.landlord.api.IOwnedLand;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprLandFriends extends SimpleExpression<OfflinePlayer>{
	static {
		Registry.newProperty(ExprLandFriends.class, "land[lord] friends", "landclaim");
	}

	private Expression<IOwnedLand> ol;
	@Override
	public Class<? extends OfflinePlayer> getReturnType() {
		return OfflinePlayer.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.ol = (Expression<IOwnedLand>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "land friends of " + this.ol;
	}

	@Override
	@Nullable
	protected OfflinePlayer[] get(Event e) {
		IOwnedLand ol = this.ol.getSingle(e);
		if (ol != null) return ol.getFriends().stream().map(Bukkit::getOfflinePlayer).toArray(OfflinePlayer[]::new);
		return null;
	}
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		IOwnedLand ol = this.ol.getSingle(e);
		OfflinePlayer[] ob = null;
		if (mode != ChangeMode.RESET || mode != ChangeMode.DELETE) ob = (OfflinePlayer[]) delta;
		if (ol != null){
			switch (mode){
				case RESET:
				case DELETE: 
					ol.getFriends().forEach(ol::removeFriend);
					break;
				case SET:
					ol.getFriends().forEach(ol::removeFriend);
				case ADD:
					Stream.of(ob).map(OfflinePlayer::getUniqueId).forEach(ol::addFriend);
					break;	
				case REMOVE:
					Stream.of(ob).map(OfflinePlayer::getUniqueId).forEach(ol::removeFriend);
					break;
				default:
					break;
			}
		}
	}

	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode != ChangeMode.REMOVE_ALL)
			return CollectionUtils.array(OfflinePlayer[].class);
		return null;
		
	}
}
