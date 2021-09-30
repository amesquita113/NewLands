package NewLands;

import java.awt.Color;

public class Creature {
    private World world;

    public int x;
    public int y;
    public int z;

    private char glyph;
    public char glyph() { return glyph; }

    private Color color;
    public Color color() { return color; }

    private CreatureAi ai;
    public void setCreatureAi(CreatureAi ai) { this.ai = ai; }

    private int maxHp;
    public int maxHp() { return maxHp; }

    private int hp;
    public int hp() { return hp; }

    private int attackValue;
    public int attackValue() { 
        return attackValue
        + (weapon == null ? 0 : weapon.attackValue())
        + (armour == null ? 0 : armour.attackValue()); 
    }

    private int defenseValue;
    public int defenseValue() { 
        return defenseValue
        + (weapon == null ? 0 : weapon.defenseValue())
        + (armour == null ? 0 : armour.defenseValue()); 
    }

    private int visionRadius;
    public int visionRadius() { return visionRadius; }

    private String name;
    public String name() { return name; }

    private Inventory inventory;
    public Inventory inventory() { return inventory; }

    private int maxFood;
    public int maxFood() { return maxFood; }

    private int food;
    public int food() { return food; }

    private Item weapon;
    public Item weapon() { return weapon; }

    private Item armour;
    public Item armour() { return armour; }

    private int xp;
    public int xp() { return xp; }

    public void modifyXp(int amount) {
        xp += amount;

        notify("You %s %d xp.", amount < 0 ? "lose" : "gain", amount);

        while (xp > (int)(Math.pow(level, 1.5) * 20)) {
            level++;
            doAction("advance to level %d", level);
            ai.onGainLevel();
            modifyHp(level * 2);
        }
    }

    private int level;
    public int level() { return level; }

    public Creature(World world, char glyph, Color color, String name, int maxHp, int attack, int defense) {
        this.world = world;
        this.glyph = glyph;
        this.color = color;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.attackValue = attack;
        this.defenseValue = defense;
        this.visionRadius = 9;
        this.name = name;
        this.inventory = new Inventory(20);
        this.maxFood = 1500;
        this.food = maxFood / 3 * 2;
    }

    public void moveBy(int mx, int my, int mz) {
        if (mx == 0 && my == 0 && mz == 0)
            return;

        Tile tile = world.tile(x + mx, y + my, z + mz);

        if (mz == -1) {
            if (tile == Tile.STAIRS_DOWN) {
                modifyFood(-2);
                doAction("walk up the stairs to level %d", z + mz + 1);
            } else {
                doAction("try to go up but are stopped by the cave ceiling");
                return;
            }
        } else if (mz == 1) {
            if (tile == Tile.STAIRS_UP) {
                modifyFood(-2);
                doAction("Walk down the stairs to level %d", z + mz + 1);
            } else {
                doAction("try to go down but are stopped by the cave floor");
                return;
            }
        }


        Creature other = world.creature(x + mx, y + my, z + mz);

        if (other == null)
            ai.onEnter(x + mx, y + my, z + mz, tile);
        else
            attack(other);
    }

    public void attack(Creature other) {
        int amount = Math.max(0, attackValue() - other.defenseValue());

        amount = (int)(Math.random() * amount) + 1;

        doAction("attack the '%s' for %d damage", other.name, amount);

        other.modifyHp(-amount);

        if (other.hp < 1)
            gainXp(other);
    }

    public void gainXp(Creature other) {
        int amount = other.maxHp
            + other.attackValue()
            + other.defenseValue()
            - level * 2;

        if (amount > 0)
            modifyXp(amount);
    }

    public void modifyHp(int amount) {
        hp += amount;

        if (hp < 1) {
            doAction("die");
            leaveCorpse();
            world.remove(this);
        }
    }

    private void leaveCorpse() {
        Item corpse = new Item('%', color, name + " corpse");
        corpse.modifyFoodValue(maxHp * 3);
        world.addAtEmptySpace(corpse, x, y, z);
    }

