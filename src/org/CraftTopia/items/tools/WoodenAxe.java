package org.CraftTopia.items.tools;

import org.CraftTopia.blocks.BlockType.BlockClass;
import org.CraftTopia.items.Tool;
import org.CraftTopia.math.Vec2i;

public class WoodenAxe extends Tool
{
	public WoodenAxe()
	{
		super("wooden_axe", BlockClass.WOOD, Material.WOOD, new Vec2i(0, 7), 7.2f);
	}
}