package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.gui.*;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import pepse.display.EnergyGraphic;
import pepse.display.NumericBottleCounter;
import pepse.world.RumBottle;
import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;

import java.awt.*;
import java.util.Random;

/**
 * The main class of the simulator.
 *
 * @author Tamuz Gitler
 */
public class PepseGameManager extends GameManager {

    //================ public constants ===============

    public static final int TREE_LAYER = Layer.BACKGROUND + 11;
    public static final int LEAF_LAYER = Layer.BACKGROUND + 12;
    public static final int FALLING_LEAF_LAYER = Layer.BACKGROUND + 13;
    public static final int AVATAR_LAYER = Layer.DEFAULT;
    public static final int COLLIDABLE_TERRIAN_LAYER = Layer.STATIC_OBJECTS + 1;
    public static final int SUN_HALO_LAYER = Layer.BACKGROUND + 10;
    public static final int SKY_LAYER = Layer.BACKGROUND;
    public static final int RUM_LAYER = Layer.BACKGROUND + 14;
    public static final int TERRAIN_LAYER = Layer.STATIC_OBJECTS;
    public static final int NIGHT_LAYER = Layer.FOREGROUND;
    public static final int SUN_LAYER = Layer.BACKGROUND;
    public static final int BANNERS_LAYER = Layer.BACKGROUND + 15;
    public static final int WINNING_BAR = 10;

    public static final String RUM_TAG = "rum-tag";

    public static SoundReader soundReader; //global so avatar can use it to make awsome sounds

    enum Direction {NO_NEED_TO_CHANGE, LEFT_DIRECTION, RIGHT_DIRECTION}

    //================ private constants ==============

    private static final int PADDING = (int) (Block.SIZE * 10);
    private static final int seed = 754223;
    private static final int RUM_BOTTLE_Y_AXIS = 100;
    private static final int minTime = 5;
    private static final int maxTime = 15;
    private static final int AVATAR_LOCATION_FACTOR = 2;
    private static final int ROUNDED_BLOCK = 0;
    private static final int HALF_BLOCK_SIZE = 15;
    private static final int AVERAGE_DIVIDOR = 2;
    private static final int INIT_COUNTER_VALUE = 0;
    private static final int BOUND_FACTOR = 2;

    private static final float DIMENSION_FACTOR = 0.5f;
    private static final float CYCLE_LENGTH = 24;

    private static final Color SUN_HALO_COLOR = new Color(255, 255, 0, 20);

    private static final Vector2 GameWindowDimentions = new Vector2(1500, 1200);
    private static final Vector2 textDimension = new Vector2(25, 25);
    private static final Vector2 graphicDimension = new Vector2(200, 200);
    private static final Vector2 numericEnergyLocation = new Vector2(50, 25);
    private static final Vector2 bottleCounterLocation = new Vector2(1300, 100);
    private static final Vector2 RUM_BOTTLE_DIMENSION = Vector2.of(60, 60);
    private static final Vector2 RUM_BOTTLE_VELOCITY = Vector2.of(0, 100);

    private static final String PEPSE_GAME_TITLE = "Pepse Game";
    private static final String RUM_PATH = "pepse/assets/rum_image.png";
    private static final String NEW_GAME_MSG = "                         Welcome aboard Pirate!\n" +
            "       The games goal is to get the potato pirate drunk\n\n" +
            "                               The Pirate Rules\n" +
            "1. Collect the falling rum bottles and achieve 10 points\n" +
            "2. Collected rum bottles adds 2 points\n" +
            "3. Falling bottle to the terrain will shatter and decrease 1 point\n";
    private static final String PLAY_AGAIN_MSG = " Play again?";
    private static final String EMPTY_PROMPT = "";
    private static final String WINNING_MSG = "You Got The Potato Pirate Drunk,";
    private static final String WINNING_SOUND = "pepse/assets/winning_sound.wav";


    //================ fields =========================

    private final Vector2 windowDimensions;
    private Potato avatar;
    private Terrain terrain;
    private ImageReader imageReader;
    private WindowController windowController;
    private Tree tree;
    private Counter collectedBottles;
    private boolean newGame = true;
    private Sound winningSound;

    /* window bounds*/
    private int leftAvatarBound;
    private int rightAvatarBound;
    private int rightRenderBound;
    private int leftRenderBound;


    //================ constructor ====================

    /**
     * Constructor
     *
     * @param windowTitle      title of windwon
     * @param windowDimensions the dimension of the game window
     */
    private PepseGameManager(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
        this.windowDimensions = windowDimensions;
    }

    //================ public methods =================


