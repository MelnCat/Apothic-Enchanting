package dev.shadowsoffire.apothic_enchanting.data;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import dev.shadowsoffire.apothic_enchanting.Ench;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
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
                new LootTableProvider.SubProviderEntry(VanillaBlockLoot::new, LootContextParamSets.BLOCK)),
            registries);
    }

    public static class BlockLoot extends BlockLootSubProvider {

        public static final Set<Item> EXPLOSION_RESISTANT = Set.of();

        protected BlockLoot(Provider registries) {
            super(EXPLOSION_RESISTANT, FeatureFlags.REGISTRY.allFlags(), registries);
        }

        @Override
        protected void generate() {
            this.dropSelf(Ench.Blocks.HELLSHELF.get());
            this.dropSelf(Ench.Blocks.INFUSED_HELLSHELF.get());
            this.dropSelf(Ench.Blocks.BLAZING_HELLSHELF.get());
            this.dropSelf(Ench.Blocks.GLOWING_HELLSHELF.get());
            this.dropSelf(Ench.Blocks.SEASHELF.get());
            this.dropSelf(Ench.Blocks.INFUSED_SEASHELF.get());
            this.dropSelf(Ench.Blocks.CRYSTAL_SEASHELF.get());
            this.dropSelf(Ench.Blocks.HEART_SEASHELF.get());
            this.dropSelf(Ench.Blocks.DORMANT_DEEPSHELF.get());
            this.dropSelf(Ench.Blocks.DEEPSHELF.get());
            this.dropSelf(Ench.Blocks.ECHOING_DEEPSHELF.get());
            this.dropSelf(Ench.Blocks.SOUL_TOUCHED_DEEPSHELF.get());
            this.dropSelf(Ench.Blocks.ECHOING_SCULKSHELF.get());
            this.dropSelf(Ench.Blocks.SOUL_TOUCHED_SCULKSHELF.get());
            this.dropSelf(Ench.Blocks.ENDSHELF.get());
            this.dropSelf(Ench.Blocks.PEARL_ENDSHELF.get());
            this.dropSelf(Ench.Blocks.DRACONIC_ENDSHELF.get());
            this.dropSelf(Ench.Blocks.BEESHELF.get());
            this.dropSelf(Ench.Blocks.MELONSHELF.get());
            this.dropSelf(Ench.Blocks.STONESHELF.get());
            this.dropSelf(Ench.Blocks.LIBRARY.get());
            this.dropSelf(Ench.Blocks.GEODE_SHELF.get());
            this.dropSelf(Ench.Blocks.SIGHTSHELF.get());
            this.dropSelf(Ench.Blocks.SIGHTSHELF_T2.get());
            this.dropSelf(Ench.Blocks.ENDER_LIBRARY.get());
            this.dropSelf(Ench.Blocks.FILTERING_SHELF.get());
            this.dropSelf(Ench.Blocks.TREASURE_SHELF.get());
        }

    }
}