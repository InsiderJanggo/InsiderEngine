package org.CraftTopia.blocks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.CraftTopia.Side;
import org.CraftTopia.datastructures.AABB;
import org.CraftTopia.datastructures.AABBObject;
import org.CraftTopia.game.Game;
import org.CraftTopia.inventory.InventoryItem;
import org.CraftTopia.math.Vec3i;
import org.CraftTopia.world.Chunk;
import org.CraftTopia.world.ChunkData;
import org.CraftTopia.world.LightBuffer;

public abstract class Block implements AABBObject
{

	protected BlockType _blockType;
	protected Vec3i _postion;
	protected Chunk _chunk;
	protected AABB _aabb;
	protected float _health;
	
	public int _distanceID;
	public float _distance;
	
	/* List facts */
	protected boolean _updating;
	protected boolean _rendering;
	protected boolean _renderManually;
	protected int _specialBlockPoolIndex;

	public Block(BlockType type, Chunk chunk, Vec3i pos)
	{
		_postion = pos;
		_blockType = type;
		_chunk = chunk;
		_health = type.getResistance();
	}
	
	public void setSpecialBlockPoolIndex(int specialBlockPoolIndex)
	{
		this._specialBlockPoolIndex = specialBlockPoolIndex;
	}
	
	public int getSpecialBlockPoolIndex()
	{
		return _specialBlockPoolIndex;
	}
	
	public Vec3i getPosition()
	{
		return _postion;
	}
	
	public BlockType getBlockType()
	{
		return _blockType;
	}
	
	public Chunk getBlockChunk()
	{
		return _chunk;
	}
	
	public int getX()
	{
		return _postion.x();
	}
	
	public int getY()
	{
		return _postion.y();
	}
	
	public int getZ()
	{
		return _postion.z();
	}
	
	public int getChunkDataIndex()
	{
		return ChunkData.positionToIndex(getX() - _chunk.getAbsoluteX(), getY(), getZ() - _chunk.getAbsoluteZ());
	}
	
	public boolean isMoving()
	{
		return false;
	}
	
	public boolean inflictDamage(float damage)
	{
		_health -= damage;
		if (_health <= 0)
		{
			destroy();
			return true;
		}
		return false;
	}
	
	public final void destroy()
	{
		Game.getInstance().getWorld().getChunkManager().removeBlock(getX(), getY(), getZ());
		_chunk.needsNewVBO();
	}
	
	public void destruct()
	{
		
	}
	
	
	public synchronized void removeFromVisibilityList()
	{
		if (_rendering)
		{
			_chunk.getVisibleBlocks().bufferRemove(getChunkDataIndex());
			_rendering = false;
			removeFromManualRenderList();
		}
	}
	
	public synchronized void addToVisibilityList()
	{
		if (!_rendering)
		{
			_chunk.getVisibleBlocks().bufferAdd(getChunkDataIndex());
			_rendering = true;
		}
	}
	
	public synchronized void addToUpdateList()
	{
		if (!_updating)
		{
			_chunk.getUpdatingBlocks().bufferAdd(getChunkDataIndex());
			_updating = true;
		}
	}
	
	public synchronized void removeFromUpdateList()
	{
		if (_updating)
		{
			_chunk.getUpdatingBlocks().bufferRemove(getChunkDataIndex());
			_updating = false;
		}
	}
	
	public synchronized void addToManualRenderList()
	{
		if (!_renderManually)
		{
			_chunk.getManualRenderingBlocks().bufferAdd(getChunkDataIndex());
			_renderManually = true;
		}
	}
	
	public synchronized void removeFromManualRenderList()
	{
		if (_renderManually)
		{
			_chunk.getManualRenderingBlocks().bufferRemove(getChunkDataIndex());
			_renderManually = false;
		}
	}

	public abstract void update();
	public abstract void render(LightBuffer lightBuffer);
	public abstract void storeInVBO(FloatBuffer vbo, LightBuffer lightBuffer);
	public abstract boolean isVisible();
	public abstract AABB getAABB();
	public abstract void smash(InventoryItem item);
	public abstract void neighborChanged(Side side);
	public abstract void checkVisibility();
	public abstract int getVertexCount();

	public void performSpecialAction()
	{
		
	}
	
	public void saveSpecialSaveData(DataOutputStream dos) throws IOException
	{
		
	}
	
	public void readSpecialSaveData(DataInputStream dis) throws IOException
	{
		
	}

	public synchronized void setUpdatingFlag(boolean u)
	{
		_updating = u;
		
	}

	public synchronized void setRenderingFlag(boolean v)
	{
		_rendering = v;
	}

	public void setChunk(Chunk chunk)
	{
		_chunk = chunk;	
	}

	public synchronized boolean isRenderingManually()
	{
		return _renderManually;
	}
	
	public synchronized boolean isUpdating()
	{
		return _updating;
	}

	public byte getMetaData()
	{
		return 0;
	}

	public synchronized boolean isRendering()
	{
		return _rendering;
	}




}