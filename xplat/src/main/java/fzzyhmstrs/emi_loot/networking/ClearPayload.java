package fzzyhmstrs.emi_loot.networking;

import fzzyhmstrs.emi_loot.EMILoot;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class ClearPayload implements CustomPayload {
	public static final ClearPayload INSTANCE = new ClearPayload();
	public static final Id<ClearPayload> TYPE = new CustomPayload.Id<>(EMILoot.identity("clear_loot"));
	public static final PacketCodec<ByteBuf, ClearPayload> CODEC = PacketCodec.unit(INSTANCE);

	@Override
	public Id<ClearPayload> getId() {
		return TYPE;
	}
}