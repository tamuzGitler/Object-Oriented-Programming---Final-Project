package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents the sun - moves across the sky in an elliptical path.
 *
 * @author Tamuz Gitler
 */
public class Sun {
    private static final String SUN_TAG = "sun-tag";

    //================ private constants ==============

    private static final float SUN_FIRST_DEG = 0;
    private static final float SUN_LAST_DEG = 360;
    private static final float SUN_DIAMETER = 50;
    private static final float VEC_CENTER_FACTOR = 0.9f;
    private static final float VEC_CENTRY_FACTOR = 0.6f;
    private static final float SUN_POSITION_DIVIDER = 2f;
    private static final float SUN_DIMENSION_FACTOR = 0.5f;

    //================ public methods =================

    /**
     * This function creates a yellow circle that moves in the sky in an elliptical path (in camera
     * coordinates).
     *
     * @param windowDimensions The dimensions of the windows.
     * @param cycleLength      The amount of seconds it should take the created game object to complete a
     *                         full cycle.
     * @param gameObjects      The collection of all participating game objects.
     * @param layer            The number of the layer to which the created sun should be added.
     * @return A new game object representing the sun.
     */
    public static GameObject create(
            GameObjectCollection gameObjects,
            int layer,
            Vector2 windowDimensions,
            float cycleLength
            ) {
        Vector2 sunSize = new Vector2(SUN_DIAMETER, SUN_DIAMETER);
        GameObject sun = new GameObject(
                windowDimensions.mult(SUN_DIMENSION_FACTOR), sunSize,
                new OvalRenderable(Color.YELLOW)); //creates sun object
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES); //sky will follow the screen
        gameObjects.addGameObject(sun, layer);
        sun.setTag(SUN_TAG);

        Transition sunTransition = new Transition<Float>(
                sun, //the game object being changed
                (Float angleInSky) -> sun.setCenter(calcSunPosition(windowDimensions, angleInSky)), //the
                // method to call
                SUN_FIRST_DEG,    //initial transition value
                SUN_LAST_DEG,   //final transition value
                Transition.LINEAR_INTERPOLATOR_FLOAT,  //use a cubic interpolator
                cycleLength,   //transtion fully over half a day
                Transition.TransitionType.TRANSITION_LOOP,
                null);  //nothing further to execute upon reaching final value

        return sun;

    }

    //================ private methods ================

    /**
     * calculates new position of sun
     *
     * @param windowDimensions The dimensions of the windows.
     * @param angleInSky       degree of sun
     * @return new sun Position
     */
    private static Vector2 calcSunPosition(Vector2 windowDimensions, float angleInSky) {
        Vector2 vecCenter = windowDimensions.mult(VEC_CENTER_FACTOR);
        Vector2 vecCenerty = windowDimensions.mult(VEC_CENTRY_FACTOR);
        vecCenter = vecCenter.rotated(angleInSky);
        vecCenerty = vecCenerty.rotated(angleInSky);
        return new Vector2((windowDimensions.x() - vecCenter.x()) / SUN_POSITION_DIVIDER,
                (windowDimensions.y() -
                        vecCenerty.y()) / SUN_POSITION_DIVIDER);
    }

}

