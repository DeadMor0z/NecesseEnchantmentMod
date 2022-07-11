package enchantmentmod.resources;

import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.inventory.container.mob.MageContainer;
import net.bytebuddy.asm.Advice;

@ModMethodPatch(target = MageContainer.class, name = "getEnchantCost", arguments = {})
public class getEnchantCostPatch {
    @Advice.OnMethodEnter
    static void onEnter(@Advice.This MageContainer mob) {
        // Empty
    }
    @Advice.OnMethodExit
    static boolean onExit(@Advice.This MageContainer mob, @Advice.Return(readOnly = false) int value) {
        if (value != 0) {
            value = Config.getInstance().getEnchantmentCosts();
        }
        return false;
    }
}

