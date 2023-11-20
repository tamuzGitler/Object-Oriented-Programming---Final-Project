package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.world.Block;
import pepse.world.Terrain;

import java.util.Objects;
import java.util.Random;

/**
 * Responsible for the creation and management of trees.
 *
 * @author Tamuz Gitler
 */
public class Tree {

    //================ private constants ==============

    private static final int CHANCE_TO_PLANT_TREE = 4;
    private static final int HEIGHT_FACTOR = 2;
    private static final int MAX_CHANCE_FOR_CREATING_TREE = 100;

    private static final float TREE_FACTOR = 1.5f;
    private static final float SPACE_BETWEEN_TREES = 2 * Block.SIZE;


    //================ fields =========================

    private final int seed;

    private final float cycleLength;

    private Random randomAccordingToXCoordinate;

    private final Vector2 windowDimensions;

    private final Terrain terrain;

    private final GameObjectCollection gameObjects;


    //================ constructor ====================

    /**
     * Constructor
     *
     * @param gameObjects      The collection of all participating game objects.
     * @param windowDimensions The dimensions of the windows.
     * @param seed             - A seed for a random number generator.
     */
    public Tree(GameObjectCollection gameObjects,
                Vector2 windowDimensions,
                int seed,
                Terrain terrain,
                float cycleLength) {
        /* init fields */
        this.gameObjects = gameObjects;
        this.windowDimensions = windowDimensions;
        this.terrain = terrain;
        this.cycleLength = cycleLength;
        this.seed = seed;
    }

    //================ public methods =================

    /**
     * This method creates trees in a given range of x-values.
     *
     * @param minRange The lower bound of the given range (will be rounded to a multiple of Block.SIZE).
     * @param maxRange The upper bound of the given range (will be rounded to a multiple of Block.SIZE).
     */
    public void createInRange(int minRange, int maxRange) {
        for (int xCoordinate = minRange; xCoordinate < maxRange; xCoordinate += Block.SIZE) {
            this.randomAccordingToXCoordinate = new Random(hashCode(xCoordinate, seed));
            int rand = randomAccordingToXCoordinate.nextInt(MAX_CHANCE_FOR_CREATING_TREE);
            if (rand < CHANCE_TO_PLANT_TREE) {
                createTree(xCoordinate);
                xCoordinate += SPACE_BETWEEN_TREES; //trees are not exactly next to each other
            }
        }
    }


    //================ private methods ================

    /*
     * creates hash value for tree
     * @param coordinateX for building tree located on x axis
     * @param seed  A seed for a random number generator.
     * @return hash of tree in coordinateX with given seed
     */
    private long hashCode(int coordinateX, int seed) {
        return Objects.hash(coordinateX, seed);
    }

    /*
     * Creates full tree with trunk and leafs.
     * @param treeXCoordinate for building tree located on x axis
     */
    private void createTree(int treeXCoordinate) {
        float totalHeight = windowDimensions.y();

        float currFloorHeight = ((int) (terrain.groundHeightAt(treeXCoordinate) / Block.SIZE)) * Block.SIZE;
        float floorHeightSize = (totalHeight - currFloorHeight);
        float maxTreeHeightSize = (totalHeight - floorHeightSize) / TREE_FACTOR;
        float minTreeHeightRange = (maxTreeHeightSize / HEIGHT_FACTOR);
        int treeHeight = (int) (this.randomAccordingToXCoordinate.nextInt(
                        (int) (maxTreeHeightSize - minTreeHeightRange)) + (minTreeHeightRange));
        treeHeight = PepseGameManager.roundUp(treeHeight);

        /* create trunk */
        Trunk.createTrunk(treeXCoordinate, treeHeight, (int) currFloorHeight, this.gameObjects);

        /* create leaves */
        Leaves leaves = new Leaves(this.randomAccordingToXCoordinate, cycleLength, gameObjects, seed);
        leaves.createLeafs(treeXCoordinate, treeHeight);
    }


}
