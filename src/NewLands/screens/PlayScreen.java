package NewLands.screens;

import java.awt.event.KeyEvent;
import asciiPanel.AsciiPanel;

public class PlayScreen implements Screen {

    public void displayOutput(AsciiPanel terminal) {
        terminal.write("This is the play screen.", 1, 1);
        terminal.writeCenter("--- press [ESC] to lose or [Enter] to win ---", 22);
    }
    
    public Screen respondToUserInput(KeyEvent key) {
        switch (key.getKeyCode()){
            case KeyEvent.VK_ESCAPE: return new LoseScreen();
            case KeyEvent.VK_ENTER: return new WinScreen();
        }

        return this;
    }
}
