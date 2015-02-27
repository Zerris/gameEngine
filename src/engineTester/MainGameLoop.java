package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.ObJLoader;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import guis.GuiRenderer;
import guis.GuiTexture;

public class MainGameLoop {

    public static void main(String[] args) {

	DisplayManager.createDisplay();
	Loader loader = new Loader();

	// ***********TERRAIN TEXTURE STUFF**********
	TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy"));
	TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
	TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("pinkflowers"));
	TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("mossPath256"));

	TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);
	TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

	// ***********ENTITY TEXTURE STUFF**********

	TexturedModel pine = new TexturedModel(ObJLoader.loadObjModel("pine", loader), new ModelTexture(loader.loadTexture("pine")));
	TexturedModel tree = new TexturedModel(ObJLoader.loadObjModel("lowPolyTree", loader), new ModelTexture(loader.loadTexture("lowPolyTree")));
	TexturedModel grass = new TexturedModel(ObJLoader.loadObjModel("grassModel", loader), new ModelTexture(loader.loadTexture("grassTexture")));

	ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
	fernTextureAtlas.setNumberOfRows(2);
	TexturedModel fern = new TexturedModel(ObJLoader.loadObjModel("fern", loader), fernTextureAtlas);

	TexturedModel lamp = new TexturedModel(ObJLoader.loadObjModel("lamp", loader), new ModelTexture(loader.loadTexture("lamp")));

	Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightMap");

	List<Entity> entities = new ArrayList<Entity>();
	Random random = new Random(676452);
	for (int i = 0; i < 500; i++) {
	    if (i % 2 == 0) {
		float x = random.nextFloat() * 800 - 400;
		float z = random.nextFloat() * -600;
		float y = terrain.getHeightOfTerrain(x, z);

		entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat(), 0, 0.9f));

	    }
	    if (i % 5 == 0) {
		float x = random.nextFloat() * 800 - 400;
		float z = random.nextFloat() * -600;
		float y = terrain.getHeightOfTerrain(x, z);

		entities.add(new Entity(pine, new Vector3f(x, y, z), 0, random.nextFloat(), 0, 3));

		x = random.nextFloat() * 800 - 400;
		z = random.nextFloat() * -600;
		y = terrain.getHeightOfTerrain(x, z);

		entities.add(new Entity(tree, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, random.nextFloat() * 0.1f + 0.6f));
	    }
	    float x = random.nextFloat() * 800 - 400;
	    float z = random.nextFloat() * -600;
	    float y = terrain.getHeightOfTerrain(x, z);
	    
	    entities.add(new Entity(grass, new Vector3f(x, y, x), 0, random.nextFloat() * 360, 0, 1));
	    grass.getTexture().setHasTransparency(true);
	    grass.getTexture().setUseFakeLighting(true);
	    grass.getTexture().setHasTransparency(true);
	}

	// Light source position and color
	List<Light> lights = new ArrayList<Light>();
	lights.add(new Light(new Vector3f(0, 10000, -7000), new Vector3f(0.4f, 0.4f, 0.4f)));
	lights.add(new Light(new Vector3f(185, 10, -293), new Vector3f(2, 0, 0), new Vector3f(1, 0.01f, 0.002f)));
	lights.add(new Light(new Vector3f(370, 17, -300), new Vector3f(0, 2, 2), new Vector3f(1, 0.01f, 0.002f)));
	lights.add(new Light(new Vector3f(293, 7, -305), new Vector3f(2, 2, 0), new Vector3f(1, 0.01f, 0.002f)));

	entities.add(new Entity(lamp, new Vector3f(185, -4.7f, -293), 0, 0, 0, 1));
	entities.add(new Entity(lamp, new Vector3f(370, 4.2f, -300), 0, 0, 0, 1));
	entities.add(new Entity(lamp, new Vector3f(293, -6.8f, -305), 0, 0, 0, 1));

	MasterRenderer renderer = new MasterRenderer(loader);

	RawModel bunnyModel = ObJLoader.loadObjModel("person", loader);
	TexturedModel stanfordBunny = new TexturedModel(bunnyModel, new ModelTexture(loader.loadTexture("playerTexture")));

	Player player = new Player(stanfordBunny, new Vector3f(100, 5, -150), 0, 180, 0, 0.6f);
	Camera camera = new Camera(player);

	List<GuiTexture> guis = new ArrayList<GuiTexture>();
	GuiTexture gui = new GuiTexture(loader.loadTexture("socuwan"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
	guis.add(gui);

	GuiRenderer guiRenderer = new GuiRenderer(loader);

	while (!Display.isCloseRequested()) {
	    player.move(terrain);
	    camera.move();
	    renderer.processEntity(player);
	    renderer.processTerrain(terrain);
	    for (Entity entity : entities) {
		renderer.processEntity(entity);
	    }
	    renderer.render(lights, camera);
	    guiRenderer.render(guis);
	    DisplayManager.updateDisplay();
	}

	guiRenderer.cleanUp();
	renderer.cleanUp();
	loader.cleanUP();
	DisplayManager.closeDisplay();

    }
}
