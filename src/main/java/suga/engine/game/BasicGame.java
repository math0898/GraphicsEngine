package suga.engine.game;

import suga.engine.GameEngine;
import suga.engine.game.objects.AIAgent;
import suga.engine.game.objects.GameObject;
import suga.engine.graphics.DrawListener;
import suga.engine.graphics.GraphicsPanel;
import suga.engine.input.mouse.BasicMouseListener;
import suga.engine.input.mouse.GameMouseListener;
import suga.engine.logger.Level;
import suga.engine.physics.BasicPhysicsEngine;
import suga.engine.physics.PhysicsEngine;
import suga.engine.physics.collidables.Collidable;
import suga.engine.threads.SugaThread;
import suga.engine.input.keyboard.GameKeyListener;
import suga.engine.input.keyboard.KeyValue;

import java.awt.event.MouseEvent;
import java.util.*;

/**
 * Games require a main game loop to run along with game components that need to be run every game cycle.
 *
 * @author Sugaku
 */
public class BasicGame implements Game {

    /**
     * A list of game objects for this game. Their logic should be called every cycle.
     */
    protected Map<String, GameObject> objects = new HashMap<>();

    /**
     * The physics engine that will be used with this game.
     */
    protected PhysicsEngine physics = new BasicPhysicsEngine();

    /**
     * A list of AIAgents that should have their logic run every cycle.
     */
    protected List<AIAgent> agents = new ArrayList<>();

    /**
     * A Map of scenes indexed by name.
     */
    protected Map<String, Scene> scenes = new HashMap<>();

    /**
     * The currently loaded scene object.
     */
    protected Scene loadedScene;

    /**
     * The graphics panel that should be used to register draw listeners to.
     */
    protected GraphicsPanel panel;

    /**
     * The key listener that is being used by this game.
     */
    protected GameKeyListener keyListener;

    /**
     * The mouse listener that is being used by this game.
     */
    protected GameMouseListener mouseListener;

    /**
     * The thread that is currently running this Game. May be null in some cases.
     */
    protected SugaThread thread = null;

    /**
     * Creates a new game without a given panel and game input listeners. These should be set with setters before
     * calling {@link #loop()}.
     */
    public BasicGame () {

    }

    /**
     * Creates a new game with the given panel used to register GameObjects as draw listeners to.
     *
     * @param panel The panel that GameObjects should register as a listener to.
     * @param listener The game key listener being used by this game object.
     * @param mouseListener The mouse listener being using by this game object.
     */
    public BasicGame (GraphicsPanel panel, GameKeyListener listener, BasicMouseListener mouseListener) {
        this.panel = panel;
        keyListener = listener;
        this.mouseListener = mouseListener;
    }

    /**
     * Sets the thread that is calling loop() on this Game.
     *
     * @param thread The thread that will be calling loop() on this object.
     */
    @Override
    public final void setThread (SugaThread thread) {
        this.thread = thread;
    }

    /**
     * Sets the graphics panel being used by this game.
     *
     * @param panel The panel to assign to this game.
     */
    @Override
    public void setPanel (GraphicsPanel panel) {
        this.panel = panel;
    }

    /**
     * Sets the key listener currently being used by this game.
     *
     * @param listener The new key listener that this game should use.
     */
    @Override
    public void setKeyListener (GameKeyListener listener) {
        this.keyListener = listener;
    }

    /**
     * Sets the mouse listener currently being used by this game.
     *
     * @param listener The new mouse listener that this game should use.
     */
    @Override
    public void setMouseListener (GameMouseListener listener) {
        this.mouseListener = listener;
    }

    /**
     * Accessor method for the thread that is calling loop(). This can be used to determine whether the game has stopped
     * or paused.
     *
     * @return The thread that is running this game instance.
     */
    @Override
    public final SugaThread getThread () {
        return thread;
    }

    /**
     * The main logic loop for the game. Will be called depending on the rate of the logic thread.
     */
    @Override
    public void loop () {
        physics.checkCollisions();
        physics.update();
        for (AIAgent a : agents) a.runLogic();
        for (GameObject gO : objects.values()) gO.runLogic();
    }

    /**
     * Processes inputs given by players. Is run during pause.
     */
    @Override
    public void processInput () {
        if (loadedScene == null) {
            GameEngine.getInstance().getLogger().log("BasicGame: No loaded scene. Cannot process inputs.", Level.WARNING);
            return;
        }
        Stack<MouseEvent> mice = mouseListener.getEvents();
        while (mice.size() > 0) {
            MouseEvent e = mice.pop();
            loadedScene.mouseInput(e.getPoint(), e.getButton() == 1);
        }
        Stack<KeyValue> keys = keyListener.getKeyPresses();
        while (keys.size() > 0)
            loadedScene.keyboardInput(keys.pop(), true);
        keys = keyListener.getKeyReleases();
        while (keys.size() > 0)
            loadedScene.keyboardInput(keys.pop(), false);
    }

    /**
     * Adds a GameObject by the given name.
     *
     * @param name The name of the game object. Can be used later to remove the item.
     * @param object The game object to add.
     */
    @Override
    public void addGameObject (String name, GameObject object) {
        objects.put(name, object);
        DrawListener listener = object.getDrawListener();
        if (listener != null) panel.registerListener(listener);
        Collidable collidable = object.getCollider();
        if (collidable != null) physics.addObject(collidable);
    }

    /**
     * Accessor method for game objects.
     *
     * @param name The name of the object to attempt to get.
     * @return The found object or null.
     */
    @Override
    public GameObject getGameObject (String name) {
        return objects.get(name);
    }

    /**
     * Registers a new AI agent so that it can be called every tick after collisions but before object logic.
     *
     * @param agent The AIAgent to add into the list of agents.
     */
    @Override
    public void addAgent (AIAgent agent) {
        agents.add(agent);
    }

    /**
     * Registers a new DrawingListener. This will not register anything beyond the draw method. Usually used by UI,
     * hence the name.
     *
     * @param listener The draw listener that should be registered to this panel.
     */
    @Override
    public void addDrawingListener (DrawListener listener) {
        panel.registerListener(listener);
    }

    /**
     * Clears all AIAgents, physics managers, GameObjects, and PanelListeners.
     */
    @Override
    public void clear () {
        physics = new BasicPhysicsEngine();
        agents = new ArrayList<>();
        objects = new HashMap<>();
        if (panel != null) panel.clearListeners();
        else GameEngine.getInstance().getLogger().log("A clear of game objects was requested but this game does not have an active panel.", Level.WARNING);
        GameEngine.getInstance().getLogger().log("Cleared game objects.", Level.INFO);
    }

    /**
     * Attempts to load the starting scene for this game. If successful will return true, if un-found or loading fails,
     * will return false.
     *
     * @return True on a successful first scene load.
     */
    @Override
    public boolean loadStartScene () {
        return loadScene("start");
    }

    /**
     * Attempts to load the given scene. If successful with return true, if un-found or loading fails, will return
     * false.
     *
     * @param name The name of the scene to attempt loading.
     */
    @Override
    public boolean loadScene (String name) {
        Scene scene = scenes.get(name);
        if (scene == null) return false;
        if (scene.load(this)) {
            loadedScene = scene;
            return true;
        }
        return false;
    }

    /**
     * Accessor method for the graphics panel used by the server.
     *
     * @return The panel used by the server.
     */
    @Override
    public GraphicsPanel getPanel () {
        return panel;
    }

    /**
     * Accessor method for the mouse listener used by the server.
     *
     * @return The mouse listener used by the server.
     */
    @Override
    public GameMouseListener getMouseListener () {
        return mouseListener;
    }
}
