package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.app.SimpleApplication;
import com.jme3.system.AppSettings;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.HingeJoint;
import com.jme3.cinematic.MotionPath;
import com.jme3.cinematic.events.MotionEvent;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

import com.jme3.input.controls.TouchListener;

import com.jme3.input.event.TouchEvent;

import com.jme3.input.controls.TouchTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.texture.Texture;
import java.util.Random;

/**
 * 
 *
 * @author Manjinder Singh
 */
public class Main extends SimpleApplication implements TouchListener, AnimEventListener {

    private AnimChannel channel;
    private AnimControl control;
    Node player;
    Boolean checkWalk = false;
    private BulletAppState bulletAppState;
    private RigidBodyControl tiger_phy;
    private RigidBodyControl floor_phy;
    private RigidBodyControl wall_phy;
    private RigidBodyControl wall_phy2;
    private RigidBodyControl wall_phy3;
    private RigidBodyControl wall_phy4;
    private RigidBodyControl wall_phy5;
    private RigidBodyControl wall_phy6;
    private RigidBodyControl disc_phy;
    private HingeJoint joint;
    BitmapText txt;
    private MotionPath path;
    private MotionEvent motionControl;
    Geometry disc = new Geometry("Disc", new Cylinder(10, 10, 1, 0.5f, true));
    Geometry ball_geo;
Spatial tiger;
    private RigidBodyControl ball_phy;
    //private RigidBodyControl landscape;
    private boolean left = false, right = false, up = false, down = false;
    private static final Sphere sphere;
    private Node sBoxObject;
    float sc = 7;  // Size of Box object
    Material wall_mat;
    Material stone_mat;
    Material floor_mat;

    static {
        sphere = new Sphere(32, 32, 0.5f, true, false);
        sphere.setTextureMode(TextureMode.Projected);

    }

    public static void main(String[] args) {

        Main app = new Main();
        AppSettings appSetting = new AppSettings(true);
        appSetting.setSettingsDialogImage("Textures/technology.png");
        app.setSettings(appSetting);


        app.start();
    }

    @Override
    public void simpleInitApp() {
        this.setDisplayFps(false);
        this.setDisplayStatView(false);
        this.setShowSettings(false);
        this.inputManager.setCursorVisible(false);
        flyCam.setEnabled(false);
        setDisplayStatView(false);
//    cam.setLocation(new Vector3f(0f, 0f, 0f));
        //  cam.set
//    cam.set
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        sBoxObject = new Node();

        createBox(sc);  // create box object


        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-0.1f, -0.7f, -1.0f));
        rootNode.addLight(sun);
        AmbientLight am = new AmbientLight();
        am.setColor(ColorRGBA.White.mult(2));
        rootNode.addLight(am);


//    viewPort.setBackgroundColor(ColorRGBA.Blue);
        float c1L = cam.getFrustumLeft();
        float c1R = cam.getFrustumNear();
        cam.setFrustumLeft(c1L - ((25 / 110) * c1L));
        cam.setViewPort(0f, 0.5f, 0f, 1f);

        cam.setLocation(new Vector3f(0.10947256f, 1.5760219f, 12f));
        cam.setRotation(new Quaternion(0.0010108891f, 0.99857414f, -0.04928594f, 0.020481428f));

        Camera cam2 = cam.clone();
        float c2R = cam.getFrustumRight();
        cam.setFrustumRight(c2R - ((25 / 110) * c2R));
        cam2.setViewPort(0.5f, 1f, 0f, 1f);
        cam2.setLocation(new Vector3f(-0.10947256f, 1.5760219f, 12f));
        cam2.setRotation(new Quaternion(0.0010108891f, 0.99857414f, -0.04928594f, 0.020481428f));

        ViewPort view2 = renderManager.createMainView("Bottom Left", cam2);
        view2.setClearFlags(true, true, true);
        view2.attachScene(rootNode);


        rootNode.attachChild(sBoxObject);
        inputManager.addMapping("shoot",
                new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(actionListener, "shoot");


        inputManager.addMapping("shoot", new TouchTrigger(0));

        inputManager.addListener(this, "shoot");


        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_DOWN));
        inputManager.addListener(actionListener, "Left");
        inputManager.addListener(actionListener, "Right");
        inputManager.addListener(actionListener, "Up");
        inputManager.addListener(actionListener, "Down");

        initMaterials();
        // makeCannonBallJump();

