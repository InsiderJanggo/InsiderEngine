package org.CraftTopia.blocks.customblocks;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.CraftTopia.blocks.BlockManager;
import org.CraftTopia.blocks.DefaultBlock;
import org.CraftTopia.game.Game;
import org.CraftTopia.inventory.CraftingTableInventory;
import org.CraftTopia.inventory.InventoryIO;
import org.CraftTopia.math.Vec3i;
import org.CraftTopia.world.Chunk;

public class CraftingTable extends DefaultBlock
{
	
	static
	{
		try
		{
			Class.forName("org.CraftTopia.inventory.CraftingTableInventory");
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	private CraftingTableInventory _inventory;
	
	public CraftingTable(Chunk chunk, Vec3i pos)
	{
		super(BlockManager.getInstance().getBlockType(BlockManager.getInstance().blockID("crafting_table")), chunk, pos);
		_inventory = new CraftingTableInventory();
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
		int offset = CraftingTableInventory.CraftingTableInventoryRaster.CRAFTING_OFFSET;
		
		InventoryIO.writeInventory(_inventory, dos, offset, 9);
	}
	
	@Override
	public void readSpecialSaveData(DataInputStream dis) throws IOException
	{
		System.out.println("Read crafting table inventory");
		int offset = CraftingTableInventory.CraftingTableInventoryRaster.CRAFTING_OFFSET;
		
		InventoryIO.readInventory(dis, _inventory, offset);
		_inventory.checkForRecipe();
	}
	
}