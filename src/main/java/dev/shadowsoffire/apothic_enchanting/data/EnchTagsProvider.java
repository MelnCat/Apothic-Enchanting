package dev.shadowsoffire.apothic_enchanting.data;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;
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

        this.tag(EnchantmentTags.NON_TREASURE).add(
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

}
