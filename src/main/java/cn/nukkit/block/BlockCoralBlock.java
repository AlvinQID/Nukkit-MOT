package cn.nukkit.block;

import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockCoralBlock extends BlockSolidMeta {
    
    public BlockCoralBlock() {
        this(0);
    }
    
    public BlockCoralBlock(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return CORAL_BLOCK;
    }
    
    @Override
    public String getName() {
        String[] names = new String[] {
                "Tube Coral Block",
                "Brain Coral Block",
                "Bubble Coral Block",
                "Fire Coral Block",
                "Horn Coral Block",
                // Invalid
                "Tube Coral Block",
                "Tube Coral Block",
                "Tube Coral Block"
        };
        String name = names[this.getDamage() & 0x7];
        if (this.isDead()) {
            return "Dead " + name;
        } else {
            return name;
        }
    }
    
    @Override
    public BlockColor getColor() {
        if (this.isDead()) {
            return BlockColor.GRAY_BLOCK_COLOR;
        }
    
        BlockColor[] colors = new BlockColor[] {
                BlockColor.BLUE_BLOCK_COLOR,
                BlockColor.PINK_BLOCK_COLOR,
                BlockColor.PURPLE_BLOCK_COLOR,
                BlockColor.RED_BLOCK_COLOR,
                BlockColor.YELLOW_BLOCK_COLOR,
                // Invalid
                BlockColor.BLUE_BLOCK_COLOR,
                BlockColor.BLUE_BLOCK_COLOR,
                BlockColor.BLUE_BLOCK_COLOR
        };
        return colors[this.getDamage() & 0x7];
    }
    
    @Override
    public double getHardness() {
        return 7;
    }
    
    @Override
    public double getResistance() {
        return 6.0;
    }
    
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }
    
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!this.isDead()) {
                this.getLevel().scheduleUpdate(this, 60 + ThreadLocalRandom.current().nextInt(40));
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!this.isDead()) {
                for (BlockFace face : BlockFace.values()) {
                    if (this.getSide( face) instanceof BlockWater || this.getSide( face) instanceof BlockIceFrosted) {
                        return type;
                    }
                }
                BlockFadeEvent event = new BlockFadeEvent(this, new BlockCoralBlock(this.getDamage() | 0x8));
                if (!event.isCancelled()) {
                    this.setDead(true);
                    this.getLevel().setBlock(this, event.getNewState(), true, true);
                }
            }
            return type;
        }
        return 0;
    }
    
    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null) {
                return new Item[]{ toItem() };
            } else {
                return new Item[]{ new ItemBlock(clone(), this.getDamage() | 0x8) };
            }
        } else {
            return new Item[0];
        }
    }

    public boolean isDead() {
        return (this.getDamage() & 0x8) == 0x8;
    }

    public void setDead(boolean dead) {
        if (dead) {
            this.setDamage(this.getDamage() | 0x8);
        } else {
            this.setDamage(this.getDamage() ^ 0x8);
        }
    }
}
