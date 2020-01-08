package org.CraftTopia.items.tools;

import org.CraftTopia.blocks.BlockType.BlockClass;
import org.CraftTopia.items.Tool;
import org.CraftTopia.math.Vec2i;

public class WoodenPickaxe extends Tool
{

	public WoodenPickaxe()
	{
		super("wooden_pickaxe", BlockClass.STONE, Material.WOOD, new Vec2i(0, 6), 7.5f);
	}
}