    /**
     * The method will be called once when a GameGUIComponent is created, and again after every invocation of
     * windowController.resetGame().
     *
     * @param imageReader      Contains a single method: readImage, which reads an image from disk. See its
     * @param soundReader      Contains a single method: readSound, which reads a wav file from disk.
     * @param inputListener    Contains a single method: isKeyPressed, which returns whether a given key is
     *                         currently
     *                         pressed by the user or not.
     * @param windowController Contains an array of helpful, self explanatory methods concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {

        PepseGameManager.soundReader = soundReader;

        /* init fields*/
        this.imageReader = imageReader;
        this.windowController = windowController;
        this.winningSound = soundReader.readSound(WINNING_SOUND);

        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        /* create game objects */
        initializeNight();
        initializeSky();
        this.terrain = initializeTerrain();
        initializeSunWithHalo();
        initializeTrees(terrain);
        Avatar avatar = initializeAvatarFigure(imageReader, inputListener, windowController, terrain);
        this.avatar = (Potato) avatar;
        initializeGraphicEnergyCounter();
        initializeBottleCounter();
        createRumBottles();

        /*init borders*/
        initBorders();

        /* initialize all colliding layers */
        initializeCollidingLayers();
    }

    /**
     * Overrides update, updates screen
     *
     * @param deltaTime time between updates
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (this.newGame) {
            displayNewGameMessage();
        }
        checkForGameEnd(deltaTime);

        Direction direction = getDirection();
        if (direction.equals(Direction.NO_NEED_TO_CHANGE)) {
            return;
        }
        removeObjectsInDirection(direction); //removes first so we won't overload game
        createObjectsInDirection(direction); //then creates new object for infinite world
        updateBorders(direction); //updates the new border accordingly
    }


    //================ private methods ================

    /*
     * initialize Night gameObject
     */
    private void initializeNight() {
        Night.create(this.gameObjects(), NIGHT_LAYER, this.windowDimensions, CYCLE_LENGTH);
    }

    /*
     * initialize Sky gameObject
     */
    private void initializeSky() {
        Sky.create(this.gameObjects(), this.windowDimensions, SKY_LAYER);
    }

    /*
     * initialize Terrain gameObject
     */
    private Terrain initializeTerrain() {
        Terrain terrain = new Terrain(this.gameObjects(), TERRAIN_LAYER, this.windowDimensions,
                PepseGameManager.seed);
        terrain.createInRange(-(PADDING), (int) (this.windowDimensions.x() + PADDING));
        return terrain;
    }

    /*
     * initialize Tree gameObjects
     */
    private void initializeTrees(Terrain terrain) {
        this.tree = new Tree(this.gameObjects(), this.windowDimensions, PepseGameManager.seed, terrain,
                CYCLE_LENGTH);
        this.tree.createInRange(-PADDING, (int) windowDimensions.x() + PADDING);
    }

    /*
     * initialize sun and halo gameObject
     */
    private void initializeSunWithHalo() {
        GameObject sun = Sun.create(this.gameObjects(), SUN_LAYER, this.windowDimensions,
                CYCLE_LENGTH + 1);
        GameObject sunHalo = SunHalo.create(this.gameObjects(), SUN_HALO_LAYER, sun, SUN_HALO_COLOR);
        sunHalo.addComponent((float deltaTime) -> sunHalo.setCenter(sun.getCenter()));
    }

    /*
     * initialize avatar gameObject and camera to follow him
     */
    private Avatar initializeAvatarFigure(ImageReader imageReader, UserInputListener inputListener,
                                          WindowController
                                                  windowController, Terrain terrain) {

        Vector2 initialAvatarLocation = new Vector2(windowDimensions.x() / AVATAR_LOCATION_FACTOR,
                terrain.groundHeightAt(windowDimensions.x() / AVATAR_LOCATION_FACTOR) - Block.SIZE);
        Avatar avatar = Avatar.create(this.gameObjects(), AVATAR_LAYER, initialAvatarLocation,
                inputListener, imageReader);
        setCamera(new Camera(avatar,
                windowController.getWindowDimensions().mult(DIMENSION_FACTOR).subtract(initialAvatarLocation),
                windowController.getWindowDimensions(), windowController.getWindowDimensions()));
        return avatar;
    }


    /*
     * initialize NumericEnergyCounter gameObject to render avatar energy on screen
     */
    private void initializeGraphicEnergyCounter() {
        EnergyGraphic graphicEnergyCounter = new EnergyGraphic(
                PepseGameManager.numericEnergyLocation, PepseGameManager.graphicDimension,
                this.avatar, this.imageReader);
        gameObjects().addGameObject(graphicEnergyCounter, PepseGameManager.BANNERS_LAYER);
    }

    private void initializeBottleCounter() {
        this.collectedBottles = new Counter(INIT_COUNTER_VALUE);
        NumericBottleCounter numericBottleCounter = new NumericBottleCounter(
                PepseGameManager.bottleCounterLocation, PepseGameManager.textDimension,
                this.collectedBottles);
        gameObjects().addGameObject(numericBottleCounter, PepseGameManager.BANNERS_LAYER);

    }

    /*
     * initialize all wanted layers to collide / not collide with other layers
     */
    private void initializeCollidingLayers() {
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, COLLIDABLE_TERRIAN_LAYER, true);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER, TREE_LAYER, true);
        gameObjects().layers().shouldLayersCollide(LEAF_LAYER, TERRAIN_LAYER, false);
        gameObjects().layers().shouldLayersCollide(LEAF_LAYER, COLLIDABLE_TERRIAN_LAYER, false);

    }

    /*
     * rounds down num
     * @param num to round
     * @return down value
     */
    private int roundDown(int num) {
        return (int) (num - (num % Block.SIZE));
    }

    /*
     * rounds up num
     * @param num to round
     * @return up value
     */
    public static int roundUp(int num) {
        return (int) (num - (num % Block.SIZE) + Block.SIZE);

    }

    /*
     * removes all gameObject in given range.
     * @param minX to remove
     * @param maxX to remove
     */
    private void removeObjectsInDirection(Direction direction) {
        if (!gameObjects().isLayerEmpty(TERRAIN_LAYER)) {
            removeObjectsFromLayer(direction, TERRAIN_LAYER);
        }
        if (!gameObjects().isLayerEmpty(COLLIDABLE_TERRIAN_LAYER)) {
            removeObjectsFromLayer(direction, COLLIDABLE_TERRIAN_LAYER);
        }
        if (!gameObjects().isLayerEmpty(TREE_LAYER)) {
            removeObjectsFromLayer(direction, TREE_LAYER);
        }
        if (!gameObjects().isLayerEmpty(LEAF_LAYER)) {
            removeObjectsFromLayer(direction, LEAF_LAYER);
        }
        if (!gameObjects().isLayerEmpty(FALLING_LEAF_LAYER)) {
            removeObjectsFromLayer(direction, FALLING_LEAF_LAYER);
        }
    }

    /*
     * removes all game object from given layer
     * @param minX to remove
     * @param maxX to remove
     * @param layerToRemoveFrom the layer to remove gameojbects from
     */
    private void removeObjectsFromLayer(Direction direction, int layerToRemoveFrom) {
        if (direction.equals(Direction.RIGHT_DIRECTION)) {
            removeRightObjects(layerToRemoveFrom);
            return;
        }
        removeLeftObjects(layerToRemoveFrom); //direction equals left
    }

    /*
     * remove all game objects in given layer that are out of bound
     * @param layerToRemoveFrom layer to remove objects from
     */
    private void removeRightObjects(int layerToRemoveFrom) {
        Iterable<GameObject> iter = gameObjects().objectsInLayer(layerToRemoveFrom);
        for (GameObject obj : iter) {
            float obj_x_location = obj.getCenter().x();
            if (obj_x_location < leftRenderBound + PADDING) {
                this.gameObjects().removeGameObject(obj, layerToRemoveFrom);
            }
        }
    }

    /*
     * remove all game objects in given layer that are out of bound
     * @param layerToRemoveFrom layer to remove objects from
     */
    private void removeLeftObjects(int layerToRemoveFrom) {
        Iterable<GameObject> iter = gameObjects().objectsInLayer(layerToRemoveFrom);
        for (GameObject obj : iter) {
            float obj_x_location = obj.getCenter().x();
            if (obj_x_location > rightRenderBound - PADDING) {
                this.gameObjects().removeGameObject(obj, layerToRemoveFrom);
            }
        }
    }


    /*
     * creates falling rum bottles
     */
    private void createRumBottles() {
        Renderable rumImage = imageReader.readImage(RUM_PATH, true);

        Runnable makeRumFall = () -> {

            int rumBottlePosition = getBottleXPosition();
            Vector2 rumStartingLocation = Vector2.of(rumBottlePosition, RUM_BOTTLE_Y_AXIS);
            RumBottle rumBottle = new RumBottle(rumStartingLocation, RUM_BOTTLE_DIMENSION, rumImage,
                    gameObjects(), PepseGameManager.soundReader, this.collectedBottles);
            rumBottle.setTag(RUM_TAG);
            rumBottle.setVelocity(RUM_BOTTLE_VELOCITY);
            this.gameObjects().addGameObject(rumBottle, RUM_LAYER);
            gameObjects().layers().shouldLayersCollide(RUM_LAYER, AVATAR_LAYER, true);
            gameObjects().layers().shouldLayersCollide(RUM_LAYER, COLLIDABLE_TERRIAN_LAYER, true);
        };
        Random rand = new Random(seed);
        int timeTillBottleFalls = rand.nextInt(maxTime - minTime) + minTime;
        ScheduledTask bottleScheduler = new ScheduledTask(
                this.avatar,
                timeTillBottleFalls,
                true,
                makeRumFall);
    }

    /*
     * displays message of new game with instructions
     */
    private void displayNewGameMessage() {
        windowController.showMessageBox(NEW_GAME_MSG);
        this.newGame = false;
    }

    /*
     * rounds give coordinate to Block.Size
     * @param avatarXLocation
     * @return
     */
    private int roundAccordingToBlock(int avatarXLocation) {
        if (avatarXLocation % Block.SIZE == ROUNDED_BLOCK) {
            return avatarXLocation;
        }
        if (avatarXLocation % Block.SIZE > HALF_BLOCK_SIZE) {
            return roundUp(avatarXLocation);
        }
        return roundDown(avatarXLocation);
    }

    /*
     *  gets rum bottle x axis
     * @param rand Random
     * @return x-axis for new falling rum bottle
     */
    private int getBottleXPosition() {

        int avatarXLocation = (int) this.avatar.getCenter().x();
        Random rand = new Random(); //no seed sent to random because it will make the bottle fall in same
        // place
        int roundedAvatarXLocation = roundAccordingToBlock(avatarXLocation);
        int roundedLeftCorner = roundAccordingToBlock((int) (roundedAvatarXLocation -
                this.windowDimensions.x() / AVERAGE_DIVIDOR));
        int roundedRightCorner = roundAccordingToBlock((int) (roundedAvatarXLocation +
                this.windowDimensions.x() / AVERAGE_DIVIDOR));
        int rumBottlePosition = rand.nextInt(roundedRightCorner - roundedLeftCorner) +
                roundedLeftCorner;

        return roundUp(rumBottlePosition);
    }

    /*
     * checks player lost or won, if so it will offer the player the chance to play again.
     *
     * @param deltaTime time between updates. For internal use by game engine. You do not need to call this
     *  method
     *                  yourself.
     */
    private void checkForGameEnd(float deltaTime) {
        String prompt = EMPTY_PROMPT;
        prompt = checkIfPlayerHasWon(prompt);
        if (!prompt.isEmpty()) {
            prompt += PLAY_AGAIN_MSG;
            if (windowController.openYesNoDialog(prompt)) {
                windowController.resetGame();
                this.newGame = true;
            } else {
                windowController.closeWindow();
            }
        }
    }

    /*
     * checks if player has won, meaning he got the potato pirate drunk!
     *
     * @param prompt message to prompt
     * @return updated message
     */
    private String checkIfPlayerHasWon(String prompt) {
        if (this.collectedBottles.value() >= WINNING_BAR) {
            prompt = WINNING_MSG;
            this.winningSound.play();

        }
        return prompt;
    }

    /*
     * inits the values of the borders
     */
    private void initBorders() {

        this.leftAvatarBound = (int) (windowDimensions.x() / BOUND_FACTOR) - PADDING;
        this.rightAvatarBound = (int) (windowDimensions.x() / BOUND_FACTOR) + PADDING;
        this.leftRenderBound = -PADDING;
        this.rightRenderBound = (int) windowDimensions.x() + PADDING;
    }

    /*
     * gets the direction of avatar according to left/right bounds
     * @return Direction
     */
    private Direction getDirection() {
        Direction direction = Direction.NO_NEED_TO_CHANGE;
        float avatarXPosition = this.avatar.getCenter().x();
        if (avatarXPosition > this.rightAvatarBound) {
            direction = Direction.RIGHT_DIRECTION;
        }
        if (avatarXPosition < this.leftAvatarBound) {
            direction = Direction.LEFT_DIRECTION;
        }
        return direction;
    }

    /*
     * creates objects in given Direction
     * @param direction
     */
    private void createObjectsInDirection(Direction direction) {
        int minRange = rightRenderBound;
        int maxRange = rightRenderBound + PADDING;
        if (direction.equals(Direction.LEFT_DIRECTION)) {
            minRange = leftRenderBound - PADDING;
            maxRange = leftRenderBound;
        }
        this.tree.createInRange(minRange, maxRange);
        this.terrain.createInRange(minRange, maxRange);
    }


    /*
     * update borders according to direction
     * @param direction right/left
     */
    private void updateBorders(Direction direction) {
        int borderUpdate = PADDING; //assuming direction equals right, then will add padding
        if (direction.equals(Direction.LEFT_DIRECTION)) {
            borderUpdate = -PADDING; //direction equals left, then will subtract padding
        }
        leftAvatarBound += borderUpdate;
        rightAvatarBound += borderUpdate;
        leftRenderBound += borderUpdate;
        rightRenderBound += borderUpdate;
    }
    //==================== main ====================

    /**
     * Runs the entire simulation.
     *
     * @param args This argument should not be used.
     */
    public static void main(String[] args) {
        PepseGameManager gameManager = new PepseGameManager(PEPSE_GAME_TITLE, GameWindowDimentions);
        gameManager.run();
    }

}