//    BitmapFont fnt = assetManager.loadFont("Interface/Fonts/Default.fnt");
//        txt = new BitmapText(fnt, false);
//        txt.setBox(new Rectangle(0, 0, settings.getWidth(), settings.getHeight()));
//        txt.setSize(fnt.getPreferredSize() * 2f);
//        txt.setText("HELLO");
//        txt.setLocalTranslation(0, txt.getHeight(), 0);
//        guiNode.attachChild(txt);

//         guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
//        final BitmapText sometext = new BitmapText(guiFont, false);
//        sometext.setSize(guiFont.getCharSet().getRenderedSize());
//sometext.setText("sometsxt");
//        guiNode.attachChild(sometext);

    }
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String binding, boolean value, float tpf) {
            if (binding.equals("shoot") && !value) {
                //makeRandomCannonBall();
                makeCannonBall();
//           jumpCannonBall();
            }
            if (binding.equals("Left")) {
                if (value) {
                    tiger_phy.setLinearVelocity(new Vector3f(-2f, 0f, 0f));
                }
            } else if (binding.equals("Right")) {
                if (value) {
                    tiger_phy.setLinearVelocity(new Vector3f(2f, 0f, 0f));
                }
            } else if (binding.equals("Up")) {
                if (value) {
                    tiger_phy.setLinearVelocity(new Vector3f(0f, 0f, -2f));
                }
            } else if (binding.equals("Down")) {
                if (value) {
                    tiger_phy.setLinearVelocity(new Vector3f(0f, 0f, 2f));
                }
            }
        }
    };

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        tiger.rotate(0, 0, tpf);
player.rotate(0, tpf, 0);

        //ball_phy.getPhysicsLocation(new Vector3f(sc, sc/2,-2*sc ));
        //CollisionResults results = new CollisionResults();

    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    public void onAction(String binding, boolean value, float tpf) {
    }

    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

    @Override
    public void onTouch(String binding, TouchEvent evt, float tpf) {

        float x;

        float y;

        float pressure;

        switch (evt.getType()) {

            case MOVE:

                x = evt.getX();

                y = evt.getY();



                pressure = evt.getPressure();

                break;



            case TAP:

                x = evt.getX();

                y = evt.getY();
//    makeRandomCannonBall();
                //  jumpCannonBall();
                makeCannonBall();
//Geometry bulletg = new Geometry("bullet", bullet);
//                bulletg.setMaterial(mat2);
//                bulletg.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
//                bulletg.setLocalTranslation(cam.getLocation());
//


//check collision


                break;



            case LONGPRESSED:

                // move forward

                up = true;

                break;



            case UP:

                up = false;

                break;



            case FLING:

                break;



            default:



                break;

        }


        evt.setConsumed();





    }

    public void createBox(Float sc) {
        Texture floorTex = assetManager.loadTexture("Textures/floor_tex.jpg");
        Geometry quad = new Geometry("Quad", new Box(sc, sc, 0.01f));
        Material matQuad = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //   matQuad.setColor("Color", new ColorRGBA( 0.0f, 0.0f, 1f, 0.3f)); // ColorRGBA.Blue);
        matQuad.setTexture("ColorMap", floorTex);

        // matQuad.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/shockwave.png"));
        matQuad.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);

        quad.setMaterial(matQuad);
        quad.setQueueBucket(RenderQueue.Bucket.Transparent);
        wall_phy = new RigidBodyControl(0.0f);
        quad.addControl(wall_phy);
        bulletAppState.getPhysicsSpace().add(wall_phy);

        Vector3f translate = new Vector3f(sc, 0f, -sc);
        Quaternion q_rot = new Quaternion();
        q_rot.fromAngleAxis(-90f * FastMath.DEG_TO_RAD,
                new Vector3f(0f, 1f, 0f));
        Vector3f scale = new Vector3f(1f, 1f, 1f);
        Transform tr = new Transform(translate, q_rot, scale);

        Material mat2Quad = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture wall_tex = assetManager.loadTexture("Textures/wood_tex.jpg");

        //      mat2Quad.setColor("Color", new ColorRGBA( 0.0f, 0.0f, 1f, 1f)); // ColorRGBA.Blue);
        mat2Quad.setTexture("ColorMap", wall_tex);
        mat2Quad.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);


        Geometry quad2 = new Geometry("Quad2", new Box(sc, sc, 0.01f));
        quad2.setMaterial(mat2Quad);
