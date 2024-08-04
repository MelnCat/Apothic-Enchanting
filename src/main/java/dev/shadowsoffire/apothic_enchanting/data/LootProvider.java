package dev.shadowsoffire.apothic_enchanting.data;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import dev.shadowsoffire.apothic_enchanting.ApothicEnchanting;
import dev.shadowsoffire.apothic_enchanting.Ench;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public class LootProvider extends LootTableProvider {

    private LootProvider(PackOutput output, Set<ResourceKey<LootTable>> requiredTables, List<SubProviderEntry> subProviders, CompletableFuture<Provider> registries) {
        super(output, requiredTables, subProviders, registries);
    }

    public static LootProvider create(PackOutput output, CompletableFuture<Provider> registries) {
        return new LootProvider(
            output,
            Set.of(),
            List.of(
                new LootTableProvider.SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK)),
            registries);
    }

    public static class BlockLoot extends BlockLootSubProvider {

        public static final Set<Item> EXPLOSION_RESISTANT = Set.of();

        protected BlockLoot(Provider registries) {
            super(EXPLOSION_RESISTANT, FeatureFlags.REGISTRY.allFlags(), registries);
        }

        @Override
        protected void generate() {
            this.dropSelf(Ench.Blocks.HELLSHELF);
            this.dropSelf(Ench.Blocks.INFUSED_HELLSHELF);
            this.dropSelf(Ench.Blocks.BLAZING_HELLSHELF);
            this.dropSelf(Ench.Blocks.GLOWING_HELLSHELF);
            this.dropSelf(Ench.Blocks.SEASHELF);
            this.dropSelf(Ench.Blocks.INFUSED_SEASHELF);
            this.dropSelf(Ench.Blocks.CRYSTAL_SEASHELF);
            this.dropSelf(Ench.Blocks.HEART_SEASHELF);
            this.dropSelf(Ench.Blocks.DORMANT_DEEPSHELF);
            this.dropSelf(Ench.Blocks.DEEPSHELF);
            this.dropSelf(Ench.Blocks.ECHOING_DEEPSHELF);
            this.dropSelf(Ench.Blocks.SOUL_TOUCHED_DEEPSHELF);
            this.dropSelf(Ench.Blocks.ECHOING_SCULKSHELF);
            this.dropSelf(Ench.Blocks.SOUL_TOUCHED_SCULKSHELF);
            this.dropSelf(Ench.Blocks.ENDSHELF);
            this.dropSelf(Ench.Blocks.PEARL_ENDSHELF);
            this.dropSelf(Ench.Blocks.DRACONIC_ENDSHELF);
            this.dropSelf(Ench.Blocks.BEESHELF);
            this.dropSelf(Ench.Blocks.MELONSHELF);
            this.dropSelf(Ench.Blocks.STONESHELF);
            this.dropSelf(Ench.Blocks.LIBRARY);
            this.dropSelf(Ench.Blocks.GEODE_SHELF);
            this.dropSelf(Ench.Blocks.SIGHTSHELF);
            this.dropSelf(Ench.Blocks.SIGHTSHELF_T2);
            this.dropSelf(Ench.Blocks.ENDER_LIBRARY);
            this.dropSelf(Ench.Blocks.FILTERING_SHELF);
            this.dropSelf(Ench.Blocks.TREASURE_SHELF);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return BuiltInRegistries.BLOCK.holders().filter(h -> h.getKey().location().getNamespace().equals(ApothicEnchanting.MODID)).map(Holder::value).toList();
        }

        protected void dropSelf(Holder<Block> block) {
            this.dropSelf(block.value());
        }

    }
}
