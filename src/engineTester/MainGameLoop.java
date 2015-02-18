package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.ObJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import entities.Camera;
import entities.Entity;
import entities.Light;

public class MainGameLoop {

    public static void main(String[] args) {

	DisplayManager.createDisplay();
	Loader loader = new Loader();

	TexturedModel tree = new TexturedModel(ObJLoader.loadObjModel("tree",
		loader), new ModelTexture(loader.loadTexture("tree")));
	TexturedModel grass = new TexturedModel(ObJLoader.loadObjModel(
		"grassModel", loader), new ModelTexture(
		loader.loadTexture("grassTexture")));
	TexturedModel fern = new TexturedModel(ObJLoader.loadObjModel("fern",
		loader), new ModelTexture(loader.loadTexture("fern")));

	List<Entity> entities = new ArrayList<Entity>();
	Random random = new Random();
	for (int i = 0; i < 500; i++) {
	    entities.add(new Entity(tree, new Vector3f(
		    random.nextFloat() * 800 - 400, 0, random.nextFloat()
			    * -600), 0, 0, 0, 3));
	    entities.add(new Entity(grass, new Vector3f(
		    random.nextFloat() * 800 - 400, 0, random.nextFloat()
			    * -600), 0, 0, 0, 1));
	    entities.add(new Entity(fern, new Vector3f(
		    random.nextFloat() * 800 - 400, 0, random.nextFloat()
			    * -600), 0, 0, 0, 0.6f));
	}

	// Light source position and color
	Light light = new Light(new Vector3f(20000, 20000, 2000), new Vector3f(
		1, 1, 1));

	Terrain terrain = new Terrain(0, 0, loader, new ModelTexture(
		loader.loadTexture("grass")));

	Camera camera = new Camera();

	MasterRenderer renderer = new MasterRenderer();
	while (!Display.isCloseRequested()) {
	    // entity.increasePostion(0,0,0);
	    // entity.increaseRotation(0,1,0);
	    camera.move();
	    // for (Entity cube : allCubes {
	    // renderer.processEntity(cube);
	    // }

	    renderer.processTerrain(terrain);
	    for (Entity entity : entities) {
		renderer.processEntity(entity);
	    }

	    renderer.render(light, camera);
	    DisplayManager.updateDisplay();
	}

	renderer.cleanUp();
	loader.cleanUP();
	DisplayManager.closeDisplay();

    }
}
