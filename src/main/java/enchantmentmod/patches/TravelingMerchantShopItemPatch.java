package enchantmentmod.patches;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.friendly.human.humanShop.TravelingMerchantMob;
import necesse.level.maps.levelData.villageShops.ShopItem;
import necesse.level.maps.levelData.villageShops.VillageShopsData;
import net.bytebuddy.asm.Advice;

import java.util.ArrayList;

@ModMethodPatch(target = TravelingMerchantMob.class, name = "getShopItems", arguments = {VillageShopsData.class, ServerClient.class})
public class TravelingMerchantShopItemPatch {

    @Advice.OnMethodExit
    static void onExit(@Advice.This TravelingMerchantMob merch,
                       @Advice.Return(readOnly = false) ArrayList<ShopItem> list) {
        GameRandom random = new GameRandom(merch.getShopSeed() + 5L);
        list.add(ShopItem.item("shardpouch", random.getIntBetween(1200, 1500)));
    };

}