package enchantmentmod.resources;

import necesse.engine.modLoader.annotations.ModConstructorPatch;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.entity.mobs.friendly.human.humanShop.MageHumanMob;
import necesse.inventory.container.mob.MageContainer;
import net.bytebuddy.asm.Advice;

/**
 * Intercepts a constructor
 * Check out ExampleMethodPatch class for some documentation
 */
@ModConstructorPatch(target = MageContainer.class, arguments = {NetworkClient.class, int.class, MageHumanMob.class, PacketReader.class}) // No arguments
public class MageContainerPatch {
    @Advice.OnMethodEnter
    static void onEnter(@Advice.This MageContainer rabbitMob, @Advice.Argument(0) NetworkClient client, @Advice.Argument(1) int seed, @Advice.Argument(2) MageHumanMob mob, @Advice.Argument(3) PacketReader reader) {
        // Empty
    }
    @Advice.OnMethodExit
    static void onExit(@Advice.This MageContainer mageContainer, @Advice.Argument(0) NetworkClient client, @Advice.Argument(1) int seed, @Advice.Argument(2) MageHumanMob mob, @Advice.Argument(3) PacketReader reader) {
        mageContainer = new CustomMageContainer(client, seed, mob, reader);
    }
}

