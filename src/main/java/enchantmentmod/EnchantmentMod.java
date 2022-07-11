package enchantmentmod;

import enchantmentmod.resources.*;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.registries.*;
import necesse.entity.mobs.hostile.*;
import necesse.entity.mobs.hostile.bosses.*;
import necesse.entity.mobs.hostile.pirates.PirateCaptainMob;
import necesse.entity.mobs.hostile.pirates.PirateMob;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.lootTable.lootItem.LootItem;
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

    }

    public void initResources() {
        // Empty
    }

    public void postInit() {
        Config cfg = Config.getInstance();
        final int minAmount = cfg.getMinAmount();
        final int maxAmount = cfg.getMaxAmount();
        final int mixBossAmount = cfg.getMinBossAmount();
        final int maxBossAmount = cfg.getMaxBossAmount();

        SandwormHead.lootTable.items.add(
            LootItem.between(enchantmentShard, minAmount, maxAmount)
        );

        VampireMob.lootTable.items.add(
            LootItem.between(enchantmentShard, minAmount, maxAmount)
        );

        SkeletonThrowerMob.lootTable.items.add(
            LootItem.between(enchantmentShard, minAmount, maxAmount)
        );

        SkeletonMob.lootTable.items.add(
            LootItem.between(enchantmentShard, minAmount, maxAmount)
        );

        NinjaMob.lootTable.items.add(
            LootItem.between(enchantmentShard, minAmount, maxAmount)
        );

        HumanRaiderMob.lootTable.items.add(
            LootItem.between(enchantmentShard, minAmount, maxAmount)
        );

        GiantCaveSpiderMob.lootTable.items.add(
            LootItem.between(enchantmentShard, minAmount, maxAmount)
        );

        FrozenDwarfMob.lootTable.items.add(
            LootItem.between(enchantmentShard, minAmount, maxAmount)
        );

        FrostSentryMob.lootTable.items.add(
            LootItem.between(enchantmentShard, minAmount, maxAmount)
        );

        BlackCaveSpiderMob.lootTable.items.add(
            LootItem.between(enchantmentShard, minAmount, maxAmount)
        );

        AncientSkeletonMob.lootTable.items.add(
            LootItem.between(enchantmentShard, minAmount, maxAmount)
        );

        AncientSkeletonMageMob.lootTable.items.add(
            LootItem.between(enchantmentShard, minAmount, maxAmount)
        );

        AncientArmoredSkeletonMob.lootTable.items.add(
            LootItem.between(enchantmentShard, minAmount, maxAmount)
        );

        PirateMob.lootTable.items.add(
            LootItem.between(enchantmentShard, minAmount, maxAmount)
        );

        PirateCaptainMob.lootTable.items.add(
            LootItem.between(enchantmentShard, mixBossAmount, maxBossAmount)
        );

        AncientVultureMob.lootTable.items.add(
            LootItem.between(enchantmentShard, mixBossAmount, maxBossAmount)
        );

        CryoQueenMob.lootTable.items.add(
            LootItem.between(enchantmentShard, mixBossAmount, maxBossAmount)
        );

        EvilsProtectorMob.lootTable.items.add(
            LootItem.between(enchantmentShard, mixBossAmount, maxBossAmount)
        );

        FallenWizardMob.lootTable.items.add(
            LootItem.between(enchantmentShard, mixBossAmount, maxBossAmount)
        );

        QueenSpiderMob.lootTable.items.add(
            LootItem.between(enchantmentShard, mixBossAmount, maxBossAmount)
        );

        ReaperMob.lootTable.items.add(
            LootItem.between(enchantmentShard, mixBossAmount, maxBossAmount)
        );

        SwampGuardianHead.lootTable.items.add(
            LootItem.between(enchantmentShard, mixBossAmount, maxBossAmount)
        );

        VoidWizard.lootTable.items.add(
            LootItem.between(enchantmentShard, mixBossAmount, maxBossAmount)
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
