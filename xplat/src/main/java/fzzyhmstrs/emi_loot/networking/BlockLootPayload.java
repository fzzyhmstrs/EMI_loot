package fzzyhmstrs.emi_loot.networking;

import fzzyhmstrs.emi_loot.EMILoot;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import org.jetbrains.annotations.NotNull;

public record BlockLootPayload(PacketByteBuf buf) implements CustomPayload {
    public static final Id<BlockLootPayload> TYPE = new Id<>(EMILoot.identity("block"));
    public static final PacketCodec<PacketByteBuf, BlockLootPayload> CODEC = PacketCodec.of(BlockLootPayload::write, BlockLootPayload::new);

    public BlockLootPayload(PacketByteBuf buf) {
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
