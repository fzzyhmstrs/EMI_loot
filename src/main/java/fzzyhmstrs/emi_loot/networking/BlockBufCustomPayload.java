package fzzyhmstrs.emi_loot.networking;

import fzzyhmstrs.emi_loot.EMILoot;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BlockBufCustomPayload(PacketByteBuf buf) implements CustomPayload {
	@Override
	public Id<? extends CustomPayload> getId() {
		return TYPE;
	}

	public static Id<BlockBufCustomPayload> TYPE = new CustomPayload.Id<>(Identifier.of(EMILoot.MOD_ID, "block"));

	public static PacketCodec<ByteBuf, BlockBufCustomPayload> CODEC = CustomPayload.codecOf((p, b) -> b.writeBytes(p.buf), (b) -> new BlockBufCustomPayload((PacketByteBuf) b.readBytes(PacketByteBufs.create())));

}