package dev.shadowsoffire.apothic_enchanting.data;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class EnchTagsProvider extends EnchantmentTagsProvider {

    public EnchTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(Provider provider) {
        this.tag(EnchantmentTags.CURSE).add(
            ApothEnchantmentProvider.BERSERKERS_FURY,
            ApothEnchantmentProvider.LIFE_MENDING);

        spawnsRandomly(
            ApothEnchantmentProvider.BERSERKERS_FURY,
            ApothEnchantmentProvider.LIFE_MENDING,
            ApothEnchantmentProvider.CHAINSAW,
            ApothEnchantmentProvider.CRESCENDO_OF_BOLTS,
            ApothEnchantmentProvider.EARTHS_BOON,
            ApothEnchantmentProvider.ENDLESS_QUIVER,
            ApothEnchantmentProvider.CHAINSAW,
            ApothEnchantmentProvider.ICY_THORNS,
            ApothEnchantmentProvider.KNOWLEDGE_OF_THE_AGES,
            ApothEnchantmentProvider.MINERS_FERVOR,
            ApothEnchantmentProvider.NATURES_BLESSING,
            ApothEnchantmentProvider.REBOUNDING,
            ApothEnchantmentProvider.REFLECTIVE_DEFENSES,
            ApothEnchantmentProvider.SCAVENGER,
            ApothEnchantmentProvider.SHIELD_BASH,
            ApothEnchantmentProvider.STABLE_FOOTING);
    }

    @SafeVarargs
    private void spawnsRandomly(ResourceKey<Enchantment>... keyArray) {
        List<ResourceKey<Enchantment>> keys = Arrays.asList(keyArray);
        this.tag(EnchantmentTags.ON_MOB_SPAWN_EQUIPMENT).addAll(keys);
        this.tag(EnchantmentTags.ON_TRADED_EQUIPMENT).addAll(keys);
        this.tag(EnchantmentTags.ON_RANDOM_LOOT).addAll(keys);
    }

}
