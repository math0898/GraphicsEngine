package graphics.flat;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A 2d graphics window that can be manipulated with the given methods.
 *
 * @author Sugaku
 */
public class Graphics2d extends JPanel {

    /**
     * The serial version of the panel.
     */
    @Serial
    private static final long serialVersionUID = 9879187320923L;

    /**
     * A map of drawing listeners with their priority.
     */
    private final Map<DrawListener.Priorities, ArrayList<DrawListener>> drawingListeners = new HashMap<>();

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
     * Adds the drawing listener to this Graphics2d instance.
     *
     * @param listener The listener to register.
     */
    public void registerListener (DrawListener listener) {
        registerListener(DrawListener.Priorities.FOREGROUND, listener);
    }

    /**
     * Adds the drawing listener to this Graphics2d instance with the given priority.
     *
     * @param priority The priority to register the listener at.
     * @param listener The listener to register.
     */
    public void registerListener (DrawListener.Priorities priority, DrawListener listener) {
        if (!drawingListeners.containsKey(priority)) drawingListeners.put(priority, new ArrayList<>());
        drawingListeners.get(priority).add(listener);
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
}
