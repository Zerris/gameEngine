//Loads the 3d models into memory
//Stores positional data about the model in a VAO
package renderEngine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import models.RawModel;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
//import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class Loader {

    // List to keep track of the VAOS and VBOS that we create so we can delete
    // them when we close the game. (memory management).
    private List<Integer> vaos = new ArrayList<Integer>();
    private List<Integer> vbos = new ArrayList<Integer>();
    private List<Integer> textures = new ArrayList<Integer>();

    // Takes in positions about the models vertices, loads data into VAO, return
    // the info about the VAO as a RawModel object.
    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
	int vaoID = createVAO();// Creates VAO and stores ID in vaoID.
	bindIndicesBuffer(indices);
	storeDataInAttributeList(0, 3, positions);// stores positional data in
						  // attribute list
	storeDataInAttributeList(1, 2, textureCoords);// texture coordinates
	storeDataInAttributeList(2, 3, normals);
	unbindVAO();
	return new RawModel(vaoID, indices.length); // Each vertex has 3 floats.
						    // x,y,z. Hence divide by 3.
    }

    public RawModel loadToVAO(float[] positions) {
	int vaoID = createVAO();
	this.storeDataInAttributeList(0, 2, positions);
	unbindVAO();
	return new RawModel(vaoID, positions.length / 2);
    }

    // Loads texture, returns textureID.
    public int loadTexture(String fileName) {
	Texture texture = null;
	try {
	    texture = TextureLoader.getTexture("PNG", new FileInputStream("res/" + fileName + ".png"));
	    // GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
	    // GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
	    // GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	int textureID = texture.getTextureID();
	textures.add(textureID);
	return textureID;
    }

    // Deletes the VAOs and VBOs in the lists.
    public void cleanUP() {
	for (int vao : vaos) {
	    GL30.glDeleteVertexArrays(vao);
	}
	for (int vbo : vbos) {
	    GL15.glDeleteBuffers(vbo);
	}
	for (int texture : textures) {
	    GL11.glDeleteTextures(texture);
	}
    }

    // Return ID of created VAO
    private int createVAO() {
	// Creates the empty VAO
	int vaoID = GL30.glGenVertexArrays();
	// Adds vaoID to vao list
	vaos.add(vaoID);
	// Activates the VAO
	GL30.glBindVertexArray(vaoID);
	return vaoID;
    }

    private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
	// Creates empty VBO
	int vboID = GL15.glGenBuffers();
	// Adds vboID to vbo list.
	vbos.add(vboID);
	// Activates VBO. Can now store data into it.
	GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
	// Converts the data into a float buffer.
	FloatBuffer buffer = storeDataInFloatBuffer(data);
	// Stores data into the VBO.
	GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	// Puts the VBO into the VAO
	GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
	// Unbinds the VBO
	GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

    }

    // When you finish using a VAO you have to unbind it.
    private void unbindVAO() {
	// Unbinds the currently bound VAO
	GL30.glBindVertexArray(0);
    }

    // Loads up the indices buffer = binds it to the VAO that we want to render.
    private void bindIndicesBuffer(int[] indices) {
	// Creates empty VBO
	int vboID = GL15.glGenBuffers();
	// Adds to VBO list
	vbos.add(vboID);
	// Binds the VBO
	GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
	// Converts array of indices into an IntBuffer
	IntBuffer buffer = storeDataInIntBuffer(indices);
	// Stores the IntBuffer into the VBO.
	GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    // Converts the above array of indices into an IntBuffer.
    private IntBuffer storeDataInIntBuffer(int[] data) {
	// Creates empty IntBuffer
	IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
	// Puts data into the IntBuffer
	buffer.put(data);
	// Flips data so it can be read from.
	buffer.flip();
	return buffer;
    }

    // Data has to be stored in a VBO as a FloatBuffer
    private FloatBuffer storeDataInFloatBuffer(float[] data) {
	// Creates an empty FloatBuffer.
	FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
	// Puts data into the FloatBuffer.
	buffer.put(data);
	// Prepares data to be read from.
	buffer.flip();
	return buffer;
    }

}
