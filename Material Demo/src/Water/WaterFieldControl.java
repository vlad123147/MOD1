/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Water;

import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Box;
import com.jme3.terrain.Terrain;
import java.util.Random;

/**
 *
 * @author dprovorn
 */
public class WaterFieldControl extends AbstractControl{
    
    private int width;
    private int height;
    private float zmax;
    public float delta = 0;
    private float current_max = 1;
    WaterCell waterField[][];
    Node water;
    Material material;
    Spatial spatial;
    Terrain terrain;
    Random rand = new Random(3);
    private static int q = 0;
    Mesh m = new Box(1f, 1f, 1f);

    public WaterFieldControl(int width, int height, Material material, Terrain terrain, float zmax) {
        this.width = width;
        this.height = height;
        this.material = material;
        this.waterField = new WaterCell[width][height];
        this.terrain = terrain;
        this.zmax = zmax;
    }

   
    
    @Override
    public void setSpatial(Spatial spatial)
    {
        this.spatial = spatial;
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                WaterCell cell = new WaterCell();
                cell.terrainHeitgt = terrain.getHeight(new Vector2f(x * 10.25f, y * 10.25f));
                waterField[x][y] = cell;
            }
        }
    }

    @Override
    protected void controlUpdate(float tpf) {
        updateCells();
        createGeometry(spatial);

    }
    
    public  void    resetCells(){
        WaterCell cell;
        for(int x = 0; x < width; x++){
            for(int y = 0; y < height; y++){
                cell = waterField[x][y];
                cell.setAmount(0);
                cell.incomingAmount = 0;
            }
        }
        delta = 0;
        current_max = 1;
    }
    
    void    updateCells()
    {
        for(int i = 0 ; i < width ; i++)
            waterField[i][0].setAmount(delta);
        if (q == 0)
        {
            delta += 0.04f;
        }
        q = 0;
        int count = 0;
        WaterCell cell;
        WaterCell neighborCell;
        float adjustAmount;
        float cellAmount;
        int direction;
        for (int i = 0 ;i < waterField.length; i++)
        {
            for (int j = 0; j < waterField[i].length;j++)
            {
                cell = waterField[i][j];
                cellAmount = cell.getAmount();
                if(cellAmount > 0f){
                    direction = cell.direction;
                    for(int x = 0; x < 8; x++){
                        int[] dir = CellUtil.getDirection((direction + x) % 8);
                        if (dir[0] + i >= width || dir[1] + j >= height || dir[0] + i < 0 || dir[1] + j < 0 )
                            continue;
                        neighborCell = waterField[i+dir[0]][j + dir[1]];
                        if(cell.getAmount() > 0.5f){
                            adjustAmount = neighborCell.compareCells(cell);
                            if(adjustAmount > 0){
                                if (waterField[i+dir[0]][j + dir[1]].getAmount() == 0)
                                {
                                    count++;
                                    if (count < 1)
                                        q = 1;
                                }
                                neighborCell.direction = (CellUtil.getDirection(dir[0], dir[1]));
                            }
                        }
                        else
                            break;
                    }
                }
            }
        }

        for (int i = 0 ;i < waterField.length; i++)
        {
            for (int j = 0; j < waterField[i].length;j++)
            {
                cell = waterField[i][j];
                cell.adjustAmount(cell.incomingAmount);
                if (cell.getAmount() > 0)
                {
                    if (q == 0 ){
                        if (1f + delta > cell.terrainHeitgt)
                            cell.setAmount(1f + delta - cell.terrainHeitgt);
                        else
                            cell.setAmount(1f);   
                    }
                    if (cell.terrainHeitgt > current_max)
                        current_max = cell.terrainHeitgt;
                }
                cell.incomingAmount = 0;
            }
        }
    }
    
    Geometry getGeometry(float amount){
        Geometry res = null;
        if (amount > 0){
            res = new Geometry("WaterCell", m);
            res.setLocalScale(10f, current_max, 10f);
        }
        return res;
    }
    
    void    createGeometry(Spatial s){
        Node par  = (Node)s;
        if (water != null )
           par.detachChildNamed("Water");
        water = new Node("Water");
        WaterCell cell = null;
        Geometry geometry = null;
        for (int i = 0 ;i < waterField.length; i++)
        {
            for (int j = 0; j < waterField[i].length;j++)
            {
                cell = waterField[i][j];
                geometry = getGeometry(cell.getAmount());
                if (geometry != null)
                {
                    geometry.setLocalTranslation(i * 10.25f, 1 ,j * 10.25f);
                    water.attachChild(geometry);
                }
   
            }
        }
        //water = GeometryBatchFactory.optimize(water, false);
        water.setMaterial(material);
        ((Node)s).attachChild(water);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
       
    }
}
