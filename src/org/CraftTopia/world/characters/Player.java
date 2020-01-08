package org.CraftTopia.world.characters;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.CraftTopia.GameObject;
import org.CraftTopia.blocks.Block;
import org.CraftTopia.blocks.BlockConstructor;
import org.CraftTopia.blocks.BlockManager;
import org.CraftTopia.blocks.BlockType;
import org.CraftTopia.datastructures.AABB;
import org.CraftTopia.game.ControlSettings;
import org.CraftTopia.game.Game;
import org.CraftTopia.inventory.DefaultPlayerInventory;
import org.CraftTopia.inventory.Inventory.InventoryPlace;
import org.CraftTopia.inventory.InventoryIO;
import org.CraftTopia.inventory.InventoryItem;
import org.CraftTopia.inventory.SharedInventoryContent;
import org.CraftTopia.items.ItemManager;
import org.CraftTopia.math.MathHelper;
import org.CraftTopia.math.RayBlockIntersection;
import org.CraftTopia.math.Vec3f;
import org.CraftTopia.math.Vec3i;
import org.CraftTopia.rendering.Camera;
import org.CraftTopia.utilities.IOUtilities;
import org.CraftTopia.world.Chunk;
import org.CraftTopia.world.Chunk.LightType;
import org.CraftTopia.world.ChunkManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

/**
 * 
 * @author martijncourteaux
 */
public class Player extends GameObject
{

	private static BlockManager _blockManager = BlockManager.getInstance();

	private Camera _camera;
	/** Postion of the player absolute in the world */
	private float x, y, z;
	private Vec3f _position;
	/** Rotation of the players head, in radians */
	private float rotX, rotY; // Radians
	private float bobbing, bobbingProcess;

	/* Spawn Point */
	private Vec3f _spawnPoint;

	/* Movement variables */
	private float speedForward = 0.0f;
	private float speedSide = 0.0f;
	private float maxSpeed = 6.0f;
	private float eyeHeight = 1.7f;
	private float playerHeight = 1.85f;
	private float acceleration = 15.0f;
	private float ySpeed = 0.0f;
	private boolean onGround = false;
	private boolean _flying = false;
	private int _rotationSegment = 0;

	/* Body */
	private CharacterBody _body;
	/* Editing */
	private float _rayCastLength;
	private AABB _intersectionTestingAABB;
	private AABB _aimedBlockAABB;
	private byte _aimedBlockType;
	private Vec3i _aimedBlockPosition;
	private Vec3i _aimedAdjacentBlockPosition;
	private float _aimedBlockHealth;
	private AABB _rayAABB;
	private ChunkManager _chunkManager;
	private InventoryItem _selectedItem;
	private int _selectedInventoryItemIndex = 0;
	private boolean _airSmashed;
	private boolean _destroying;
	/* Inventory */
	private DefaultPlayerInventory _inventory;
	private SharedInventoryContent _sharedInventoryContent;

	public Player(float x, float y, float z)
	{
		_position = new Vec3f(x, y, z);
		_spawnPoint = new Vec3f(x, y, z);

		_chunkManager = Game.getInstance().getWorld().getChunkManager();
		_body = new CharacterBody();

		_camera = new Camera();
		_camera.setFovy(Game.getInstance().getConfiguration().getFOVY());
		this.x = x;
		this.y = y;
		this.z = z;

		_inventory = new DefaultPlayerInventory();
		_sharedInventoryContent = new SharedInventoryContent(4 * 9);
		_inventory.setSharedContent(_sharedInventoryContent);

		_rayCastLength = Game.getInstance().getConfiguration().getMaximumPlayerEditingDistance();
		_rayAABB = new AABB(new Vec3f(), new Vec3f());
		_aimedBlockPosition = new Vec3i(0, -1, 0);
		_aimedBlockAABB = new AABB(new Vec3f(), new Vec3f());
		_intersectionTestingAABB = new AABB(new Vec3f(), new Vec3f());

		setSelectedInventoryItemIndex(0);
	}

	public Player(Vec3f spawnPoint)
	{
		this(spawnPoint.x(), spawnPoint.y(), spawnPoint.z());
	}

