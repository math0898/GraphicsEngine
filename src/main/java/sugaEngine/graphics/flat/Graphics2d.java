package sugaEngine.graphics.flat;

import sugaEngine.graphics.GraphicsPanel;

import java.awt.*;
import java.io.Serial;
import java.util.ArrayList;

/**
 * A 2d graphics window that can be manipulated with the given methods.
 *
 * @author Sugaku
 */
public class Graphics2d extends GraphicsPanel {

    /**
     * The serial version of the panel.
     */
    @Serial
    private static final long serialVersionUID = 9879187320923L;

    /**
     * A list of pixels that need to be updated in the next frame.
     */
    private ArrayList<Pixel> updatePoints = new ArrayList<>();

    /**
     * Calls every program that would like add pixels to the panel before it's displayed.
     *
     * @param width The width of the pixels that can be defined.
     * @param height The height of the pixels that can be defined.
     */
    @Override
    public void drawing (int width, int height) {
        DrawListener.Priorities[] order = new DrawListener.Priorities[]{ DrawListener.Priorities.BACKGROUND, DrawListener.Priorities.FOREGROUND, DrawListener.Priorities.GUI };
        for (DrawListener.Priorities priorities : order) {
            ArrayList<DrawListener> listeners = drawingListeners.get(priorities);
            if (listeners != null) for (DrawListener l : listeners) l.applyChanges(width, height, this);
        }
    }

    /**
     * Called during runtime to repaint the graphics.
     *
     * @param graphics The graphics object to manipulate and use in drawing.
     */
    @Override
    public void paintComponent (Graphics graphics) {
        super.paintComponent(graphics);
        int width = getWidth();
        int height = getHeight();
        drawing(width, height);
        for (Pixel p : updatePoints) {
            graphics.setColor(p.color());
            graphics.fillRect(p.x(), p.y(), 1, 1);
        }
        updatePoints = new ArrayList<>();
    }

    /**
     * Used to set the values of pixels.
     *
     * @param x The x to set.
     * @param y The y to set.
     * @param c The color to set the pixel to.
     */
    public void setPixel (int x, int y, Color c) {
        updatePoints.add(new Pixel(x, y, c));
    }

    /**
     * Used to create a rectangle of pixels assigned to a particular color.
     *
     * @param x The top left most corner of the rectangle.
     * @param y The top left most corner of the rectangle.
     * @param width How wide the rectangle is.
     * @param height How tall the rectangle is.
     * @param c The color to set the rectangle to.
     */
    public void setRectangle (int x, int y, int width, int height, Color c) {
        for (int i = x; i < width + x; i++)
            for (int j = y; j < height + y; j++)
                updatePoints.add(new Pixel(i, j, c));
    }

    /**
     * Used to set a block of pixels with the given radius.
     *
     * @param x The x position to center this 'big pixel'.
     * @param y The y position to center this 'big pixel'.
     * @param r The radius of this 'big pixel'.
     * @param c The color to set these pixels to.
     */
    public void setBigPixel (int x, int y, int r, Color c) {
        setRectangle(x - r, y - r, r, r, c);
    }
}
