package org.CraftTopia.world.generators;

import org.CraftTopia.blocks.BlockManager;
import org.CraftTopia.game.Game;
import org.CraftTopia.world.ChunkManager;

public class Generator
{
	
	protected ChunkManager _chunkManager;
	protected BlockManager _blockManager;
	protected final long _worldSeed;

	public Generator()
	{
		_chunkManager = Game.getInstance().getWorld().getChunkManager();
		_blockManager = BlockManager.getInstance();
		_worldSeed = Game.getInstance().getWorld().getWorldSeed();
	}
}