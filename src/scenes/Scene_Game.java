package scenes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import engine.Engine;
import window.Drawable;
import window.Screen;
/*
 * This scene is where the simulation is run.
 */
public class Scene_Game extends Scene{

	@Override
	public void screenInit() {

		screen = new Screen(new Drawable()
		{
			Dimension screenSize = Engine.engine.window.getSize();	//Screen Size
			boolean init = false;
			
			//Bounds for the grid
			Rectangle grid_Bounds = new Rectangle(
					(int) ((screenSize.getWidth() - (int) (screenSize.getHeight()*.85))/2),
					(int) (screenSize.getHeight()*.05),
					(int) (screenSize.getHeight()*.85),
					(int) (screenSize.getHeight()*.85)
			);
			
			//Bounds for the buttons
			Rectangle button_Bounds = new Rectangle(
					(int) (screenSize.getHeight()*.05),
					(int) (screenSize.getHeight()*.05),
					(int) (screenSize.getWidth()*.2),
					(int) (screenSize.getHeight()*.85)
			);
			
			//Bounds for the mouse indicator
			Rectangle mouse_Bounds = new Rectangle(
					(int) (screenSize.getWidth()*.80),
					(int) (screenSize.getHeight()*.05),
					(int) (15*10),
					(int) (26*10)
				);
			
			//Bounds for the color changer
			Rectangle color_Bounds = new Rectangle(
					(int) (screenSize.getWidth()*.75),
					(int) (screenSize.getHeight()*.4),
					(int) (screenSize.getWidth()*.2),
					(int) (screenSize.getWidth()*.2)
				);
					
			//Abstraction of the states of each cell in the grid
			int gridSize = 20;
			boolean [][] grid = new boolean[gridSize][gridSize];
			boolean [][] tempGrid = new boolean[gridSize][gridSize];
			
			//Animation Related Variables

			boolean isRunning = true;
			
			double animationTick = 0;
			double animationSpeed = .1;
			
			//Color
			Color cellColor = new Color(0,0,0);
			//Generic update method that is called 60 times a second in the main loop
			@Override
			public void update() {
				init();
				clickFunctions();
				
				if(simulationReady() && isRunning)
				{
					updateGrid();
				}
			}
			//Private update methods that divide the update loop into manageable parts
			
			//Initializes the grid;
			private void init()
			{
				if(!init)
				{
					grid[5][5] = true;
					grid[5][6] = true;
					grid[5][7] = true;
					grid[4][6] = true;
					grid[6][6] = true;
					init = true;
				}
			}
			//Determines what happens if the mouse clicks on an object
			private void clickFunctions()
			{
				if(Engine.engine.input1.isReleased(1))
				{
					//Grid
					if(inBounds(grid_Bounds.x, grid_Bounds.y, grid_Bounds.width, grid_Bounds.height))
					{
						grid[(int)((Engine.engine.mouseY-grid_Bounds.y)/((double)grid_Bounds.height/grid.length))][(int)((Engine.engine.mouseX-grid_Bounds.x)/((double)grid_Bounds.width/grid[0].length))]
							= !grid[(int)((Engine.engine.mouseY-grid_Bounds.y)/((double)grid_Bounds.height/grid.length))][(int)((Engine.engine.mouseX-grid_Bounds.x)/((double)grid_Bounds.width/grid[0].length))];
					}
					//Buttons
					else if(inBounds(button_Bounds.x, button_Bounds.y, button_Bounds.width, button_Bounds.height/4))
					{
						isRunning = !isRunning;
					}
					else if(inBounds(button_Bounds.x, button_Bounds.y+(3*button_Bounds.height/8), button_Bounds.width, button_Bounds.height/4))
					{
						updateGrid();
					}
					else if(inBounds(button_Bounds.x, button_Bounds.y+2*(3*button_Bounds.height/8), button_Bounds.width, button_Bounds.height/4))
					{
						if(animationSpeed > .3)
						{
							animationSpeed = .1;
						}
						else
						{
							animationSpeed+= .1;
						}
					}
					//Color Changer
					else if(inBounds(color_Bounds.x,color_Bounds.y,color_Bounds.width,color_Bounds.height)) 
					{
						int j = (Engine.engine.mouseX - color_Bounds.x)/(color_Bounds.width/3);
						int i = (Engine.engine.mouseY - color_Bounds.y)/(color_Bounds.height/3);
						i *=3;
						int num = i + j;
						cellColor = keyColor(num);
					}
				}
			}
			//Slows down the animation relative to the actual update loop
			private boolean simulationReady()
			{
				if(animationTick > 1)
				{
					animationTick = 0;
					return true;
				}
				animationTick += animationSpeed;
				return false;
			}
			//If the simulation is running and the animation cycle has finished, the grid is updated.
			private void updateGrid()
			{
				//Resets the temporary grid
				for(int i = 0;i<grid.length;i++)
				{
					for(int j =0;j<grid[i].length;j++)
					{
						tempGrid[i][j] = false;
					}
				}
				//Updates each cell of the grid
				for(int i = 0; i< grid.length;i++)
				{
					for(int j = 0; j<grid[i].length;j++)
					{
						updateCell(i,j);
					}
				}
				//Copies the results from the temporary grid to the original grid
				for(int i = 0;i<grid.length;i++)
				{
					for(int j = 0; j<grid[i].length;j++)
					{
						grid[i][j] = tempGrid[i][j];
					}
				}
			}
			//Determines whether this cell is alive or dead in the next simulation step
			private void updateCell(int row, int col)
			{
				int numAlive = 0;
				
				//Loop to check the states of the neighbors
				for(int i = -1; i< 2;i++)
				{
					for(int j = -1; j < 2;j++)
					{
						if(i == j && i == 0)	//Prevents a comparison to itself
						{
							continue;
						}
						if(i+row < 0 || i + row == grid.length ||
							j+col < 0 || j + col == grid[row].length ||
							!grid[row+i][col+j])	//If it is out of bounds, ignore it
						{
							continue;
						}
						else
						{
							numAlive++;
						}
					}
				}
				//If the cell is alive and there are two or three alive neighbors, the cell continues to live.
				//If the cell is dead and there are three alive neighbors, the cell becomes alive.
				//Otherwise the cell dies or stays dead.
				if((grid[row][col] && (numAlive == 2 || numAlive == 3)) ||
					(!grid[row][col] && numAlive == 3))	
				{
					tempGrid[row][col] = true;
				}
				else
				{
					tempGrid[row][col] = false;
				}
				
			}
			@Override
			//The render method of the program
			public void draw(Graphics g) {
				drawGrid(g);
				drawButtons(g);
				drawMouse(g);
				drawColorChanger(g);
			}	
			//Draws the grid where the simulation is taking place
			private void drawGrid(Graphics g)
			{
				Color tempColor = g.getColor();
				Color c = new Color(255,0,5);
				g.setColor(c);
				
				for(int i = 0; i<grid.length;i++)
				{
					for(int j = 0; j<grid[i].length;j++)
					{
						//Fills in the grid with a solid color if it is alive
						if(grid[i][j])
						{
							g.setColor(cellColor);
							g.fillRect(grid_Bounds.x+j*(int)(grid_Bounds.width/grid[i].length), grid_Bounds.y+i*(int)(grid_Bounds.height/grid.length), (int)(grid_Bounds.width/grid[i].length), (int)(grid_Bounds.height/grid.length));
						}
						else
						{
							g.setColor(c);
							g.drawRect(grid_Bounds.x+j*(int)(grid_Bounds.width/grid[i].length), grid_Bounds.y+i*(int)(grid_Bounds.height/grid.length), (int)(grid_Bounds.width/grid[i].length), (int)(grid_Bounds.height/grid.length));
						}
					}
				}
				g.setColor(tempColor);
			}		
			//Draws the buttons to control the simulation
			private void drawButtons(Graphics g)
			{
				Color c = new Color(0,0,0);
				g.setColor(c);
				for(int i = 0;i<3;i++)
				{
					//Allows the pause button to be highlighted if turned on
					if(i == 0  && !isRunning)
					{
						c = new Color(255,0,5);
						g.setColor(c);
						g.fillRect(button_Bounds.x, button_Bounds.y+i*(3*button_Bounds.height/8), button_Bounds.width, button_Bounds.height/4);
					}
					else
					{
						g.drawRect(button_Bounds.x, button_Bounds.y+i*(3*button_Bounds.height/8), button_Bounds.width, button_Bounds.height/4);
					}
					
					g.setColor(new Color(0,0,0));
					
					String buttonName = "";
					//Case to assign the name for the buttons
					switch(i)
					{
						case 0:
						{
							buttonName = "Pause";
							break;
						}
						case 1:
						{
							buttonName = "Step";
							break;
						}
						case 2:
						{
							buttonName = "Change Speed ("+(int)(animationSpeed*10)+"x)";
							break;
						}
					}
					drawString_Centered(g,buttonName,
							button_Bounds.x,
							button_Bounds.y+i*(3*button_Bounds.height/8),
							button_Bounds.width, 
							button_Bounds.height/4,
							defaultFont);
				}
			}
			//Draws mouse indicator to the screen
			private void drawMouse(Graphics g)
			{
				try
				{
					//Loads spritesheet of the mouse image
					BufferedImage img = ImageIO.read(this.getClass().getResource("/mouse.png"));
					if(Engine.engine.input1.isPressed(1))
					{
						Color c = new Color(cellColor.getRed(),cellColor.getGreen(),cellColor.getBlue(),122);
						g.setColor(c);
						g.fillRect((int)(mouse_Bounds.x+mouse_Bounds.width*2.0/15), (int)(mouse_Bounds.y+mouse_Bounds.height*2.0/26), (int)(mouse_Bounds.width*7.0/15),(int)(mouse_Bounds.height*12.0/26) );
						g.fillRect((int)(mouse_Bounds.x+mouse_Bounds.width*1.0/15), (int)(mouse_Bounds.y+mouse_Bounds.height*3.0/26), (int)(mouse_Bounds.width*1.0/15),(int)(mouse_Bounds.height*12.0/26) );
						g.fillRect((int)(mouse_Bounds.x+mouse_Bounds.width*5.0/15), (int)(mouse_Bounds.y+mouse_Bounds.height*1.0/26), (int)(mouse_Bounds.width*5.0/15),(int)(mouse_Bounds.height*1.0/26) );
						
						g.drawImage(img.getSubimage(15, 0, 15, 26),mouse_Bounds.x, mouse_Bounds.y, mouse_Bounds.width, mouse_Bounds.height, null);
					}
					else
					{
						g.drawImage(img.getSubimage(0, 0, 15, 26),mouse_Bounds.x, mouse_Bounds.y, mouse_Bounds.width, mouse_Bounds.height, null);
					}
				}
				catch (IOException | IllegalArgumentException e)
				{
					System.out.println("Image Error");
				}
				
			}
			//Draws color changer to the screen
			private void drawColorChanger(Graphics g)
			{
				for(int i = 0; i< 3;i++)
				{
					for(int j = 0; j<3;j++)
					{
						int num = i*3 + j;
						Color c = keyColor(num);
						g.setColor(c);
						g.fillRect(color_Bounds.x+(int)(j*color_Bounds.width/3),
								color_Bounds.y+(int)(i*color_Bounds.height/3),
								color_Bounds.width/3,
								color_Bounds.height/3);
					}
				}
				g.setColor(new Color(0,0,0));
			}
			//Helper method for determining whether the mouse is inside an object
			private boolean inBounds(int x, int y, int width, int height)
			{
				return (Engine.engine.mouseX > x &&
						Engine.engine.mouseX < x + width &&
						Engine.engine.mouseY > y &&
						Engine.engine.mouseY < y + height
				);
			}
			//Helper method to convert an integer key into a Color
			private Color keyColor(int num)
			{
				Color c = new Color(0,0,0);
				switch (num)
				{
					case 0:
					{
						c = Color.BLUE;
						break;
					}
					case 1:
					{
						c = Color.CYAN;
						break;
					}
					case 2:
					{
						c = Color.GREEN;
						break;
					}
					case 3:
					{
						c = Color.MAGENTA;
						break;
					}
					case 4:
					{
						c = Color.ORANGE;
						break;
					}
					case 5:
					{
						c = Color.PINK;
						break;
					}
					case 6:
					{
						c = Color.RED;
						break;
					}
					case 7:
					{
						c = Color.YELLOW;
						break;
					}
					case 8:
					{
						c = Color.BLACK;
						break;
					}
				}
				return c;
			}
		});
	}

}
