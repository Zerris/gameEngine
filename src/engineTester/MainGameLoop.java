package engineTester;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;

import org.lwjgl.opengl.Display;
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

public class MainGameLoop {

    public static void main(String[] args) {

	DisplayManager.createDisplay();
	Loader loader = new Loader();

	// ***********TERRAIN TEXTURE STUFF**********
	TerrainTexture backgroundTexture = new TerrainTexture(
		loader.loadTexture("grassy"));
	TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("dirt"));
	TerrainTexture gTexture = new TerrainTexture(
		loader.loadTexture("pinkflowers"));
	TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));

	TerrainTexturePack texturePack = new TerrainTexturePack(
		backgroundTexture, rTexture, gTexture, bTexture);
	TerrainTexture blendMap = new TerrainTexture(
		loader.loadTexture("blendMap"));

	ModelData treeData = OBJFileLoader.loadOBJ("tree");
	ModelData lpTreeData = OBJFileLoader.loadOBJ("lowPolyTree");
	ModelData grassData = OBJFileLoader.loadOBJ("grassModel");
	ModelData fernData = OBJFileLoader.loadOBJ("fern");

	RawModel treeModel = loader.loadToVAO(treeData.getVertices(),
		treeData.getTextureCoords(), treeData.getNormals(),
		treeData.getIndices());
	RawModel lpTreeModel = loader.loadToVAO(lpTreeData.getVertices(),
		lpTreeData.getTextureCoords(), lpTreeData.getNormals(),
		lpTreeData.getIndices());
	RawModel grassModel = loader.loadToVAO(grassData.getVertices(),
		grassData.getTextureCoords(), grassData.getNormals(),
		grassData.getIndices());
	RawModel fernModel = loader.loadToVAO(fernData.getVertices(),
		fernData.getTextureCoords(), fernData.getNormals(),
		fernData.getIndices());
	
	TexturedModel tree = new TexturedModel(treeModel, new ModelTexture(
		loader.loadTexture("tree")));
	TexturedModel lpTree = new TexturedModel(lpTreeModel, new ModelTexture(
		loader.loadTexture("lowPolyTree")));
	TexturedModel grass = new TexturedModel(grassModel, new ModelTexture(
		loader.loadTexture("grassTexture")));
	TexturedModel fern = new TexturedModel(fernModel, new ModelTexture(
		loader.loadTexture("fern")));


	List<Entity> entities = new ArrayList<Entity>();
	Random random = new Random();
	for (int i = 0; i < 500; i++) {
	    entities.add(new Entity(tree, new Vector3f(
		    random.nextFloat() * 800 - 400, 0, random.nextFloat()
			    * -600), 0, 0, 0, 3));
	    entities.add(new Entity(lpTree, new Vector3f(
		    random.nextFloat() * 800 - 400, 0, random.nextFloat()
			    * -600), 0, 0, 0, 0.5f));
	    entities.add(new Entity(grass, new Vector3f(
		    random.nextFloat() * 800 - 400, 0, random.nextFloat()
			    * -600), 0, 0, 0, 1));
	    grass.getTexture().setHasTransparency(true);
	    grass.getTexture().setUseFakeLighting(true);
	    entities.add(new Entity(fern, new Vector3f(
		    random.nextFloat() * 800 - 400, 0, random.nextFloat()
			    * -600), 0, 0, 0, 0.6f));
	    grass.getTexture().setHasTransparency(true);
	}

	// Light source position and color
	Light light = new Light(new Vector3f(20000, 20000, 2000), new Vector3f(
		1, 1, 1));

	Terrain terrain = new Terrain(0, -1, loader, texturePack, blendMap);
	Terrain terrain2 = new Terrain(-1, -1, loader, texturePack, blendMap);

	MasterRenderer renderer = new MasterRenderer();

	RawModel bunnyModel = ObJLoader.loadObjModel("person", loader);
	TexturedModel stanfordBunny = new TexturedModel(bunnyModel,
		new ModelTexture(loader.loadTexture("playerTexture")));

	Player player = new Player(stanfordBunny, new Vector3f(100, 0, -50), 0,
		180, 0, 0.6f);
	Camera camera = new Camera(player);

	while (!Display.isCloseRequested()) {
	    camera.move();
	    player.move();
	    renderer.processTerrain(terrain);
	    renderer.processTerrain(terrain2);
	    renderer.processEntity(player);
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
