package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.PerlinNoise;
import pepse.PepseGameManager;
import pepse.world.Block;

import java.util.Random;

/**
 * Leaf class - inits a leaf with life cycle - will start falling, disappear and return to the tree
 *
 * @author Tamuz Gitler
 */
public class Leaf extends Block {

    //================ public constants ===============

    public static final String LEAF_TAG = "leaf-tag";
    public static final String FALLING_LEAF_TAG = "falling_leaf-tag";

    //================ private constants ==============

    private static final int NO_MASS = 0;
    private static final int NO_MOVEMENT = 0;
    private static final int CYCLE_FACOTR = 2;
    private static final int X_WIDTH_CHANGE = 3;
    private static final int Y_WIDTH_CHANGE = 1;
    private static final int MAX_WAIT_TIME = 15;
    private static final int NOISE_FACTOR = 60;
    private static final int TRANSITION_TIME = 20;
    private static final int FALLING_VELOCITY = 50;
    private static final int MIN_WAIT_TIME = 3;
    private static final int FADEOUT_TIME = 25;
    private static final int MIN_DEAD_TIME = 5;
    private static final int MAX_DEAD_TIME = 50;
    private static final int MIN_FALLING_TIME = 10;
    private static final int MAX_FALLING_TIME = 200;

    private static final float TRANSITION_VALUE = 100f;
    private static final float DARK_OPAQUENESS = 1f;
    private static final float DOWN = -1f;
    private static final float MIN_FALL_TRANISITION = 25f;
    private static final float MAX_FALL_TRANSITION = 50f;

    private static final Vector2 LEAF_TRANSITION_SIZE = new Vector2(30, 30);


    //================ fields =========================

    private float cycleLength;

    private Transition<Float> leafFallingTransition;
    private Transition<Float> leafWidthTransition;
    private Transition<Float> leafAngleTransition;

    private final PerlinNoise myNoiseGenerator;

    private final Random random;

    private final Vector2 leafPosition;

    private final GameObjectCollection gameObjects;

    //================ constructor ====================

    /**
     * Constructor
     *
     * @param topLeftCorner The location of the top-left corner of the created block.
     * @param renderable    A renderable to render as the block.
     */
    public Leaf(Vector2 topLeftCorner,
                Renderable renderable,
                float cycleLength,
                GameObjectCollection gameObjects,
                int seed) {
        super(topLeftCorner, renderable);

        /* initialize fields*/
        this.cycleLength = cycleLength;
        this.leafPosition = topLeftCorner;
        this.gameObjects = gameObjects;
        this.random = new Random(seed);
        this.myNoiseGenerator = new PerlinNoise(seed);

        createLeafCycleLife(); //starts cycle of leaf life
    }

    //================ public methods =================

    /**
     * Overrides onCollision to change behaviour
     *
     * @param other     collides with this
     * @param collision data
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (!this.getTag().equals(FALLING_LEAF_TAG)) {
            return;
        }
        this.physics().setMass(NO_MASS);
        preventIntersection();
        removeTransition();
        stopVelocity();
        this.setDimensions(new Vector2(Block.SIZE, Block.SIZE)); //reset dimension of leaf
    }

    //================ private methods ================

    /*
     * creates leaf angle transition
     */
    private void createAngleTransition() {
        //(Float angle) -> leaf.renderer().setRenderableAngle(angle),   //the method to call
        this.leafAngleTransition = new Transition<Float>(
                this, //the game object being changed
                (Float angle) -> this.renderer().setRenderableAngle(angle),   //the method to call
                -TRANSITION_VALUE, TRANSITION_VALUE   //initial transition value
                ,   //final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT,  //use a cubic interpolator
                this.cycleLength,   //transtion fully over half a day
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);  //nothing further to execute upon reaching final value
    }

    /*
     * creates leaf width transition
     */
    private void createWidthTransition() {
        Vector2 leafTransitionSize = LEAF_TRANSITION_SIZE.add(Vector2.of(
                this.random.nextInt(
                        X_WIDTH_CHANGE + X_WIDTH_CHANGE) + X_WIDTH_CHANGE,
                random.nextInt(Y_WIDTH_CHANGE + Y_WIDTH_CHANGE) + Y_WIDTH_CHANGE));
        this.leafWidthTransition = new Transition<Float>(
                this, //the game object being changed
                (Float angle) -> this.setDimensions(leafTransitionSize),   //the method to call
                -TRANSITION_VALUE,    //initial transition value
                TRANSITION_VALUE,   //final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT,  //use a cubic interpolator
                cycleLength / CYCLE_FACOTR,   //transtion fully over half a day
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);  //nothing further to execute upon reaching final value
    }

    /*
     * creates leaf life cycle
     */
    private void createLeafCycleLife() {
        initLeafOnTreeBehaviour();
        Runnable afterFadeOut = initAfterFadeOut();
        initFallingLeafScheduler(afterFadeOut);
        initLeafAngleScheduler();
    }

