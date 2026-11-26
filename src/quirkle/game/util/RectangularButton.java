package quirkle.game.util;

import quirkle.engine.*;

import java.awt.*;

public class RectangularButton extends Entity {

    public static final String[] BUTTONCLICK_PENTATONIC = {"btnclickpenta/B4","btnclickpenta/Cis5","btnclickpenta/E5","btnclickpenta/Fis5","btnclickpenta/Gis4"};

    protected static final String SPRITE_ACTIVE = "util/rectangularbutton/active";
    private static final String SPRITE_INACTIVE = "util/rectangularbutton/inactive";
    private static final String FONT_NAME = "poly_regular";
    private static final double BASE_FONT_SIZE = 50.0;
    private static final double TEXT_Y_OFFSET = -4.0;
    private static final Color TEXT_COLOR = Color.WHITE;

    private static final double DEFAULT_MIN_SIZE = 1.0;
    private static final double DEFAULT_MAX_SIZE_MODIFIER = 1.1;
    private static final double DEFAULT_GROWTH_SPEED = 0.01;

    private final String text;
    private double minSize = DEFAULT_MIN_SIZE;
    private double maxSizeModifier = DEFAULT_MAX_SIZE_MODIFIER;
    private double growthSpeed = DEFAULT_GROWTH_SPEED;
    private double sizeModifier = minSize;

    public RectangularButton(String text) {
        this.text = text;
    }

    public void setSizeRange(double minSize, double maxSizeModifier) {
        this.minSize = minSize;
        this.maxSizeModifier = maxSizeModifier;
        this.sizeModifier = minSize;
    }

    public void setGrowthSpeed(double growthSpeed) {
        this.growthSpeed = growthSpeed;
    }

    @Override
    public void onCreate() {
        origin = OriginPresets.CENTER;
        scale = minSize;
        setSprite(SPRITE_INACTIVE);
        x = SceneManager.getCurrentScene().getCenterX();
    }

    @Override
    public void onTick(double dt) {
        if (isHovered()) {
            sizeModifier = Math.min(maxSizeModifier, sizeModifier + growthSpeed);
            setSprite(SPRITE_ACTIVE);
        } else {
            sizeModifier = Math.max(minSize, sizeModifier - growthSpeed);
            setSprite(SPRITE_INACTIVE);
        }

        scale = sizeModifier;
    }

    @Override
    public void onRender(Graphics2D g) {
        drawText(text, (float) (BASE_FONT_SIZE * sizeModifier), TEXT_COLOR,
                FONT_NAME, x, y + TEXT_Y_OFFSET, 0.0, OriginPresets.CENTER, g);
    }

    @Override
    public void onDestroy() {
        if (isHovered()) {
            PlayRandomSound.playRand(BUTTONCLICK_PENTATONIC);
        }
    }
}