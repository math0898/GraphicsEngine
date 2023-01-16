package suga.engine.threads;

import suga.engine.GameEngine;
import suga.engine.game.Game;

/**
 * A thread used to run game logic once every 1/60th of a second.
 *
 * @author Sugaku
 */
public class GameLogicThread extends Thread implements SugaThread {

    /**
     * Whether to exit the thread.
     */
    protected boolean stopped = false;

    /**
     * Whether to simulate game logic or not.
     */
    protected boolean paused = false;

    /**
     * The game that should be called once every 1/60th of a second.
     */
    private final Game game;

    /**
     * A constant on how fast game logic should be called.
     */
    private final int LOGIC_RATE;

    /**
     * Creates a new GameLogicThread.
     *
     * @param game The game to run.
     * @param rate How many times the logic should be run per second as a maximum.
     */
    public GameLogicThread (Game game, int rate) {
        this.game = game;
        LOGIC_RATE = rate;
        game.setThread(this);
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
     * Called to run the Game logic thread.
     */
    @Override
    public void run () {
        long lastFinished = 0;
        while (!stopped) {
            long logicTime = System.currentTimeMillis() - lastFinished;
            if (logicTime < (1000 / LOGIC_RATE)) {
                try {
                    //noinspection BusyWait
                    sleep((1000 / LOGIC_RATE) - logicTime);
                } catch (Exception e) {
                    GameEngine.getInstance().getLogger().log(e);
                }
            }
            lastFinished = System.currentTimeMillis();
            game.processInput();
            if (!paused) {
                try {
                    game.loop();
                } catch (Exception e) {
                    GameEngine.getInstance().getLogger().log(e);
                }
            }
        }
    }
}
