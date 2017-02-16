package mygame;

import Terrain.Calculate;
import Terrain.HillGenerator;
import Terrain.Reader;
import Water.WaterFieldControl;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.audio.AudioNode;
import com.jme3.audio.LowPassFilter;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.DepthOfFieldFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.FogFilter;
import com.jme3.post.filters.LightScatteringFilter;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.HillHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.util.SkyFactory;
import com.jme3.water.SimpleWaterProcessor;
import com.jme3.water.WaterFilter;
import java.util.ArrayList;
import java.util.List;
import other.myWater;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main1deRezerva extends SimpleApplication {
    private FilterPostProcessor fpp;
    private Node reflectedScene;
   private Vector3f lightDir =
 new Vector3f(-4.9f, -1.3f, 5.9f);
    private ParticleEmitter points;
    private float gravity = 200f;
    private float radius = 2000f;
    private float height = 300f;
    private int particlesPerSec = 300;
    private int weather = 1;
    private myWater water;
    /*public static void main(String[] args) {
        Main1deRezerva app = new Main1deRezerva();
        app.start();
    }*/
    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(125);

        Material mat = new Material(assetManager, "Materials/Unshaded_my.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Materials/DsAnez.png"));
        
        AbstractHeightMap heightmap = null;
        try {
             ArrayList<Vector3f> list = Reader.readPoints("resources/demo3.mod1");
             System.out.println(list);
         ArrayList<Vector3f> gotList = Calculate.surface(list);
         float zmax = Calculate.ZMAX;
         heightmap = new HillGenerator(2049, gotList, 9, 30, 3, zmax);
        } catch (Exception e) { e.printStackTrace(); }
        TerrainQuad terrain = new TerrainQuad("terrain", 65, 1025, heightmap.getHeightMap());
         List<Camera> cameras = new ArrayList<Camera>();
        cameras.add(getCamera());
        terrain.setLocalTranslation(new Vector3f(512.5f, 0, 512.5f));
        terrain.setLocked(false);
        terrain.setShadowMode(RenderQueue.ShadowMode.Receive);
        terrain.setMaterial(mat);
    
         Spatial sky = SkyFactory.createSky(assetManager, "Scenes/Beach/FullskiesSunset0068.dds", false);
        sky.setLocalScale(350);
        rootNode.attachChild(sky);
        rootNode.attachChild(terrain);
        cam.setFrustumFar(4000);
        cam.setLocation(new Vector3f(0, 512, 512));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        
//        fpp = (FilterPostProcessor) assetManager.loadAsset("Models/Water.j3f");
//        viewPort.addProcessor(fpp);
//        DirectionalLight light = new DirectionalLight(lightDir);
//        rootNode.addLight(light);
        Node mainScene = new Node("Scene");
        rootNode.attachChild(mainScene);
        fpp = new FilterPostProcessor(assetManager);
        water = new myWater(rootNode, lightDir);
        water.setCenter(new Vector3f(512.5f, 100f, 512.5f));
        water.setRadius(512);
        water.setWaterHeight(10f);
        fpp.addFilter(water);
        viewPort.addProcessor(fpp);
    }
    
        private float time = 0.0f;
        private float waterHeight = 0.0f;
        private float initialWaterHeight = 10f;//0.8f;
        private boolean uw = false;
    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        super.simpleUpdate(tpf);
        //     box.updateGeometricState();
        time += tpf;
        if(initialWaterHeight + waterHeight < 200)
        {
            waterHeight+=0.2f;
            water.setWaterHeight(initialWaterHeight + waterHeight);
        }
   //     lightSphere.setLocalTranslation(lightPos);
   //     waterProcessor.setLightPosition(lightPos);
         points = new ParticleEmitter(
            "rainPoints", ParticleMesh.Type.Triangle, particlesPerSec * weather);
          points.setShape(new EmitterSphereShape(Vector3f.ZERO, radius));
        points.setLocalTranslation(new Vector3f(0f, height, 0f));
        points.getParticleInfluencer().setInitialVelocity(new Vector3f(0.0f, -1.0f, 0.0f));
        points.getParticleInfluencer().setVelocityVariation(0.1f);
        points.setImagesX(1);
        points.setImagesY(1);
        points.setGravity(0, gravity * weather, 0);
        points.setLowLife(2);
        points.setHighLife(5);
        points.setStartSize(2f);
        points.setEndSize(1f);
        points.setStartColor(new ColorRGBA(0.0f, 0.0f, 1.0f, 0.8f));
        points.setEndColor(new ColorRGBA(0.8f, 0.8f, 1.0f, 0.6f));
        points.setFacingVelocity(false);
        points.setParticlesPerSec(particlesPerSec * weather);
        points.setRotateSpeed(0.0f);
        points.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Particle.j3md");
    // "raindrop.png" is just "spark.png", rotated by 90 degrees.
    mat.setTexture(
            "Texture", assetManager.loadTexture(
            "Textures/rain2.png"));
    points.setMaterial(mat);
    points.setQueueBucket(RenderQueue.Bucket.Transparent);

    rootNode.attachChild(points);
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
