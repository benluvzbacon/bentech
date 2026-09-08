package com.bentech.item;

import com.bentech.api.Material;
import net.minecraft.world.item.Item;

/**
 * A material-form item (dust, ingot, plate...). Keeps references to which
 * material and form it represents, used by the recipe catalogue. Subclassing
 * also lets us distinguish material items from vanilla items in recipes.
 */
public class MaterialItem extends Item {
    private final Material material;
    private final String form;

    public MaterialItem(Material material, String form, Properties properties) {
        super(properties);
        this.material = material;
        this.form = form;
    }

    public Material getMaterial() {
        return material;
    }

    public String getForm() {
        return form;
    }
}
