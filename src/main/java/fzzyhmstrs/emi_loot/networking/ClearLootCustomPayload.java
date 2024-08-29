package fzzyhmstrs.emi_loot.networking;

import fzzyhmstrs.emi_loot.EMILoot;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class ClearLootCustomPayload implements CustomPayload {
	@Override
	public Id<ClearLootCustomPayload> getId() {
		return TYPE;
	}

	public static ClearLootCustomPayload INSTANCE = new ClearLootCustomPayload();

	public static Id<ClearLootCustomPayload> TYPE = new CustomPayload.Id<>(Identifier.of(EMILoot.MOD_ID, "clear_loot"));

	public static PacketCodec<ByteBuf, ClearLootCustomPayload> CODEC = PacketCodec.unit(INSTANCE);

}