package fzzyhmstrs.emi_loot.util;

import dev.emi.emi.api.stack.EmiIngredient;
import fzzyhmstrs.emi_loot.client.ClientBuiltPool;
import fzzyhmstrs.emi_loot.client.ClientMobLootTable;
import it.unimi.dsi.fastutil.floats.Float2ObjectArrayMap;
import it.unimi.dsi.fastutil.floats.Float2ObjectMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class WidgetRowBuilder {

    public WidgetRowBuilder(int maxWidth){
        this.maxWidth = maxWidth;
    }

    private final int maxWidth;
    private final List<ClientBuiltPool> poolList = new LinkedList<>();
    private int width = 0;

    public List<ClientBuiltPool> getPoolList(){
        return poolList;
    }
    public int getWidth() {
        return width;
    }

    private boolean add(ClientBuiltPool newPool){
        int newWidth;
        if (poolList.isEmpty()){
            newWidth = 14 + (11 * ((newPool.list().size() - 1)/2)) + 20 * newPool.stackMap().size();
        } else {
            newWidth = 20 + (11 * ((newPool.list().size() - 1)/2)) + 20 * newPool.stackMap().size();
        }
        //System.out.println(newPool.stackMap());
        //System.out.println("width: " + (width + newWidth));
        if (width + newWidth > maxWidth) return false;
        width += newWidth;
        poolList.add(newPool);
        return true;
    }

    public Optional<ClientBuiltPool> addAndTrim(ClientBuiltPool newPool){
        if (add(newPool)) return Optional.empty();
        if (width == 0) {
            Float2ObjectMap<EmiIngredient> madeItIn = new Float2ObjectArrayMap<>();
            Float2ObjectMap<EmiIngredient> leftOvers = new Float2ObjectArrayMap<>();
            AtomicInteger newWidth = new AtomicInteger(14 + (11 * (((newPool.list().size() - 1) / 2) - 1)));
            newPool.stackMap().forEach((weight, stacks) -> {
                if (newWidth.addAndGet(20) <= maxWidth) {
                    madeItIn.put((float) weight, stacks);
                } else {
                    leftOvers.put((float) weight, stacks);
                }
            });
            add(new ClientBuiltPool(newPool.list(), madeItIn));
            return Optional.of(new ClientBuiltPool(newPool.list(), leftOvers));
        } else {
            return Optional.of(newPool);
        }
    }

    public boolean canAddOther(WidgetRowBuilder other){
        return (width + other.width + 6) <= maxWidth;
    }

    public void addOther(WidgetRowBuilder other){
        this.poolList.addAll(other.poolList);
    }

}
