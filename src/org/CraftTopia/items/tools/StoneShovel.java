package org.CraftTopia.items.tools;

import org.CraftTopia.blocks.BlockType.BlockClass;
import org.CraftTopia.items.Tool;
import org.CraftTopia.math.Vec2i;

public class StoneShovel extends Tool
{
    public StoneShovel()
    {
        super("stone_shovel", BlockClass.SAND, Material.STONE, new Vec2i(1, 5), 8.0f);
    }
}
