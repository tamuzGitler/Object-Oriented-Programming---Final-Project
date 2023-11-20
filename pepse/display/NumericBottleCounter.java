package pepse.display;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Display a graphic object on the game window showing a numeric count of lives left.
 *
 * @author Tamuz Gitler
 */
public class NumericBottleCounter extends GameObject {

    //================ fields =========================

    private final Counter collectedBottles;
    private static final String POINTS_TEXT = "Points ";

    //================ constructor ====================

    /**
     * Constructor
     *
     * @param topLeftCorner    top left corner of left most life widgets. Other widgets will be displayed
     *                         to its
     *                         right, aligned in hight.
     * @param dimensions       of Counter to be displayed.
     * @param collectedBottles counter
     */
    public NumericBottleCounter(
            Vector2 topLeftCorner,
            Vector2 dimensions
            , Counter collectedBottles) {

        super(topLeftCorner, dimensions, new TextRenderable(POINTS_TEXT + collectedBottles.value()));
        this.collectedBottles = collectedBottles;
    }

    //================ public methods =================

    /**
     * Overrides update, removes text from gameObjectCollection
     *
     * @param deltaTime time between updates
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        TextRenderable text = new TextRenderable(POINTS_TEXT + collectedBottles.value());
        text.setColor(Color.BLACK);
        this.renderer().setRenderable(text);
        this.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    }

}
