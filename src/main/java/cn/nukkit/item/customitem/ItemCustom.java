package cn.nukkit.item.customitem;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * @author lt_name
 */
public abstract class ItemCustom extends Item {

    public ItemCustom(int id) {
        super(id);
    }

    public ItemCustom(int id, Integer meta) {
        super(id, meta);
    }

    public ItemCustom(int id, Integer meta, int count) {
        super(id, meta, count);
    }

    public ItemCustom(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    public boolean allowOffHand() {
        return false;
    }

    public CompoundTag getComponentsData() {
        CompoundTag data = new CompoundTag();
        data.putCompound("components", new CompoundTag()
                .putCompound("item_properties", new CompoundTag()
                        .putBoolean("allow_off_hand", this.allowOffHand())
                        .putBoolean("hand_equipped", this.isTool())
                        .putInt("creative_category", 3)
                        .putInt("max_stack_size", this.getMaxStackSize()))
                .putCompound("minecraft:icon", new CompoundTag()
                        .putString("texture", this.getName()))
        );
        return data;
    }



}