    public void dig(int wx, int wy, int wz) {
        // use 10 food for digging 
        modifyFood(-10);

        world.dig(wx, wy, wz);
        doAction("dig");
    }

    public void update() {
        // use 1 food for movement
        modifyFood(-1);

        ai.onUpdate();
    }

    public boolean canEnter(int  wx, int wy, int wz) {
        return world.tile(wx, wy, wz).isGround() && world.creature(wx, wy, wz) == null;
    }

    public void notify(String message, Object ... params) {
        ai.onNotify(String.format(message, params));
    }

    public void doAction(String message, Object ... params) {
        int r = 9;
        for (int ox = -r; ox < r + 1; ox++) {
            for (int oy = -r; oy < r + 1; oy++){
                if (ox*ox + oy*oy > r*r) 
                    continue;

                Creature other = world.creature(x + ox, y + oy, z);

                if (other == null) 
                    continue;

                if (other == this) 
                    other.notify("You " + message + ".", params);
                else if (other.canSee(x, y, z))
                    other.notify(String.format("The '%s' %s.", name, makeSecondPerson(message)), params);
            }
        }
    }

    private String makeSecondPerson(String text) {
        String[] words = text.split(" ");
        words[0] = words[0] + "s";

        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            builder.append(" ");
            builder.append(word);
        }

        return builder.toString().trim();
    }

    public boolean canSee(int wx, int wy, int wz) {
        return ai.canSee(wx, wy, wz);
    }

    public Tile tile(int wx, int wy, int wz) {
        return world.tile(wx, wy, wz);
    }

    public Creature creature(int wx, int wy, int wz) {
        return world.creature(wx, wy, wz);
    }

    public void pickup() {
        Item item = world.item(x, y, z);

        if (inventory.isFull() || item == null) {
            doAction("grab at the ground");
        } else {
            doAction("pickup a %s", item.name());
            world.remove(x, y, z);
            inventory.add(item);
        }
    }

    public void drop(Item item) {
        if (world.addAtEmptySpace(item, x, y, z)) {
            doAction("drop a " + item.name());
            inventory.remove(item);
            unequip(item);
        } else {
            notify("There's nowhere to drop the %s.", item.name());
        }
    }
    
    public void modifyFood(int amount) {
        food += amount;

        if (food > maxFood) {
            maxFood = maxFood + food / 2;
            food = maxFood;
            notify("You can't believe your stomach can hold that much!");
            modifyHp(-1);
        } else if (food < 1 && isPlayer()) {
            modifyHp(-1);
        }
    }

    public boolean isPlayer() {
        return glyph == '@';
    }

    public void eat(Item item) {
        if (item.foodValue() < 0)
            notify("Gross!");

        modifyFood(item.foodValue());
        inventory.remove(item);
        unequip(item);
        if (hp < maxHp && item.foodValue() > 0) {                       // food heals you for 1/10 of its food value
            hp += item.foodValue() * 0.1;
            if (hp > maxHp) hp = maxHp;
        }
    }

    public void unequip(Item item) {
        if (item == null)
            return;

        if (item == armour) {
            doAction("remove a " + item.name());
            armour = null;
        } else if (item == weapon) {
            doAction("put away a " + item.name());
            weapon = null;
        }
    }

    public void equip(Item item) {
        if (item.attackValue() == 0 && item.defenseValue() == 0)
            return;

        if (item.attackValue() >= item.defenseValue()) {
            unequip(weapon);
            doAction("weild a " + item.name());
            weapon = item;
        } else {
            unequip(armour);
            doAction("put on a " + item.name());
            armour = item;
        }
    }

    public void gainMaxHp() {
        maxHp += 10;
        hp += 10;
        doAction("look healthier");
    }

    public void gainAttackValue() {
        attackValue += 2;
        doAction("look stronger");
    }

    public void gainDefenseValue() {
        defenseValue += 2;
        doAction("look tougher");
    }

    public void gainVision() {
        visionRadius += 1;
        doAction("look more aware");
    }
}
