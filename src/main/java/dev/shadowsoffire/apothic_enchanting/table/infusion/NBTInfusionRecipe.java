package dev.shadowsoffire.apothic_enchanting.table.infusion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.shadowsoffire.apothic_enchanting.table.EnchantingStatRegistry.Stats;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class NBTInfusionRecipe extends InfusionRecipe {

    public static final Codec<InfusionRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(InfusionRecipe::getOutput),
        Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(InfusionRecipe::getInput),
        Stats.CODEC.fieldOf("requirements").forGetter(InfusionRecipe::getRequirements),
        ExtraCodecs.strictOptionalField(Stats.CODEC, "max_requirements", NO_MAX).forGetter(InfusionRecipe::getMaxRequirements))
        .apply(inst, NBTInfusionRecipe::new));

    public static final Serializer SERIALIZER = new Serializer();

    public NBTInfusionRecipe(ItemStack output, Ingredient input, Stats requirements, Stats maxRequirements) {
        super(output, input, requirements, maxRequirements);
    }

    @Override
    public ItemStack assemble(ItemStack input, float eterna, float quanta, float arcana) {
        ItemStack out = this.getOutput().copy();
        if (input.hasTag()) out.setTag(input.getTag().copy());
        return out;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return NBTInfusionRecipe.SERIALIZER;
    }

    public static class Serializer extends InfusionRecipe.Serializer {

        @Override
        public Codec<InfusionRecipe> codec() {
            return CODEC;
        }

    }

}
