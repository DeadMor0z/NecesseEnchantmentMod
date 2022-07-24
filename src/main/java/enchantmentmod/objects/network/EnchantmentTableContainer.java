package enchantmentmod.objects.network;

import enchantmentmod.Config;
import necesse.engine.Screen;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.MageHumanMob;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.customAction.ContentCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.object.OEInventoryContainer;
import necesse.inventory.enchants.Enchantable;
import necesse.level.maps.hudManager.floatText.ItemPickupText;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;


public class EnchantmentTableContainer extends OEInventoryContainer {
    int thisEnchantmentCosts = 0;
    public final EmptyCustomAction getEnchantCosts = this.registerAction(new EmptyCustomAction() {

        @Override
        protected void run() {
            if (!client.isServerClient()) {
                return;
            }
            SettlementLevelData data = (SettlementLevelData) client.playerMob.getLevel().getLevelData("settlement");
            int happiness = 0;
            int costs = Config.getInstance().getEnchantmentCosts();
            for (LevelSettler a : data.getSettlers()) {
                if (a.getMob() instanceof MageHumanMob) {
                    if (happiness == 0) {
                        happiness = a.getMob().getSettlerHappiness();
                    }
                    else if (happiness < a.getMob().getSettlerHappiness())
                    {
                        happiness = a.getMob().getSettlerHappiness();
                    }
                }
            }
            if (happiness != 0) {
                costs = costs -(int)(happiness / 10);
            }
            if (happiness > 0) {
                costs = costs - 1;
            }
            if (costs < 2) {
                costs = 2;
            }
            Packet costsPacket = new Packet();
            PacketWriter writer = new PacketWriter(costsPacket);
            writer.putNextInt(costs);
            EnchantmentTableContainer.this.getEnchantCostsResponse.runAndSend(costsPacket);
        }
    });
    public final ContentCustomAction getEnchantCostsResponse = this.registerAction(new ContentCustomAction() {

        @Override
        protected void run(Packet costsPacket) {
            PacketReader reader = new PacketReader(costsPacket);
            EnchantmentTableContainer.this.thisEnchantmentCosts = reader.getNextInt();
        }
    });

    public final EmptyCustomAction enchantButton = this.registerAction(new EmptyCustomAction() {
        protected void run() {
            if (!client.isServerClient()) {
                return;
            }
            OEInventory oeInventory = EnchantmentTableContainer.this.getOEInventory();
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
            client.playerMob.getInv().main.removeItems(
                client.playerMob.getLevel(),
                client.playerMob,
                ItemRegistry.getItem("enchantmentorb"),
                EnchantmentTableContainer.this.getEnchantCost(),
                "buy"
            );
            Packet itemContent = InventoryItem.getContentPacket(ivItemCandidate);
            EnchantmentTableContainer.this.enchantButtonResponse.runAndSend(itemContent);
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
        }
    });

    public EnchantmentTableContainer(NetworkClient client, int uniqueSeed, OEInventory oeInventory, PacketReader reader) {
        super(client, uniqueSeed, oeInventory, reader);
        getEnchantCosts.runAndSend();
    }
    public int getEnchantCost() {
        return this.canEnchant() ? this.thisEnchantmentCosts : 0;
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
        ) >= this.thisEnchantmentCosts;
    }
}
