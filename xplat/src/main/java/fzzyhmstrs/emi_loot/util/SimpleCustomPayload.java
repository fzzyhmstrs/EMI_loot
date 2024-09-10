package fzzyhmstrs.emi_loot.util;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public record SimpleCustomPayload(PacketByteBuf buf, Identifier id) implements CustomPayload {
    @Override
    public void write(@NotNull PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBytes(buf);
    }

    @Override
    public Identifier id() {
        return id;
    }
}
