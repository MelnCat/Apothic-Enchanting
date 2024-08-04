package dev.shadowsoffire.apothic_enchanting;

import dev.shadowsoffire.apothic_enchanting.PowerFunction.DefaultMaxPowerFunction;
import dev.shadowsoffire.apothic_enchanting.PowerFunction.DefaultMinPowerFunction;
import dev.shadowsoffire.apothic_enchanting.PowerFunction.ExpressionPowerFunction;
import dev.shadowsoffire.placebo.config.Configuration;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.enchantment.Enchantment;

/**
 * EnchantmentInfo retains all configurable data about an {@link Enchantment}.
 */
public record EnchantmentInfo(Holder<Enchantment> ench, int maxLevel, int maxLootLevel, PowerFunction maxPower, PowerFunction minPower) {

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentInfo> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.holderRegistry(Registries.ENCHANTMENT), EnchantmentInfo::ench,
        ByteBufCodecs.VAR_INT, EnchantmentInfo::maxLevel,
        ByteBufCodecs.VAR_INT, EnchantmentInfo::maxLootLevel,
        PowerFunction.STREAM_CODEC, EnchantmentInfo::maxPower,
        PowerFunction.STREAM_CODEC, EnchantmentInfo::minPower,
        EnchantmentInfo::new);

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

    public static EnchantmentInfo fallback(Holder<Enchantment> ench) {
        return new EnchantmentInfo(ench, ench.value().getMaxLevel(), ench.value().getMaxLevel(), DefaultMaxPowerFunction.INSTANCE, new DefaultMinPowerFunction(ench));
    }

    public static EnchantmentInfo load(Holder<Enchantment> ench, Configuration cfg) {
        String category = ench.getKey().location().toString();
        int vanillaMax = ench.value().definition().maxLevel();
        int max = cfg.getInt("Max Level", category, ApothicEnchanting.getDefaultMaxLevel(ench), 1, 127, "The max level of this enchantment - originally " + vanillaMax + ".");
        int maxLoot = cfg.getInt("Max Loot Level", category, vanillaMax, 1, 127, "The max level of this enchantment available from loot sources.");
        String maxF = cfg.getString("Max Power Function", category, "", "A function to determine the max enchanting power.  The variable \"x\" is level.  See: https://github.com/uklimaschewski/EvalEx#usage-examples");
        String minF = cfg.getString("Min Power Function", category, "", "A function to determine the min enchanting power.");
        PowerFunction maxPower = maxF.isEmpty() ? DefaultMaxPowerFunction.INSTANCE : new ExpressionPowerFunction(maxF);
        PowerFunction minPower = minF.isEmpty() ? new DefaultMinPowerFunction(ench) : new ExpressionPowerFunction(minF);
        return new EnchantmentInfo(ench, max, maxLoot, maxPower, minPower);
    }

}
