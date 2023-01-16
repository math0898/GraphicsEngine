package suga.engine.threads;

import suga.engine.GameEngine;
import suga.engine.graphics.GraphicsPanel;

/**
 * A thread used to refresh the graphics of a panel as fast as possible.
 *
 * @author Sugaku
 */
public class GraphicsThread extends Thread implements SugaThread {

    /**
     * Whether to exit the thread.
     */
    protected boolean stopped = false;

    /**
     * Whether to simulate game logic or not.
     */
    protected boolean paused = false;

    /**
     * The panel that should be redrawn every frame.
     */
    private final GraphicsPanel panel;

    /**
     * The time that this graphics thread was started. Used in calculating average frame rate.
     */
    private static long startTime = 0;

    /**
     * The number of frames that have been rendered since the thread started.
     */
    private static long frames = 0;

    /**
     * The target frame rate for this GraphicsThread.
     */
    private final int FRAME_RATE;

    /**
     * Creates a new graphics thread with the given panel.
     *
     * @param panel     The panel to refresh for every frame.
     * @param frameRate The target frequency to draw frames at.
     */
    public GraphicsThread (GraphicsPanel panel, int frameRate) {
        this.panel = panel;
        FRAME_RATE = frameRate;
        panel.setThread(this);
    }

    /**
     * Sets whether the thread is paused or not.
     *
     * @param val Whether the thread should be paused or not.
     */
    public void setPaused (boolean val) {
        paused = val;
    }

    /**
     * Accessor method for the current status of the thread.
     *
     * @return Whether the thread is paused currently or not.
     */
    public boolean getPaused () {
        return paused;
    }

    /**
     * Sets whether the thread is stopped or not.
     *
     * @param val Whether the thread should be stopped.
     */
    public void setStopped (boolean val) {
        stopped = val;
    }

    /**
     * Accessor method for the current status of the thread.
     *
     * @return Whether this thread has been stopped or not.
     */
    public boolean getStopped () {
        return stopped;
    }

    /**
     * Called to run the Graphics thread.
     */
    @Override
    public void run () {
        startTime = System.currentTimeMillis();
        long lastFinished = 0;
        while (!stopped) {
            long drawTime = System.currentTimeMillis() - lastFinished;
            if (drawTime < (1000 / FRAME_RATE)) {
                try {
                    //noinspection BusyWait
                    sleep((int) ((1000 / FRAME_RATE) - drawTime));
                } catch (Exception e) {
                    GameEngine.getInstance().getLogger().log(e);
                }
            }
            lastFinished = System.currentTimeMillis();
            if (!paused) {
                try {
                    panel.repaint();
                } catch (Exception e) {
                    GameEngine.getInstance().getLogger().log(e);
                }
            }
            frames++;
        }
    }

    /**
     * Returns the average frame rate while this GraphicsThread has been running.
     *
     * @return The average frame rate of this thread since starting.
     */
    public static double getFPS () {
        return (frames * 1.0) / ((System.currentTimeMillis() - startTime) / 1000.0);
    }
}
