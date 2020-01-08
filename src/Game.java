import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

import javax.swing.JFrame;

public class Game {
    
	private final JFrame window = new JFrame();
	private final ScreenFactory screenFactory;
	private final GameThread gameThread;
	private final KeyboardListener keyboardListener;
	private final MousePadListener mousepadListener;
	
	public Game(int windowX, int WindowY, String Title) {
		window.setSize(windowX, WindowY);
		window.setResizable(false);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setFocusable(true);
		window.setLocationRelativeTo(null);
		window.setTitle(Title);
		window.setVisible(true);
		screenFactory = new ScreenFactory(this);
		gameThread = new GameThread(this);
		keyboardListener = new KeyboardListener();
		mousepadListener = new MousePadListener();
		
		window.add(gameThread);
		window.addKeyListener(keyboardListener);
		window.addMouseListener(mousepadListener);
		
		new Thread(gameThread).start();
	}
	
	public MousePadListener getMouseListener() {
		return mousepadListener;
	}
	
	public KeyboardListener getKeyboardListener() {
		return keyboardListener;
	}
	
	public ScreenFactory getScreenFactory() {
		return screenFactory;
	}
	
	public JFrame getWindow() {
		return window;
	}
}
