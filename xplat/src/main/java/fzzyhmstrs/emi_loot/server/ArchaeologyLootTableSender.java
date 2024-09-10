package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.networking.ArchaeologyLootPayload;
import io.netty.buffer.Unpooled;
import me.fzzyhmstrs.fzzy_config.api.ConfigApi;
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

	public ArchaeologyLootTableSender(Identifier id) {
		this.idToSend = LootSender.getIdToSend(id);
	}

	@Override
	public String getId() {
		return idToSend;
	}

	@Override
	public void send(ServerPlayerEntity player) {
		if (!ConfigApi.INSTANCE.network().canSend(ArchaeologyLootPayload.TYPE.id(), player)) return;
		PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
		buf.writeString(idToSend);
		buf.writeShort(floatMap.size());
		floatMap.forEach((item, floatWeight) -> {
			writeItemStack(buf, item, player.getServerWorld());
			buf.writeFloat(floatWeight);
		});
		ConfigApi.INSTANCE.network().send(new ArchaeologyLootPayload(buf), player);
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