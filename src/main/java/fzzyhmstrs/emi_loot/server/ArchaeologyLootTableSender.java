package fzzyhmstrs.emi_loot.server;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ArchaeologyLootTableSender implements LootSender<ArchaeologyLootPoolBuilder> {

	private final String idToSend;
	final List<ArchaeologyLootPoolBuilder> builderList = new LinkedList<>();
	HashMap<ItemStack, Float> floatMap = new HashMap<>();
	public static Identifier ARCHAEOLOGY_SENDER = new Identifier("e_1", "a_s");

	public ArchaeologyLootTableSender(Identifier id) {
		this.idToSend = LootSender.getIdToSend(id);
	}

	@Override
	public String getId() {
		return idToSend;
	}

	@Override
	public void send(ServerPlayerEntity player) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeString(idToSend);
		buf.writeShort(floatMap.size());
		floatMap.forEach((item, floatWeight) -> {
			buf.writeItemStack(item);
			buf.writeFloat(floatWeight);
		});
		ServerPlayNetworking.send(player, ARCHAEOLOGY_SENDER, buf);
	}

	@Override
	public void addBuilder(ArchaeologyLootPoolBuilder builder) {
		builderList.add(builder);
	}

	@Override
	public List<ArchaeologyLootPoolBuilder> getBuilders() {
		return builderList;
	}

	@Override
	public void build() {
		builderList.forEach(builder -> {
			builder.build();
			builder.builtMap.forEach((item, weight) -> {
				if(floatMap.containsKey(item)) {
					float oldWeight = floatMap.getOrDefault(item, 0f);
					floatMap.put(item, oldWeight + weight);
				} else {
					floatMap.put(item, weight);
				}
			});
		});
	}
}
