package org.CraftTopia.blocks.customblocks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.CraftTopia.blocks.BlockManager;
import org.CraftTopia.blocks.DefaultBlock;
import org.CraftTopia.game.Game;
import org.CraftTopia.inventory.DoubleContainerInventory;
import org.CraftTopia.inventory.InventoryIO;
import org.CraftTopia.math.Vec3i;
import org.CraftTopia.world.Chunk;

public class Container extends DefaultBlock
{

	
	static
	{
		try
		{
			Class.forName("org.CraftTopia.inventory.DoubleContainerInventory");
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	private DoubleContainerInventory _inventory;
	
	public Container(Chunk chunk, Vec3i pos)
	{
		super(BlockManager.getInstance().getBlockType(BlockManager.getInstance().blockID("container")), chunk, pos);
		_inventory = new DoubleContainerInventory();
		_inventory.setSharedContent(Game.getInstance().getWorld().getActivePlayer().getSharedInventoryContent());
	}

	@Override
	public void performSpecialAction()
	{
		Game.getInstance().getWorld().setActivatedInventory(_inventory);
	}
	
	@Override
	public void saveSpecialSaveData(DataOutputStream dos) throws IOException
	{
		int offset = DoubleContainerInventory.DoubleContainerInventoryRaster.CONTENT_OFFSET;
		
		InventoryIO.writeInventory(_inventory, dos, offset, 54);
	}
	
	@Override
	public void readSpecialSaveData(DataInputStream dis) throws IOException
	{
		System.out.println("Read crafting table inventory");
		int offset = DoubleContainerInventory.DoubleContainerInventoryRaster.CONTENT_OFFSET;
		
		InventoryIO.readInventory(dis, _inventory, offset);
	}
}