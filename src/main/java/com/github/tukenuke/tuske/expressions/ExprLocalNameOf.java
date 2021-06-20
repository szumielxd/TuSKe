package com.github.tukenuke.tuske.expressions;

import com.github.tukenuke.tuske.util.Translate;
import com.github.tukenuke.tuske.util.Registry;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.slot.Slot;
import ch.njol.util.Kleenean;
;

public class ExprLocalNameOf extends SimpleExpression<String>{
	static {
		Registry.newProperty(ExprLocalNameOf.class, "[json] client id" , "object");
	}
	private Expression<Object> o;

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(Expression<?>[] arg, int arg1, Kleenean arg2, ParseResult arg3) {
		this.o = (Expression<Object>) arg[0];
		if (!(o.getReturnType().equals(Object.class) || o.getReturnType().equals(ItemStack.class) || o.getReturnType().equals(ItemType.class) || o.getReturnType().equals(Block.class) || o.getReturnType().equals(Slot.class) || o.getReturnType().equals(Enchantment.class) || o.getReturnType().equals(Entity.class) )){
			Skript.error("The '" + o + "' isn't a item, entity nor enchantment.");
			return false;
			
		}
		return true;
	}

	@Override
	public String toString(@Nullable Event e, boolean arg1) {
		return "local name of " + this.o;
	}

	@Override
	@Nullable
	protected String[] get(Event e) {
		if (this.o.getSingle(e) == null)
			return null;
		Object o = this.o.getSingle(e);
		String result = null;
		
		Class<?> slotClass = null;
		Method slotMethod = null;
		try {
			slotClass = Class.forName("ch.njol.skript.util.Slot");
		} catch (ClassNotFoundException ex) {
			try {
				slotClass = Class.forName("ch.njol.skript.util.slot.Slot");
			} catch (ClassNotFoundException ex1) {
				ex1.printStackTrace();
			}
		}
		try {
			slotMethod = slotClass.getMethod("getItem");
		} catch (NoSuchMethodException | SecurityException e1) {
			e1.printStackTrace();
		}
		
		if (o instanceof Block) result = Translate.getIDTranslate((Block)o);
		else if (o instanceof ItemStack) result = Translate.getIDTranslate((ItemStack)o); 
		else if (o instanceof Entity) result = Translate.getIDTranslate(((Entity)o).getType());
		else if (o instanceof Enchantment) result = Translate.getIDTranslate((Enchantment)o);
		else if (slotClass.isInstance(o)) try { result = Translate.getIDTranslate((ItemStack) slotMethod.invoke(o)); } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {}
		else return null;
		return new String[]{result};
	}
}