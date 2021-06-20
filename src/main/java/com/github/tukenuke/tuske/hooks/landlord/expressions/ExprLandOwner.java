package com.github.tukenuke.tuske.hooks.landlord.expressions;

import com.github.tukenuke.tuske.util.Registry;

import biz.princeps.landlord.api.IOwnedLand;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import javax.annotation.Nullable;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

public class ExprLandOwner extends SimplePropertyExpression<IOwnedLand, OfflinePlayer>{
	static {
		Registry.newProperty(ExprLandOwner.class, "land[lord] owner", "landclaim");
	}

	@Override
	public Class<? extends OfflinePlayer> getReturnType() {
		return OfflinePlayer.class;
	}

	@Override
	@Nullable
	public OfflinePlayer convert(IOwnedLand ol) {
		return Bukkit.getOfflinePlayer(ol.getOwner());
	}

	@Override
	protected String getPropertyName() {
		return "land[lord] owner";
	}

}
