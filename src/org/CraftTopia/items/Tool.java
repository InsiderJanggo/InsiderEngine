package org.CraftTopia.items;

import org.CraftTopia.blocks.BlockManager;
import org.CraftTopia.blocks.BlockType;
import org.CraftTopia.blocks.BlockType.BlockClass;
import org.CraftTopia.game.TextureStorage;
import org.CraftTopia.math.Vec2i;

/**
 *
 * @author martijncourteaux
 */
public abstract class Tool extends TexturedItem
{

    public enum Material
    {

        WOOD, STONE, IRON, DIAMOND, GOLD, BEDROCK
    }
    private BlockClass _blockClass;
    private float _health;
    private Material _material;

    protected Tool(String name, BlockClass blockClass, Material material, Vec2i texturePosition, float animationSpeed)
    {
        super(name, animationSpeed, TextureStorage.getTexture("items"), texturePosition);

        this._blockClass = blockClass;
        this._material = material;
    }

    @Override
    public void update()
    {
        // Do nothing
    }

    @Override
    public float calcDamageFactorToBlock(byte block)
    {
        BlockType bt = BlockManager.getInstance().getBlockType(block);

        if (bt.getBlockClass() == getBlockClass())
        {
        	if (_material == null) return 4.0f;
            return (_material.ordinal() / 4.0f) + 4.0f;
        }
        return 1.2f;
    }

    @Override
    public float calcDamageInflictedByBlock(byte block)
    {
    	if (_material == Material.BEDROCK)
    	{
    		return 0.0f;
    	}
        BlockType bt = BlockManager.getInstance().getBlockType(block);
        float materialResistance = (0.2f / (float) Math.pow(_material.ordinal(), 1.2d));
        if (bt.getBlockClass() == getBlockClass())
        {
            return materialResistance * (0.05f * bt.getResistance());
        }
        return bt.getResistance() * materialResistance;
    }

    public BlockClass getBlockClass()
    {
        return _blockClass;
    }

    @Override
    public boolean isStackable()
    {
        return false;
    }

    @Override
    public void inflictDamage(float toolDamage)
    {
        this._health -= toolDamage;
    }

    @Override
    public float getHealth()
    {
        return _health;
    }
}
