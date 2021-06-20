package com.github.tukenuke.tuske.hooks.landlord;

import ch.njol.skript.SkriptAddon;
import ch.njol.skript.lang.ParseContext;
import com.github.tukenuke.tuske.util.SimpleType;

import biz.princeps.landlord.api.IOwnedLand;

import javax.annotation.Nullable;

/**
 * @author Tuke_Nuke on 10/04/2017
 */
public class LandlordRegister {

	public LandlordRegister(SkriptAddon tuske) {
		types();
		try {
			tuske.loadClasses(this.getClass().getPackage().getName(), "effects", "expressions");
		} catch (Exception e) {

		}
	}

	private void types() {
		new SimpleType<IOwnedLand>(IOwnedLand.class, "landclaim", "land ?claim(s)?"){
			@Override
			@Nullable
			public IOwnedLand parse(String s, ParseContext arg1) {
				return null;
			}

			@Override
			public String toString(IOwnedLand ol, int arg1) {
				return String.valueOf(ol.getName());
			}

			@Override
			public String toVariableNameString(IOwnedLand ol) {
				return "ownedland:" + ol.getName();
			}};

	}
}