	public Camera getFirstPersonCamera()
	{
		return _camera;
	}

	public void setPosition(Vec3f position)
	{
		this.x = position.x();
		this.y = position.y();
		this.z = position.z();
	}

	/**
	 * The returned vector is the single instanced vector representing the
	 * players position. DO NOT MODIFY THIS VECTOR
	 * 
	 * @return the position of the player
	 */
	public Vec3f getPosition()
	{
		return _position;
	}

	private Vec3f getSpawnPoint()
	{
		return _spawnPoint;
	}

	@Override
	public void render()
	{

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);

		if (_aimedBlockPosition.y() != -1)
		{
			GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glDisable(GL11.GL_TEXTURE_2D);

			_aimedBlockAABB.render(0.0f, 0.0f, 0.0f, 0.1f);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}

		if (_selectedItem != null)
		{
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_CULL_FACE);

			_body.transformToRightHand();

			/* Prepare the light buffer */
			Chunk c = Game.getInstance().getWorld().getChunkManager().getChunkContaining(MathHelper.floor(_position.x()), MathHelper.floor(_position.y()) + 1, MathHelper.floor(_position.z()), false, false, false);
			if (c != null)
			{
				c.getLightBuffer().setReferencePoint(MathHelper.floor(_position.x()), MathHelper.floor(_position.y()) + 1, MathHelper.floor(_position.z()));

				/* Render the object, with the lightbuffer */
				_selectedItem.renderHoldableObject(c.getLightBuffer());
			}

			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glPopMatrix();
		}
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	@Override
	public void update()
	{
		physics();
		movement();
		_position.set(x, y, z);

		/* Update the camera */
		_camera.setPosition(x, y + eyeHeight, z);
		_camera.setRotation(rotX, rotY, bobbing);

		_body.update();
		_body.disableUsingRightHand();

		if (_selectedItem != null)
		{
			_body._animationSpeedRightHand = _selectedItem.getAnimationSpeed();
		} else
		{
			_body._animationSpeedRightHand = 1.0f;
		}

		rayCastBlock();

		if (ControlSettings.isActionHold(ControlSettings.SMASH))
		{
			smash();
		} else
		{
			_destroying = false;
			_airSmashed = false;
		}
	}

	private void movement()
	{
		float step = Game.getInstance().getStep();
		float factor = (onGround ? 1.0f : 0.5f);
		float accelerationStep = acceleration * step;

		float xStep = 0.0f;
		float zStep = 0.0f;
		// Forward movement
		{
			if (ControlSettings.isActionHold(ControlSettings.MOVE_FORWARD))
			{
				speedForward = Math.min(maxSpeed * (_flying ? 3.0f : 1.0f), speedForward + acceleration * step);
			} else if (ControlSettings.isActionHold(ControlSettings.MOVE_BACK))
			{
				speedForward = Math.max(-maxSpeed * (_flying ? 3.0f : 1.0f), speedForward - acceleration * step);
			} else
			{
				if (speedForward != 0.0f)
				{
					if (speedForward < 0.0f)
					{
						speedForward += factor * accelerationStep;
						if (speedForward > 0.0f)
						{
							speedForward = 0.0f;
						}
					} else
					{
						speedForward -= factor * accelerationStep;
						if (speedForward < 0.0f)
						{
							speedForward = 0.0f;
						}
					}
				}

			}
			float xAdd = (float) (MathHelper.cos(rotY)) * speedForward * step;
			float zAdd = (float) (MathHelper.sin(-rotY)) * speedForward * step;

			xStep += xAdd;
			zStep += zAdd;

		}
		// Side movement
		{
			if (ControlSettings.isActionHold(ControlSettings.MOVE_LEFT))
			{
				speedSide = Math.max(-maxSpeed * (_flying ? 3.0f : 1.0f), speedSide - acceleration * step);
			} else if (ControlSettings.isActionHold(ControlSettings.MOVE_RIGHT))
			{
				speedSide = Math.min(maxSpeed * (_flying ? 3.0f : 1.0f), speedSide + acceleration * step);
			} else
			{
				if (speedSide != 0.0f)
				{
					if (speedSide < 0.0f)
					{
						speedSide += factor * accelerationStep;
						if (speedSide > 0.0f)
						{
							speedSide = 0.0f;
						}
					} else
					{
						speedSide -= factor * accelerationStep;
						if (speedSide < 0.0f)
						{
							speedSide = 0.0f;
						}
					}
				}
			}
			float xAdd = (float) (MathHelper.sin(rotY)) * speedSide * step;
			float zAdd = (float) (MathHelper.cos(rotY)) * speedSide * step;

			xStep += xAdd;
			zStep += zAdd;
		}

		x += xStep;
		z += zStep;

		Chunk bc = null;

		byte wall = 0;
		byte wall2 = 0;
		{
			int xx = MathHelper.floor(x), yy = MathHelper.floor(y + 0.1f), zz = MathHelper.floor(z);
			bc = _chunkManager.getChunkContaining(xx, yy, zz, false, true, true);
			if (bc != null)
			{
				wall = bc.getBlockTypeAbsolute(xx, yy, zz, false, false, false);
			}
		}
		{
			int xx = MathHelper.floor(x), yy = MathHelper.floor(y + 0.1f) + 1, zz = MathHelper.floor(z);
			if (bc != null)
			{
				wall2 = bc.getBlockTypeAbsolute(xx, yy, zz, false, false, false);
			} else
			{
				bc = _chunkManager.getChunkContaining(xx, yy, zz, false, true, true);
				if (bc != null)
				{
					wall2 = bc.getBlockTypeAbsolute(xx, yy, zz, false, false, false);
				}
			}
		}
		if ((wall > 0 && BlockManager.getInstance().getBlockType(wall).isSolid()) || (wall2 > 0 && BlockManager.getInstance().getBlockType(wall2).isSolid()))
		{

			x -= xStep;
			z -= zStep;

			speedForward *= -0.2f;
			speedSide *= -0.2f;
		}

		float speed = MathHelper.sqrt(speedForward * speedForward + speedSide * speedSide);
		speed /= maxSpeed;

		bobbingProcess += step * 10.0f;
		bobbingProcess = MathHelper.simplifyRadians(bobbingProcess);

		float dx = Mouse.getDX();
		float dy = Mouse.getDY();

		dx /= 300.0f;
		dy /= 300.0f;

		float rotSeg = _rotationSegment;
		_rotationSegment = MathHelper.round(rotY / MathHelper.f_PI * 10.0f);

		rotY -= dx;
		rotX += dy;

		/* If it changes from chunk, recheck visible chunks */
		if (MathHelper.floorDivision(MathHelper.floor(x + xStep), Chunk.CHUNK_SIZE_HORIZONTAL) != MathHelper.floorDivision(MathHelper.floor(x), Chunk.CHUNK_SIZE_HORIZONTAL)
				|| MathHelper.floorDivision(MathHelper.floor(z + zStep), Chunk.CHUNK_SIZE_HORIZONTAL) != MathHelper.floorDivision(MathHelper.floor(z), Chunk.CHUNK_SIZE_HORIZONTAL) || _rotationSegment != rotSeg)
		{
			Game.getInstance().getWorld().selectLocalChunks();
			Game.getInstance().getWorld().checkForNewVisibleChunks();
		}

		rotY = MathHelper.simplifyRadians(rotY);
		rotX = MathHelper.clamp(rotX, -MathHelper.f_PI / 2.001f, MathHelper.f_PI / 2.001f);
		if (_flying)
		{
			bobbing *= 0.8f;
		} else
		{
			bobbing = MathHelper.sin(bobbingProcess) * 0.03f * speed * (MathHelper.f_PI / 2.0f - Math.abs(rotX)) / (MathHelper.f_PI / 2.0f);
		}
	}

	private void physics()
	{
		float step = Game.getInstance().getStep();
		ChunkManager chunkManager = Game.getInstance().getWorld().getChunkManager();
		byte support = chunkManager.getBlock(MathHelper.floor(x), MathHelper.floor(y) - 1, MathHelper.floor(z), false, false, false);
		byte subSupport = chunkManager.getBlock(MathHelper.floor(x), MathHelper.floor(y) - 2, MathHelper.floor(z), false, false, false);
		float supportHeight = Float.NEGATIVE_INFINITY;

		if (ControlSettings.isActionHold(ControlSettings.JUMP))
		{
			if (onGround)
			{
				ySpeed = 14f;
				onGround = false;
			} else if (_flying)
			{
				ySpeed += 14f * step;
			}
		} else if (_flying && ControlSettings.isActionHold(ControlSettings.CROUCH))
		{
			ySpeed -= 14f * step;
		}

		if (support > 0)
		{
			if (BlockManager.getInstance().getBlockType(support).isSolid())
			{
				supportHeight = MathHelper.floor(y);
				onGround = false;
			}
		} else if (subSupport > 0)
		{
			if (BlockManager.getInstance().getBlockType(subSupport).isSolid())
			{
				supportHeight = MathHelper.floor(y) - 1;
				onGround = false;
			}
		}

		if (supportHeight > y)
		{
			y = supportHeight;
			ySpeed = 0.0f;
			onGround = true;
		} else
		{
			if (!_flying)
			{
				ySpeed -= step * 55f;
			} else
			{
				float newSpeedY = ySpeed * 0.1f;
				float diffY = newSpeedY - ySpeed;
				ySpeed += diffY * step;
			}
			y += ySpeed * step;
			if (ySpeed > 0.0f)
			{
				int headbangY = MathHelper.floor(y + 0.1f) + 2;
				byte headbang = chunkManager.getBlock(MathHelper.floor(x), headbangY, MathHelper.floor(z), false, false, false);
				if (headbang > 0 && (headbangY < y + playerHeight))
				{
					y = (float) headbangY - playerHeight;
					ySpeed = 0.0f;
				}
			}
			{
				onGround = false;
				if (supportHeight >= y)
				{
					y = supportHeight;
					ySpeed = 0.0f;
					onGround = true;
				}
			}
		}
	}

	private void rayCastBlock()
	{
		float rayLenSquared = _rayCastLength * _rayCastLength;

		Vec3f rayDirection = _camera.getLookDirection();
		Vec3f rayOrigin = _camera.getPosition();

		rayDirection.normalise();

		/* Construct the AABB for the ray cast */
		_rayAABB.getPosition().set(rayOrigin);
		_rayAABB.getPosition().addFactor(rayDirection, _rayCastLength * 0.5f);
		_rayAABB.getDimensions().set(Math.abs(rayDirection.x()), Math.abs(rayDirection.y()), Math.abs(rayDirection.z()));
		_rayAABB.getDimensions().scale(_rayCastLength * 0.5f);

		int aabbX = MathHelper.round(_rayAABB.getPosition().x());
		int aabbY = MathHelper.round(_rayAABB.getPosition().y());
		int aabbZ = MathHelper.round(_rayAABB.getPosition().z());

		RayBlockIntersection.Intersection closestIntersection = null;
		byte closestBlock = 0;

		/* Iterate over all possible candidates for the raycast */
		Vec3f v = new Vec3f();
		Vec3i newAimedBlockPosition = new Vec3i();
		byte bl = 0;
		boolean special = false;
		Chunk chunk = Game.getInstance().getWorld().getChunkManager().getChunkContaining(aabbX, aabbY, aabbZ, false, false, false);
		if (chunk == null)
			return;

		for (int x = MathHelper.floor(_rayAABB.minX()); x <= MathHelper.ceil(_rayAABB.maxX()); ++x)
			for (int y = MathHelper.floor(_rayAABB.minY()); y <= MathHelper.ceil(_rayAABB.maxY()); ++y)
				for (int z = MathHelper.floor(_rayAABB.minZ()); z <= MathHelper.ceil(_rayAABB.maxZ()); ++z)
				{
					bl = chunk.getBlockTypeAbsolute(x, y, z, false, false, false);
					if (bl == 0 || bl == -1)
						continue;

					special = chunk.isBlockSpecialAbsolute(x, y, z);
					Block block = null;
					if (special)
					{
						block = chunk.getSpecialBlockAbsolute(x, y, z);
						if (block.isMoving())
						{
							continue;
						}
						_intersectionTestingAABB.set(block.getAABB());
					}

					v.set(x + 0.5f, y + 0.5f, z + 0.5f);

					v.sub(getPosition());
					float lenSquared = v.lengthSquared();
					if (lenSquared < rayLenSquared)
					{
						if (!special)
						{
							_intersectionTestingAABB.getPosition().set(x + 0.5f, y + 0.5f, z + 0.5f);
							_intersectionTestingAABB.getDimensions().set(_blockManager.getBlockType(bl).getDimensions());
							_intersectionTestingAABB.recalcVertices();
						}
						/* Perform the raycast */
						List<RayBlockIntersection.Intersection> intersections = RayBlockIntersection.executeIntersection(x, y, z, _intersectionTestingAABB, rayOrigin, rayDirection);
						if (!intersections.isEmpty())
						{
							if (closestIntersection == null || intersections.get(0).getDistance() < closestIntersection.getDistance())
							{
								closestIntersection = intersections.get(0);
								closestBlock = bl;
								newAimedBlockPosition.set(x, y, z);

								if (special)
								{
									_aimedBlockAABB.set(block.getAABB());
								} else
								{
									BlockType aimedBlockType = _blockManager.getBlockType(bl);
									_aimedBlockAABB.getPosition().set(_aimedBlockPosition.x(), _aimedBlockPosition.y(), _aimedBlockPosition.z()).add(aimedBlockType.getCenter());
									_aimedBlockAABB.getDimensions().set(aimedBlockType.getDimensions());
								}
							}
						}
					}

				}

		if (closestIntersection != null)
		{
			if (!_aimedBlockPosition.equals(newAimedBlockPosition))
			{
				_aimedBlockHealth = _blockManager.getBlockType(closestBlock).getResistance();
				_aimedBlockPosition.set(newAimedBlockPosition);
				_aimedBlockType = closestBlock;

				_aimedBlockAABB.recalcVertices();
			}
			_aimedAdjacentBlockPosition = closestIntersection.calcAdjacentBlockPos();
		} else
		{
			_aimedBlockPosition.setY(-1);
			_aimedAdjacentBlockPosition = null;
			_aimedBlockType = 0;
		}
		if (closestIntersection != null && _selectedItem != null && _aimedBlockPosition.y() != -1)
		{
			_body.setBlockDistance(closestIntersection.getDistance());
		} else
		{
			_body.setBlockDistance(0.0f);
		}
	}

	public DefaultPlayerInventory getInventory()
	{
		return _inventory;
	}

	private void setSelectedInventoryItemIndex(int i)
	{
		InventoryPlace oldPlace = _inventory.getInventoryPlace(_selectedInventoryItemIndex);
		if (oldPlace != null)
		{
			if (oldPlace.getItem() != null)
			{
				_body.forceDisableUsingRightHand();
			}
		}
		_selectedInventoryItemIndex = i;
		InventoryPlace newPlace = _inventory.getInventoryPlace(_selectedInventoryItemIndex);
		if (newPlace != null)
		{
			if (newPlace.isStack())
			{
				int itemType = newPlace.getStack().getItemType();
				_selectedItem = ItemManager.getInstance().getInventoryItem((short) itemType);
			} else
			{
				_selectedItem = newPlace.getItem();
			}
		} else
		{
			_selectedItem = null;
		}

	}

	public void inventoryContentChanged()
	{
		setSelectedInventoryItemIndex(_selectedInventoryItemIndex);
	}

	public String coordinatesToString()
	{
		return String.format("x: %8s, y: %8s, z: %8s", String.format("%.3f", x), String.format("%.3f", y), String.format("%.3f", z));
	}

	public SharedInventoryContent getSharedInventoryContent()
	{
		return _sharedInventoryContent;
	}

	public BlockType getAimedBlockType()
	{
		return BlockManager.getInstance().getBlockType(_aimedBlockType);
	}

	public void toggleFlying()
	{
		_flying = !_flying;
	}

	public void spreadLight()
	{
		if (_aimedBlockType != 0)
		{
			System.out.println("Spread Light! (" + _aimedAdjacentBlockPosition.x() + ", " + _aimedAdjacentBlockPosition.y() + ", " + _aimedAdjacentBlockPosition.z() + ")");
			Chunk chunk = Game.getInstance().getWorld().getChunkManager().getChunkContaining(_aimedAdjacentBlockPosition.x(), _aimedAdjacentBlockPosition.y(), _aimedAdjacentBlockPosition.z(), false, false, false);
			chunk.spreadLight(_aimedAdjacentBlockPosition.x(), _aimedAdjacentBlockPosition.y(), _aimedAdjacentBlockPosition.z(), (byte) 15, LightType.BLOCK);
		}
	}

	public void unspreadLight()
	{
		if (_aimedBlockType != 0)
		{
			System.out.println("Spread Light! (" + _aimedAdjacentBlockPosition.x() + ", " + _aimedAdjacentBlockPosition.y() + ", " + _aimedAdjacentBlockPosition.z() + ")");
			Chunk chunk = Game.getInstance().getWorld().getChunkManager().getChunkContaining(_aimedAdjacentBlockPosition.x(), _aimedAdjacentBlockPosition.y(), _aimedAdjacentBlockPosition.z(), false, false, false);
			chunk.unspreadLight(_aimedAdjacentBlockPosition.x(), _aimedAdjacentBlockPosition.y(), _aimedAdjacentBlockPosition.z(), (byte) 15, LightType.BLOCK);
		}
	}

	public void toggleLight()
	{
		if (_aimedBlockType != 0)
		{
			Chunk chunk = Game.getInstance().getWorld().getChunkManager().getChunkContaining(_aimedAdjacentBlockPosition.x(), _aimedAdjacentBlockPosition.y(), _aimedAdjacentBlockPosition.z(), false, false, false);
			byte light = chunk.getLightAbsolute(_aimedAdjacentBlockPosition.x(), _aimedAdjacentBlockPosition.y(), _aimedAdjacentBlockPosition.z(), LightType.BLOCK);
			if (light == 15)
			{
				unspreadLight();
			} else
			{
				spreadLight();
			}
		}
	}

	public void save() throws IOException
	{
		File file = Game.getInstance().getRelativeFile(Game.FILE_BASE_USER_DATA, "${world}/player.dat");

		DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));

		/* Position */
		IOUtilities.writeVec3f(dos, getPosition());
		dos.writeInt(_rotationSegment);

		/* Spawn Point */
		IOUtilities.writeVec3f(dos, getSpawnPoint());

		/* Inventory */
		InventoryIO.writeInventory(getInventory(), dos, 0, getInventory().size());

		/* Selected inventory index */
		dos.writeByte(_selectedInventoryItemIndex);
		
		dos.close();

	}

	public boolean load() throws IOException
	{
		File file = Game.getInstance().getRelativeFile(Game.FILE_BASE_USER_DATA, "${world}/player.dat");

		if (!file.exists())
		{
			return false;
		}

		DataInputStream dis = new DataInputStream(new FileInputStream(file));

		/* Position */
		IOUtilities.readVec3f(dis, getPosition());
		x = getPosition().x();
		y = getPosition().y();
		z = getPosition().z();

		/* Rotation */
		_rotationSegment = dis.readInt();
		rotY = _rotationSegment / 10.0f * MathHelper.f_PI;

		/* Spawn Point */
		IOUtilities.readVec3f(dis, getSpawnPoint());

		/* Inventory */
		InventoryIO.readInventory(dis, getInventory(), 0);

		/* Selected inventory index */
		_selectedInventoryItemIndex = dis.readByte();
		setSelectedInventoryItemIndex(_selectedInventoryItemIndex);

		dis.close();
		
		return true;
	}

	public void smash()
	{
		if (_aimedBlockType != 0)
		{
			_destroying = true;
			float toolDamage = 0.0f;
			if (_selectedItem != null)
			{
				_body.enableUsingRightHand();
				toolDamage = _selectedItem.calcDamageInflictedByBlock(_aimedBlockType);
				_aimedBlockHealth -= _selectedItem.calcDamageFactorToBlock(_aimedBlockType) * Game.getInstance().getStep() * 5.0f;
			} else
			{
				_aimedBlockHealth -= Game.getInstance().getStep() * 5.0f;
			}
			boolean destroy = _aimedBlockHealth <= 0.0f;
			if (destroy)
			{
				_chunkManager.removeBlock(_aimedBlockPosition.x(), _aimedBlockPosition.y(), _aimedBlockPosition.z());

				/* Add block to the inventory */
				int mineResult = getAimedBlockType().getMineResult();
				int mineResultCount = getAimedBlockType().getMineResultCount();
				if (mineResult != 0)
				{
					for (int i = 0; i < mineResultCount; ++i)
					{
						boolean added = _inventory.addToInventory(ItemManager.getInstance().getInventoryItem((short) (mineResult == -1 ? getAimedBlockType().getInventoryTypeID() : mineResult)));

						if (added)
						{
							// TODO Play sound for taking an item
						}
					}
				}
				_aimedBlockPosition.set(0, -1, 0);
				_aimedAdjacentBlockPosition = null;
				_aimedBlockType = 0;
				if (_selectedItem != null)
				{
					_selectedItem.inflictDamage(toolDamage);
				}
			}
		} else if (_selectedItem != null)
		{
			if (!_airSmashed && !_destroying)
			{
				_body.airSmash();
				_airSmashed = true;
			}
		}
	}

	public void buildOrAction()
	{
		if (_aimedBlockPosition.y() != -1)
		{
			if (getAimedBlockType().hasSpecialAction())
			{
				Block block = _chunkManager.getSpecialBlock(_aimedBlockPosition.x(), _aimedBlockPosition.y(), _aimedAdjacentBlockPosition.z());
				block.performSpecialAction();
			} else if (_selectedItem instanceof BlockType)
			{
				int bX = _aimedAdjacentBlockPosition.x();
				int bY = _aimedAdjacentBlockPosition.y();
				int bZ = _aimedAdjacentBlockPosition.z();

				int pX = MathHelper.floor(x);
				int pY = MathHelper.floor(y);
				int pZ = MathHelper.floor(z);

				if (bX == pX && (bY == pY || bY == pY + 1) && bZ == pZ)
				{
					// Player is where the block has to come
				} else
				{
					Chunk bc = _chunkManager.getChunkContaining(bX, bY, bZ, true, true, true);
					byte currentBlock = bc.getBlockTypeAbsolute(bX, bY, bZ, false, false, false);
					if (currentBlock == 0)
					{
						BlockType type = ((BlockType) _selectedItem);
						if (type.getCustomClass() == null)
						{
							bc.setDefaultBlockAbsolute(bX, bY, bZ, type, (byte) 0, true, true, true);
						} else
						{
							Block block = BlockConstructor.construct(bX, bY, bZ, bc, type.getID(), (byte) 0);
							System.out.println("Build special block! " + block + " (" + block.getClass().getSimpleName() + ")");
							bc.setSpecialBlockAbsolute(bX, bY, bZ, block, true, true, true);
						}

						_inventory.getInventoryPlace(_selectedInventoryItemIndex).getStack().decreaseItemCount();
						setSelectedInventoryItemIndex(_selectedInventoryItemIndex);
					}
				}
			}
		}
	}

	public void scrollInventoryItem()
	{
		float wheel = Mouse.getEventDWheel();
		if (wheel != 0)
		{
			wheel /= 210.0f;
			setSelectedInventoryItemIndex(MathHelper.clamp(_selectedInventoryItemIndex + MathHelper.round(wheel), 0, 8));
		}
	}

	public float getEyeHeight()
	{
		return eyeHeight;
	}

	public int getSelectedInventoryItemIndex()
	{
		return _selectedInventoryItemIndex;
	}

}