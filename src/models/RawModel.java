// 3d Models stored in memory

//VAO.VErtex Array Object: Object in which you can store data about a 3d Model
//consists of around 16 Attribute lists - each one can hold different sets of data.

//VBO.Vertex Buffer Object: Data, could be anything.
//VBo's are stored in the VAO Attribute lists.
package models;

public class RawModel {

    private int vaoID;
    private int vertexCount; //How many vertices there are in that model.

    public RawModel(int vaoID, int vertexCount) {
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
    }

    public int getVaoID() {
        return vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }

}
