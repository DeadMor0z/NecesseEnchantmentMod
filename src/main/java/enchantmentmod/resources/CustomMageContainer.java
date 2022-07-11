package enchantmentmod.resources;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.MageHumanMob;
import necesse.inventory.Inventory;
import necesse.inventory.PlayerTempInventory;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.mob.MageContainer;
import necesse.inventory.container.slots.EnchantableSlot;

public class CustomMageContainer extends MageContainer {
    static int enchantCost = Config.getInstance().getEnchantmentCosts();
    private Inventory inventory;
    public final EmptyCustomAction enchantButton = this.registerAction(
        new EmptyCustomAction() {
            protected void run() {
                if (!client.isServerClient()) {
                    return;
                }
                if (!CustomMageContainer.this.canEnchant()) {
                    CustomMageContainer.this.getSlot(CustomMageContainer.this.ENCHANT_SLOT).markDirty();
                    return;
                }
                client.playerMob.getInv().main.override(inventory);
                client.playerMob.getInv().main.removeItems(
                    client.playerMob.getLevel(),
                    client.playerMob,
                    ItemRegistry.getItem("enchantmentorb"),
                    enchantCost,
            "buy"
                );
                CustomMageContainer.this.getSlot(CustomMageContainer.this.ENCHANT_SLOT).markDirty();
            }
        }
    );

    public final EmptyCustomAction copyInventory = this.registerAction(new EmptyCustomAction() {
        @Override
        protected void run() {
        inventory = client.playerMob.getInv().main.copy();
        }
    });
    public final BooleanCustomAction setIsEnchanting = this.registerAction(new BooleanCustomAction() {
        protected void run(boolean value) {
        CustomMageContainer.this.isEnchanting = value;
        }
    });
    private boolean isEnchanting;
    public final int ENCHANT_SLOT;
    public MageHumanMob mageMob;
    public final long enchantCostSeed;
    public final PlayerTempInventory enchantInv;

    public CustomMageContainer(NetworkClient client, int uniqueSeed, MageHumanMob mob, PacketReader contentReader) {
        super(client, uniqueSeed, mob, contentReader);
        this.enchantInv = client.playerMob.getInv().applyTempInventoryPacket(contentReader.getNextContentPacket(), (m) -> {
            return false;
        });
        this.ENCHANT_SLOT = this.addSlot(new EnchantableSlot(this.enchantInv, 0));
        this.setInventoryQuickTransfer((s) -> {
            return this.isEnchanting;
        }, this.ENCHANT_SLOT, this.ENCHANT_SLOT);
        this.mageMob = mob;
        this.isEnchanting = false;
        this.enchantCostSeed = this.priceSeed * (long) GameRandom.prime(28);
    }
    @Override
    public boolean canEnchant() {
        return this.isItemEnchantable() && client.playerMob.getInv().getAmount(
            ItemRegistry.getItem("enchantmentorb"),
            true,
            false,
            false,
            "buy"
        ) >= enchantCost;
    }
}
