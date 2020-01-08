package org.CraftTopia.items.tools;

import org.CraftTopia.blocks.BlockType.BlockClass;
import org.CraftTopia.items.Tool;
import org.CraftTopia.math.Vec2i;

public class StonePickaxe extends Tool
{
    public StonePickaxe()
    {
        super("stone_pickaxe", BlockClass.STONE, Material.STONE, new Vec2i(1, 6), 7.0f);
    }
}
