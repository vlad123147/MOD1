package other;

import Terrain.Calculate;
import Terrain.HillGenerator;
import Terrain.Reader;
import Water.WaterFieldControl;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.util.SkyFactory;
import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author dprovorn
 */
public class Main extends SimpleApplication{
   /*public static void main(String args[])
    {
        Main main = new Main();
        main.start();
    }
    */
    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(125);

        Material mat = new Material(assetManager, "Materials/Unshaded_my.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Materials/final4.png"));
        
        Material mat_wat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_wat.setColor("Color", ColorRGBA.Blue);
        float zmax = 0;
        AbstractHeightMap heightmap = null;
        try {
            ArrayList<Vector3f> list = Reader.readPoints("resources/demo5.mod1");
            ArrayList<Vector3f> gotList = Calculate.surface(list);
            zmax = Calculate.ZMAX;
            heightmap = new HillGenerator(1025, gotList, 9, 30, 3, zmax);
        } catch (Exception e) { e.printStackTrace(); }
        TerrainQuad terrain = new TerrainQuad("terrain", 65, 1025, heightmap.getHeightMap());
        
    //    terrain.setLocalScale(new Vector3f(5, 5, 5));
        terrain.setLocalTranslation(new Vector3f(512.5f, 0, 512.5f));
        terrain.setLocked(false); // unlock it so we can edit the height

        terrain.setShadowMode(RenderQueue.ShadowMode.Receive);
        
        terrain.setMaterial(mat);
    
        Spatial sky = SkyFactory.createSky(assetManager, "Scenes/Beach/FullskiesSunset0068.dds", false);
        sky.setLocalScale(350);
        rootNode.attachChild(sky);
        rootNode.attachChild(terrain);
        
        WaterFieldControl wf = new WaterFieldControl(100, 100, mat_wat, terrain,zmax);
        wf.setSpatial(rootNode);
        rootNode.addControl(wf);
        
        
        cam.setFrustumFar(4000);
        cam.setLocation(new Vector3f(-189.99348f, 495.7605f, -199.6851f));
        cam.setRotation(new Quaternion(0.20046186f, 0.37389073f, -0.083119504f, 0.90172714f));
        
        Node mainScene = new Node("Scene");
        rootNode.attachChild(mainScene);
        
    }
}
