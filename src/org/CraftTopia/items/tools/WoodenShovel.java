package org.CraftTopia.items.tools;

import org.CraftTopia.blocks.BlockType.BlockClass;
import org.CraftTopia.items.Tool;
import org.CraftTopia.math.Vec2i;

public class WoodenShovel extends Tool
{

	public WoodenShovel()
	{
		super("wooden_shovel", BlockClass.SAND, Material.WOOD, new Vec2i(0, 5), 8.0f);
	}
}
