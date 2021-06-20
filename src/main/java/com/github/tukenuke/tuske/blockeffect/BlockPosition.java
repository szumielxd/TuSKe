package com.github.tukenuke.tuske.blockeffect;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
public class BlockPosition {
	
	public double x;
	public double y;
	public double z;
	public Material id;
	public byte data;
	
	@SuppressWarnings("deprecation")
	@Deprecated
	public BlockPosition(double x, double y, double z, int id, byte data){
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = Stream.of(Material.values()).filter(m -> m.getId() == id).findAny().orElse(null);
		this.data = data;
	}
	
	public BlockPosition(double x, double y, double z, Material id, byte data){
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
		this.data = data;
	}
	
	public void setBlock(Location loc){
		loc.add(x, y, z);
		Block b = loc.getBlock();
		b.setType(id, false);
		try {
			b.getClass().getMethod("setData", Byte.TYPE, Boolean.TYPE).invoke(b, data, false);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
		}
	}
	@Override
	public boolean equals(Object bp){
		if (bp instanceof BlockPosition)
			return Double.compare(x, ((BlockPosition) bp).x) == 0 && Double.compare(y, ((BlockPosition) bp).y) == 0 && Double.compare(z, ((BlockPosition) bp).z) == 0 && id == ((BlockPosition) bp).id && data == ((BlockPosition) bp).data;
		return false;
		
	}

}
