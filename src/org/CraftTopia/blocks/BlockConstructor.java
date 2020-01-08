package org.CraftTopia.blocks;

import java.lang.reflect.Constructor;

import org.CraftTopia.blocks.customblocks.TallGrass;
import org.CraftTopia.math.Vec3i;
import org.CraftTopia.world.Chunk;

public class BlockConstructor
{
	@SuppressWarnings("unchecked")
	public static Block construct(int x, int y, int z, Chunk chunk, byte blockType, byte metadata)
	{
		if (blockType == 0)
			return null;
		BlockType type = BlockManager.getInstance().getBlockType(blockType);
		String customClass = type.getCustomClass();
		boolean crossed = type.isCrossed();
		if (crossed)
		{
			if (customClass == null)
			{
				return new CrossedBlock(type, chunk, new Vec3i(x, y, z));
			}
		} else
		{
			if (customClass == null)
			{
				return new DefaultBlock(type, chunk, new Vec3i(x, y, z));
			}
		}

		if (blockType == BlockManager.getInstance().blockID("tallgrass"))
		{
			return new TallGrass(chunk, new Vec3i(x, y, z), metadata);
		}

		try
		{
			Class<? extends Block> blockClass = (Class<? extends Block>) Class.forName(customClass);
			Constructor<? extends Block> constructor = blockClass.getConstructor(Chunk.class, Vec3i.class);
			Block block = constructor.newInstance(chunk, new Vec3i(x, y, z));
			return block;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
}