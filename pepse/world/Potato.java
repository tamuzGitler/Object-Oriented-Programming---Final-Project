package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.Sound;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;

import java.awt.event.KeyEvent;

/**
 * creates potato avatar that can move in the endless world and get drunk.
 *
 * @author Tamuz Gitler
 */
public class Potato extends Avatar {


    //================ private constants ==============

    private final int FULL_ENERGY = 100;
    private final int EMPTY_ENERGY = 0;
    private final int NO_MOVEMENT = 0;
    private static final int FLYING_VELOCITY = -350;

    private static final float VELOCITY_FACTOR = 0.5f;
    private static final float VELOCITY_X = 400;
    private static final float JUMPING_VELOCITY = -300;
    private static final float GRAVITY = 700;

    private static final String FLYING_SOUND_PATH = "pepse/assets/flying_sound.wav";
    private static final String JUMPING_SOUND_PATH = "pepse/assets/jumping_sound.wav";

    //================ fields =========================

    private final UserInputListener inputListener;
    private float energy;
    private final Sound jumpingSound;
    private final Sound flyingSound;
    private boolean isFlying;

    //================ constructor ====================

    /**
     * Constructor.
     *
     * @param position      Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     * @param inputListener Contains a single method: isKeyPressed, which returns whether a given key is
     *                      currently
     */
    public Potato(Vector2 position, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener) {
        super(position, dimensions, renderable);
        this.jumpingSound = PepseGameManager.soundReader.readSound(JUMPING_SOUND_PATH);
        this.flyingSound = PepseGameManager.soundReader.readSound(FLYING_SOUND_PATH);
        this.isFlying = false;
        this.inputListener = inputListener;
        this.energy = FULL_ENERGY;
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
    }

    //================ public methods =================


    /**
     * sets potato velocity to zero when collides
     *
     * @param other     object that potato collides with
     * @param collision stores information about collision
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (!other.getTag().equals(PepseGameManager.RUM_TAG)) {
            this.transform().setVelocityY(NO_MOVEMENT);
        }
    }

    /**
     * Should be called once per frame.
     *
     * @param deltaTime The time elapsed, in seconds, since the last frame.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        moveHorizontal();
        jump();
        fly();
        updateEnergy();
    }

    /**
     * gets potato energy
     *
     * @return energy
     */
    public float getEnergy() {
        return energy;
    }


    //================ private methods =================

    /*
     * moves potato on x-axis if pressed VK_LEFT or VK_RIGHT
     */
    private void moveHorizontal() {
        float xVel = NO_MOVEMENT;
        if (this.inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            xVel -= VELOCITY_X;
            renderer().setIsFlippedHorizontally(true);
        }

        if (this.inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            xVel += VELOCITY_X;
            renderer().setIsFlippedHorizontally(false);
        }
        transform().setVelocityX(xVel);
    }

    /*
     * causes potato to jump if VK_SPACE pressed
     */
    private void jump() {
        if (this.inputListener.isKeyPressed(KeyEvent.VK_SPACE) && getVelocity().y() == 0 &&
                !this.inputListener.isKeyPressed(KeyEvent.VK_SHIFT)) {
            transform().setVelocityY(JUMPING_VELOCITY);
            this.jumpingSound.play();
        }

    }

    /*
     * causes potato to fly if (VK_SPACE & VK_SHIFT) pressed
     */
    private void fly() {
        if (this.inputListener.isKeyPressed(KeyEvent.VK_SPACE) &&
                this.inputListener.isKeyPressed(KeyEvent.VK_SHIFT)
                && energy == FULL_ENERGY) {
            transform().setVelocityY(FLYING_VELOCITY);
            transform().setAccelerationY(NO_MOVEMENT);
            this.isFlying = true;
            this.flyingSound.play();
        }
    }

    /*
     *this function updates the avatar energy according to his game situation,
     * if he is flying, we reduce his energy while making sure its positive, if he is moving were not
     * adding any energy
     * and if he is resting, energy is added.
     */
    private void updateEnergy() {
        if (isFlying && energy >= EMPTY_ENERGY) {
            energy = (energy - VELOCITY_FACTOR);
            if (energy <= EMPTY_ENERGY) {
                energy = EMPTY_ENERGY;
                isFlying = false;
                transform().setAccelerationY(GRAVITY);
                return;
            }
        }

        if (isResting()) {
            energy = (energy + VELOCITY_FACTOR);
            if (energy > FULL_ENERGY) {
                energy = FULL_ENERGY;
            }
        }
    }

    /*Checking if the avatar is not moving on the floor, so he can gain every.
     * @return boolean is resting or not
     */
    private boolean isResting() {
        return getVelocity().y() == NO_MOVEMENT &&
                !this.inputListener.isKeyPressed(KeyEvent.VK_SPACE) &&
                !this.inputListener.isKeyPressed(KeyEvent.VK_LEFT) &&
                !this.inputListener.isKeyPressed(KeyEvent.VK_RIGHT);
    }
}
