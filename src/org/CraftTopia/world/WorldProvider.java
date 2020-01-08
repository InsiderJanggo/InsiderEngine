package org.CraftTopia.world;

import java.util.List;

import org.CraftTopia.math.Vec3f;


public abstract class WorldProvider
{
	/* 2D Map */
	public abstract int getHeightAt(int x, int z);
	public abstract int getTemperatureAt(int x, int z);
	public abstract int getHumidityAt(int x, int z);
	
	/* 3D Calculated */
	public abstract int getTemperatureAt(int x, int y, int z);
	public abstract int getHumidityAt(int x, int y, int z);
	
	public abstract Biome getBiomeAt(int x, int y, int z);
	
	/* Calculate methods */
	public abstract Biome calculateBiome(int temperature, int humidity);
	public abstract int calculateTemperature(int temperature, int y);
	
	
	public abstract Vec3f getSpawnPoint();
	public abstract List<TreeDefinition> getTrees();
	
	public abstract void save() throws Exception;
	public abstract void load() throws Exception;
	
	public static class TreeDefinition
	{
		public TreeDefinition(int x, int y, int z, int type)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.type = type;
		}
		
		public int x, y, z;
		public int type;
	}
	
}
