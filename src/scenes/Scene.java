package scenes;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import engine.Engine;
import window.Screen;
/*
 * This class is the template for all the major scenes in the application.
 */
public abstract class Scene {
//VARIABLES
	protected Font defaultFont = new Font("Candara",Font.PLAIN,36);
	public static Screen screen;	//The scene in memory that will be loaded to the Engine's screen.

//METHODS
	public abstract void screenInit();	//Initializes Screens
	public void loadScene()	//Sets the static screen into the Engine
	{	
		screenInit();
		Engine.engine.window.setScreen(screen);
	}
	protected void drawString_Centered(Graphics g, String text,int x, int y, int width, int height, Font font) //Draws a String centered
	{
		FontMetrics fm = g.getFontMetrics(font);
	    g.setFont(font);
	    int newx = x + (width - fm.stringWidth(text)) / 2;
	    int newy = y + ((height - fm.getHeight()) / 2) + fm.getAscent();
	    g.drawString(text, newx, newy);
	}
	protected void drawString_MultiLine(Graphics g, String text, int x, int y, int width, Font font)	//Draws a multi-line String
	{
		FontMetrics fm = g.getFontMetrics(font);
	    g.setFont(font);
	    String temp [] = text.split("\n"); //Creates an array text split by \n
	    for(int i =0; i<temp.length;i++)
	    {
	    	if(fm.getStringBounds(temp[i], g).getWidth() > width)	//If the String is too wide, cut it up
	    	{
	    		String originalString = temp[i];
	    		String cutString = temp[i];
	    		while(fm.getStringBounds(cutString, g).getWidth() > width	//Cuts string until it fits inside the width
	    			|| (cutString.length() !=1 && !cutString.substring(cutString.length()-1).equals(" ")))	//And cut it off at a space to preserve words
	    		{
	    			cutString = cutString.substring(0,cutString.length()-1);
	    		}
	    		if(i == temp.length -1 )	//If at the end of the array
	    		{
	    			temp = new String[temp.length+1];
	    			temp[i+1] = "";
	    		}
	    		//Sets the string to only the cutString
	    		temp[i] = cutString;
	    		//Adding the cut string to the next line
	    		temp[i+1] = originalString.substring(cutString.length()) + temp[i+1];
	    	}
	    	y+= fm.getAscent()+fm.getDescent()+fm.getLeading();
	    	g.drawString(temp[i], x, y);
	    }
	}
}
