package sugaEngine.input;

import javax.swing.*;

/**
 * Reference to the StackGameKeyListener which is the GameKeyListener for version 1.1.1-BETA. This name will be taken by
 * an interface in v2.1.1.
 *
 * @author Sugaku
 */
@Deprecated
public class GameKeyListener extends StackGameKeyListener {

    /**
     * Creates a new StackGameKeyListener for use in a game.
     *
     * @param frame The JFrame that this KeyListener is using.
     */
    @Deprecated
    public GameKeyListener (JFrame frame) {
        super(frame);
    }

    /**
     * Sets the frame that this key listener is listening to. Does not deregister with the old frame.
     *
     * @param frame The new frame to listen to.
     */
    public void setFrame (JFrame frame) {
        frame.addKeyListener(this);
    }
}
