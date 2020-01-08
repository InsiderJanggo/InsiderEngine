package org.CraftTopia.game;


import static org.lwjgl.opengl.GL11.*;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.CraftTopia.blocks.BlockManager;
import org.CraftTopia.blocks.BlockXMLLoader;
import org.CraftTopia.game.PerformanceMonitor.Operation;
import org.CraftTopia.items.ItemXMLLoader;
import org.CraftTopia.math.MathHelper;
import org.CraftTopia.math.Vec3f;
import org.CraftTopia.recipes.RecipeManager;
import org.CraftTopia.rendering.BufferManager;
import org.CraftTopia.rendering.GLFont;
import org.CraftTopia.rendering.GLUtils;
import org.CraftTopia.world.World;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.NVFogDistance;

public class Game
{
	private World _world;
	private int _fps;
	private float _step;
	private int _sleepTimeMillis;
	private Configuration _configuration;
	private static Game __instance;
	private int[] _fpsDataBuffer;
	private float _averageFPS;

	public static boolean RENDER_INFORMATION_OVERLAY = false;

	public static Game getInstance()
	{
		if (__instance == null)
		{
			__instance = new Game();
		}
		return __instance;
	}

	/**
	 * Set the display mode to be used
	 * 
	 * @param width
	 *            The width of the display required
	 * @param height
	 *            The height of the display required
	 * @param fullscreen
	 *            True if we want fullscreen mode
	 */
	public void setDisplayMode(int width, int height, boolean fullscreen)
	{

		// return if requested DisplayMode is already set
		if ((Display.getDisplayMode().getWidth() == width) && (Display.getDisplayMode().getHeight() == height) && (Display.isFullscreen() == fullscreen))
		{
			return;
		}

		try
		{
			DisplayMode targetDisplayMode = null;

			if (fullscreen)
			{
				DisplayMode[] modes = Display.getAvailableDisplayModes();
				int freq = 0;

				for (int i = 0; i < modes.length; i++)
				{
					DisplayMode current = modes[i];
					if ((current.getWidth() == width) && (current.getHeight() == height))
					{
						if ((targetDisplayMode == null) || (current.getFrequency() >= freq))
						{
							if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel()))
							{
								targetDisplayMode = current;
								freq = targetDisplayMode.getFrequency();
							}
						}

						// if we've found a match for bpp and frequence against
						// the
						// original display mode then it's probably best to go
						// for this one
						// since it's most likely compatible with the monitor
						if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) && (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency()))
						{
							targetDisplayMode = current;
							break;
						}
					}
				}
			} else
			{
				targetDisplayMode = new DisplayMode(width, height);
			}

			if (targetDisplayMode == null)
			{
				System.out.println("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
				return;
			}
			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);

		} catch (LWJGLException e)
		{
			System.out.println("Unable to setup mode " + width + "x" + height + " fullscreen=" + fullscreen + e);
		}
	}

	public void init() throws IOException
	{
		loadConfiguration();
		this._fpsDataBuffer = new int[16];
		this._fps = _configuration.getFPS();
		try
		{
			Display.setTitle("CraftTopia");

			setDisplayMode(_configuration.getWidth(), _configuration.getHeight(), _configuration.isFullscreen());
			if (_configuration.getVSync())
			{
				Display.setVSyncEnabled(true);
			}
			Display.create();
		} catch (LWJGLException e)
		{
			e.printStackTrace();
			System.exit(0);
		}

		System.out.println("LWJGL Version: " + Sys.getVersion());
		System.out.println("GPU: " + Display.getAdapter());
		
		initOpenGL();
		loadTextures();
		loadFonts();
		loadItems();
		loadBlocks();
		RecipeManager.getInstance().loadRecipes();
		loadSpecialStuff();
		Mouse.setGrabbed(true);
	}

	private void loadSpecialStuff()
	{
		try
		{
			Class.forName("org.CraftTopia.rendering.particles.Smoke");
		} catch (Exception e)
		{
			// TODO: handle exception
		}
	}

	public void initOpenGL() throws IOException
	{
		// init OpenGL
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, 800, 600, 0, 1, 300);
		glMatrixMode(GL_MODELVIEW);

		GL11.glShadeModel(GL11.GL_SMOOTH);

		Vec3f fog = _configuration.getFogColor();

		glClearColor(fog.x(), fog.y(), fog.z(), 1.0f);
		glEnable(GL_TEXTURE_2D);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);

		// glEnable(GL_DEPTH_TEST);
		// glDepthFunc(GL_ALWAYS);

		glEnable(GL_CULL_FACE);

		glEnable(GL_FOG);
		glFog(GL_FOG_COLOR, GLUtils.wrapDirect(fog.x(), fog.y(), fog.z(), 1.0f));
		glFogi(GL_FOG_MODE, GL11.GL_LINEAR);
		glFogf(GL_FOG_START, _configuration.getViewingDistance() * 0.55f);
		glFogf(GL_FOG_END, _configuration.getViewingDistance());
		glFogi(NVFogDistance.GL_FOG_DISTANCE_MODE_NV, NVFogDistance.GL_EYE_RADIAL_NV);
		glHint(GL_FOG_HINT, GL_NICEST);

		System.out.println("VBO Supported: " + GLUtils.isVBOSupported());

		/* Instantiate the BufferManager */
		BufferManager.getInstance();
	}

	public float getFPS()
	{
		return _fps;
	}

	public float getStep()
	{
		return _step;
	}

	public void update()
	{
		if (_world != null)
		{
			_world.update();
		}
		BufferManager.getInstance().deleteQueuedBuffers();
	}

	public void render()
	{
		// Clear the screen and depth buffer
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		if (_world != null)
		{
			_world.render();
		}

		if (RENDER_INFORMATION_OVERLAY)
			renderOnScreenInfo();
	}

	private void renderOnScreenInfo()
	{
		GLFont infoFont = FontStorage.getFont("Monospaced_20");
		glColor3f(1, 1, 1);

		/* Top Left Info */
		infoFont.print(4, _configuration.getHeight() - 20, String.format("FPS: %5.1f", getAverageFPS()));
		infoFont.print(4, _configuration.getHeight() - 20 - 15, "Sleeping: " + String.format("%4d", Game.getInstance().getSleepTime()));
		infoFont.print(4, _configuration.getHeight() - 20 - 30, "Heap Size: " + MathHelper.bytesToMagaBytes(Runtime.getRuntime().totalMemory()) + " MB");
		infoFont.print(4, _configuration.getHeight() - 20 - 45, "Heap Use:  " + MathHelper.bytesToMagaBytes(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) + " MB");
		infoFont.print(4, _configuration.getHeight() - 20 - 60, "Buffers: " + BufferManager.getInstance().getAliveBuffers());
		
		Operation[] ops = Operation.values();
		for (int i = 0; i < ops.length; ++i)
		{
			String name = ops[i].name();
			infoFont.print(_configuration.getWidth() - 200, _configuration.getHeight() - 20 - 15 * i, String.format("%-20s %7.3f", name, PerformanceMonitor.getInstance().get(ops[i])));
		}
	}

	public void initOverlayRendering()
	{

		Configuration conf = Game.getInstance().getConfiguration();

		glDisable(GL_FOG);

		glClear(GL_DEPTH_BUFFER_BIT);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, conf.getWidth(), 0, conf.getHeight(), -100, 100);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glEnable(GL_COLOR_MATERIAL);
		glEnable(GL_ALPHA_TEST);
		glDisable(GL_CULL_FACE);
		glDisable(GL_DEPTH_TEST);
	}

	public void initSceneRendering()
	{

		glDisable(GL_BLEND);
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LEQUAL);
		glEnable(GL_CULL_FACE);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		glEnable(GL_FOG);
	}

	public void renderTransculentOverlayLayer()
	{
		glColor4f(0.0f, 0.0f, 0.0f, 0.4f);
		glDisable(GL_TEXTURE_2D);
		glBegin(GL_QUADS);
		glVertex2i(0, 0);
		glVertex2i(_configuration.getWidth(), 0);
		glVertex2i(_configuration.getWidth(), _configuration.getHeight());
		glVertex2i(0, _configuration.getWidth());
		glEnd();
	}

	public void startGameLoop()
	{

		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		while (!Display.isCloseRequested())
		{
			if (Keyboard.isKeyDown(Keyboard.KEY_LMENU) && Keyboard.isKeyDown(Keyboard.KEY_RETURN))
			{
			}
			long startTiming = System.nanoTime();
			_step = 1.0f / _fps;
			
			/* Update */
			PerformanceMonitor.getInstance().start(Operation.UPDATE);
			update();
			PerformanceMonitor.getInstance().stop(Operation.UPDATE);
			
			/* Render */
			PerformanceMonitor.getInstance().start(Operation.RENDER_ALL);
			render();
			Display.update();
			PerformanceMonitor.getInstance().stop(Operation.RENDER_ALL);
			
			
			
			long stopTiming = System.nanoTime();

			long frameTimeNanos = (stopTiming - startTiming);
			long desiredFrameTimeNanos = 1000000000L;
			if (_configuration.getVSync())
			{
				// desiredFrameTimeNanos /= 1;
			} else
			{
				desiredFrameTimeNanos /= _configuration.getFPS();
			}

			long diff = desiredFrameTimeNanos - frameTimeNanos;
			if (frameTimeNanos < desiredFrameTimeNanos)
			{
				if (!_configuration.getVSync())
				{
					try
					{
						_sleepTimeMillis = (int) (diff / 1000000L);
						Thread.sleep(_sleepTimeMillis, (int) (diff % 1000000L));
					} catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			} else
			{
				_sleepTimeMillis = 0;
			}
			_fps = (int) (1000000000.0f / (frameTimeNanos + (_sleepTimeMillis * 1000000L)));

			/* Average FPS System */
			float fpsSum = _fps;
			for (int i = 0; i < _fpsDataBuffer.length - 1; ++i)
			{
				_fpsDataBuffer[i] = _fpsDataBuffer[i + 1];
				fpsSum += _fpsDataBuffer[i];
			}
			_fpsDataBuffer[_fpsDataBuffer.length - 1] = _fps;
			fpsSum /= _fpsDataBuffer.length;
			_averageFPS = fpsSum;

		}

		if (_world != null)
		{
			try
			{
				getWorld().save();
			} catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		BufferManager.getInstance().deleteQueuedBuffers();
		BlockManager.getInstance().release();
		TextureStorage.release();
		FontStorage.release();
		Display.destroy();
	}

	private void loadTextures() throws IOException
	{
		TextureStorage.setTexturePack(_configuration.getTexturePack());
		TextureStorage.loadTexture("terrain", "PNG", "terrain.png");
		TextureStorage.loadTexture("particles", "PNG", "particles.png");
		TextureStorage.loadStichedTexture("items", "gui/items.png", "gui/items_custom.png");
		TextureStorage.loadTexture("gui.gui", "PNG", "gui/gui.png");
		TextureStorage.loadTexture("gui.container", "PNG", "gui/container.png");
		TextureStorage.loadTexture("gui.inventory", "PNG", "gui/inventory.png");
		TextureStorage.loadTexture("gui.crafting", "PNG", "gui/crafting.png");
		TextureStorage.loadTexture("environment.clouds", "PNG", "environment/clouds.png");
	}

	private void loadFonts() throws IOException
	{
		FontStorage.loadFont("Monospaced_20", "novamono.ttf", 22);
		FontStorage.loadFont("InventoryAmount", "visitor1.ttf", 14);
	}

	private void loadItems()
	{
		try
		{
			ItemXMLLoader.parseXML();
		} catch (Exception ex)
		{
			Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	private void loadBlocks()
	{
		try
		{
			BlockXMLLoader.parseXML();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setWorld(World world)
	{
		this._world = world;
	}

	public World getWorld()
	{
		return this._world;
	}

	private void loadConfiguration() throws IOException
	{
		this._configuration = new Configuration();
		this._configuration.loadFromFile("conf/conf.txt");
		this._configuration.setMaximumPlayerEditingDistance(5.0f);
	}

	public Configuration getConfiguration()
	{
		return _configuration;
	}

	/**
	 * Returns the sleeptime in milliseconds for the last frame.
	 * 
	 * @return
	 */
	public int getSleepTime()
	{
		return _sleepTimeMillis;
	}

	public float getAverageFPS()
	{
		return _averageFPS;
	}

	public static final int FILE_BASE_APPLICATION = 0x01;
	public static final int FILE_BASE_USER_DATA = 0x02;

	public File getRelativeFile(int fileBase, String string)
	{
		if (string.contains("${world}"))
		{
			string = string.replace("${world}", getWorld().getWorldName());
		}
		switch (fileBase)
		{
		case FILE_BASE_USER_DATA:
			return new File(getUserDataFolder(), string);
		case FILE_BASE_APPLICATION:
		default:
			return new File(string);
		}
	}

	private File getUserHome()
	{
		return new File(System.getProperty("user.home"));
	}

	private File getUserDataFolder()
	{
		String os = System.getProperty("os.name").toLowerCase();
		File f = null;
		if (os.contains("mac"))
		{
			f = new File(getUserHome(), "Library/Application Support/CraftTopia");
		} else if (os.contains("inux") || os.contains("nix"))
		{
			f = new File(getUserHome(), ".CraftTopia");
		} else if (os.contains("win"))
		{
			f = new File(new File(System.getenv("APPDATA")), ".CraftTopia");
		}
		f.mkdir();
		return f;
	}
}
