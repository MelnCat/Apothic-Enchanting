package dev.shadowsoffire.apothic_enchanting.data;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

import dev.shadowsoffire.apothic_enchanting.ApothicEnchanting;
import dev.shadowsoffire.apothic_enchanting.Ench.Enchantments;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EnchantmentTagsProvider;
import net.minecraft.tags.EnchantmentTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class EnchTagsProvider extends EnchantmentTagsProvider {

    public EnchTagsProvider(PackOutput output, CompletableFuture<Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ApothicEnchanting.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(Provider provider) {
        this.tag(EnchantmentTags.CURSE).add(
            Enchantments.BERSERKERS_FURY,
            Enchantments.LIFE_MENDING);

        this.tag(EnchantmentTags.NON_TREASURE).add(
            Enchantments.BERSERKERS_FURY,
            Enchantments.LIFE_MENDING,
            Enchantments.CHAINSAW,
            Enchantments.CRESCENDO_OF_BOLTS,
            Enchantments.EARTHS_BOON,
            Enchantments.ENDLESS_QUIVER,
            Enchantments.CHAINSAW,
            Enchantments.ICY_THORNS,
            Enchantments.KNOWLEDGE_OF_THE_AGES,
            Enchantments.MINERS_FERVOR,
            Enchantments.NATURES_BLESSING,
            Enchantments.REBOUNDING,
            Enchantments.REFLECTIVE_DEFENSES,
            Enchantments.SCAVENGER,
            Enchantments.SHIELD_BASH,
            Enchantments.STABLE_FOOTING);
    }

}
