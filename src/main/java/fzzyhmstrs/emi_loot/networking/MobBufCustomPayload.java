package fzzyhmstrs.emi_loot.networking;

import fzzyhmstrs.emi_loot.EMILoot;
import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record MobBufCustomPayload(PacketByteBuf buf) implements CustomPayload {
	@Override
	public Id<? extends CustomPayload> getId() {
		return TYPE;
	}

	public static Id<MobBufCustomPayload> TYPE = new Id<>(Identifier.of(EMILoot.MOD_ID, "mob"));

	public static PacketCodec<ByteBuf, MobBufCustomPayload> CODEC = CustomPayload.codecOf((p, b) -> b.writeBytes(p.buf), (b) -> new MobBufCustomPayload((PacketByteBuf) b.readBytes(PacketByteBufs.create())));

}