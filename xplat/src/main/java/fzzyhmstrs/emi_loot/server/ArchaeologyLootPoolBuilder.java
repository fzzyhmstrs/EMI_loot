package fzzyhmstrs.emi_loot.server;

import fzzyhmstrs.emi_loot.parser.LootTableParser;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.LinkedList;
import java.util.List;

public class ArchaeologyLootPoolBuilder extends AbstractLootPoolBuilder {
	public ArchaeologyLootPoolBuilder(float rollWeight) {
		super(rollWeight);
	}

	final Object2IntMap<ItemStack> map = new Object2IntOpenHashMap<>();
	Integer totalWeight = 0;
	Object2FloatMap<ItemStack> builtMap = new Object2FloatOpenHashMap<>();

	@Override
	public void addItem(LootTableParser.ItemEntryResult result) {
		ItemStack item = result.item();
		int weight = result.weight();
		totalWeight += weight;
		if(map.containsKey(item)) {
			int oldWeight = map.getOrDefault(item, 0);
			map.put(item, oldWeight + weight);
		} else {
			map.put(item, weight);
		}
	}

	@Override
	public void build() {
		if(map.isEmpty()) {
			isEmpty = true;
			return;
		}

		Object2FloatMap<ItemStack> floatMap = new Object2FloatOpenHashMap<>();
		map.forEach((item, itemWeight) -> {
			if(!item.isOf(Items.AIR)) {
				floatMap.put(item, itemWeight.floatValue() / totalWeight * 100f * rollWeight);
			}
		});

		builtMap = floatMap;
		if(builtMap.size() == 1) {
			isSimple = true;
			for(Object2FloatMap.Entry<ItemStack> entry : builtMap.object2FloatEntrySet()) {
				simpleStack = entry.getKey();
				if(entry.getFloatValue() != 100f || entry.getKey().getCount() != 1) {
					isSimple = false;
					break;
				}
			}
		}
	}

	@Override
	public List<LootTableParser.ItemEntryResult> revert() {
		List<LootTableParser.ItemEntryResult> list= new LinkedList<>();
		map.forEach((stack, weight) -> list.add(new LootTableParser.ItemEntryResult(stack, weight, new LinkedList<>(), new LinkedList<>())));
		return list;
	}
}
