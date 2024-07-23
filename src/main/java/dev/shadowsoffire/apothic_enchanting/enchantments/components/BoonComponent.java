package dev.shadowsoffire.apothic_enchanting.enchantments.components;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.shadowsoffire.apothic_enchanting.Ench;
import dev.shadowsoffire.apothic_enchanting.table.ApothEnchantmentHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

public record BoonComponent(TagKey<Block> target, TagKey<Item> drops, List<ConditionalEffect<EnchantmentValueEffect>> dropChance) {

    public static Codec<BoonComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
        TagKey.codec(Registries.BLOCK).fieldOf("target").forGetter(BoonComponent::target),
        TagKey.codec(Registries.ITEM).fieldOf("drops").forGetter(BoonComponent::drops),
        ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.HIT_BLOCK).listOf().fieldOf("drop_chance").forGetter(BoonComponent::dropChance))
        .apply(inst, BoonComponent::new));

    public static void provideBenefits(BlockDropsEvent e) {
        if (e.getBreaker() == null || e.getTool().isEmpty()) return;

        ItemStack stack = e.getTool();

        EnchantmentHelper.runIterationOnItem(stack, (ench, level) -> {
            BoonComponent comp = ench.value().effects().get(Ench.EnchantEffects.EARTHS_BOON);
            if (comp != null && e.getState().is(comp.target)) {
                LootContext ctx = Enchantment.blockHitContext(e.getLevel(), level, e.getBreaker(), Vec3.atCenterOf(e.getPos()), e.getState());
                float chance = ApothEnchantmentHelper.processValue(comp.dropChance, ctx, level, 0);
                RandomSource rand = e.getLevel().random;

                if (rand.nextFloat() <= chance) {
                    Item selected = BuiltInRegistries.ITEM.getTag(comp.drops).flatMap(set -> set.getRandomElement(rand)).map(Holder::value).orElse(Items.AIR);
                    Vec3 pos = e.getDrops().stream().findAny().map(ItemEntity::position).orElse(Vec3.atCenterOf(e.getPos()));
                    ItemEntity entity = new ItemEntity(e.getLevel(), pos.x, pos.y, pos.z, selected.getDefaultInstance());
                    e.getDrops().add(entity);
                }
            }
        });
    }

}
