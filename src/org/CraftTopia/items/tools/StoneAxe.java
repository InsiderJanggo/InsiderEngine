package org.CraftTopia.items.tools;

import org.CraftTopia.blocks.BlockType.BlockClass;
import org.CraftTopia.items.Tool;
import org.CraftTopia.math.Vec2i;

public class StoneAxe extends Tool
{
    public StoneAxe()
    {
        super("stone_axe", BlockClass.WOOD, Material.STONE, new Vec2i(1, 7), 7.2f);
    }
}
