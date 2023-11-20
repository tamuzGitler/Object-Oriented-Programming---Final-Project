package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;

import java.awt.*;

/**
 * Represents the halo of sun.
 *
 * @author Tamuz Gitler
 */
public class SunHalo {

    //================ private constants ==============

    private static final String SUNHALO_TAG = "sun-halo-tag";
    private static final float HALO_FACTOR = 4f;

    //================ public methods =================

    /**
     * @param gameObjects The collection of all participating game objects.
     * @param sun         A game object representing the sun (it will be followed by the created game object).
     * @param color       The color of the halo.
     * @param layer       The number of the layer to which the created halo should be added.
     * @return A new game object representing the sun's halo.
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            GameObject sun,
            Color color
            ) {
        GameObject sunHalo = new GameObject(sun.getTopLeftCorner(), sun.getDimensions().mult(HALO_FACTOR),
                new OvalRenderable(color));
        gameObjects.addGameObject(sunHalo, layer);
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES); //sky will follow the screen
        sunHalo.setTag(SUNHALO_TAG);
        return sunHalo;
    }
}
