package org.CraftTopia.items;
import org.CraftTopia.math.Vec2i;
import org.newdawn.slick.opengl.Texture;


/**
 *
 * @author martijncourteaux
 */
public class GenericItem extends TexturedItem
{

    private boolean stackable;

    public GenericItem(String name, float animSpeed, boolean stackable, Texture tex, Vec2i texPos)
    {
        super(name, animSpeed, tex, texPos);
        this.stackable = stackable;
    }
    
    @Override
    public boolean isStackable()
    {
        return stackable;
    }

    @Override
    public void update()
    {
        // Do nothing
    }
    
}
public class GenericItem {

}
