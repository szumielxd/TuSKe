package com.github.tukenuke.tuske.hooks.landlord.expressions;

import com.github.tukenuke.tuske.util.Registry;

import biz.princeps.landlord.api.IOwnedLand;

import org.bukkit.Location;

import javax.annotation.Nullable;

import ch.njol.skript.expressions.base.SimplePropertyExpression;

public class ExprLandLocation extends SimplePropertyExpression<IOwnedLand, Location>{
	static {
		Registry.newProperty(ExprLandLocation.class, "land[lord] location", "landclaim");
	}

	@Override
	public Class<? extends Location> getReturnType() {
		return Location.class;
	}

	@Override
	@Nullable
	public Location convert(IOwnedLand ol) {
		if (ol != null){
			return ol.getALocation();
		}
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "land location";
	}

}
