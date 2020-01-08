import java.util.HashMap;
import java.util.Map;

import org.lwjgl.input.Keyboard*;
import org.lwjgl.input.Mouse;

public class ControlSettings
{

	public static enum KeyboardPreset
	{
		AZERTY, QWERTY
	}


	private static Map<Integer, Boolean> MAP_MOUSE = new HashMap<Integer, Boolean>();
	private static Map<Integer, Integer> MAP_SETTINGS = new HashMap<Integer, Integer>();

	/* Walking */
	public static final int MOVE_FORWARD = 1;
	public static final int MOVE_BACK = 2;
	public static final int MOVE_LEFT = 3;
	public static final int MOVE_RIGHT = 4;

	/* Others */
	public static final int JUMP = 5;
	public static final int CROUCH = 6;
	public static final int INVENTORY = 7;
	public static final int SMASH = 8;
	public static final int BUILD_OR_ACTION = 9;

	/* Debug */
	public static final int TOGGLE_GOD_MODE = 100;
	public static final int TOGGLE_LIGHT_POINT = 101;
	public static final int TOGGLE_OVERLAY = 102;
	public static final int SET_SUN_HIGHT = 103;

	public static void initialize(KeyboardPreset preset)
	{
		switch (preset)
		{
		case AZERTY:
			intializeAzerty();
			break;
		case QWERTY:
			intializeQwerty();
		default:
			intializeQwerty();
			break;
		}
	}

	public static void intializeAzerty()
	{
		/* Walking */
		configureAction(MOVE_FORWARD, false, Keyboard.KEY_Z);
		configureAction(MOVE_BACK, false, Keyboard.KEY_S);
		configureAction(MOVE_LEFT, false, Keyboard.KEY_Q);
		configureAction(MOVE_RIGHT, false, Keyboard.KEY_D);

		/* Others */
		configureAction(JUMP, false, Keyboard.KEY_SPACE);
		configureAction(CROUCH, false, Keyboard.KEY_LSHIFT);
		configureAction(INVENTORY, false, Keyboard.KEY_E);
		configureAction(SMASH, false, Keyboard.KEY_A);
		configureAction(BUILD_OR_ACTION, true, 0);

		/* Debug */
		configureAction(TOGGLE_LIGHT_POINT, false, Keyboard.KEY_L);
		configureAction(TOGGLE_OVERLAY, false, Keyboard.KEY_O);
		configureAction(TOGGLE_GOD_MODE, false, Keyboard.KEY_F);
		configureAction(SET_SUN_HIGHT, false, Keyboard.KEY_P);
	}

	public static void intializeQwerty()
	{
		/* Walking */
		configureAction(MOVE_FORWARD, false, Keyboard.KEY_W);
		configureAction(MOVE_BACK, false, Keyboard.KEY_S);
		configureAction(MOVE_LEFT, false, Keyboard.KEY_A);
		configureAction(MOVE_RIGHT, false, Keyboard.KEY_D);

		/* Others */
		configureAction(JUMP, false, Keyboard.KEY_SPACE);
		configureAction(CROUCH, false, Keyboard.KEY_LSHIFT);
		configureAction(INVENTORY, false, Keyboard.KEY_E);
		configureAction(SMASH, false, Keyboard.KEY_Q);
//		configureAction(SMASH, true, 1);
		configureAction(BUILD_OR_ACTION, true, 0);

		/* Debug */
		configureAction(TOGGLE_LIGHT_POINT, false, Keyboard.KEY_L);
		configureAction(TOGGLE_GOD_MODE, false, Keyboard.KEY_F);
		configureAction(TOGGLE_OVERLAY, false, Keyboard.KEY_O);
		configureAction(SET_SUN_HIGHT, false, Keyboard.KEY_P);
	}

	public static boolean isKeyboard(int action)
	{
		return !MAP_MOUSE.get(action);
	}
	
	public static boolean isMouse(int action)
	{
		return MAP_MOUSE.get(action);
	}

	public static int getButton(int action)
	{
		return MAP_SETTINGS.get(action);
	}

	public static void configureAction(int action, boolean mouse, int button)
	{
		MAP_MOUSE.put(action, mouse);
		MAP_SETTINGS.put(action, button);
	}
	
	public static boolean isCurrentKeyboardEvent(int action)
	{
		boolean keyboard = isKeyboard(action);
		if (!keyboard) return false;
		if (!Keyboard.getEventKeyState()) return false;
		int button = getButton(action);
		return (Keyboard.getEventKey() == button);
	}
	
	public static boolean isCurrentMouseEvent(int action)
	{
		boolean mouse = isMouse(action);
		if (!mouse) return false;
		if (!Mouse.getEventButtonState()) return false;
		int button = getButton(action);
		return (Mouse.getEventButton() == button);
	}
	
	public static boolean isCurrentEvent(int action, boolean mouse)
	{
		if (mouse)
		{
			return isCurrentMouseEvent(action);
		} else
		{
			return isCurrentKeyboardEvent(action);
		}
	}
	
	public static boolean isActionHold(int action)
	{
		boolean mouse = MAP_MOUSE.get(action);
		int button = MAP_SETTINGS.get(action);
		if (mouse)
		{
			return Mouse.isButtonDown(button);
		} else
		{
			return Keyboard.isKeyDown(button);
		}
	}
}