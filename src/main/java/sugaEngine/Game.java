package sugaEngine;

import sugaEngine.graphics.GraphicsPanel;

import java.util.HashMap;
import java.util.Map;

/**
 * Games require a main game loop to run along with game components that need to be run every game cycle.
 *
 * @author Sugaku
 */
public abstract class Game {

    /**
     * A list of game objects for this game. Their logic should be called every cycle.
     */
    protected Map<String, GameObject> objects = new HashMap<>();

    /**
     * The graphics panel that should be used to register draw listeners to.
     */
    protected GraphicsPanel panel;

    /**
     * The key listener that is being used by this game.
     */
    protected GameKeyListener keyListener;

    /**
     * Creates a new game with the given panel used to register GameObjects as draw listeners to.
     *
     * @param panel The panel that GameObjects should register as a listener to.
     * @param listener The game key listener being used by this game object.
     */
    public Game (GraphicsPanel panel, GameKeyListener listener) {
        this.panel = panel;
        keyListener = listener;
    }

    /**
     * The main logic loop for the game. Will be called depending on the rate of the logic thread.
     */
    public void loop () {
        for (GameObject gO : objects.values()) gO.runLogic();
    }

    /**
     * Processes inputs given by players. Is run during pause.
     */
    public void processInput () {

    }

    /**
     * Adds a GameObject by the given name.
     *
     * @param name The name of the game object. Can be used later to remove the item.
     * @param object The game object to add.
     */
    public void addGameObject (String name, GameObject object) {
        objects.put(name, object);
        panel.registerListener(object);
    }
}
