package org.CraftTopia.items.tools;

import org.CraftTopia.blocks.BlockType.BlockClass;
import org.CraftTopia.items.Tool;
import org.CraftTopia.math.Vec2i;

public class BedrockAxe extends Tool

{

	public BedrockAxe()
	{
		 super("bedrock_axe", BlockClass.WOOD, Material.BEDROCK, new Vec2i(1, 16), 7.0f);		 
	}
}
