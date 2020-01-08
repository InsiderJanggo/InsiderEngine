package org.CraftTopia.items.tools;

import org.CraftTopia.blocks.BlockManager;
import org.CraftTopia.blocks.BlockType.BlockClass;
import org.CraftTopia.items.Tool;
import org.CraftTopia.math.Vec2i;

public class BedrockPickaxe extends Tool
{
	public BedrockPickaxe()
	{
		super("bedrock_pickaxe", BlockClass.STONE, Material.BEDROCK, new Vec2i(0, 16), 6.0f);
	}
	
	@Override
	public float calcDamageFactorToBlock(byte block)
	{
		if (block == BlockManager.getInstance().blockID("bedrock"))
		{
			return 15000.0f;
		}
		return super.calcDamageFactorToBlock(block) * 2.0f;
	}
}