    /*
     * init after fadeOut efects
     * @return runnable afterFadeOut
     */
    private Runnable initAfterFadeOut() {
        Runnable reviveLeaf = () -> {
            this.setCenter(this.leafPosition);
            this.renderer().setOpaqueness(DARK_OPAQUENESS);
            gameObjects.removeGameObject(this);
            gameObjects.addGameObject(this, PepseGameManager.LEAF_LAYER);
            this.setTag(LEAF_TAG);
            createLeafCycleLife();
        };

        Runnable afterFadeOut = () -> {
            float randomTimeThatLeafIsDead = random.nextInt(MAX_DEAD_TIME - MIN_DEAD_TIME) +
                    MIN_DEAD_TIME;
            ScheduledTask scheduleTillLeafRenew = new ScheduledTask(
                    this,
                    randomTimeThatLeafIsDead,
                    false,
                    reviveLeaf);
        };
        return afterFadeOut;
    }

    /*
     * inits falling leaf scheduler
     * @param afterFadeOut runnable
     */
    private void initFallingLeafScheduler(Runnable afterFadeOut) {
        float randomTimeTillFalling = random.nextInt(MAX_FALLING_TIME - MIN_FALLING_TIME) +
                MIN_FALLING_TIME;

        Runnable fallingLeaf = () -> {

            float LeafRandomFadeoutTime = random.nextInt(FADEOUT_TIME) + randomTimeTillFalling;
            gameObjects.removeGameObject(this);
            gameObjects.addGameObject(this, PepseGameManager.FALLING_LEAF_LAYER);
            gameObjects.layers().shouldLayersCollide(PepseGameManager.FALLING_LEAF_LAYER,
                    PepseGameManager.COLLIDABLE_TERRIAN_LAYER, true);
            creatLeafFallingTransition();
            this.renderer().fadeOut(LeafRandomFadeoutTime, afterFadeOut);
            this.renderer().fadeOut(LeafRandomFadeoutTime);
            this.setTag(FALLING_LEAF_TAG);
        };

        //2. after LeafRandomLifeTime is reached leaf start to fall
        ScheduledTask scheduleTillLeafFall = new ScheduledTask(
                this,
                randomTimeTillFalling,
                false,
                fallingLeaf);
    }

    /*
     * init leaf angle scheduler
     */
    private void initLeafAngleScheduler() {
        float waitTime = random.nextInt(MAX_WAIT_TIME - MIN_WAIT_TIME) + MIN_WAIT_TIME;

        ScheduledTask leafAngleScheduler = new ScheduledTask(
                this,
                waitTime,
                false,
                this::createAngleTransition);
    }

    /*
     * init leaf on tree behaviour
     */
    private void initLeafOnTreeBehaviour() {
        this.transform().setVelocityY(NO_MOVEMENT);
        this.transform().setVelocityX(NO_MOVEMENT);
        this.physics().setMass(NO_MASS);
        createWidthTransition();
    }

    /*
     * sets the velocity of the falling speed
     * @param speed to set
     */
    private void setFallingLeafVelocity(float speed) {
        this.transform().setVelocityY(FALLING_VELOCITY);
        this.transform().setVelocityX((float) (NOISE_FACTOR * this.myNoiseGenerator.noise(
                this.leafPosition.x())));
    }

    /*
     * sets the velocity of the leaf to zero
     * @param speed to set
     */
    private void stopVelocity() {
        this.transform().setVelocityY(NO_MOVEMENT);
        this.transform().setVelocityX(NO_MOVEMENT);
    }

    /*
     * remove all transition
     */
    private void removeTransition() {
        this.removeComponent(leafFallingTransition);
        this.removeComponent(leafAngleTransition);
        this.removeComponent(leafWidthTransition);
    }

    /*
     * prevents leaf intersection
     */
    private void preventIntersection() {
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().preventIntersectionsFromDirection(Vector2.UP);
        physics().preventIntersectionsFromDirection(Vector2.DOWN);
        physics().preventIntersectionsFromDirection(Vector2.LEFT);
        physics().preventIntersectionsFromDirection(Vector2.RIGHT);
        physics().preventIntersectionsFromDirection(Vector2.ONES);
        physics().preventIntersectionsFromDirection(Vector2.ONES.mult(DOWN));
    }

    /*
     * creates leaf falling transition
     */
    private void creatLeafFallingTransition() {
        this.leafFallingTransition = new Transition<Float>(
                this, //the game object being changed
                this::setFallingLeafVelocity,   //the method to call
                MIN_FALL_TRANISITION, MAX_FALL_TRANSITION   //initial transition value
                ,   //final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT,  //use a cubic interpolator
                TRANSITION_TIME,   //transtion fully over half a day
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);  //nothing further to execute upon reaching final value
    }

}
