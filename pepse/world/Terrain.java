package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.PerlinNoise;
import pepse.PepseGameManager;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

/**
 * Responsible for the creation and management of terrain.
 *
 * @author Tamuz Gitler
 */
public class Terrain {

    //================ public constants ===============

    public static final String TERRAIN_TAG = "terrain-tag";

    //================ private constants ==============

    private static final int SECOND_FLOOR = 2;
    private static final int WINDOW_PADDING = (int) (3 * Block.SIZE);
    private static final int NOISE_DIVIDER = 15;
    private static final int NOISE_MULT = 120;
    private static final int FIRST_FLOOR = 0;

    private static final float HEIGHT_FACTOR = 3 / 4f;

    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);


    //================ fields =========================

    private final int seed;
    private final int groundLayer;

    private final float groundHeightAtX0;

    private final GameObjectCollection gameObjects;

    private final PerlinNoise myNoiseGenerator;

    private final Vector2 windowDimensions;

    private Random randomAccordingToXCoordinate;

    //================ constructor ====================

    /**
     * Constructor
     *
     * @param gameObjects      The collection of all participating game objects.
     * @param groundLayer      The number of the layer to which the created ground objects should be added.
     * @param windowDimensions The dimensions of the windows.
     * @param seed             - A seed for a random number generator.
     */
    public Terrain(GameObjectCollection gameObjects,
                   int groundLayer,
                   Vector2 windowDimensions,
                   int seed) {
        /* init fields */
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.windowDimensions = windowDimensions;
        this.seed = seed;
        this.myNoiseGenerator = new PerlinNoise(seed);
        this.groundHeightAtX0 = windowDimensions.y() * HEIGHT_FACTOR;
    }

    //================ public methods =================

    /**
     * returns ground height at given x location
     *
     * @param x location
     * @return ground height - y
     */
    public float groundHeightAt(float x) {
        return (float) (this.groundHeightAtX0 + NOISE_MULT *
                this.myNoiseGenerator.noise(x / NOISE_DIVIDER));
    }

    /**
     * This method creates terrain in a given range of x-values.
     *
     * @param minRange The lower bound of the given range (will be rounded to a multiple of Block.SIZE).
     * @param maxRange The upper bound of the given range (will be rounded to a multiple of Block.SIZE).
     */

    public void createInRange(int minRange, int maxRange) {
        int roundedMinX = (int) (minRange - minRange % Block.SIZE);
        int roundedMaxX = (int) ((maxRange + Block.SIZE) - maxRange % Block.SIZE);
        int terrain_floor = FIRST_FLOOR;
        boolean notChanged = true; //for creating first terrain level in diffrenet layer
        int collideLayer = PepseGameManager.COLLIDABLE_TERRIAN_LAYER;

        for (int curX = roundedMinX; curX < roundedMaxX; curX += Block.SIZE) {

            int curLayer = collideLayer;
            this.randomAccordingToXCoordinate = new Random(hashCode(curX, seed));
            float y = (((groundHeightAt(curX) / Block.SIZE)) * (Block.SIZE));

            for (int curY = (int) y; curY < windowDimensions.y() + WINDOW_PADDING; curY += Block.SIZE) {
                if (terrain_floor == SECOND_FLOOR && notChanged) {
                    curLayer = this.groundLayer;
                    notChanged = false;
                }
                RectangleRenderable rectangleRenderable = new RectangleRenderable(
                        ColorSupplier.approximateColor(BASE_GROUND_COLOR));
                GameObject curBlock = new Block(
                        new Vector2(curX, curY),
                        rectangleRenderable); //creates block object
                this.gameObjects.addGameObject(curBlock, curLayer);
                curBlock.setTag(TERRAIN_TAG);
                terrain_floor++;
            }
        }
    }
    //================ private methods =================

    /*
     * calculates hash of curx and seed
     * @param curX on terrain
     * @param  A seed for a random number generator.
     * @return hash of curX and seed
     */
    private long hashCode(int curX, int seed) {
        return Objects.hash(curX, seed);
    }
}