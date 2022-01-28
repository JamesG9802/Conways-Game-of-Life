package scenes;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import engine.Engine;
import window.Drawable;
import window.Screen;
/*
 * This scene is where the instructions are shown to the player.
 */
public class Scene_Tutorial extends Scene {

	@Override
	public void screenInit() {
		screen = new Screen(new Drawable()
		{
			Dimension screenSize = Engine.engine.window.getSize();	//Screen Size
			@Override
			public void update() {
				if(Engine.engine.input1.isReleased(1))
					SceneManager.setScene(SceneKey.Scene_Game);
				
			}

			@Override
			public void draw(Graphics g) {
				
				
				drawString_MultiLine(g,"Cellular Automata Simulator",
						(int)(screenSize.width*.1),
						(int)(screenSize.height*.1),
						(int)(screenSize.width*.8),
						defaultFont.deriveFont(48f).deriveFont(Font.BOLD));
				drawString_MultiLine(g,"The simulation is played out on a two-dimensional grid where each cell is alive or dead. \n\n"
						+ "During each simulation step, an alive cell can die to underpopulation and overpopulation. \n\n"
						+ "A dead cell can also become alive due to reproduction. \n\n"
						+ "You can click on the cells to toggle their states between alive and dead. \n\n"
						+ "Click the screen to access the simulation. ",
						(int)(screenSize.width*.1),
						(int)(screenSize.height*.23),
						(int)(screenSize.width*.8),
						defaultFont);
			}
			
		});
	}

}
