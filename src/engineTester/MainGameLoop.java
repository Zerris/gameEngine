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

	ModelData treeData = OBJFileLoader.loadOBJ("tree");
	RawModel treeModel = loader.loadToVAO(treeData.getVertices(), treeData.getTextureCoords(), treeData.getNormals(), treeData.getIndices());
	TexturedModel tree = new TexturedModel(treeModel, new ModelTexture(loader.loadTexture("tree")));

	ModelData lpTreeData = OBJFileLoader.loadOBJ("lowPolyTree");
	RawModel lpTreeModel = loader.loadToVAO(lpTreeData.getVertices(), lpTreeData.getTextureCoords(), lpTreeData.getNormals(),
		lpTreeData.getIndices());
	TexturedModel lpTree = new TexturedModel(lpTreeModel, new ModelTexture(loader.loadTexture("lowPolyTree")));

	ModelData grassData = OBJFileLoader.loadOBJ("grassModel");
	RawModel grassModel = loader.loadToVAO(grassData.getVertices(), grassData.getTextureCoords(), grassData.getNormals(), grassData.getIndices());
	TexturedModel grass = new TexturedModel(grassModel, new ModelTexture(loader.loadTexture("grassTexture")));

	ModelTexture fernTextureAtlas = new ModelTexture(loader.loadTexture("fern"));
	fernTextureAtlas.setNumberOfRows(2);

	TexturedModel fern = new TexturedModel(ObJLoader.loadObjModel("fern", loader), fernTextureAtlas);

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

		entities.add(new Entity(tree, new Vector3f(x, y, z), 0, random.nextFloat(), 0, 3));

		x = random.nextFloat() * 800 - 400;
		z = random.nextFloat() * -600;
		y = terrain.getHeightOfTerrain(x, z);

		entities.add(new Entity(lpTree, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, random.nextFloat() * 0.1f + 0.6f));
	    }
	    if (i % 8 == 0) {
		float x = random.nextFloat() * 800 - 400;
		float z = random.nextFloat() * -600;
		float y = terrain.getHeightOfTerrain(x, z);
		entities.add(new Entity(grass, new Vector3f(x, y, x), 0, random.nextFloat(), 0, 1));
		grass.getTexture().setHasTransparency(true);
		grass.getTexture().setUseFakeLighting(true);
		grass.getTexture().setHasTransparency(true);
	    }
	}

	// Light source position and color
	Light light = new Light(new Vector3f(20000, 20000, 2000), new Vector3f(1, 1, 1));

	MasterRenderer renderer = new MasterRenderer();

	RawModel bunnyModel = ObJLoader.loadObjModel("person", loader);
	TexturedModel stanfordBunny = new TexturedModel(bunnyModel, new ModelTexture(
		loader.loadTexture("playerTexture")));

	Player player = new Player(stanfordBunny, new Vector3f(100, 5, -150), 0, 180, 0, 0.6f);
	Camera camera = new Camera(player);

	List<GuiTexture> guis = new ArrayList<GuiTexture>();
	GuiTexture gui = new GuiTexture(loader.loadTexture("socuwan"), new Vector2f(0.5f, 0.5f), new Vector2f(0.25f, 0.25f));
	GuiTexture gui2 = new GuiTexture(loader.loadTexture("thinmatrix"), new Vector2f(0.30f, 0.58f), new Vector2f(0.4f, 0.4f));
	guis.add(gui);
	guis.add(gui2);
	
	GuiRenderer guiRenderer = new GuiRenderer(loader);

	while (!Display.isCloseRequested()) {
	    player.move(terrain);
	    camera.move();
	    renderer.processEntity(player);
	    renderer.processTerrain(terrain);
	    for (Entity entity : entities) {
		renderer.processEntity(entity);
	    }
	    renderer.render(light, camera);
	    guiRenderer.render(guis);
	    DisplayManager.updateDisplay();
	}

	guiRenderer.cleanUp();
	renderer.cleanUp();
	loader.cleanUP();
	DisplayManager.closeDisplay();

    }
}
