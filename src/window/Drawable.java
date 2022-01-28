package window;
import java.awt.Graphics;
/*
 * This interface stores the format for drawing to the screen.
 */
public interface Drawable {
	
	public void update();
	public void draw(Graphics g);
}
