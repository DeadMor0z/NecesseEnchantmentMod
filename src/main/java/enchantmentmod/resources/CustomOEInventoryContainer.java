package enchantmentmod.resources;

import necesse.engine.Screen;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.customAction.ContentCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.enchants.Enchantable;
import necesse.level.maps.hudManager.floatText.ItemPickupText;


public class CustomOEInventoryContainer extends OEInventoryContainer {
    static int enchantCost = Config.getInstance().getEnchantmentCosts();
    public final EmptyCustomAction enchantButton = this.registerAction(new EmptyCustomAction() {
        protected void run() {
            if (!client.isServerClient()) {
                return;
            }
            OEInventory oeInventory = CustomOEInventoryContainer.this.getOEInventory();
            InventoryItem ivItemCandidate = oeInventory.getInventory().getItem(0);
            if (ivItemCandidate == null || !ivItemCandidate.item.isEnchantable(ivItemCandidate)) {
                return;
            }
            short randomSeed = (short)GameRandom.globalRandom.nextInt();
            GameRandom gameRandom = new GameRandom(randomSeed);
            ((Enchantable<?>)ivItemCandidate.item).addRandomEnchantment(ivItemCandidate, gameRandom);
            oeInventory.getInventory().clearInventory();
            oeInventory.getInventory().setItem(0, ivItemCandidate);
            client.getServerClient().newStats.items_enchanted.increment(1);
            if (client.getServerClient().achievementsLoaded()) {
                client.getServerClient().achievements().ENCHANT_ITEM.markCompleted(client.getServerClient());
            }
            Packet itemContent = InventoryItem.getContentPacket(ivItemCandidate);
            CustomOEInventoryContainer.this.enchantButtonResponse.runAndSend(itemContent);
        }
    });

    public final ContentCustomAction enchantButtonResponse = this.registerAction(new ContentCustomAction() {
        protected void run(Packet content) {
            if (!client.isClientClient()) {
                return;
            }
            Screen.playSound(GameResources.pop, SoundEffect.effect(client.playerMob));
            InventoryItem enchantedItem = InventoryItem.fromContentPacket(content);
            client.playerMob.getLevel().hudManager.addElement(new ItemPickupText(client.playerMob, enchantedItem));
            client.playerMob.getInv().main.removeItems(
                client.playerMob.getLevel(),
                client.playerMob,
                ItemRegistry.getItem("enchantmentorb"),
                enchantCost,
                "buy"
            );
        }
    });

    public CustomOEInventoryContainer(NetworkClient client, int uniqueSeed, OEInventory oeInventory, PacketReader reader) {
        super(client, uniqueSeed, oeInventory, reader);
    }
    public int getEnchantCost() {

        return this.canEnchant() ? enchantCost : 0;
    }

    public boolean canBeEnchanted() {
        if (this.oeInventory.getInventory().getAmount(0) != 1) {
            return false;
        }
        InventoryItem ivi = this.oeInventory.getInventory().getItem(0);
        return ivi != null && ivi.item.isEnchantable(ivi);
    }

    public boolean canEnchant() {
        return this.canBeEnchanted() && client.playerMob.getInv().getAmount(
            ItemRegistry.getItem("enchantmentorb"),
            true,
            false,
            false,
            "buy"
        ) >= enchantCost;
    }
}
