package NewLands;

import java.util.List;

import asciiPanel.AsciiPanel;

public class StuffFactory {
    private World world;

    public StuffFactory(World world) {
        this.world = world;
    }

    /************************************ Player/NPC Section **************************************/
    public Creature newPlayer(List<String> messages, FieldOfView fov) {
        Creature player = new Creature(world, '@', AsciiPanel.brightWhite, "player", 100, 20, 5);
        world.addAtEmptyLocation(player, 0);
        new PlayerAi(player, messages, fov);
        return player;
    }

    public Creature newFungus(int depth) {
        Creature fungus = new Creature(world, 'f', AsciiPanel.green, "fungus", 10, 0, 0);
        world.addAtEmptyLocation(fungus, depth);
        new FungusAi(fungus, this);
        
        return fungus;
    }

    public Creature newBat(int depth) {
        Creature bat = new Creature(world, 'b', AsciiPanel.yellow, "bat", 15, 5, 0);
        world.addAtEmptyLocation(bat, depth);
        new BatAi(bat);
        return bat;
    }

    // Zombie, the first player hunting creature
    public Creature newZombie(int depth, Creature player) {
        Creature zombie = new Creature(world, 'z', AsciiPanel.white, "zombie", 50, 10, 10);
        world.addAtEmptyLocation(zombie, depth);
        new ZombieAi(zombie, player);
        return zombie;
    }

    public Creature newGoblin(int depth, Creature player) {
        Creature goblin = new Creature(world, 'g', AsciiPanel.brightGreen, "goblin", 66, 15, 5);
        goblin.equip(randomWeapon(depth));
        goblin.equip(randomArmour(depth));
        world.addAtEmptyLocation(goblin, depth);
        new GoblinAi(goblin, player);
        return goblin;
    }

    public Item newRock(int depth) {
        Item rock = new Item(',', AsciiPanel.yellow, "rock");
        rock.modifyThrownAttackValue(5);
        world.addAtEmptyLocation(rock, depth);
        return rock;
    }

    /************************************ Food Section **************************************/
    // Bread, a high value food item
    public Item newBread(int depth) {
        Item bread = new Item('\'', AsciiPanel.brightWhite, "bread");
        world.addAtEmptyLocation(bread, depth);
        bread.modifyFoodValue(300);
        return bread;
    }

    // Apple, a high value food item
    public Item newApple(int depth) {
        Item apple = new Item('\'', AsciiPanel.brightRed, "apple");
        world.addAtEmptyLocation(apple, depth);
        apple.modifyFoodValue(500);
        return apple;
    }

