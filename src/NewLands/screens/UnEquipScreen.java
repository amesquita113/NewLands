package NewLands.screens;

import NewLands.Creature;
import NewLands.Item;

public class UnEquipScreen extends InventoryBasedScreen {
    
    public UnEquipScreen(Creature player) {
        super(player);
    }

    protected String getVerb() {
        return "unequip";
    }

    protected boolean isAcceptable(Item item) {
        return item.attackValue() > 0 || item.defenseValue() > 0;
    }

    protected Screen use(Item item) {
        player.unequip(item);
        return null;
    }
}
