package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Darkens the entire window.
 *
 * @author Tamuz Gitler
 */
public class Night {

    //================ private constants ==============

    private static final String NIGHT_TAG = "night-tag";
    private static final float DAY_OPACITY = 0;
    private static final float MIDNIGHT_OPACITY = 0.5f;
    private static final int CYCLE_FACTOR = 2;

    //================ public methods =================

    /**
     * @param gameObjects      The collection of all participating game objects.
     * @param windowDimensions The dimensions of the windows.
     * @param cycleLength      The amount of seconds it should take the created game object to complete a
     *                         full cycle.
     * @param layer            The number of the layer to which the created game object should be added
     * @return A new game object representing day-to-night transitions.
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            Vector2 windowDimensions,
            float cycleLength
            ) {

        RectangleRenderable nightRenderable = new RectangleRenderable(Color.BLACK);
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions, nightRenderable);
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES); //nightRenderable will follow the screen
        gameObjects.addGameObject(night, layer);
        night.setTag(NIGHT_TAG);

        Transition nightTransition = new Transition<Float>(
                night, //the game object being changed
                night.renderer()::setOpaqueness,  //the method to call
                DAY_OPACITY,    //initial transition value
                MIDNIGHT_OPACITY,   //final transition value
                Transition.CUBIC_INTERPOLATOR_FLOAT,  //use a cubic interpolator
                cycleLength / CYCLE_FACTOR,   //transtion fully over half a day
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);  //nothing further to execute upon reaching final value

        return night;
    }

}
