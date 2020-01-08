package org.CraftTopia.items.tools;

import org.CraftTopia.blocks.BlockType.BlockClass;
import org.CraftTopia.items.Tool;
import org.CraftTopia.math.Vec2i;

public class BedrockShovel extends Tool

{
	public BedrockShovel()
	{
		super("bedrock_shovel", BlockClass.SAND, Material.BEDROCK, new Vec2i(2, 16), 7.0f);
	}

}