//        quad2.setQueueBucket(RenderQueue.Bucket.Transparent);
        quad2.setLocalTransform(tr);
        wall_phy2 = new RigidBodyControl(0.0f);
        quad2.addControl(wall_phy2);
        bulletAppState.getPhysicsSpace().add(wall_phy2);

        Material matt2Quad = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //  matt2Quad.setColor("Color", ColorRGBA.Brown); // ColorRGBA.Blue);
        matt2Quad.setTexture("ColorMap", wall_tex);
        matt2Quad.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);


        Geometry quad3 = new Geometry("Quad3", new Box(sc, sc, 0.01f));
        translate.set(new Vector3f(-sc, 0f, -sc));
        q_rot.fromAngleAxis(90f * FastMath.DEG_TO_RAD,
                new Vector3f(0f, 1f, 0f));
        tr.setRotation(q_rot);
        tr.setTranslation(translate);
        quad3.setMaterial(matt2Quad);
//       quad3.setQueueBucket(RenderQueue.Bucket.Transparent);
        quad3.setLocalTransform(tr);
        wall_phy3 = new RigidBodyControl(0.0f);
        quad3.addControl(wall_phy3);
        bulletAppState.getPhysicsSpace().add(wall_phy3);

//       Geometry quad4 = new Geometry("Quad4", new Box(sc/3, sc, 0.01f));  // This is window
//       Material matQuadWindow = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        matQuadWindow.setColor("Color", new ColorRGBA( 1.0f, 0.0f, 0.0f, 0.7f)); // ColorRGBA.Blue);
//        matQuadWindow.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
//       quad4.setMaterial(matQuadWindow);
//       quad4.setQueueBucket(RenderQueue.Bucket.Transparent);
//       translate.set(new Vector3f(sc-sc/3, 0f,-2*sc));
//       q_rot.fromAngleAxis( 90f * FastMath.DEG_TO_RAD,
//       new Vector3f( 0f, 0f, 0f));
//       tr.setRotation(q_rot);
//       tr.setTranslation(translate);
//        quad4.setLocalTransform(tr);
//        wall_phy4 = new RigidBodyControl(1f);
//        quad4.addControl(wall_phy4);
//        bulletAppState.getPhysicsSpace().add(wall_phy4);
//
//
//         Material matQuad5 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        matQuad5.setColor("Color", new ColorRGBA( 1f, 0.4f, 0.4f, 0.7f)); // ColorRGBA.Blue);
//        matQuad5.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
//
//
//         Geometry quad5 = new Geometry("Quad5", new Box(2*sc/3, sc, 0.01f));
//       translate.set(new Vector3f(-(sc/3), 0f,-2*sc));
//       q_rot.fromAngleAxis( 90f * FastMath.DEG_TO_RAD,
//       new Vector3f( 0f, 0f, 0f));
//       tr.setRotation(q_rot);
//       tr.setTranslation(translate);
//       quad5.setMaterial(matQuad5);
////       quad5.setQueueBucket(RenderQueue.Bucket.Transparent);
//       quad5.setLocalTransform(tr);
//       wall_phy5 = new RigidBodyControl(1.0f);
//       quad5.addControl(wall_phy5);
//       bulletAppState.getPhysicsSpace().add(wall_phy5);


        Material matTbQuad = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //       matTbQuad.setColor("Color", new ColorRGBA( 1f, 0.4f, 0.8f, 1f)); // ColorRGBA.Blue);
        matTbQuad.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        matTbQuad.setTexture("ColorMap", floorTex);


        Geometry floorQuad = new Geometry("Quad5", new Box(sc, sc, 0.01f));
        floorQuad.setMaterial(matTbQuad);
        floorQuad.setQueueBucket(RenderQueue.Bucket.Transparent);
        translate.set(new Vector3f(0f, -sc, -sc));
        q_rot.fromAngleAxis(90f * FastMath.DEG_TO_RAD,
                new Vector3f(-1f, 0f, 0f));
        tr.setRotation(q_rot);
        tr.setTranslation(translate);
        floorQuad.setLocalTransform(tr);
        /* Make the floor physical with mass 0.0f! */
        floor_phy = new RigidBodyControl(0.0f);
        floorQuad.addControl(floor_phy);
        bulletAppState.getPhysicsSpace().add(floor_phy);

        Geometry top_quad = new Geometry("Top_quad", new Box(sc, sc, 0.01f));
        top_quad.setMaterial(matTbQuad);
        top_quad.setQueueBucket(RenderQueue.Bucket.Transparent);
        translate.set(new Vector3f(0f, sc, -sc));
        q_rot.fromAngleAxis(90f * FastMath.DEG_TO_RAD,
                new Vector3f(-1f, 0f, 0f));
        tr.setRotation(q_rot);
        tr.setTranslation(translate);
        top_quad.setLocalTransform(tr);
        wall_phy6 = new RigidBodyControl(0.0f);
        top_quad.addControl(wall_phy6);
        bulletAppState.getPhysicsSpace().add(wall_phy6);

        //    Geometry disc = new Geometry("Disc", new Cylinder(10, 10,1, 0.5f,true));
        Material discMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //discMat.setColor("Color", ColorRGBA.White); // ColorRGBA.Blue);
        //     discMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        Texture disc_tex = assetManager.loadTexture("Textures/fire.jpg");

        discMat.setTexture("ColorMap", disc_tex);
        disc.setMaterial(discMat);

        disc.setQueueBucket(RenderQueue.Bucket.Transparent);
        translate.set(new Vector3f(0f, sc / 2, -2 * sc));
        tr.setTranslation(translate);
        q_rot.fromAngleAxis(90f * FastMath.DEG_TO_RAD,
                new Vector3f(0f, 0f, 0f));
        tr.setRotation(q_rot);
        disc.setLocalTransform(tr);
