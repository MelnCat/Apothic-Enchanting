package dev.shadowsoffire.apothic_enchanting.mixin;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.shadowsoffire.apothic_enchanting.asm.EnchHooks;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

@Mixin(value = ItemStack.class, priority = 500, remap = false)
public class ItemStackMixin {

    /**
     * Rewrites the enchantment tooltip lines to include the effective level, as well as the (NBT + bonus) calculation.
     */
    @SuppressWarnings("deprecation")
    @Inject(method = "addToTooltip", at = @At(value = "HEAD"), cancellable = true)
    public <T extends TooltipProvider> void apoth_enchTooltipRewrite(DataComponentType<T> component, Item.TooltipContext ctx, Consumer<Component> tooltip, TooltipFlag tooltipFlag, CallbackInfo ci) {
        ItemStack ths = (ItemStack) (Object) this;
        T t = ths.get(component);
        if (t instanceof ItemEnchantments enchants) {
            HolderLookup.Provider regs = ctx.registries();
            HolderSet<Enchantment> iterationOrder = getTagOrEmpty(regs, Registries.ENCHANTMENT, EnchantmentTags.TOOLTIP_ORDER);
            ItemEnchantments realLevels = ths.getAllEnchantments(regs.lookupOrThrow(Registries.ENCHANTMENT));

            // Apply tooltips in the following order:
            // 1. By iteration order.
            // 2. Any other NBT enchantments not in the iteration order.
            // 3. Any other Gameplay enchantments not in either of the other two passes.

            Consumer<Holder<Enchantment>> applyTooltip = ench -> applyEnchTooltip(ench, enchants, realLevels, tooltip);

            iterationOrder.forEach(applyTooltip);

            Set<Holder<Enchantment>> seen = new HashSet<>();

            enchants.entrySet().stream()
                .map(Entry::getKey)
                .filter(ench -> !iterationOrder.contains(ench))
                .forEach(applyTooltip);

            realLevels.entrySet().stream()
                .map(Entry::getKey)
                .filter(ench -> !iterationOrder.contains(ench))
                .filter(ench -> !seen.contains(ench))
                .forEach(applyTooltip);

            ci.cancel();
        }
    }

    @Unique
    private static void applyEnchTooltip(Holder<Enchantment> ench, ItemEnchantments nbt, ItemEnchantments gameplay, Consumer<Component> tooltip) {
        int nbtLevel = nbt.getLevel(ench);
        int realLevel = gameplay.getLevel(ench);

        if (nbtLevel == realLevel) {
            // Default logic when levels are the same
            if (realLevel > 0) {
                tooltip.accept(Enchantment.getFullname(ench, realLevel));
            }
        }
        else {
            // Show the change vs nbt level
            appendModifiedEnchTooltip(tooltip, ench, realLevel, nbtLevel);
        }
    }

    @Unique
    private static void appendModifiedEnchTooltip(Consumer<Component> tooltip, Holder<Enchantment> ench, int realLevel, int nbtLevel) {
        MutableComponent mc = Enchantment.getFullname(ench, realLevel).copy();
        mc.getSiblings().clear();
        Component nbtLevelComp = Component.translatable("enchantment.level." + nbtLevel);
        Component realLevelComp = Component.translatable("enchantment.level." + realLevel);
        if (realLevel != 1 || EnchHooks.getMaxLevel(ench.value()) != 1) mc.append(CommonComponents.SPACE).append(realLevelComp);

        int diff = realLevel - nbtLevel;
        char sign = diff > 0 ? '+' : '-';
        Component diffComp = Component.translatable("(%s " + sign + " %s)", nbtLevelComp, Component.translatable("enchantment.level." + Math.abs(diff))).withStyle(ChatFormatting.DARK_GRAY);
        mc.append(CommonComponents.SPACE).append(diffComp);
        if (realLevel == 0) {
            mc.withStyle(ChatFormatting.DARK_GRAY);
        }
        tooltip.accept(mc);
    }

    @Unique
    private static <T> HolderSet<T> getTagOrEmpty(@Nullable HolderLookup.Provider registries, ResourceKey<Registry<T>> registryKey, TagKey<T> key) {
        if (registries != null) {
            Optional<HolderSet.Named<T>> optional = registries.lookupOrThrow(registryKey).get(key);
            if (optional.isPresent()) {
                return optional.get();
            }
        }

        return HolderSet.direct();
    }
}
