package pepse.display;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Potato;

/**
 * Display a graphic object on the game window showing a numeric count of lives left.
 *
 * @author Tamuz Gitler
 */
public class EnergyGraphic extends GameObject {


    //================ private constants ==============

    private static final int FULL_ENERGY_BAR = 4;
    private static final int NUM_OF_BARS = 5;
    private static final int EMPTY_BAR = 0;
    private static final int QUARTER_BAR = 25;
    private static final int HALF_BAR = 50;
    private static final int THREE_QUARTER_BAR = 75;
    private static final int FULL_BAR = 100;
    private static final int FIRST_BAR = 1;
    private static final int SECOND_BAR = 2;
    private static final int THIRD_BAR = 3;
    private static final int FOURTH_BAR = 4;

    private static final String[] BARS_PATH = {"pepse/assets/bar0.jpg", "pepse/assets/bar1.jpg",
            "pepse/assets/bar2.jpg", "pepse/assets/bar3.jpg", "pepse/assets/bar4.jpg"};


    //================ fields =========================

    private final Potato avatar;
    private final ImageReader imageReader;
    Renderable[] barRenderabels;

    //================ constructor ====================


    /**
     * Constructor
     *
     * @param position    top left corner of left most life widgets. Other widgets will be displayed to its
     *                    //     *                             right, aligned in hight.
     * @param dimensions  of widgets to be displayed.
     * @param avatar      for getting his energy
     * @param imageReader to read energy images
     */
    public EnergyGraphic(
            Vector2 position,
            Vector2 dimensions,
            Potato avatar,
            ImageReader imageReader) {

        super(position,
                dimensions,
                imageReader.readImage(BARS_PATH[FULL_ENERGY_BAR],
                        true));

        /*init fields*/
        this.avatar = avatar;
        this.imageReader = imageReader;
        this.barRenderabels = new Renderable[NUM_OF_BARS];

        createBarsRenderables();
    }

    //================ public methods =================

    /**
     * Overrides update, switches beetwen energy pictures
     *
     * @param deltaTime time between updates
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        int barRenderableNum = getBarRenderableNum();
        this.renderer().setRenderable(this.barRenderabels[barRenderableNum]);
        this.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES); //sky will follow the screen
    }

    //================ private methods ================

    /*
     * decides which barRendereable to display coresponding to avatar energy.
     * @return barRenderableNum
     */
    private int getBarRenderableNum() {
        int barRenderableNum = EMPTY_BAR;
        if (this.avatar.getEnergy() >= QUARTER_BAR && this.avatar.getEnergy() < HALF_BAR) {
            barRenderableNum = FIRST_BAR;
        }
        if (this.avatar.getEnergy() >= HALF_BAR && this.avatar.getEnergy() < THREE_QUARTER_BAR) {
            barRenderableNum = SECOND_BAR;
        }
        if (this.avatar.getEnergy() >= THREE_QUARTER_BAR && this.avatar.getEnergy() < FULL_BAR) {
            barRenderableNum = THIRD_BAR;
        }
        if (this.avatar.getEnergy() == FULL_BAR) {
            barRenderableNum = FOURTH_BAR;
        }
        return barRenderableNum;
    }

    /*
     * inits barsRenderables for later use
     */
    private void createBarsRenderables() {
        for (int i = 0; i < NUM_OF_BARS; i++) {
            this.barRenderabels[i] = imageReader.readImage(BARS_PATH[i], true);
        }
    }
}
