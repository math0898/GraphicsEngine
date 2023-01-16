package suga.engine.graphics;

import suga.engine.GameEngine;
import suga.engine.logger.Level;
import suga.engine.threads.SugaThread;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serial;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

/**
 * A graphics panel is used to draw graphics to the computer screen. It can either be 3d or 2d.
 *
 * @author Sugaku
 */
public abstract class GraphicsPanel extends JPanel implements GraphicsPanelInterface {

    /**
     * The serial version of the panel.
     */
    @Serial
    private static final long serialVersionUID = 9879187320923L;

    /**
     * A map of drawing listeners with their priority.
     */
    protected volatile Map<DrawListener.Priorities, ArrayList<DrawListener>> drawingListeners = new HashMap<>();

    /**
     * The thread that is currently running this GraphicsPanel. May be null in some cases.
     */
    protected SugaThread thread = null;

    /**
     * Calls every program that would like add pixels to the panel before it's displayed.
     *
     * @param width The width of the pixels that can be defined.
     * @param height The height of the pixels that can be defined.
     */
    public void drawing (int width, int height) {
        DrawListener.Priorities[] order = new DrawListener.Priorities[]{
                DrawListener.Priorities.BACKGROUND,
                DrawListener.Priorities.FOREGROUND,
                DrawListener.Priorities.GUI };
        try {
            for (DrawListener.Priorities priorities : order) {
                ArrayList<DrawListener> listeners = drawingListeners.get(priorities);
                if (listeners != null)
                    for (DrawListener l : listeners)
                        l.applyChanges(width, height, this);
            }
        } catch (ConcurrentModificationException e) {
            GameEngine.getInstance().getLogger().log(e, Level.WARNING); // This sometimes occurs when loading while drawing a frame.
        }
    }

    /**
     * Sets the thread that is calling repaint on this GraphicsPanel.
     *
     * @param thread The thread that will be calling repaint() on this object.
     */
    public final void setThread (SugaThread thread) {
        this.thread = thread;
    }

    /**
     * Adds the drawing listener to this instance.
     *
     * @param listener The listener to register.
     */
    public final void registerListener (DrawListener listener) {
        registerListener(DrawListener.Priorities.FOREGROUND, listener);
    }

    /**
     * Adds the drawing listener to this instance with the given priority.
     *
     * @param priority The priority to register the listener at.
     * @param listener The listener to register.
     */
    public final void registerListener (DrawListener.Priorities priority, DrawListener listener) {
        if (!drawingListeners.containsKey(priority)) drawingListeners.put(priority, new ArrayList<>());
        drawingListeners.get(priority).add(listener);
    }

    /**
     * De-registers all draw listeners for this panel. New ones need to be added with
     * {@link #registerListener(DrawListener)}.
     */
    public final void clearListeners () {
        drawingListeners = new HashMap<>();
    }

    /**
     * Used to set the values of pixels.
     *
     * @param x The x to set.
     * @param y The y to set.
     * @param c The color to set the pixel to.
     */
    public abstract void setPixel (int x, int y, Color c);

    /**
     * Used to create a rectangle of pixels assigned to a particular color.
     *
     * @param x The top left most corner of the rectangle.
     * @param y The top left most corner of the rectangle.
     * @param width How wide the rectangle is.
     * @param height How tall the rectangle is.
     * @param c The color to set the rectangle to.
     */
    public abstract void setRectangle (int x, int y, int width, int height, Color c);

    /**
     * Used to set a block of pixels with the given radius.
     *
     * @param x The x position to center this 'big pixel'.
     * @param y The y position to center this 'big pixel'.
     * @param r The radius of this 'big pixel'.
     * @param c The color to set these pixels to.
     */
    public abstract void setBigPixel (int x, int y, int r, Color c);

    /**
     * Adds the given image to the list of images to draw this frame. Attempts to load an image from resources.
     *
     * @param x The x position of this image.
     * @param y The y position of this image.
     * @param width The width of this image.
     * @param height The height of this image.
     * @param path The path of the image to draw on screen.
     */
    public abstract void addImage (int x, int y, int width, int height, String path);

    /**
     * Adds the given image to the list of images to draw this frame.
     *
     * @param x The x position of this image.
     * @param y The y position of this image.
     * @param width The width of this image.
     * @param height The height of this image.
     * @param image The image to draw to the screen.
     */
    public abstract void addImage (int x, int y, int width, int height, BufferedImage image);
}
