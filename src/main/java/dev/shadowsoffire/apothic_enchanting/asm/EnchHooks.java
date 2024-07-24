package dev.shadowsoffire.apothic_enchanting.asm;

import dev.shadowsoffire.apothic_enchanting.ApothicEnchanting;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * Methods injected by Javascript Coremods.
 *
 * @author Shadows
 */
public class EnchHooks {

    /**
     * Replaces the call to {@link Enchantment#getMaxLevel()} in various classes.
     * Injected by coremods/ench/ench_info_redirector.js
     */
    public static int getMaxLevel(Enchantment ench) {
        return ApothicEnchanting.getEnchInfo(ench).getMaxLevel();
    }

    /**
     * Replaces the call to {@link Enchantment#getMaxLevel()} in loot-only classes.
     * Injected by coremods/ench/ench_info_loot_redirector.js
     */
    public static int getMaxLootLevel(Enchantment ench) {
        return ApothicEnchanting.getEnchInfo(ench).getMaxLootLevel();
    }

    /**
     * Calculates the delay for catching a fish. Ensures that the value never returns <= 0, so that it doesn't get infinitely locked.
     * Called at the end of {@link FishingBobberEntity#catchingFish(BlockPos)}
     * Injected by coremods/ench/fishing_hook.js
     */
    public static int getTicksCaughtDelay(FishingHook bobber) {
        int lowBound = Math.max(1, 100 - bobber.lureSpeed * 10);
        int highBound = Math.max(lowBound, 600 - bobber.lureSpeed * 60);
        return Mth.nextInt(bobber.level().getRandom(), lowBound, highBound);
    }

}
