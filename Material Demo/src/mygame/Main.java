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
import com.jme3.system.AppSettings;
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
public class Main extends SimpleApplication implements ActionListener{
    private FilterPostProcessor fpp;
    private Node reflectedScene;
   private Vector3f lightDir =
 new Vector3f(-4.9f, -1.3f, 5.9f);
    private ParticleEmitter points;
    private float gravity = 300f;
    private float radius = 1000f;
    private float height = 300f;
    private int particlesPerSec = 50;
    private int weather = 1;
    private myWater water;
    private float time = 0.0f;
    private float waterHeight = 0.0f;
    private float initialWaterHeight = -10f;//0.8f;
    private boolean uw = false;
    private Spatial sky;
    private Node heaven;
    private Node rain;
    WaterFieldControl wf;
    Node forControl;
    Geometry geom;
    Boolean isGogo = false;
    private static String arg;
    public static void main(String[] args) {
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Mod1 by Volk_Dan4o & Vlad_Tzari");
        settings.setResolution(1920, 1080);
        arg = args[0];
        Main app = new Main();
        app.setSettings(settings);
        app.setShowSettings(false);
        app.start();
    }
    private float zmax;
    
    @Override
    public void simpleInitApp() {
        InitInput();
        flyCam.setMoveSpeed(125);

        Material mat = new Material(assetManager, "Materials/Unshaded_my.j3md");
        mat.setTexture("ColorMap", assetManager.loadTexture("Materials/newPic2.png"));
        
        AbstractHeightMap heightmap = null;
        try {
            ArrayList<Vector3f> list = Reader.readPoints(arg);
            ArrayList<Vector3f> gotList = Calculate.surface(list);
            zmax = Calculate.ZMAX;
            heightmap = new HillGenerator(2049, gotList, 9, 30, 3, zmax);
        } catch (Exception e) { e.printStackTrace(); }
        TerrainQuad terrain = new TerrainQuad("terrain", 65, 1025, heightmap.getHeightMap());
        terrain.setLocalTranslation(new Vector3f(512.5f, 0, 512.5f));
        terrain.setLocked(false);
        terrain.setShadowMode(RenderQueue.ShadowMode.Receive);
        terrain.setMaterial(mat);
         Texture west, east, north, south, up, down;
        west = assetManager.loadTexture("Scenes/ame_ash/ashcanyon_rt.tga");
        east = assetManager.loadTexture("Scenes/ame_ash/ashcanyon_lf.tga");
        north = assetManager.loadTexture("Scenes/ame_ash/ashcanyon_ft.tga");
        south = assetManager.loadTexture("Scenes/ame_ash/ashcanyon_bk.tga");
        up = assetManager.loadTexture("Scenes/ame_ash/ashcanyon_up.tga");
        down = assetManager.loadTexture("Scenes/ame_ash/ashcanyon_dn.tga");
        sky = (SkyFactory.createSky(assetManager, west, east, north, south, up, down));
        heaven = new Node("Heaven");
        heaven.attachChild(sky);
        rootNode.attachChild(heaven);
        rootNode.attachChild(terrain);
        cam.setFrustumFar(4000);
        cam.setLocation(new Vector3f(0, 512, 512));
        cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
        
        
        Box b = new Box(1, 1, 1);
        geom = new Geometry("Box", b);

        Material mat_wat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat_wat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat_wat);
        geom.setLocalTranslation(512.5f, 0.5f, 512.5f);
        geom.setLocalScale(512.5f, 1, 512.5f);
        rain = new Node("rain");
        
        wf = new WaterFieldControl(100, 100, mat_wat, terrain,zmax);
        wf.setSpatial(rootNode);
        forControl = new Node("Yes");
        forControl.addControl(wf);
    }
    
    public void InitInput()
    {
        inputManager.addMapping("scenario1", new KeyTrigger(KeyInput.KEY_1));
        inputManager.addListener(this, "scenario1");
        inputManager.addMapping("scenario2", new KeyTrigger(KeyInput.KEY_2));
        inputManager.addListener(this, "scenario2");
        inputManager.addMapping("day", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addListener(this, "day");
        inputManager.addMapping("night", new KeyTrigger(KeyInput.KEY_N));
        inputManager.addListener(this, "night");
        inputManager.addMapping("reset", new KeyTrigger(KeyInput.KEY_R));
        inputManager.addListener(this, "reset");
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
        time += tpf;
        if(initialWaterHeight + waterHeight < 260 && isGogo)
        {
            waterHeight+=0.1f;
            if(initialWaterHeight + waterHeight > 1f)
            {
                rootNode.attachChild(geom);
                geom.setLocalScale(510f, initialWaterHeight+waterHeight, 512.5f);
            }
        }
         points = new ParticleEmitter(
            "rainPoints", ParticleMesh.Type.Triangle, particlesPerSec * weather);
          points.setShape(new EmitterSphereShape(Vector3f.ZERO, radius));
        points.setLocalTranslation(new Vector3f(512.5f, height, 512.5f));
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
    mat.setTexture(
            "Texture", assetManager.loadTexture(
            "Textures/rain2.png"));
    points.setMaterial(mat);
    points.setQueueBucket(RenderQueue.Bucket.Transparent);
    if(isGogo)
    {
        rain.attachChild(points);
        rootNode.attachChild(rain);
    }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if(name.equals("day") && isPressed)
        {
            rootNode.attachChild(heaven);
        }
        if(name.equals("night") && isPressed)
        {
            rootNode.detachChildNamed("Heaven");
        }
        if(name.equals("scenario1") && isPressed)
        {
            rootNode.detachChildNamed("rain");
            rootNode.detachChild(geom);
            rootNode.detachChild(rain);
            rootNode.detachChild(forControl);
            waterHeight = 0;
            isGogo = true;
        }
        if(name.equals("reset") && isPressed)
        {
            rootNode.detachChild(points);
            isGogo = false;
            rootNode.detachChildNamed("rain");
            rain.detachAllChildren();
            rootNode.detachChild(geom);
            rootNode.detachChild(forControl);
            waterHeight = 0;
            wf.resetCells();
        }
        if(name.equals("scenario2") && isPressed)
        {
            rootNode.detachChild(points);
            isGogo = false;
            rootNode.detachChildNamed("rain");
            rootNode.detachChild(geom);
            waterHeight = 0;
            wf.resetCells();
            rootNode.attachChild(forControl);
        }
    }
}
