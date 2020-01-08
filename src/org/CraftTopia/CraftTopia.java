package org.CraftTopia;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JOptionPane;

import org.CraftTopia.game.Game;
import org.CraftTopia.utilities.SmartRandom;
import org.CraftTopia.world.World;
import org.CraftTopia.world.characters.Player;


public class CraftTopia {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception
	{
//		test();

		loadNativeLibs();
		initUncaughtExceptionHandler();

		/* Initialize the Game */
		Game game = Game.getInstance();
		game.init();

		/* Construct a new World */
		World world = new World("world_1");
		game.setWorld(world);
		world.getWorldProvider().load();

		/* Load the Player */
		Player player = new Player(world.getWorldProvider().getSpawnPoint());
		player.load();

		world.setPlayer(player);

		/* Start the Game */
		game.startGameLoop();
	}

	private static void initUncaughtExceptionHandler()
	{
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
		{

			@Override
			public void uncaughtException(Thread t, Throwable e)
			{
				try
				{
					e.printStackTrace();

					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					e.printStackTrace(new PrintStream(baos, true, "UTF-8"));

					JOptionPane.showMessageDialog(null, t.getName() + ": " + baos.toString("UTF-8"));
				} catch (Exception e2)
				{
					e2.printStackTrace();
				}

			}
		});
	}

	private static void loadNativeLibs() throws Exception
	{
		if (System.getProperty("os.name").equals("Mac OS X"))
		{
			addLibraryPath("natives/macosx");
		} else if (System.getProperty("os.name").equals("Linux"))
		{
			addLibraryPath("natives/linux");
		} else
		{
			addLibraryPath("natives/windows");
		}
	}

	private static void addLibraryPath(String s) throws Exception
	{
		final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
		usrPathsField.setAccessible(true);

		final String[] paths = (String[]) usrPathsField.get(null);

		for (String path : paths)
		{
			if (path.equals(s))
			{
				return;
			}
		}

		final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
		newPaths[newPaths.length - 1] = s;
		usrPathsField.set(null, newPaths);
	}
	
	public static void test()
	{
		SmartRandom random = new SmartRandom(new Random());
		
		int CHUNK_SIZE_HORIZONTAL = 16;
		
		long start = System.nanoTime();
		int relativeX = random.randomInt(0, 12);
		int relativeZ = random.randomInt(0, 12);
		System.out.println(((relativeX | relativeZ) & 0xFFFFFFF0) == 0);

		for (long i = 0; i < 90000000000l; ++i)
		{
//			relativeX = random.randomInt(-12800, 12800);
//			relativeZ = random.randomInt(-12800, 12800);
//			boolean correct = (0 <= relativeX && relativeX < CHUNK_SIZE_HORIZONTAL && 0 <= relativeZ && relativeZ< CHUNK_SIZE_HORIZONTAL);
		
//			boolean experiment = (((relativeX | relativeZ) & 0xFFFFFFF0) == 0);
			
//			if (correct != experiment)
//			{
//				System.out.println("Wrong for: " + relativeX + ", " + relativeZ);
//			}
		
		}
		long time = System.nanoTime() - start;
		System.out.println(time / 1e9f);
		
		System.exit(0);
	}

}
