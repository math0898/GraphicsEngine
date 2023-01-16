package suga.engine.input.mouse;

import suga.engine.GameEngine;
import suga.engine.logger.Level;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Stack;

/**
 * The BasicMouseListener is a simple implementation of the GameMouseListener which only pays attention to mouse presses
 * and mouse releases.
 *
 * @author Sugaku
 */
public class BasicMouseListener implements GameMouseListener {

    /**
     * A reference to the JFrame object that this GameMouseListener is listening to.
     */
    protected JFrame frame;

    /**
     * A stack of mouse click events that need to be processed.
     */
    protected Stack<MouseEvent> events = new Stack<>();

    /**
     * Creates a new GameMouseListener without a JFrame
     */
    public BasicMouseListener () {
        frame = null;
    }

    /**
     * Creates a new GameMouseListener with the given JFrame so that the mouse position can be grabbed at any point.
     *
     * @param frame The JFrame instance that this mouse listener should be listening to.
     */
    public BasicMouseListener (JFrame frame) {
        setFrame(frame);
    }

    /**
     * Returns the current position of the mouse.
     *
     * @return A point object which contains data about the current mouse position.
     */
    @Override
    public Point getMousePos () {
        if (frame == null) {
            GameEngine.getInstance().getLogger().log("BasicMouseListener: Attempted to read mouse position when JFrame is not initialized.", Level.EXCEPTION);
            return null;
        }
        GameEngine.getInstance().getLogger().log("BasicMouseListener: Read mouse position: " + frame.getMousePosition(), Level.VERBOSE);
        return frame.getMousePosition();
    }

    /**
     * Invoked when the mouse button has been clicked (pressed
     * and released) on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseClicked (MouseEvent e) {

    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mousePressed (MouseEvent e) {
        GameEngine.getInstance().getLogger().log("BasicMouseListener: Received mouse pressed event: " + e, Level.VERBOSE);
        events.add(e);
    }

    /**
     * Invoked when a mouse button has been released on a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseReleased (MouseEvent e) {
        GameEngine.getInstance().getLogger().log("BasicMouseListener: Received mouse release event: " + e, Level.VERBOSE);
        events.add(e);
    }

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseEntered (MouseEvent e) {

    }

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e the event to be processed
     */
    @Override
    public void mouseExited (MouseEvent e) {

    }

    /**
     * Accessor method for the stack of mouse events that need to be handled.
     *
     * @return The stack of mouse events.
     */
    @Override
    public Stack<MouseEvent> getEvents () {
        return events;
    }

    /**
     * Sets the frame that this mouse listener is listening to. Does not deregister with the old frame.
     *
     * @param frame The new frame to listen to.
     */
    @Override
    public void setFrame (JFrame frame) {
        GameEngine.getInstance().getLogger().log("BasicMouseListener: Assigning this listener to a new JFrame.", Level.DEBUG);
        frame.addMouseListener(this);
        this.frame = frame;
    }
}
