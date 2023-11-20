package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a single block (larger objects can be created from blocks).
 *
 * @author Tamuz Gitler
 */
public class Block extends GameObject {

    //================ public constants ===============

    public static final float SIZE = 30;

    //================ constructor ====================

    /**
     * Constructor.
     *
     * @param topLeftCorner The location of the top-left corner of the created block.
     * @param renderable    A renderable to render as the block.
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable); //initialize constant size
        physics().preventIntersectionsFromDirection(Vector2.ZERO); //prevents intersections from specific
        // direction
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS); //on collision block wouldn't move
    }

}
