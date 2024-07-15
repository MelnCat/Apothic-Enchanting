package dev.shadowsoffire.apothic_enchanting.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import dev.shadowsoffire.apothic_enchanting.api.EnchantableItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Mixin(value = Item.class, remap = false)
public class ItemMixin implements EnchantableItem {

    /**
     * @author Shadows
     * @reason Enables all items to be enchantable by default.
     */
    @Overwrite
    public int getEnchantmentValue() {
        return 1;
    }

    @Redirect(method = "isEnchantable(Lnet/minecraft/world/item/ItemStack;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;isDamageable(Lnet/minecraft/world/item/ItemStack;)Z", remap = false))
    private boolean apoth_ignoreDamageForEnchantable(Item ths, ItemStack stack) {
        return true;
    }

}