//     disc.setLocalTranslation(translate);
        disc_phy = new RigidBodyControl(1.0f);
        disc.addControl(disc_phy);
        bulletAppState.getPhysicsSpace().add(disc_phy);



        //   sBoxObject.attachChild(quad);
        sBoxObject.attachChild(quad2);
        sBoxObject.attachChild(quad3);
//        sBoxObject.attachChild(quad4);
//        sBoxObject.attachChild(quad5);
        sBoxObject.attachChild(floorQuad);
        sBoxObject.attachChild(top_quad);
        rootNode.attachChild(disc);
        //Node holderNode = new Node("PhysicsNode");

        RigidBodyControl holdercontrol = new RigidBodyControl(new BoxCollisionShape(new Vector3f(.1f, .1f, .1f)), 0);

        //holdercontrol.setPhysicsLocation(new Vector3f(0f, 0f, 0f));
        holdercontrol.setPhysicsLocation(disc.getLocalTranslation().add(new Vector3f(1f, 0f, 0f)));
        bulletAppState.getPhysicsSpace().add(holdercontrol);


//       joint=new HingeJoint(holdercontrol, disc.getControl(RigidBodyControl.class),
//               new Vector3f(sc,0f,-2*sc), new Vector3f(sc/3,0f,0f), Vector3f.UNIT_Y, Vector3f.UNIT_Y);

//       joint=new HingeJoint(holdercontrol, disc.getControl(RigidBodyControl.class),
//               new Vector3f(0f, sc/2,-2*sc), new Vector3f(sc/15,0f,0f), Vector3f.UNIT_Y, Vector3f.UNIT_Y);


        joint = new HingeJoint(holdercontrol, disc.getControl(RigidBodyControl.class),
                new Vector3f(0f, 0f, 0f), new Vector3f(1f, 0f, 0f), Vector3f.UNIT_X, Vector3f.UNIT_X);


        bulletAppState.getPhysicsSpace().add(joint);
//        joint.setLimit(0f,90f * FastMath.DEG_TO_RAD );

        joint.setCollisionBetweenLinkedBodys(false);

        tiger = assetManager.loadModel("Models/tiger.j3o");
        tiger.setLocalTranslation(-sc / 2, -3 * sc / 4, -3 * sc / 2);
        tiger.scale(0.2f);

        tiger_phy = new RigidBodyControl(3f);
   //     tiger.addControl(tiger_phy);
