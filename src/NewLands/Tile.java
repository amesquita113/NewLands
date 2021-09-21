package NewLands;

import java.awt.Color;
import asciiPanel.AsciiPanel;

public enum Tile {
    FLOOR((char)250, AsciiPanel.yellow),    // use a code page middle dot character for floors 
    WALL((char)176, AsciiPanel.green),      // use a code page block character for walls
    STAIRS_DOWN('>', AsciiPanel.white),
    STAIRS_UP('<', AsciiPanel.white),
    BOUNDS('X', AsciiPanel.brightBlack),    // use a 'x' character for boundries
    UNKNOWN(' ', AsciiPanel.white);
 
    private char glyph;
    public char glyph() { return glyph; }

    private Color color;
    public Color color() { return color; }

    Tile(char glyph, Color color){
        this.glyph = glyph;
        this.color = color;
    }

    public boolean isDiggable() {
        return this == Tile.WALL;
    }

    public boolean isGround() {
        return this != WALL && this != BOUNDS;
    }
}
