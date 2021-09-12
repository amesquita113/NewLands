package NewLands.screens;

import java.awt.event.KeyEvent;
import NewLands.World;
import asciiPanel.AsciiPanel;
import NewLands.WorldBuilder;

public class PlayScreen implements Screen {

    private World world;
    private int centerX;
    private int centerY;
    private int screenWidth;
    private int screenHeight;

    public PlayScreen() {
        screenWidth = 80;
        screenHeight = 21;
        createWorld();
    }

    private void createWorld() {
        world = new WorldBuilder(90, 32)
                                .makeCaves()    
                                .build();
    }

    public int getScrollx() {
        return Math.max(0, Math.min(centerX - screenWidth / 2, world.width() - screenWidth));
    }

    public int getScrolly() {
        return Math.max(0, Math.min(centerY - screenHeight / 2, world.height() - screenHeight));
    }

    public void displayOutput(AsciiPanel terminal) {
        int left = getScrollx();
        int top = getScrolly();

        displayTiles(terminal, left, top);

        terminal.write('@', centerX - left, centerY - top);

        terminal.writeCenter("--- press [ESC] to lose or [Enter] to win ---", 22);
    }

    private void displayTiles(AsciiPanel terminal, int left, int top) {
        for (int x = 0; x < screenWidth; x++){
            for (int y = 0; y < screenHeight; y++){
                int wx = x + left;
                int wy = y + top;

                terminal.write(world.glyph(wx, wy), x, y, world.color(wx, wy));
            }
        }
    }
    
    private void scrollBy(int mx, int my) {
        centerX = Math.max(0, Math.min(centerX + mx, world.width() - 1));
        centerY = Math.max(0, Math.min(centerY + my, world.height() - 1));
    }

    public Screen respondToUserInput(KeyEvent key) {
        switch (key.getKeyCode()){
            case KeyEvent.VK_ESCAPE: return new LoseScreen();
            case KeyEvent.VK_ENTER: return new WinScreen();
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_H: scrollBy(-1, 0); break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_L: scrollBy( 1, 0); break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_K: scrollBy( 0, -1); break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_J: scrollBy( 0, 1); break;
            case KeyEvent.VK_Y: scrollBy(-1,-1); break;
            case KeyEvent.VK_U: scrollBy( 1,-1); break;
            case KeyEvent.VK_B: scrollBy(-1, 1); break;
            case KeyEvent.VK_N: scrollBy( 1, 1); break;
        }

        return this;
    }

}
