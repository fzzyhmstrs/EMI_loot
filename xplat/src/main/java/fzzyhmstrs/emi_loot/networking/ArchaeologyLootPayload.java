package fzzyhmstrs.emi_loot.networking;

import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;

public record ArchaeologyLootPayload(PacketByteBuf buf) implements CustomPayload {
    public static final Id<ArchaeologyLootPayload> TYPE = new Id<>(EMILoot.identity("archaeology"));
    public static final PacketCodec<PacketByteBuf, ArchaeologyLootPayload> CODEC = PacketCodec.of(ArchaeologyLootPayload::write, ArchaeologyLootPayload::new);

    public ArchaeologyLootPayload(PacketByteBuf buf) {
        this.buf = (PacketByteBuf) buf.readBytes(buf.readableBytes());
    }

    private void write(@NotNull PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBytes(buf);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}
