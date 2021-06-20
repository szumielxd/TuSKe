package com.github.tukenuke.tuske.hooks.landlord.expressions;

import com.github.tukenuke.tuske.util.Registry;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IOwnedLand;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.Event;
import javax.annotation.Nullable;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

public class ExprLandClaimAt extends SimpleExpression<IOwnedLand>{
	static {
		Registry.newSimple(ExprLandClaimAt.class, "land[lord] claim at %location/chunk%");
	}

	private Expression<Object> l;
	@Override
	public Class<? extends IOwnedLand> getReturnType() {
		return IOwnedLand.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.l = (Expression<Object>) arg[0];
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "land[lord] claim at " + this.l;
	}

	@Override
	@Nullable
	protected IOwnedLand[] get(Event e) {
		Object l = this.l.getSingle(e);
		if (l != null) {
			ILandLord api = (ILandLord) Bukkit.getPluginManager().getPlugin("Landlord");
			if (l instanceof Location) return new IOwnedLand[] {api.getWGManager().getRegion((Location) l)};
			if (l instanceof Chunk) return new IOwnedLand[] {api.getWGManager().getRegion((Chunk) l)};
		}
		return null;
	}

}
