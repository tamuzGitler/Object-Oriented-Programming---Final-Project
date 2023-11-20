package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import pepse.PepseGameManager;

/**
 * This class Creates RumBottle that avatar will collect
 *
 * @author Tamuz Gitler
 */
public class RumBottle extends GameObject {

    //================ private constants ==============

    private static final int INCREASE_VALUE = 2;
    private static final int MIN_SCORE = 0;

    private static final String BREAKING_BOTTLE_WAV_PATH = "pepse/assets/breaking_bottle.wav";
    private static final String RUM_ONLY_WAV_PATH = "pepse/assets/bottle_of_rum.wav";

    //================ fields =========================

    private boolean alreadyCollided = false;
    private final GameObjectCollection gameObjectCollection;
    private final Sound breakingBottleSound;
    private final Sound drinkingBottleSound;
    private final Counter collectedBottles;

    //================ constructor ====================

    /**
     * Constructor
     *
     * @param topLeftCorner        Position of the object, in window coordinates (pixels).
     * @param dimensions           Width and height in window coordinates.
     * @param renderable           The renderable representing the object. Can be null, in which case
     * @param gameObjectCollection gameObject to add / remove obejcts from
     * @param soundReader          Contains a single method: readSound, which reads a wav file from disk.
     * @param collectedBottles     counter
     */
    public RumBottle(Vector2 topLeftCorner,
                     Vector2 dimensions,
                     Renderable renderable,
                     GameObjectCollection gameObjectCollection,
                     SoundReader soundReader, Counter collectedBottles) {
        super(topLeftCorner, dimensions, renderable);

        /* init variables */
        this.gameObjectCollection = gameObjectCollection;
        this.breakingBottleSound = soundReader.readSound(BREAKING_BOTTLE_WAV_PATH);
        this.drinkingBottleSound = soundReader.readSound(RUM_ONLY_WAV_PATH);
        this.collectedBottles = collectedBottles;

    }
    //================ public methods =================

    /**
     * defines the action when this Seter collides with another object.
     *
     * @param other     GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        /* checks if setter collided with Paddle */
        if (other instanceof Potato) {
            this.collectedBottles.increaseBy(INCREASE_VALUE);
            if (this.collectedBottles.value() < PepseGameManager.WINNING_BAR) {
                this.drinkingBottleSound.play();
            }
        } else {
            if (other.getTag().equals(Terrain.TERRAIN_TAG)) {
                this.breakingBottleSound.play();
                if (this.collectedBottles.value() > MIN_SCORE && !alreadyCollided) {
                    this.collectedBottles.decrement();
                    this.alreadyCollided = true;
                }
            }
        }
        this.gameObjectCollection.removeGameObject(this, PepseGameManager.RUM_LAYER);
    }
}
