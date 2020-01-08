
public class MyGame {

	private Game game;
	
	public MyGame() {
		game = new Game(720, 500, "InsiderEngine");
		game.getScreenFactory().showScreen(new MyScreen(game.getScreenFactory()));
	}
	
	public static void main(String[] args) {
		new MyGame();
	}
	
}