    /************************************ Potion Section **************************************/
    // +15 health potion
    public Item newPotionOfHealth(int depth) {
        Item item = new Item('!', AsciiPanel.white, "health potion");
        item.setQuaffEffect(new Effect(1){
            public void start(Creature creature) {
                if (creature.hp() == creature.maxHp())
                    return;

                creature.modifyHp(15);
                creature.doAction("look healthies");
            }
        });

        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newManaPotion(int depth) {
        Item item = new Item('!', AsciiPanel.white, "mana potion");
        item.setQuaffEffect(new Effect(1){
            public void start(Creature creature) {
                if (creature.mana() == creature.maxMana())
                    return;

                creature.modifyMana(10);
                creature.doAction("feel more magical");
            }
        });

        world.addAtEmptyLocation(item, depth);
        return item;
    }

    // poison potion
    public Item newPotionOfPoison(int depth) {
        Item item = new Item('!', AsciiPanel.white, "poison potion");
        item.setQuaffEffect(new Effect(20){
            public void start(Creature creature) {
                creature.doAction("look sick");
            }

            public void update(Creature creature) {
                super.update(creature);
                creature.modifyHp(-1);
            }
        });

        world.addAtEmptyLocation(item, depth);
        return item;
    }

    // warriors potion - increases attack and defence by 5
    public Item newPotionOfWarrior(int depth) {
        Item item = new Item('!', AsciiPanel.white, "warrior's potion");
        item.setQuaffEffect(new Effect(20){
            public void start(Creature creature) {
                creature.modifyAttackValue(5);
                creature.modifyDefenseValue(5);
                creature.doAction("look stronger");
            }

            public void end(Creature creature) {
                creature.modifyAttackValue(-5);
                creature.modifyDefenseValue(-5);
                creature.doAction("look normal");
            }
        });

        world.addAtEmptyLocation(item, depth);
        return item;
    }

    // randomizer to create potions on the maps
    public Item randomPotion(int depth) {
        switch ((int)(Math.random() * 4)){
            case 0: return newPotionOfHealth(depth);
            case 1: return newPotionOfPoison(depth);
            case 2: return newManaPotion(depth);
            default: return newPotionOfWarrior(depth);
        }
    }

    /************************************ Victory item Section **************************************/
    public Item newVictoryItem(int depth) {
        Item item = new Item('*', AsciiPanel.brightWhite, "teddy bear");
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    /************************************ Spellbook Section **************************************/
    public Item newWhiteMagesSpellbook(int depth) {
        Item item = new Item('?', AsciiPanel.brightWhite, "white mage's spellbook");
        
        item.addWrittenSpell("minor heal", 4, new Effect(1){
            public void start(Creature creature){
                if (creature.hp() == creature.maxHp())
                    return;
                
                creature.modifyHp(20);
                creature.doAction("look healthier");
            }
        });

        item.addWrittenSpell("major heal", 8, new Effect(1){
            public void start(Creature creature){
                if (creature.hp() == creature.maxHp())
                    return;

                creature.modifyHp(50);
                creature.doAction("look much healthier");
            }
        });

        item.addWrittenSpell("heal over time", 12, new Effect(50){
            public void update(Creature creature){
                super.update(creature);
                creature.modifyHp(2);
            }
        });

        item.addWrittenSpell("inner strength", 12, new Effect(50){
            public void start(Creature creature){
                creature.modifyAttackValue(2);
                creature.modifyDefenseValue(2);
                creature.modifyVisionRadius(2);
                creature.modifyRegenHpPer1000(10);
                creature.modifyRegenManaPer1000(-10);
                creature.doAction("glows with inner strength");
            }

            public void update(Creature creature){
                super.update(creature);
                if (Math.random() < 0.25)
                    creature.modifyHp(1);
            }

            public void end(Creature creature){
                creature.modifyAttackValue(-2);
                creature.modifyDefenseValue(-2);
                creature.modifyVisionRadius(-2);
                creature.modifyRegenHpPer1000(-10);
                creature.modifyRegenManaPer1000(10);
            }
        });

        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newBlueMagesSpellbook(int depth) {
        Item item = new Item('?', AsciiPanel.brightBlue, "blue mage's spellbook");

        item.addWrittenSpell("blood to mana", 1, new Effect(1){
            public void start(Creature creature){
                int amount = Math.min(creature.hp() - 1, creature.maxMana() - creature.mana());
                creature.modifyHp(-amount);
                creature.modifyMana(amount);
            }
        });

        item.addWrittenSpell("blink", 6, new Effect(1){
            public void start(Creature creature){
                creature.doAction("fade out");

                int mx = 0;
                int my = 0;

                do
                {
                    mx = (int)(Math.random() * 11) - 5;
                    my = (int)(Math.random() * 11) - 5;
                }
                while (!creature.canEnter(creature.x+mx, creature.y+my, creature.z)
                        && creature.canSee(creature.x+mx, creature.y+my, creature.z));
                creature.moveBy(mx, my, 0);

                creature.doAction("fade in");
            }
        });

        item.addWrittenSpell("summon bats", 11, new Effect(1){
            public void start(Creature creature){
                for (int ox = -1; ox < 2; ox++){
                    for (int oy = -1; oy < 2; oy++){
                        int nx = creature.x + ox;
                        int ny = creature.y + oy;
                        if (ox == 0 && oy == 0 || creature.creature(nx, ny, creature.z) != null)
                            continue;

                        Creature bat = newBat(0);

                        if (!bat.canEnter(nx, ny, creature.z)){
                            world.remove(bat);
                            continue;
                        }

                        bat.x = nx;
                        bat.y = ny;
                        bat.z = creature.z;

                        creature.summon(bat);
                    }
                }
            }
        });

        item.addWrittenSpell("detect creatures", 16, new Effect(75){
            public void start(Creature creature){
                creature.doAction("look far off into the distance");
                creature.modifyDetectCreatures(1);
            }
            
            public void end(Creature creature){
                creature.modifyDetectCreatures(-1);
            }
        });

        world.addAtEmptyLocation(item, depth);
        return item; 
    }

    public Item randomSpellBook(int depth) {
        switch ((int)(Math.random() * 2)){
            case 0: return newWhiteMagesSpellbook(depth);
            default: return newBlueMagesSpellbook(depth);
        }
    }

    /************************************ Weapons Section **************************************/
    public Item newDagger(int depth) {
        Item item = new Item('|', AsciiPanel.white, "dagger");
        item.modifyAttackValue(5);
        item.modifyThrownAttackValue(5);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newSword(int depth) {
        Item item = new Item('|', AsciiPanel.brightWhite, "sword");
        item.modifyAttackValue(10);
        item.modifyThrownAttackValue(3);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newStaff(int depth) {
        Item item = new Item('_', AsciiPanel.yellow, "staff");
        item.modifyAttackValue(5);
        item.modifyDefenseValue(3);
        item.modifyThrownAttackValue(3);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newBow(int depth) {
        Item item = new Item('}', AsciiPanel.yellow, "bow");
        item.modifyAttackValue(1);
        item.modifyRangedAttackValue(5);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    /************************************ Armour Section **************************************/
    public Item newLightArmour(int depth) {
        Item item = new Item('(', AsciiPanel.green, "tunic");
        item.modifyDefenseValue(2);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newMediumArmour(int depth) {
        Item item = new Item('[', AsciiPanel.white, "chainmail");
        item.modifyDefenseValue(4);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item newHeavyArmour(int depth) {
        Item item = new Item('[', AsciiPanel.brightWhite, "platemail");
        item.modifyDefenseValue(6);
        world.addAtEmptyLocation(item, depth);
        return item;
    }

    public Item randomWeapon(int depth) {
        switch ((int)(Math.random() * 3)) {
            case 0: return newDagger(depth);
            case 1: return newSword(depth);
            case 2: return newBow(depth);
            default: return newStaff(depth);
        }
    }

    public Item randomArmour(int depth) {
        switch ((int)(Math.random() * 3)) {
            case 0: return newLightArmour(depth);
            case 1: return newMediumArmour(depth);
            default: return newHeavyArmour(depth);
        }
    }
}
