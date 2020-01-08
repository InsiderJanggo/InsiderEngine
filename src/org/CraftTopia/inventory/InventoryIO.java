package org.CraftTopia.inventory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.CraftTopia.inventory.Inventory.InventoryItemStack;
import org.CraftTopia.inventory.Inventory.InventoryPlace;
import org.CraftTopia.items.ItemManager;

public class InventoryIO
{

	public static void writeInventory(Inventory inv, DataOutputStream dos, int offset, int length) throws IOException
	{
		if (offset + length > inv.size())
		{
			throw new IndexOutOfBoundsException("Inventory index out of bounds!");
		}
		
		dos.writeInt(length);

		for (int i = offset; i < offset + length; ++i)
		{
			InventoryPlace place = inv.getInventoryPlace(i);
			if (place == null)
			{
				dos.writeByte(0);
			} else
			{
				if (place.isStack())
				{
					dos.writeByte(2);
					InventoryItemStack stack = place.getStack();
					dos.writeShort(stack.getItemType());
					dos.writeInt(stack.getItemCount());
				} else
				{
					dos.writeByte(1);
					InventoryItem item = place.getItem();
					dos.writeShort(item.getInventoryTypeID());
					dos.writeFloat(item.getHealth());
				}
			}
		}
	}

	public static void readInventory(DataInputStream dis, Inventory inv, int offset) throws IOException
	{
		int length = dis.readInt();

		if (offset + length > inv.size())
		{
			throw new IOException("Inventory size is invalid!");
		}

		for (int i = offset; i < offset + length; ++i)
		{
			byte type = dis.readByte();
			if (type == 0)
			{
				inv.setContentAt(null, i);
			} else
			{
				if (type == 2)
				{
					short stackItemType = dis.readShort();
					int stackItemCount = dis.readInt();
					inv.setContentAt(new InventoryPlace(i, inv.new InventoryItemStack(stackItemType, stackItemCount)), i);
				} else if (type == 1)
				{
					short itemType = dis.readShort();
					float health = dis.readFloat();
					// TODO use the health
					inv.setContentAt(new InventoryPlace(i, ItemManager.getInstance().getInventoryItem(itemType)), i);
				} else
				{
					throw new IOException("Inventory data is invalid!");
				}
			}
		}
	}
	

}
