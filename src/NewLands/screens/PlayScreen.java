package NewLands.screens;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import asciiPanel.AsciiPanel;
import NewLands.StuffFactory;
import NewLands.Tile;
import NewLands.Creature;
import NewLands.FieldOfView;
import NewLands.Item;
import NewLands.WorldBuilder;
import NewLands.World;

public class PlayScreen implements Screen {

    private World world;
    private Creature player;   
    private int screenWidth;
    private int screenHeight;
    private List<String> messages;
    private FieldOfView fov;
    private Screen subscreen;

    public PlayScreen() {
        screenWidth = 80;
        screenHeight = 21;
        messages = new ArrayList<String>();
        createWorld();
        fov = new FieldOfView(world);

        StuffFactory factory = new StuffFactory(world);
        createCreatures(factory);
        createItems(factory);
    }

    private void createCreatures(StuffFactory creatureFactory) {
        player = creatureFactory.newPlayer(messages, fov);

        for (int z = 0; z < world.depth(); z++) {
            
            // created 8 fungus per level
            for (int i = 0; i < 8; i++) {      
                creatureFactory.newFungus(z);
            }

            // creates 15 bats per level
            for (int i = 0; i < 15; i++) {
                creatureFactory.newBat(z);
            }

            // creates 4 zombies plus the level per level
            for (int i = 0; i < z + 4; i++) {
                creatureFactory.newZombie(z, player);
            }

            // creates 1 goblins plus the level per level
            for (int i = 0; i < z + 1; i++) {
                creatureFactory.newGoblin(z, player);
            }
        }
    }
    
    private void createWorld() {
        // System.out.println("Building world ... Please wait ...");
        world = new WorldBuilder(90, 32, 5)       // builds the world to specified dimensions (width, height, depth)
                                .makeCaves()    
                                .build();
    }

    private void createItems(StuffFactory factory) {    // adds rocks into the open tile spaces
        for (int z = 0; z < world.depth(); z++) {
            for (int i = 0; i < world.width() * world.height() / 20; i++) {
                factory.newRock(z);
            }
        }

        for (int z = 0; z < world.depth(); z++) {       // adds bread and apples in 2 random locations per level
            for (int i = 0; i < 2; i++) {
                factory.newBread(z);
                factory.newApple(z);
            }
            
            // adds one of these per level
            factory.randomArmour(z);
            factory.randomWeapon(z);
            factory.randomWeapon(z);
        }

        factory.newVictoryItem(world.depth() - 1);
    }

    public int getScrollX() {
        return Math.max(0, Math.min(player.x - screenWidth / 2, world.width() - screenWidth));
    }

    public int getScrollY() {
        return Math.max(0, Math.min(player.y - screenHeight / 2, world.height() - screenHeight));
    }

    @Override
    public void displayOutput(AsciiPanel terminal) {
        int left = getScrollX();
        int top = getScrollY();

        displayTiles(terminal, left, top);
        displayMessages(terminal, messages);

        // terminal.writeCenter("--- press [ESC] to lose or [Enter] to win ---", 23);

        String stats = String.format(" %3d/%3d HP - %8s", player.hp(), player.maxHp(), hunger());
        terminal.write(stats, 1, 23);

        if (subscreen != null)
            subscreen.displayOutput(terminal);
    }

    private String hunger() {
        if (player.food() < player.maxFood() * 0.1)
            return "Starved...";
        else if (player.food() < player.maxFood() * 0.2)
            return "Starving";
        else if (player.food() < player.maxFood() * 0.4)
            return "Hungry";
        else if (player.food() > player.maxFood() * 0.9)
            return "Stuffed";
        else if (player.food() > player.maxFood() * 0.8)
            return "Full";
        else 
            return "Well fed";
    }