//        bulletAppState.getPhysicsSpace().add(tiger_phy);

        player = (Node) assetManager.loadModel("Models/Dragon_Mesh.j3o");

//          Texture dragonTex=assetManager.loadTexture("Textures/Dragon_ground_color.jpg");
//          Material drMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//    drMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
//
//       drMat.setTexture("ColorMap", dragonTex);
//
//    ;
        player.setMaterial(assetManager.loadMaterial("Models/Dragon_Mesh.j3m"));
        player.setLocalTranslation(-sc / 2, -3 * sc / 4, -sc);

        Vector3f translate_dr = new Vector3f(0f, -3 * sc / 4, -sc);
        Quaternion q_rot_dr = new Quaternion();
        q_rot_dr.fromAngleAxis(90f * FastMath.DEG_TO_RAD,
                new Vector3f(0f, 1f, 0f));
        Vector3f scale_dr = new Vector3f(1f, 1f, 1f);
        Transform tr_dr = new Transform(translate_dr, q_rot_dr, scale_dr);

        player.setLocalTransform(tr_dr);
        player.scale(0.1f);
        q_rot_dr.fromAngleAxis(90f * FastMath.DEG_TO_RAD,
                new Vector3f(-1f, 0f, 0f));
        tr_dr.setRotation(q_rot_dr);
        tiger.setLocalTransform(tr_dr);
        
        tiger.scale(0.2f);
//       rootNode.attachChild(tiger);
        rootNode.attachChild(player);

player.addControl(tiger_phy);
//        bulletAppState.getPhysicsSpace().add(tiger_phy);
//tiger_phy.setGravity(new Vector3f(0f, -2f, 0f));
//tiger_phy.setLinearVelocity(new Vector3f(1f, 0f, 0f));

        control = player.getControl(AnimControl.class);
        control.addListener(this);

        channel = control.createChannel();
        channel.setAnim("Walk_New");


//    Quaternion q_rot2 = new Quaternion();
//    q_rot2.fromAngleAxis( 90f * FastMath.DEG_TO_RAD,
//    new Vector3f( -1f, 0f, 0f));
//   tiger.setLocalRotation(q_rot2);

        //rootNode.attachChild(tiger);
