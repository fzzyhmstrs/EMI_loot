package fzzyhmstrs.emi_loot.client;

import net.minecraft.network.PacketByteBuf;

public interface LootReceiver {
    LootReceiver fromBuf(PacketByteBuf buf);
}
