package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

/**
 * Creates leafs in given x coordinate
 *
 * @author Tamuz Gitler
 */
public class Leaves {

    //================ private constants ==============

    private static final int LOWER_BOUND_CHANCE_TO_GROW_LEAF = 5;
    private static final int MAX_CHANCE = 100; //for creating tree
    private static final int RAND_ROW_LEAVES = 4;
    private static final int RAND_COL_LEAVES = 2;
    private static final int LEAVES_IN_ROW_FACTOR = 6;
    private static final int LEAVES_IN_COL_FACTOR = 10;
    private static final int PADDING_DIVIDOR = 2;

    private static final String LEAF_TAG = "leaf-tag";

    private static final Color TREE_LEAF_COLOR = new Color(50, 200, 30);


    //================ fields =========================

    private final Random random;
    private final GameObjectCollection gameObjects;
    private final float cycleLength;
    private final int seed;
    private static int counter; //used for hashing

    //================ constructor ====================

    /**
     * Constructor.
     *
     * @param random      random randomAccordingToXCoordinate
     * @param cycleLength The amount of seconds it should take the created game object to complete a full
     *                    cycle.
     * @param gameObjects The collection of all participating game objects.
     * @param seed        A seed for a random number generator.
     */
    public Leaves(Random random, float cycleLength, GameObjectCollection gameObjects,
                  int seed) {
        this.random = random;
        this.cycleLength = cycleLength;
        this.gameObjects = gameObjects;
        this.seed = seed;
    }

    //================ public methods =================

    /**
     * Creates tree leafs.
     *
     * @param treeXCoordinate for building tree located on x axis
     * @param treeHeight      trees random height
     */
    public void createLeafs(int treeXCoordinate, float treeHeight) {
        /* deciding how many leafs to create*/
        int numOfLeavesInRow = this.random.nextInt(RAND_ROW_LEAVES) + LEAVES_IN_ROW_FACTOR;
        int numOfLeavesInCol = this.random.nextInt(RAND_COL_LEAVES) + LEAVES_IN_COL_FACTOR;
        int leafPadding = (int) ((numOfLeavesInRow * Block.SIZE) / PADDING_DIVIDOR);

        for (int col = 0; col < numOfLeavesInCol; col++) {
            float yCoordinate = treeHeight - (col * Block.SIZE);
            for (int row = 0; row < numOfLeavesInRow; row++) {
                int rand = this.random.nextInt(MAX_CHANCE);
                if (rand > LOWER_BOUND_CHANCE_TO_GROW_LEAF) {
                    float xCoordinate = treeXCoordinate - (row * Block.SIZE) + leafPadding;
                    Vector2 leafPosition = new Vector2(xCoordinate, yCoordinate);
                    RectangleRenderable rectangleRenderable = new RectangleRenderable
                            (ColorSupplier.approximateColor(TREE_LEAF_COLOR));
                    GameObject leaf = new Leaf(leafPosition,
                            rectangleRenderable,
                            this.cycleLength,
                            this.gameObjects,
                            (int) hashCode((int) (treeXCoordinate + yCoordinate + Leaves.counter),
                                    this.seed));
                    gameObjects.addGameObject(leaf, PepseGameManager.LEAF_LAYER);
                    leaf.setTag(LEAF_TAG);
                    Leaves.counter++;
                }
            }
        }
    }

    //================ private methods ================

    /*
     * overrides hash function
     * @param coordinateX of leaf
     * @param seed A seed for a random number generator.
     * @return hash calculated with coordinateX and seed
     */
    private long hashCode(int coordinateX, int seed) {
        return Objects.hash(coordinateX, seed);
    }
}