//         path = new MotionPath();
// //        path.addWayPoint(new Vector3f(0f, sc/2, -2*sc));
//        path.addWayPoint(new Vector3f(sc, sc/2, -2*sc));
//        path.addWayPoint(new Vector3f(-sc,sc/2, -2*sc));
//        path.enableDebugShape(assetManager, rootNode);
// //  path.addWayPoint(new Vector3f(0f, sc/2, -2*sc));
//
//        motionControl = new MotionEvent(disc,pat--h);
//        motionControl.setDirectionType(MotionEvent.Direction.PathAndRotation);
//        motionControl.setRotation(new Quaternion().fromAngleNormalAxis(-FastMath.HALF_PI, Vector3f.UNIT_Y));
//        motionControl.setInitialDuration(10f);
//        motionControl.setSpeed(2f);
//        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
//        final BitmapText wayPointsText = new BitmapText(guiFont, false);
//        wayPointsText.setSize(guiFont.getCharSet().getRenderedSize());
//
//        guiNode.attachChild(wayPointsText);
//
//        path.addListener(new MotionPathListener() {
//
//            public void onWayPointReach(MotionEvent control, int wayPointIndex) {
//                if (path.getNbWayPoints() == wayPointIndex + 1) {
//                    wayPointsText.setText(control.getSpatial().getName() + "Finished!!! ");
//                } else {
//                    wayPointsText.setText(control.getSpatial().getName() + " Reached way point " + wayPointIndex);
//                }
//                wayPointsText.setLocalTranslation((cam.getWidth() - wayPointsText.getLineWidth()) / 2, cam.getHeight(), 0);
//            }
//        });
//       motionControl.play();
//       motionControl.setLoopMode(LoopMode.Loop);
//

        // makeCannonBallJump();
    }

    public void initMaterials() {
        Texture stone_tex = assetManager.loadTexture("Textures/ball_tex.png");
        Texture wall_tex = assetManager.loadTexture("Textures/wood_tex.jpg");

        stone_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //  stone_mat.setColor("Color", ColorRGBA.Green);
        stone_mat.setTexture("ColorMap", stone_tex);
        wall_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        wall_mat.setTexture("ColorMap", wall_tex);

    }

    public void makeRandomCannonBall() {

        Vector3f random_loc = randomLocVector();

        /**
         * Create a cannon ball geometry and attach to scene graph.
         */
        Geometry ball_geo = new Geometry("cannon ball", sphere);
        ball_geo.setMaterial(stone_mat);
        rootNode.attachChild(ball_geo);

        ball_geo.setLocalTranslation(random_loc);
        ball_phy = new RigidBodyControl(1f);
        ball_geo.addControl(ball_phy);
        bulletAppState.getPhysicsSpace().add(ball_phy);
        ball_phy.setLinearVelocity(randomVelVector());



    }

    public void makeCannonBall() {

        //Vector3f random_loc=randomLocVector();

        /**
         * Create a cannon ball geometry and attach to scene graph.
         */
        ball_geo = new Geometry("cannon ball", sphere);
        ball_geo.setMaterial(stone_mat);
//    rootNode.attachChild(ball_geo);
        Vector3f loc = new Vector3f(0f, 0f, -3.1f);
        ball_geo.setLocalTranslation(loc);
        //   ball_phy = new RigidBodyControl(3f);
        // ball_geo.addControl(ball_phy);
        //  bulletAppState.getPhysicsSpace().add(ball_phy);
        Vector3f vel = new Vector3f(0f, 0.45f, -1f).mult(30);
//    ball_phy.setLinearVelocity(vel);
//    ball_phy.setLinearVelocity(cam.getDirection().mult(25));

        SphereCollisionShape bulletCollisionShape = new SphereCollisionShape(0.4f);
        RigidBodyControl bulletNode = new BombControl(assetManager, bulletCollisionShape, 1);
//                RigidBodyControl bulletNode = new RigidBodyControl(bulletCollisionShape, 1);
        bulletNode.setLinearVelocity(vel);
        ball_geo.addControl(bulletNode);
        rootNode.attachChild(ball_geo);
        getPhysicsSpace().add(bulletNode);
        //tiger_phy.setLinearVelocity(new Vector3f(0f, 5f,0f));
        tiger_phy.applyImpulse(new Vector3f(0f, 20f, 0f), new Vector3f(0f, 0f, 0f));
    }

    public void makeCannonBallJump() {

        //Vector3f random_loc=randomLocVector();

        /**
         * Create a cannon ball geometry and attach to scene graph.
         */
        Geometry ball_geo = new Geometry("cannon ball", sphere);
        ball_geo.setMaterial(stone_mat);
        rootNode.attachChild(ball_geo);
        Vector3f loc = new Vector3f(0f, 0f, -3.1f);
        ball_geo.setLocalTranslation(loc);
        ball_phy = new RigidBodyControl(3f);
        ball_geo.addControl(ball_phy);
        bulletAppState.getPhysicsSpace().add(ball_phy);
        Vector3f vel = new Vector3f(0f, 1f, 0f);
        ball_phy.setLinearVelocity(vel);
//    ball_phy.setLinearVelocity(cam.getDirection().mult(25));

    }

    public void jumpCannonBall() {
        //Spatial getball= rootNode.getChild("cannon ball");
        Vector3f vel = new Vector3f(0f, 10f, -1f);
        ball_phy.setLinearVelocity(vel);

    }

    public Vector3f randomLocVector() {
        Random rand = new Random();
        int x = rand.nextInt((int) sc + (int) sc + 1 - 2) - (int) sc;
        float f = rand.nextFloat();
        float sp_x = x + f;
        int y = rand.nextInt((int) sc + (int) sc + 1 - 2) - (int) sc;
        float f_y = rand.nextFloat();
        float sp_y = y + f_y;
        Vector3f random_vec = new Vector3f(sp_x, sp_y, -0.1f);
        return random_vec;
    }

    public Vector3f randomVelVector() {
        Random rand = new Random();

        float f_x = rand.nextFloat();

        float f_y = rand.nextFloat();

        float f_z = rand.nextFloat();
        int sp = rand.nextInt(80) + 1;
        Vector3f random_vec = new Vector3f(f_x, f_y, -f_z).mult(sp);
        System.out.println(random_vec);
        return random_vec;
    }

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        //      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
