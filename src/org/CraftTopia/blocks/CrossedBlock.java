package org.CraftTopia.blocks;

import java.nio.FloatBuffer;

import org.CraftTopia.Side;
import org.CraftTopia.datastructures.AABB;
import org.CraftTopia.inventory.InventoryItem;
import org.CraftTopia.math.Vec3f;
import org.CraftTopia.math.Vec3i;
import org.CraftTopia.world.Chunk;
import org.CraftTopia.world.LightBuffer;

public class CrossedBlock extends Block
{

	public CrossedBlock(BlockType type, Chunk chunk, Vec3i pos)
	{
		super(type, chunk, pos);
		addToVisibilityList();
	}

	@Override
	public void update()
	{
	}

	@Override
	public void render(LightBuffer lightBuffer)
	{
		_blockType.getCrossedBlockBrush().setPosition(_postion.x() + 0.5f, _postion.y() + 0.5f, _postion.z() + 0.5f);
		_blockType.getCrossedBlockBrush().render(lightBuffer);
	}
	
	@Override
	public void storeInVBO(FloatBuffer vbo, LightBuffer lightBuffer)
	{
		_blockType.getCrossedBlockBrush().storeInVBO(vbo, _postion.x() + 0.5f, _postion.y() + 0.5f, _postion.z() + 0.5f, lightBuffer);
	}

	@Override
	public boolean isVisible()
	{
		return true;
	}

	@Override
	public synchronized AABB getAABB()
	{
		if (_aabb == null)
		{
			_aabb = new AABB(new Vec3f(getPosition()).add(DefaultBlock.HALF_BLOCK_SIZE), new Vec3f(DefaultBlock.HALF_BLOCK_SIZE));
		}
		return _aabb;
	}

	@Override
	public void smash(InventoryItem item)
	{
		destroy();
	}

	@Override
	public void neighborChanged(Side side)
	{
	}

	@Override
	public void checkVisibility()
	{
		// TODO: Check if it is surrounded by four non-translucent blocks */
	}

	
	@Override
	public int getVertexCount()
	{
		return 8;
	}
	
}