    private void displayMessages(AsciiPanel terminal, List<String> messages) {
        int top = screenHeight - messages.size();
        for (int i = 0; i < messages.size(); i++) {
            terminal.writeCenter(messages.get(i), top + i + 2);
            //terminal.write(messages.get(i), 30, top + i + 2);
        }
        messages.clear();
    }
    
    private void displayTiles(AsciiPanel terminal, int left, int top) {
        fov.update(player.x, player.y, player.z, player.visionRadius());


        for (int x = 0; x < screenWidth; x++){
            for (int y = 0; y < screenHeight; y++){
                int wx = x + left;
                int wy = y + top;

                if (player.canSee(wx, wy, player.z))
                    terminal.write(world.glyph(wx, wy, player.z), x, y, world.color(wx, wy, player.z));
                else
                    terminal.write(fov.tile(wx, wy, player.z).glyph(), x, y, Color.darkGray);
            }
        }
    }
    
    /*
    private void scrollBy(int mx, int my) {
        centerX = Math.max(0, Math.min(centerX + mx, world.width() - 1));
        centerY = Math.max(0, Math.min(centerY + my, world.height() - 1));
    } 
    */

    @Override
    public Screen respondToUserInput(KeyEvent key) {
        int level = player.level();

        if (subscreen != null) {
            subscreen = subscreen.respondToUserInput(key);
        } else {
            switch (key.getKeyCode()){
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_H: player.moveBy(-1, 0, 0); break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_L: player.moveBy( 1, 0, 0); break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_K: player.moveBy( 0, -1, 0); break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_J: player.moveBy( 0, 1, 0); break;
                case KeyEvent.VK_Y: player.moveBy(-1,-1, 0); break;
                case KeyEvent.VK_U: player.moveBy( 1,-1, 0); break;
                case KeyEvent.VK_B: player.moveBy(-1, 1, 0); break;
                case KeyEvent.VK_N: player.moveBy( 1, 1, 0); break;
                case KeyEvent.VK_D: subscreen = new DropScreen(player); break;
                case KeyEvent.VK_E: subscreen = new EatScreen(player); break;
                case KeyEvent.VK_W: subscreen = new EquipScreen(player); break;
                case KeyEvent.VK_R: subscreen = new UnEquipScreen(player); break;
                case KeyEvent.VK_X: subscreen = new ExamineScreen(player); break;
                case KeyEvent.VK_SEMICOLON: subscreen = new LookScreen(player, "Looking",
                                                                        player.x - getScrollX(),
                                                                        player.y - getScrollY());
                                                                        break;
                case KeyEvent.VK_T: subscreen = new ThrowScreen(player, player.x - getScrollX(),
                                                                        player.y - getScrollY()); 
                                                                        break;
                case KeyEvent.VK_F:
                    if (player.weapon() == null || player.weapon().rangedAttackValue() == 0)
                        player.notify("You don't have a ranged weapon equiped.");
                    else
                        subscreen = new FireWeaponScreen(player, player.x - getScrollX(),
                                                                player.y - getScrollY()); 
                                                                break;
            }
        
            switch (key.getKeyChar()) {
                case 'g':
                case ',': player.pickup(); break;
                case '<': 
                    if (userISTryingToExit())
                        return userExits();
                    else
                        player.moveBy(0, 0, -1);
                        break;
                case '>': player.moveBy(0, 0, 1); break;
                case '?': subscreen = new HelpScreen(); break;
            }
        }

        if (player.level() > level)
            subscreen = new LevelUpScreen(player, player.level() - level);

        if (subscreen == null)
            world.update();

        if (player.hp() < 1)
            return new LoseScreen();
            
        return this;
    }

    private boolean userISTryingToExit() {
        return player.z == 0 && world.tile(player.x, player.y, player.z) == Tile.STAIRS_UP;
    }

    private Screen userExits() {
        for (Item item : player.inventory().getItems()) {
            if (item != null && item.name().equals("teddy bear"))
                return new WinScreen();
        }
        return new LoseScreen();
    }

}
