package org.CraftTopia.blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author martijncourteaux
 * thx to him
 */
public class BlockBrushStorage
{
    private static Map<String, BlockBrush> brushes = new HashMap<String, BlockBrush>();

    private BlockBrushStorage()
    {
    }
    
    public static void releaseBrushes()
    {
        for (Entry<String, BlockBrush> entry : brushes.entrySet())
        {
            entry.getValue().release();
        }
    }
    
    public static void registerBrush(String id, BlockBrush bb)
    {
        brushes.put(id, bb);
    }
    
    public static BlockBrush get(String id)
    {
        return brushes.get(id);
    }
}