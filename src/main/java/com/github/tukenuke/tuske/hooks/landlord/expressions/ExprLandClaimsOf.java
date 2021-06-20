package com.github.tukenuke.tuske.hooks.landlord.expressions;

import com.github.tukenuke.tuske.util.Registry;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprLandClaimsOf extends SimpleExpression<IOwnedLand>{
	static {
		Registry.newProperty(ExprLandClaimsOf.class ,"land[lord] claims", "player");
	}
	
	private Expression<Player> p;

	@Override
	public Class<? extends IOwnedLand> getReturnType() {
		return IOwnedLand.class;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.p = (Expression<Player>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "land claims of " + this.p;
	}

	@Override
	@Nullable
	protected IOwnedLand[] get(Event e) {
		Player p = this.p.getSingle(e);
		ILandLord api = (ILandLord) Bukkit.getPluginManager().getPlugin("Landlord");
		api.getPlayerManager().get(p.getUniqueId());
		return api.getWGManager().getRegions(p.getUniqueId()).toArray(new IOwnedLand[0]);
	}

}
