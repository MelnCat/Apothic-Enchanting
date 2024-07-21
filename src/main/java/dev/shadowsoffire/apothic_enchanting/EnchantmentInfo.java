package dev.shadowsoffire.apothic_enchanting;

import java.math.BigDecimal;

import dev.shadowsoffire.apothic_attributes.repack.evalex.Expression;
import dev.shadowsoffire.placebo.config.Configuration;
import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * EnchantmentInfo retains all configurable data about an {@link Enchantment}.
 */
public class EnchantmentInfo {

    protected final Holder<Enchantment> ench;
    protected final int maxLevel, maxLootLevel;
    protected final PowerFunc maxPower, minPower;

    public EnchantmentInfo(Holder<Enchantment> ench, int maxLevel, int maxLootLevel, PowerFunc max, PowerFunc min) {
        this.ench = ench;
        this.maxLevel = maxLevel;
        this.maxLootLevel = maxLootLevel;
        this.maxPower = max;
        this.minPower = min;
    }

    /**
     * Returns the max level of the enchantment, as set by the config or enforced by IMC.
     */
    public int getMaxLevel() {
        return Math.min(ApothicEnchanting.ENCH_HARD_CAPS.getOrDefault(this.ench, 127), this.maxLevel);
    }

    /**
     * Returns the max loot level of the enchantment, as set by the config or enforced by IMC.
     * <p>
     * The loot level is used in loot table generation as well as villager trades.
     * 
     * @see #defaultMax(Enchantment)
     */
    public int getMaxLootLevel() {
        return Math.min(ApothicEnchanting.ENCH_HARD_CAPS.getOrDefault(this.ench, 127), this.maxLootLevel);
    }

    /**
     * Returns the minimum enchanting power required to receive the given level of this enchantment in an enchanting table.
     * 
     * @see #defaultMin(Enchantment)
     */
    public int getMinPower(int level) {
        return this.minPower.getPower(level);
    }

    /**
     * Returns the maximum enchanting power required to receive the given level of this enchantment in an enchanting table.
     * <p>
     * By default, this is overridden to return 200 for all enchantments.
     */
    public int getMaxPower(int level) {
        return this.maxPower.getPower(level);
    }

    public static EnchantmentInfo load(Holder<Enchantment> ench, Configuration cfg) {
        String category = ench.getKey().location().toString();
        int max = cfg.getInt("Max Level", category, ApothicEnchanting.getDefaultMax(ench.value()), 1, 127, "The max level of this enchantment - originally " + ench.value().getMaxLevel() + ".");
        int maxLoot = cfg.getInt("Max Loot Level", category, ench.value().getMaxLevel(), 1, 127, "The max level of this enchantment available from loot sources.");
        String maxF = cfg.getString("Max Power Function", category, "", "A function to determine the max enchanting power.  The variable \"x\" is level.  See: https://github.com/uklimaschewski/EvalEx#usage-examples");
        String minF = cfg.getString("Min Power Function", category, "", "A function to determine the min enchanting power.");
        PowerFunc maxPower = maxF.isEmpty() ? defaultMax(ench.value()) : new ExpressionPowerFunc(maxF);
        PowerFunc minPower = minF.isEmpty() ? defaultMin(ench.value()) : new ExpressionPowerFunc(minF);
        return new EnchantmentInfo(ench, max, maxLoot, maxPower, minPower);
    }

    /**
     * Simple int to int function, used for converting a level into a required enchanting power.
     */
    public static interface PowerFunc {
        int getPower(int level);
    }

    public static class ExpressionPowerFunc implements PowerFunc {

        Expression ex;

        public ExpressionPowerFunc(String func) {
            this.ex = new Expression(func);
        }

        @Override
        public int getPower(int level) {
            return this.ex.setVariable("x", new BigDecimal(level)).eval().intValue();
        }

    }

    public static PowerFunc defaultMax(Enchantment ench) {
        return level -> 200;
    }

    /**
     * This is the default minimum power function.
     * If the level is equal to or below the default max level, we return the original value {@link Enchantment#getMinCost(int)}
     * If the level is above than the default max level, then we compute the following:
     * Let diff be the slope of {@link Enchantment#getMinCost(int)}, or 15, if the slope would be zero.
     * minPower = baseMinPower + diff * (level - baseMaxLevel) ^ 1.6
     */
    public static PowerFunc defaultMin(Enchantment ench) {
        return level -> {
            if (level > ench.getMaxLevel() && level > 1) {
                int diff = ench.getMinCost(ench.getMaxLevel()) - ench.getMinCost(ench.getMaxLevel() - 1);
                if (diff == 0) diff = 15;
                return ench.getMinCost(level) + diff * (int) Math.pow(level - ench.getMaxLevel(), 1.6);
            }
            return ench.getMinCost(level);
        };
    }

}
