import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class GameThread extends JPanel implements Runnable {
    
	private final Game game;
	
	public GameThread(Game game) {
		this.game = game;
		setFocusable(true);
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				if (game.getScreenFactory().getCurrentScreen() != null)
					game.getScreenFactory().getCurrentScreen().onUptade();
				    Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void Paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (game.getScreenFactory().getCurrentScreen() != null)
			game.getScreenFactory().getCurrentScreen().onDraw(g2d);
		repaint();
	}

}
