package com.bentech.item;

import com.bentech.api.Tier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * A machine component / tool item that carries a {@link Tier}. The tier is
 * shown as a tooltip so the player can see which voltage tier an item belongs
 * to. Every item currently in the mod is {@link Tier#LV}; MV (and beyond) is
 * reserved for later progression.
 */
public class ComponentItem extends Item {

    private final Tier tier;

    public ComponentItem(Tier tier, Properties properties) {
        super(properties);
        this.tier = tier;
    }

    public Tier getTier() {
        return tier;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context,
                                List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(Component.literal(tier.getDisplayName() + " (" + tier.getId() + ")"));
    }
}
