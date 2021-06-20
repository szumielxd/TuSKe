package com.github.tukenuke.tuske.hooks.landlord.effects;

import com.github.tukenuke.tuske.util.Registry;

import biz.princeps.landlord.api.ILandLord;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import javax.annotation.Nullable;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

public class EffClaimLand extends Effect{
	static {
		Registry.newEffect(EffClaimLand.class, "claim land[lord] at %location/chunk% for %player%");
	}

	private Expression<Object> l;
	private Expression<Player> p;
	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.l = (Expression<Object>) arg[0];
		this.p = (Expression<Player>) arg[1];
		return true;
	}

	@Override
	public String toString(@Nullable Event arg0, boolean arg1) {
		return "claim land at " + this.l + " for " + this.p;
	}

	@Override
	protected void execute(Event e) {
		Player p = this.p.getSingle(e);
		Object o = this.l.getSingle(e);
		if (p == null || o == null) return;
		Chunk ch;
		if (o instanceof Chunk) ch = (Chunk) o;
		else ch = ((Location) o).getChunk();
		ILandLord api = (ILandLord) Bukkit.getPluginManager().getPlugin("Landlord");
		if (api.getWGManager().canClaim(p, ch)) api.getWGManager().claim(ch, p.getUniqueId());
	}

}
