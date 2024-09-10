package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.util.SimpleCustomPayload;
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
		if (!ConfigApi.INSTANCE.network().canSend(ARCHAEOLOGY_SENDER, player)) return;
		PacketByteBuf buf = ConfigApi.INSTANCE.network().buf();
		buf.writeString(idToSend);
		buf.writeShort(floatMap.size());
		floatMap.forEach((item, floatWeight) -> {
			buf.writeItemStack(item);
			buf.writeFloat(floatWeight);
		});
		ConfigApi.INSTANCE.network().send(new SimpleCustomPayload(buf, ARCHAEOLOGY_SENDER), player);
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