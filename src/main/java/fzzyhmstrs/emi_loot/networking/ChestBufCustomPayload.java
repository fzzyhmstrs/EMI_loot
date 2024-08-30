package fzzyhmstrs.emi_loot.networking;

import fzzyhmstrs.emi_loot.EMILoot;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ChestBufCustomPayload(PacketByteBuf buf) implements CustomPayload {
	@Override
	public Id<? extends CustomPayload> getId() {
		return TYPE;
	}

	public static Id<ChestBufCustomPayload> TYPE = new Id<>(Identifier.of(EMILoot.MOD_ID, "chest"));

	public static PacketCodec<ByteBuf, ChestBufCustomPayload> CODEC = CustomPayload.codecOf((p, b) -> b.writeBytes(p.buf, p.buf.readerIndex(), p.buf.readableBytes()), (b) -> new ChestBufCustomPayload(PacketByteBufs.readBytes(b, b.readableBytes())));

}