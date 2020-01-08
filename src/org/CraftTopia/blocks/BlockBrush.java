package org.CraftTopia.blocks;

import java.nio.FloatBuffer;

import org.CraftTopia.math.Vec3f;
import org.CraftTopia.world.LightBuffer;

public abstract class BlockBrush
{

	public abstract void setPosition(float x, float y, float z);

	public void setPosition(Vec3f v)
	{
		setPosition(v.x(), v.y(), v.z());
	}

	public abstract void render(LightBuffer lightBuffer);
	public abstract void storeInVBO(FloatBuffer vbo, float x, float y, float z, LightBuffer lightBuffer);
	public abstract int getVertexCount();

	public abstract void create();

	public abstract void release();
}
