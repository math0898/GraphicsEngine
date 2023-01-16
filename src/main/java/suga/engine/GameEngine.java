package suga.engine;

import suga.engine.graphics.GraphicsPanel;
import suga.engine.input.mouse.GameMouseListener;
import suga.engine.logger.GeneralLogger;
import suga.engine.logger.Logger;
import suga.engine.threads.GameLogicThread;
import suga.engine.threads.GraphicsThread;
import suga.engine.threads.SugaThread;
import suga.engine.input.keyboard.GameKeyListener;
import suga.engine.game.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

/**
 * A collection of static methods that are used to make starting, closing and managing a game easier.
 *
 * @author Sugaku
 */
public class GameEngine {

    /**
     * Reference to the single GameEngine that exists at runtime.
     */
    private static GameEngine globalEngine;

    /**
     * The currently opened frame.
     */
    private JFrame frame;

    /**
     * The current graphics thread if one is running.
     */
    private SugaThread graphics;

    /**
     * The current game logic thread if one is running.
     */
    private SugaThread logic;

    /**
     * The logger currently being used by the GameEngine.
     */
    private Logger logger = new GeneralLogger();

    /**
     * An enum of pre-defined resolutions to open a game window at.
     *
     * @author Sugaku
     */
    public enum Window {

        /**
         * Creates the window at the largest possible resolution in full screen mode.
         */
        FULL_SCREEN,

        /**
         * Creates the window at half the maximum size in each direction. Leaves top bar.
         */
        WINDOWED;

        /**
         * Modifies the given frame's size depending on the given Window parameter.
         *
         * @param frame  The frame that should be modified to match the window state.
         */
        public void modifyFrame (JFrame frame) {
            Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
            switch (this) {
                case FULL_SCREEN -> {
                    frame.setSize(dim);
                    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    frame.setUndecorated(true);
                }
                case WINDOWED -> {
                    frame.setSize(dim.width / 2, dim.height / 2);
                    frame.setUndecorated(false);
                }
            }
        }
    }

    /**
     * The GameEngine constructor.
     */
    protected GameEngine () {

    }

    /**
     * Creates a new GameEngine object and assigns it to the static reference.
     */
    private static void createGameEngine () {
        globalEngine = new GameEngine();
    }

    /**
     * Accessor method to the global instance of the GameEngine.
     *
     * @return A reference to the GameEngine.
     */
    public static GameEngine getInstance () {
        if (globalEngine == null) createGameEngine();
        return globalEngine;
    }

    /**
     * Accessor method for the logger being used by the GameEngine.
     *
     * @return The logger currently in use by the GameEngine.
     */
    public Logger getLogger () {
        return logger;
    }

    /**
     * Setter method for the logger being used by the GameEngine.
     *
     * @param logger The new logger for the GameEngine to use.
     */
    public void setLogger (Logger logger) {
        this.logger.log("GameEngine: Switching to a new logger.");
        this.logger = logger;
        this.logger.log("GameEngine: Switched logger.");
    }

    /**
     * Closes both the logic and graphics thread.
     */
    public void stop () {
        logger.log("GameEngine: Stopping the game.");
        graphics.setStopped(true);
        logic.setStopped(true);
        logger.log("GameEngine: Stopped logic and graphics threads.");
        frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
        logger.log("GameEngine: Sent request to close frame.");
    }

    /**
     * Creates a new game window with all the possible configuration options being specified.
     *
     * @param window        An enum parameter which is used to create the desired window size.
     * @param name          The name for the resulting window.
     * @param panel         The graphics panel to be used for this game.
     * @param background    The background color for the panel.
     * @param logicRate     How many times per second the game logic should be called.
     * @param frameRate     How many frames per second should the graphics thread target.
     * @param keyListener   The key listener to be used for this new window. Will override active frame.
     * @param mouseListener The mouse listener to use for this window. Will override active frame.
     * @param game          The game to attach to this window. Will override currently active panel or input listeners.
     */
    public void launchGameWindow (Window window, String name, GraphicsPanel panel,
                                         Color background, int logicRate, int frameRate, GameKeyListener keyListener,
                                         GameMouseListener mouseListener, Game game) {
        logger.log("GameEngine: Starting the game window.");
        panel.setBackground(background);
        frame = new JFrame(name);
        window.modifyFrame(frame);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 4, dim.height / 4);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
        graphics = new GraphicsThread(panel, frameRate);
        graphics.start();
        logger.log("GameEngine: Started graphics thread and window.");
        game.setPanel(panel);
        keyListener.setFrame(frame);
        mouseListener.setFrame(frame);
        game.setKeyListener(keyListener);
        game.setMouseListener(mouseListener);
        logic = new GameLogicThread(game, logicRate);
        logic.start();
        logger.log("GameEngine: Linked input listeners and started game logic thread.");
    }
}
