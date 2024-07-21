package dev.shadowsoffire.apothic_enchanting.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.shadowsoffire.apothic_enchanting.Ench;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;

@Mixin(value = Enchantment.class, priority = 1500, remap = false)
public class EnchantmentMixin {

    /**
     * Adjusts the color of the enchantment text if above the vanilla max.
     */
    @Inject(method = "getFullname", at = @At("RETURN"), cancellable = true)
    public static void apoth_modifyEnchColorForAboveMaxLevel(Holder<Enchantment> ench, int level, CallbackInfoReturnable<Component> cir) {
        if (!ench.is(EnchantmentTags.CURSE) && level > ench.value().definition().maxLevel() && cir.getReturnValue() instanceof MutableComponent mc) {
            cir.setReturnValue(ComponentUtils.mergeStyles(mc, Style.EMPTY.withColor(Ench.Colors.LIGHT_BLUE_FLASH)));
        }
    }

}
