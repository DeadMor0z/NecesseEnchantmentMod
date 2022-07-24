package enchantmentmod.items.network;

import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.client.Client;
import necesse.inventory.InventoryItem;
import necesse.level.maps.hudManager.floatText.ItemPickupText;

public class PacketPlayerOpenedLuckyBoxRequest extends Packet {
    public PacketPlayerOpenedLuckyBoxRequest(byte[] data) {
        super(data);
    }

    public void processClient(NetworkPacket packet, Client client) {
        InventoryItem lootedItem = InventoryItem.fromContentPacket(getContentPacket(0, packet.getByteSize()));
        client.getClient().playerMob.getLevel().hudManager.addElement(
            new ItemPickupText(client.getClient().playerMob, lootedItem)
        );
    }
}
