package main;

import engine.Engine;
import scenes.SceneKey;
import scenes.SceneManager;
/*
 * This class is the starting point of the program and initializes the engine.
 */
public class Main_Class {

	public static void main(String a[])
	{
		SceneManager.init();	//Sets up the Scene Manager
		Engine.engine = new Engine("Template");
		SceneManager.setScene(SceneKey.Scene_Tutorial);
	}
}
