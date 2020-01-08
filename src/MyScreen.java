import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

public class MyScreen extends Screen {

	private int x = 0, y = 0;
	
	public MyScreen(ScreenFactory screenFactory) {
		super(screenFactory);
		
	}

	@Override
	public void onCreate() {
		
		System.out.println("Creating!");
	}

	@Override
	public void onUptade() {
		if(this.getScreenFactory().getGame().getKeyboardListener().isKeyPressed(KeyEvent.VK_A))
			x-= 2;
		if(this.getScreenFactory().getGame().getKeyboardListener().isKeyPressed(KeyEvent.VK_D))
			x+= 2;
		if(this.getScreenFactory().getGame().getKeyboardListener().isKeyPressed(KeyEvent.VK_W))
			y-= 2;
		if(this.getScreenFactory().getGame().getKeyboardListener().isKeyPressed(KeyEvent.VK_S))
			y+= 2;
		
		
		if (y >= 600 - 64)
			y = 600 - 64;
		if (y <= 0)
			y = 0;
		if (x >= 800 + 64) 
			x= -64;
		
		x++;
		
	}

	@Override
	public void onDraw(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		g2d.fillRect(300, 300, 64, 64);
	}

}
