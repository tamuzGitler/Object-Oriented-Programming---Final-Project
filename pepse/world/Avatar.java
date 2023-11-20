package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Creates a new avatar.
 *
 * @author Tamuz Gitler
 */
public class Avatar extends GameObject {

    //================ private constants ==============

    private static final String POTATO_PATH = "pepse/assets/potatoPirate.png";

    private static final Vector2 avatarDimension = Vector2.of(60, 60);

    //================ constructor ====================

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     */
    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
    }


//================ public methods =================

    /**
     * This function creates an avatar that can travel the world and is followed by the camera.
     * The can stand, walk, jump and fly, and never reaches the end of the world.
     *
     * @param gameObjects   The collection of all participating game objects.
     * @param layer         The number of the layer to which the created avatar should be added.
     * @param topLeftCorner The location of the top-left corner of the created avatar.
     * @param inputListener Used for reading input from the user.
     * @param imageReader   Used for reading images from disk or from within a jar.
     * @return A newly created representing the avatar.
     */
    public static Avatar create(GameObjectCollection gameObjects,
                                int layer,
                                Vector2 topLeftCorner,
                                UserInputListener inputListener,
                                ImageReader imageReader) {

        Renderable potatoImage = imageReader.readImage(POTATO_PATH, true);
        Potato avatar = new Potato(topLeftCorner
                , avatarDimension, potatoImage, inputListener);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }
}
