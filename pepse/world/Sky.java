package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Creates a new game object representing the sky.
 *
 * @author Tamuz Gitler
 */
public class Sky {

    //================ private constants ==============

    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");
    private static final String SKY_TAG = "sky-tag";

    //================ public methods =================

    /**
     * This function creates a light blue rectangle which is always at the back of the window.
     *
     * @param gameObjects      The collection of all participating game objects.
     * @param windowDimensions dimension of sky
     * @param skyLayer         The number of the layer to which the created sky should be added.
     * @return sky
     */
    public static GameObject create(GameObjectCollection gameObjects,
                                    Vector2 windowDimensions, int skyLayer) {
        GameObject sky = new GameObject(
                Vector2.ZERO, windowDimensions,
                new RectangleRenderable(BASIC_SKY_COLOR)); //creates sky object
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES); //sky will follow the screen
        gameObjects.addGameObject(sky, skyLayer);
        sky.setTag(SKY_TAG);
        return sky;
    }
}
