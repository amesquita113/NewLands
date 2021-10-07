package NewLands;

import java.awt.Color;
import asciiPanel.AsciiPanel;

public enum Tile {
    FLOOR((char)250, AsciiPanel.yellow, "A dirt and rock cave floor."),    // use a code page middle dot character for floors 
    WALL((char)176, AsciiPanel.green, "A dirt and rock cave wall."),      // use a code page block character for walls
    STAIRS_DOWN('>', AsciiPanel.white, "A stone staircase leading down."),
    STAIRS_UP('<', AsciiPanel.white, "A stone staircase leading up."),
    BOUNDS('x', AsciiPanel.brightBlack, "Beyond the edge of the world."),    // use a 'x' character for boundries
    UNKNOWN(' ', AsciiPanel.white, "(unknown)");

    private String details;
    public String details() { return details; }
 
    private char glyph;
    public char glyph() { return glyph; }

    private Color color;
    public Color color() { return color; }

    Tile(char glyph, Color color, String details){
        this.glyph = glyph;
        this.color = color;
        this.details = details;
    }

    public boolean isDiggable() {
        return this == Tile.WALL;
    }

    public boolean isGround() {
        return this != WALL && this != BOUNDS;
    }
}
