package enchantmentmod;

import enchantmentmod.resources.*;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.*;
import necesse.entity.mobs.hostile.*;
import necesse.entity.mobs.hostile.bosses.*;
import necesse.entity.mobs.hostile.pirates.PirateCaptainMob;
import necesse.entity.mobs.hostile.pirates.PirateMob;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.lootTable.LootTable;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.inventory.lootTable.lootItem.LootItem;
import necesse.inventory.lootTable.presets.*;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.ItemRegistry;
import necesse.entity.mobs.friendly.human.humanShop.MageHumanMob;

@ModEntry
public class EnchantmentMod {
    final String enchantmentOrb = "enchantmentorb";
    final String enchantmentShard = "enchantmentshard";
    final String getEnchantmentTable = "enchantmenttable";
    final String luckyBox = "luckybox";
    public void init() {

        ContainerRegistry.MAGE_CONTAINER = ContainerRegistry.registerMobContainer(
            (client, uniqueSeed, mob, content) ->
                new CustomMageContainerForm<>(
                    client,
                    new CustomMageContainer(
                        client.getClient(),
                        uniqueSeed,
                        (MageHumanMob)mob,
                        new PacketReader(content)
                    )
                ),
            (client, uniqueSeed, mob, content, serverObject) ->
                new CustomMageContainer(
                    client,
                    uniqueSeed,
                    (MageHumanMob)mob,
                    new PacketReader(content)
                )
        );

        int COEICContainerIndex = ContainerRegistry.registerOEContainer(
            (client, uniqueSeed, oe, content) ->
                new CustomOEInventoryContainerForm<>(
                    client,
                    new CustomOEInventoryContainer(
                        client.getClient(),
                        uniqueSeed,
                        (OEInventory)oe,
                        new PacketReader(content)
                    )
                ),
            (client, uniqueSeed, oe, content, serverObject) ->
                new CustomOEInventoryContainer(
                    client,
                    uniqueSeed,
                    (OEInventory)oe,
                    new PacketReader(content)
                )
        );

        ObjectRegistry.registerObject(
            getEnchantmentTable,
            new EnchantmentTable(COEICContainerIndex),
            1,
            true
        );

        ItemRegistry.registerItem(
            enchantmentOrb,
            new EnchantmentOrb(),
            1,
            true
        );

        ItemRegistry.registerItem(
            enchantmentShard,
            new EnchantmentShard(),
            1,
            false
        );

        ItemRegistry.registerItem(
                luckyBox,
            new LuckyBoxItem(),
            1,
            true
        );

        ItemRegistry.registerItem(
            "shardpouch",
            new ShardPouch(),
            1,
            true
        );
    }

    public void initResources() {

    }

    public void postInit() {
        Config cfg = Config.getInstance();
        final int minAmount = cfg.getMinAmount();
        final int maxAmount = cfg.getMaxAmount();
        final int mixBossAmount = cfg.getMinBossAmount();
        final int maxBossAmount = cfg.getMaxBossAmount();

        LootTable[] lootTable = {
            SandwormHead.lootTable,
            VampireMob.lootTable,
            VoidApprentice.lootTable,
            SkeletonThrowerMob.lootTable,
            SkeletonMob.lootTable,
            NinjaMob.lootTable,
            HumanRaiderMob.lootTable,
            GiantCaveSpiderMob.lootTable,
            FrozenDwarfMob.lootTable,
            FrostSentryMob.lootTable,
            BlackCaveSpiderMob.lootTable,
            AncientSkeletonMob.lootTable,
            AncientSkeletonMageMob.lootTable,
            AncientArmoredSkeletonMob.lootTable,
            PirateMob.lootTable
        };

        for (LootTable a : lootTable) {
            a.items.add(LootItem.between(enchantmentShard, minAmount, maxAmount));
        }

        LootTable[] bossTable = {
            PirateCaptainMob.lootTable,
            AncientVultureMob.lootTable,
            CryoQueenMob.lootTable,
            EvilsProtectorMob.lootTable,
            FallenWizardMob.lootTable,
            QueenSpiderMob.lootTable,
            ReaperMob.lootTable,
            SwampGuardianHead.lootTable,
            VoidWizard.lootTable
        };

        for (LootTable a : bossTable) {
            a.items.add(LootItem.between(enchantmentShard, mixBossAmount, maxBossAmount));
        }

        LootTable[] crates = {
            AlchemistChestLootTable.instance,
            AbandonedMineChestLootTable.instance,
            CaveChestLootTable.basicChest,
            CaveChestLootTable.desertChest,
            CaveChestLootTable.snowChest,
            CaveChestLootTable.swampChest,
            CaveCryptLootTable.instance,
            CaveRuinsLootTable.basicChest,
            CaveRuinsLootTable.desertChest,
            CaveRuinsLootTable.snowChest,
            CaveRuinsLootTable.swampChest,
            CrateLootTable.basicCrate,
            CrateLootTable.desertCrate,
            CrateLootTable.snowCrate,
            CrateLootTable.swampCrate,
            DeepCaveChestLootTable.basicDeepCaveChest,
            DeepCaveChestLootTable.desertDeepCaveChest,
            DeepCaveChestLootTable.snowDeepCaveChest,
            DeepCaveRuinsLootTable.basicDeepChest,
            DeepCaveRuinsLootTable.desertDeepChest,
            DeepCaveRuinsLootTable.snowDeepChest,
            DeepCaveRuinsLootTable.swampDeepChest,
            DeepCrateLootTable.basicDeepCrate,
            DeepCrateLootTable.desertDeepCrate,
            DeepCrateLootTable.snowDeepCrate,
            DungeonChestLootTable.instance,
            PirateChestLootTable.instance,
            SurfaceRuinsChestLootTable.instance,
            TempleChestLootTable.instance
        };

        for (LootTable a : crates) {
            a.items.add(new ChanceLootItem(0.10F, luckyBox, 1));
        }

        ZombieMob.lootTable.items.add(
            new ChanceLootItem(0.05F, enchantmentShard, 1)
        );

        ZombieArcherMob.lootTable.items.add(
            new ChanceLootItem(0.1F, enchantmentShard, 1)
        );

        Recipes.registerModRecipe(new Recipe(
            enchantmentOrb,
            1,
            RecipeTechRegistry.WORKSTATION,
            new Ingredient[]{
                new Ingredient(enchantmentShard, cfg.getOneOrbAreNShards())
            }
        ).showAfter("woodboat"));

        Recipes.registerModRecipe(new Recipe(
            getEnchantmentTable,
            1,
            RecipeTechRegistry.IRON_ANVIL,
            new Ingredient[]{
                new Ingredient(enchantmentOrb, cfg.getEnchantmentCosts()),
                new Ingredient("demonicbar", 5)
            }
        ));

    }


}

