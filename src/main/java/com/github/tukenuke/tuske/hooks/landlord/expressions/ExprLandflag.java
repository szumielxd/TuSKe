package com.github.tukenuke.tuske.hooks.landlord.expressions;

import com.github.tukenuke.tuske.util.Registry;

import biz.princeps.landlord.api.ILLFlag;
import biz.princeps.landlord.api.IOwnedLand;

import org.bukkit.event.Event;

import java.util.Optional;

import javax.annotation.Nullable;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

public class ExprLandflag extends SimpleExpression<Boolean>{
	static {
		Registry.newSimple(ExprLandflag.class, "landflag %text% of %landclaim% for (1�everyone|2�friends)");
	}

	private Expression<IOwnedLand> ol;
	private Expression<String> lf;
	private boolean isFriend = false;
	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.ol = (Expression<IOwnedLand>) arg[1];
		this.lf = (Expression<String>) arg[0];
		if (arg3.mark == 2)
			this.isFriend = true;
		
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "landflag " + this.lf + " of " +this.ol;
	}

	@Override
	@Nullable
	protected Boolean[] get(Event e) {
		IOwnedLand ol = this.ol.getSingle(e);
		String lf = this.lf.getSingle(e);
		Optional<ILLFlag> flag = ol.getFlags().stream().filter(f -> f.getName().equalsIgnoreCase(lf)).findAny();
		if (ol != null && flag.isPresent()) {
			if (isFriend) return new Boolean[] {flag.get().getFriendStatus()};
			return new Boolean[] {flag.get().getAllStatus()};
		}
		return new Boolean[] {false};
	}
	public void change(Event e, Object[] delta, Changer.ChangeMode mode){
		IOwnedLand ol = this.ol.getSingle(e);
		String lf = this.lf.getSingle(e);
		Optional<ILLFlag> flag = ol.getFlags().stream().filter(f -> f.getName().equalsIgnoreCase(lf)).findAny();
		if (ol != null && flag.isPresent()){
			ILLFlag f = flag.get();
			if (isFriend) {
				if ((Boolean) delta[0] != f.getFriendStatus()) f.toggleFriends();
			} else {
				if ((Boolean) delta[0] != f.getAllStatus()) f.toggleAll();
			}
		}
		
		
	}
	public Class<?>[] acceptChange(final Changer.ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(Boolean.class);
		return null;
	}

}
