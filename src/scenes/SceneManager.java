package scenes;

import java.util.HashMap;

/*
 * This class allows for the accessing of scenes.
 */
public class SceneManager {
//VARIABLES
	
	public static HashMap<SceneKey,Scene> screenMap;	//A list of all the scenes

//INITIALIZATION
	public static void init()	//Initializes all Scenes into the HashMap
	{
		screenMap = new HashMap<SceneKey,Scene>();
		screenMap.put(SceneKey.Scene_Game, new Scene_Game());
		screenMap.put(SceneKey.Scene_Tutorial, new Scene_Tutorial());
	}
//METHODS
	public static void setScene(SceneKey sk)	//Given the SceneKey, this method will load the appropriate scene into Engine
	{
		screenMap.get(sk).loadScene();
	}
}
