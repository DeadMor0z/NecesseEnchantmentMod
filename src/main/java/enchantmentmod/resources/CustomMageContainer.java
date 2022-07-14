package enchantmentmod.resources;

import necesse.engine.Screen;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.MageHumanMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerTempInventory;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.ContentCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.slots.EnchantableSlot;
import necesse.inventory.enchants.Enchantable;
import necesse.level.maps.hudManager.floatText.ItemPickupText;

public class CustomMageContainer extends ShopContainer {
    public final EmptyCustomAction enchantButton;
    public final ContentCustomAction enchantButtonResponse;
    public final BooleanCustomAction setIsEnchanting;
    private boolean isEnchanting;
    public final int ENCHANT_SLOT;
    public MageHumanMob mageMob;
    public final long enchantCostSeed;
    public final PlayerTempInventory enchantInv;

    public CustomMageContainer(final NetworkClient client, int uniqueSeed, MageHumanMob mob, PacketReader contentReader) {
        super(client, uniqueSeed, mob, contentReader.getNextContentPacket());
        Packet content = contentReader.getNextContentPacket();
        this.enchantInv = client.playerMob.getInv().applyTempInventoryPacket(content, (m) -> {
            return this.isClosed();
        });
        this.ENCHANT_SLOT = this.addSlot(new EnchantableSlot(this.enchantInv, 0));
        this.setInventoryQuickTransfer((s) -> {
            return this.isEnchanting;
        }, this.ENCHANT_SLOT, this.ENCHANT_SLOT);
        this.mageMob = mob;
        this.isEnchanting = false;
        this.enchantCostSeed = this.priceSeed * (long) GameRandom.prime(28);
        this.enchantButton = (EmptyCustomAction)this.registerAction(new EmptyCustomAction() {
            protected void run() {
            if (!client.isServerClient()) {
                return;
            }
            if (!CustomMageContainer.this.canEnchant()) {
                CustomMageContainer.this.getSlot(CustomMageContainer.this.ENCHANT_SLOT).markDirty();
                return;
            }
            int enchantCost = CustomMageContainer.this.getEnchantCost();
            short randomSeed = (short)GameRandom.globalRandom.nextInt();
            InventoryItem item = CustomMageContainer.this.getSlot(CustomMageContainer.this.ENCHANT_SLOT).getItem();
            ((Enchantable)item.item).addRandomEnchantment(item, new GameRandom((long)randomSeed));
            if (client.getServerClient().achievementsLoaded()) {
                client.getServerClient().achievements().ENCHANT_ITEM.markCompleted(client.getServerClient());
            }
            client.playerMob.getInv().main.removeItems(
                client.playerMob.getLevel(),
                client.playerMob,
                ItemRegistry.getItem("enchantmentorb"),
                enchantCost,
                "enchantment"
            );
            client.getServerClient().newStats.items_enchanted.increment(1);
            Packet itemContent = InventoryItem.getContentPacket(item);
            CustomMageContainer.this.enchantButtonResponse.runAndSend(itemContent);
            CustomMageContainer.this.getSlot(CustomMageContainer.this.ENCHANT_SLOT).markDirty();
            }
        });
        this.enchantButtonResponse = (ContentCustomAction)this.registerAction(new ContentCustomAction() {
            protected void run(Packet content) {
            if (client.isClientClient()) {
                InventoryItem enchantedItem = InventoryItem.fromContentPacket(content);
                client.playerMob.getLevel().hudManager.addElement(new ItemPickupText(client.playerMob, enchantedItem));
                Screen.playSound(GameResources.pop, SoundEffect.effect(client.playerMob));
            }
            }
        });
        this.setIsEnchanting = (BooleanCustomAction)this.registerAction(new BooleanCustomAction() {
            protected void run(boolean value) {
                CustomMageContainer.this.isEnchanting = value;
            }
        });
    }

    public int getEnchantCost() {
        if (this.getSlot(this.ENCHANT_SLOT).isClear()) {
            return 0;
        } else {
            InventoryItem item = this.getSlot(this.ENCHANT_SLOT).getItem();
            return item.item.isEnchantable(item) ? 10 : 0;
        }
    }

    public boolean isItemEnchantable() {
        if (this.getSlot(this.ENCHANT_SLOT).isClear()) {
            return false;
        } else {
            InventoryItem item = this.getSlot(this.ENCHANT_SLOT).getItem();
            return item.item.isEnchantable(item);
        }
    }

    public boolean canEnchant() {
        return this.isItemEnchantable() && client.playerMob.getInv().getAmount(
            ItemRegistry.getItem("enchantmentorb"),
            true,
            false,
            false,
            "buy"
        ) >= Config.getInstance().getEnchantmentCosts();
    }

    public static Packet getMageContainerContent(MageHumanMob mob, ServerClient client) {
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        writer.putNextContentPacket(mob.getShopItemsContentPacket(client));
        writer.putNextContentPacket(client.playerMob.getInv().getTempInventoryPacket(1));
        return packet;
    }
}
