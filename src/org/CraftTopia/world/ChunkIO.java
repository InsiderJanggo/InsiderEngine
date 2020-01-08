package org.CraftTopia.world;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.CraftTopia.blocks.Block;
import org.CraftTopia.blocks.BlockConstructor;
import org.CraftTopia.blocks.BlockManager;
import org.CraftTopia.blocks.BlockType;
import org.CraftTopia.game.Game;
import org.CraftTopia.math.MathHelper;
import org.CraftTopia.math.Vec3i;

public class ChunkIO
{
	
	private static final BlockManager _blockManager = BlockManager.getInstance();

	private long getUniquePositionID(int x, int z)
	{
		return MathHelper.cantorize(MathHelper.mapToPositive(x), MathHelper.mapToPositive(z));
	}

	private File getChunkFile(int x, int z)
	{
		long uniquePositionID = getUniquePositionID(x, z);
		File f = Game.getInstance().getRelativeFile(Game.FILE_BASE_USER_DATA, "${world}/chunks/" + Long.toHexString(uniquePositionID) + ".chunk");
		f.getParentFile().mkdirs();
		f.getParentFile().mkdir();
		return f;
	}

	private File getChunkFile(Chunk ch)
	{
		return getChunkFile(ch.getX(), ch.getZ());
	}

	public void loadChunk(Chunk chunk) throws IOException
	{
		File file = getChunkFile(chunk);
		if (!file.exists())
		{
			/* The chunk is totally new, so set it as "loaded" */
			chunk.setLoaded(true);
			return;
		}

		chunk.setLoading(true);
		DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

		boolean generated = dis.readBoolean();
		System.out.println("Load Chunk (" + chunk.getX() + ", " + chunk.getZ() + "): generated = " + generated);

		int size = dis.readInt();

		BlockType type = null;
		Vec3i blockPos = new Vec3i();
		for (int i = 0; i < size; ++i)
		{
			byte b = dis.readByte();
			byte metadata = dis.readByte();
			if (b != 0)
			{
				type = BlockManager.getInstance().getBlockType(b);
				ChunkData.indexToPosition(i, blockPos);
				int bx = blockPos.x();
				int by = blockPos.y();
				int bz = blockPos.z();
				if (type.getCustomClass() == null)
				{
					chunk.setDefaultBlockRelative(bx, by, bz, type, metadata, false, false, false);
				} else
				{
					Block block = BlockConstructor.construct(chunk.getAbsoluteX() + bx, by, chunk.getAbsoluteZ() + bz, chunk, b, metadata);
					chunk.setSpecialBlockRelative(bx, by, bz, block, false, false, false);
					
					if (block.getBlockType().hasSpecialSaveData())
					{
						block.readSpecialSaveData(dis);
					}
				}
			}
		}

		dis.close();
		chunk.setGenerated(generated);
		chunk.setLoading(false);
		chunk.performListChanges();
		chunk.markNeighborsLightPointsDirty();
		chunk.setLoaded(true);
		chunk.buildVisibileContentAABB();
	}

	protected void saveChunk(Chunk blockChunk) throws Exception
	{
		File file = getChunkFile(blockChunk.getX(), blockChunk.getZ());

		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));

		/* Store if the chunk was generated or only created */
		dos.writeBoolean(blockChunk.isGenerated());
		System.out.println("Save Chunk (" + blockChunk.getX() + ", " + blockChunk.getZ() + "): generated = " + blockChunk.isGenerated());

		ChunkData data = blockChunk.getChunkData();
		int blockCount = 0;
		for (int i = 0; i < Chunk.BLOCK_COUNT; ++i)
		{
			byte b = data.getBlockType(i);
			if (b != 0)
			{
				blockCount = i;
			}
		}
		blockCount++;
		dos.writeInt(blockCount);

		for (int i = 0; i < blockCount; ++i)
		{
			byte b = data.getBlockType(i);
			if (data.isSpecial(i)) 
			{
				Block bl = data.getSpecialBlock(i);
				BlockType type = _blockManager.getBlockType(b);
				dos.writeByte(b);
				dos.writeByte(bl.getMetaData());
				
				if (type.hasSpecialSaveData())
				{
					bl.saveSpecialSaveData(dos);
				}
			} else if (b == 0)
			{
				dos.writeShort(0);
			} else
			{
				dos.writeByte(b);
				dos.writeByte(data.getMetaData(i));
			}
		}

		dos.flush();
		dos.close();
	}
}