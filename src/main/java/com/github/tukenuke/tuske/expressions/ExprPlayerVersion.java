package com.github.tukenuke.tuske.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import com.github.tukenuke.tuske.TuSKe;
import com.github.tukenuke.tuske.documentation.Dependency;
import com.github.tukenuke.tuske.util.ReflectionUtils;
import com.github.tukenuke.tuske.util.Registry;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.ViaAPI;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import protocolsupport.api.ProtocolSupportAPI;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Level;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
@Name("Minecraft Version")
@Description("Returns the minecraft version of {{types|Player|player}}.")
@Examples({
		"on join:",
		"if minecraft version of player is \"1.9\":",
		"send \"You're joining with version %mc version of player%!\""})
@Since("1.0 (ProtocolSupport), 1.0.5 (ViaVersion)")
@Dependency("ProtocolSupport or ViaVersion")
public class ExprPlayerVersion extends SimplePropertyExpression<Player, String>{
	private static boolean hasViaVersion = Bukkit.getServer().getPluginManager().isPluginEnabled("ViaVersion");
	private static boolean hasProtocolSupport = Bukkit.getServer().getPluginManager().isPluginEnabled("ProtocolSupport");
	private static ViaAPI<Player> api; //ViaVersion API instance

	static {
		JavaPlugin viaversion = (JavaPlugin) Bukkit.getServer().getPluginManager().getPlugin("ViaVersion");
		if (viaversion != null) {
			if (ReflectionUtils.hasMethod(ViaAPI.class, "getPlayerVersion", Object.class)) {
				hasViaVersion = true;
				api = (ViaAPI<Player>)Via.getAPI();
			} else
				TuSKe.log("Couldn't hook with ViaVersion because it's outdated. At least version 2.0 is required.", Level.WARNING);
		}
		if (hasViaVersion || hasProtocolSupport)
			Registry.newProperty(ExprPlayerVersion.class, "(mc|minecraft) version", "player");
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	@Nullable
	public String convert(Player p) {
		if (hasViaVersion){
			int i = api.getPlayerVersion(p);
			return ProtocolVersion.getProtocol(i).getName().replace(".x", "");
		} else if (hasProtocolSupport)
			return ProtocolSupportAPI.getProtocolVersion(p).getName();
		return null;
	}

	@Override
	protected String getPropertyName() {
		return "minecraft version";
	}

}
