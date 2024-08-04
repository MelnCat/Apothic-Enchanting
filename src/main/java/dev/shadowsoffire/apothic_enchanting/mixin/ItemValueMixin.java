package dev.shadowsoffire.apothic_enchanting.mixin;

import java.util.Objects;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient.ItemValue;

@Mixin(value = ItemValue.class, remap = false)
public class ItemValueMixin {

    @Override
    @Overwrite
    public int hashCode() {
        ItemStack stack = ((ItemValue) (Object) this).item();
        return Objects.hash(stack.getItem(), stack.getCount());
    }

}
