package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;

/**
 * creates tree trunk in given x coordinate
 *
 * @author Tamuz Gitler
 */
public class Trunk {

    //================ private constants ==============

    private static final Color TREE_TRUNK_COLOR = new Color(100, 50, 20);

    //================ public methods =================
    /*
     * Creates tree trunk.
     * @param treeXCoordinate  for building tree located on x axis
     * @param curRandomTreeHeight trees random height
     * @param currFloorHeight for building trunk from y axis
     */
    public static void createTrunk(int treeXCoordinate, int treeHeight, int currFloorHeight,
                                   GameObjectCollection gameObjects) {
        for (int curY = currFloorHeight; curY > treeHeight; curY -= Block.SIZE) {
            RectangleRenderable rectangleRenderable = new RectangleRenderable(
                    ColorSupplier.approximateColor(TREE_TRUNK_COLOR));
            GameObject treeTrunk = new Block(
                    new Vector2(treeXCoordinate, curY),
                    rectangleRenderable); //creates block object
            gameObjects.addGameObject(treeTrunk, PepseGameManager.TREE_LAYER);
        }
    }
}
