package fzzyhmstrs.emi_loot.client;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public interface LootReceiver {
    boolean isEmpty();
    Identifier getId();
    LootReceiver fromBuf(PacketByteBuf buf);
}
