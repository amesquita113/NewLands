package NewLands;

import java.awt.Color;
import asciiPanel.AsciiPanel;

public enum Tile {
    FLOOR((char)250, AsciiPanel.yellow),    // use a code page middle dot character for floors 
    WALL((char)178, AsciiPanel.green),     // use a code page block character for walls
    BOUNDS('x', AsciiPanel.brightBlack);    // use a 'x' character for boundries
 
    private char glyph;
    public char glyph() { return glyph; }

    private Color color;
    public Color color() { return color; }

    Tile(char glyph, Color color){
        this.glyph = glyph;
        this.color = color;
    }
}
