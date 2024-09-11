package fzzyhmstrs.emi_loot.networking;

import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;

public record ChestLootPayload(PacketByteBuf buf) implements CustomPayload {
    public static final Id<ChestLootPayload> TYPE = new Id<>(EMILoot.identity("chest"));
    public static final PacketCodec<PacketByteBuf, ChestLootPayload> CODEC = PacketCodec.of(ChestLootPayload::write, ChestLootPayload::new);

    public ChestLootPayload(PacketByteBuf buf) {
        this.buf = new PacketByteBuf(buf.readBytes(buf.readableBytes()));
    }

    private void write(@NotNull PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBytes(buf, buf.readerIndex(), buf.readableBytes());
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return TYPE;
    }
}
