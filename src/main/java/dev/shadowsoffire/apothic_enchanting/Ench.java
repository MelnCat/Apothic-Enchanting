package dev.shadowsoffire.apothic_enchanting;

import java.util.List;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;

import dev.shadowsoffire.apothic_enchanting.enchantments.components.BerserkingComponent;
import dev.shadowsoffire.apothic_enchanting.enchantments.components.BoonComponent;
import dev.shadowsoffire.apothic_enchanting.library.EnchLibraryBlock;
import dev.shadowsoffire.apothic_enchanting.library.EnchLibraryContainer;
import dev.shadowsoffire.apothic_enchanting.library.EnchLibraryTile.BasicLibraryTile;
import dev.shadowsoffire.apothic_enchanting.library.EnchLibraryTile.EnderLibraryTile;
import dev.shadowsoffire.apothic_enchanting.objects.ExtractionTomeItem;
import dev.shadowsoffire.apothic_enchanting.objects.FilteringShelfBlock;
import dev.shadowsoffire.apothic_enchanting.objects.FilteringShelfBlock.FilteringShelfTile;
import dev.shadowsoffire.apothic_enchanting.objects.GeodeShelfBlock;
import dev.shadowsoffire.apothic_enchanting.objects.GlowyBlockItem;
import dev.shadowsoffire.apothic_enchanting.objects.ImprovedScrappingTomeItem;
import dev.shadowsoffire.apothic_enchanting.objects.ScrappingTomeItem;
import dev.shadowsoffire.apothic_enchanting.objects.TomeItem;
import dev.shadowsoffire.apothic_enchanting.objects.TreasureShelfBlock;
import dev.shadowsoffire.apothic_enchanting.objects.TypedShelfBlock;
import dev.shadowsoffire.apothic_enchanting.objects.TypedShelfBlock.SculkShelfBlock;
import dev.shadowsoffire.apothic_enchanting.objects.WardenLootModifier;
import dev.shadowsoffire.apothic_enchanting.table.ApothEnchantmentMenu;
import dev.shadowsoffire.apothic_enchanting.table.EnchantmentTableItemHandler;
import dev.shadowsoffire.apothic_enchanting.table.infusion.InfusionRecipe;
import dev.shadowsoffire.apothic_enchanting.table.infusion.NBTInfusionRecipe;
import dev.shadowsoffire.apothic_enchanting.util.MiscUtil;
import dev.shadowsoffire.apothic_enchanting.util.TooltipUtil;
import dev.shadowsoffire.placebo.color.GradientColor;
import dev.shadowsoffire.placebo.registry.DeferredHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * Registration and object holders. Each type of object has its own subclass.
 */
public class Ench {

    private static final DeferredHelper R = DeferredHelper.create(ApothicEnchanting.MODID);

    public static final class Blocks {

        public static final Supplier<Block> BEESHELF = woodShelf("beeshelf", MapColor.COLOR_YELLOW, 0.75F, () -> ParticleTypes.ENCHANT);

        public static final Supplier<Block> BLAZING_HELLSHELF = stoneShelf("blazing_hellshelf", MapColor.COLOR_BLACK, 1.5F, Particles.ENCHANT_FIRE);

        public static final Supplier<Block> CRYSTAL_SEASHELF = stoneShelf("crystal_seashelf", MapColor.COLOR_CYAN, 1.5F, Particles.ENCHANT_WATER);

        public static final Supplier<Block> DEEPSHELF = stoneShelf("deepshelf", MapColor.COLOR_BLACK, 2.5F, Particles.ENCHANT_SCULK);

        public static final Supplier<Block> DORMANT_DEEPSHELF = stoneShelf("dormant_deepshelf", MapColor.COLOR_BLACK, 2.5F, Particles.ENCHANT_SCULK);

        public static final Supplier<Block> DRACONIC_ENDSHELF = stoneShelf("draconic_endshelf", MapColor.SAND, 5F, Particles.ENCHANT_END);

        public static final Supplier<Block> ECHOING_DEEPSHELF = stoneShelf("echoing_deepshelf", MapColor.COLOR_BLACK, 2.5F, Particles.ENCHANT_SCULK);

        public static final Supplier<Block> ECHOING_SCULKSHELF = sculkShelf("echoing_sculkshelf");

        public static final Supplier<EnchLibraryBlock> ENDER_LIBRARY = R.block("ender_library", () -> new EnchLibraryBlock(EnderLibraryTile::new, 31));

        public static final Supplier<Block> ENDSHELF = stoneShelf("endshelf", MapColor.SAND, 4.5F, Particles.ENCHANT_END);

        public static final Supplier<Block> GLOWING_HELLSHELF = stoneShelf("glowing_hellshelf", MapColor.COLOR_BLACK, 1.5F, Particles.ENCHANT_FIRE);

        public static final Supplier<Block> HEART_SEASHELF = stoneShelf("heart_seashelf", MapColor.COLOR_CYAN, 1.5F, Particles.ENCHANT_WATER);

        public static final Supplier<Block> HELLSHELF = stoneShelf("hellshelf", MapColor.COLOR_BLACK, 1.5F, Particles.ENCHANT_FIRE);

        public static final Supplier<Block> INFUSED_HELLSHELF = stoneShelf("infused_hellshelf", MapColor.COLOR_BLACK, 1.5F, Particles.ENCHANT_FIRE);

        public static final Supplier<Block> INFUSED_SEASHELF = stoneShelf("infused_seashelf", MapColor.COLOR_CYAN, 1.5F, Particles.ENCHANT_WATER);

        public static final Supplier<EnchLibraryBlock> LIBRARY = R.block("library", () -> new EnchLibraryBlock(BasicLibraryTile::new, 16));

        public static final Supplier<Block> MELONSHELF = woodShelf("melonshelf", MapColor.COLOR_GREEN, 0.75F, () -> ParticleTypes.ENCHANT);

        public static final Supplier<Block> PEARL_ENDSHELF = stoneShelf("pearl_endshelf", MapColor.SAND, 4.5F, Particles.ENCHANT_END);

        public static final Supplier<Block> SEASHELF = stoneShelf("seashelf", MapColor.COLOR_CYAN, 1.5F, Particles.ENCHANT_WATER);

        public static final Supplier<Block> SIGHTSHELF = stoneShelf("sightshelf", MapColor.COLOR_BLACK, 1.5F, Particles.ENCHANT_FIRE);

        public static final Supplier<Block> SIGHTSHELF_T2 = stoneShelf("sightshelf_t2", MapColor.COLOR_BLACK, 1.5F, Particles.ENCHANT_FIRE);

        public static final Supplier<Block> SOUL_TOUCHED_DEEPSHELF = stoneShelf("soul_touched_deepshelf", MapColor.COLOR_BLACK, 2.5F, Particles.ENCHANT_SCULK);

        public static final Supplier<Block> SOUL_TOUCHED_SCULKSHELF = sculkShelf("soul_touched_sculkshelf");

        public static final Supplier<Block> STONESHELF = stoneShelf("stoneshelf", MapColor.STONE, 1.75F, () -> ParticleTypes.ENCHANT);

        public static final Supplier<Block> FILTERING_SHELF = R.block("filtering_shelf", FilteringShelfBlock::new,
            p -> p.mapColor(MapColor.COLOR_CYAN).sound(SoundType.STONE).strength(1.75F).requiresCorrectToolForDrops());

        public static final Supplier<Block> TREASURE_SHELF = R.block("treasure_shelf", TreasureShelfBlock::new,
            p -> p.mapColor(MapColor.COLOR_BLACK).sound(SoundType.STONE).strength(1.75F).requiresCorrectToolForDrops());

        public static final Supplier<Block> GEODE_SHELF = R.block("geode_shelf", GeodeShelfBlock::new,
            p -> p.mapColor(MapColor.TERRACOTTA_WHITE).sound(SoundType.STONE).strength(1.75F).requiresCorrectToolForDrops());

        private static void bootstrap() {}

        private static Supplier<Block> sculkShelf(String id) {
            return R.block(id, () -> new SculkShelfBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).sound(SoundType.STONE).randomTicks().requiresCorrectToolForDrops().strength(3.5F), Particles.ENCHANT_SCULK));
        }

        private static Supplier<Block> stoneShelf(String id, MapColor color, float strength, Supplier<? extends ParticleOptions> particle) {
            return R.block(id, () -> new TypedShelfBlock(Block.Properties.of().requiresCorrectToolForDrops().sound(SoundType.STONE).mapColor(color).strength(strength), particle));
        }

        private static Supplier<Block> woodShelf(String id, MapColor color, float strength, Supplier<? extends ParticleOptions> particle) {
            return R.block(id, () -> new TypedShelfBlock(Block.Properties.of().sound(SoundType.WOOD).mapColor(color).strength(strength), particle));
        }

    }

    public static class Items extends net.minecraft.world.item.Items {

        public static final Holder<Item> BEESHELF = R.item("beeshelf", () -> new BlockItem(Ench.Blocks.BEESHELF.get(), new Item.Properties()));

        public static final Holder<Item> BLAZING_HELLSHELF = R.item("blazing_hellshelf", () -> new BlockItem(Ench.Blocks.BLAZING_HELLSHELF.get(), new Item.Properties()));

        public static final Holder<Item> BOOTS_TOME = R.item("boots_tome", () -> new TomeItem(DIAMOND_BOOTS));

        public static final Holder<Item> BOW_TOME = R.item("bow_tome", () -> new TomeItem(BOW));

        public static final Holder<Item> CHESTPLATE_TOME = R.item("chestplate_tome", () -> new TomeItem(Items.DIAMOND_CHESTPLATE));

        public static final Holder<Item> CRYSTAL_SEASHELF = R.item("crystal_seashelf", () -> new BlockItem(Ench.Blocks.CRYSTAL_SEASHELF.get(), new Item.Properties()));

        public static final Holder<Item> DEEPSHELF = R.item("deepshelf", () -> new GlowyBlockItem(Ench.Blocks.DEEPSHELF.get(), new Item.Properties()));

        public static final Holder<Item> DORMANT_DEEPSHELF = R.item("dormant_deepshelf", () -> new BlockItem(Ench.Blocks.DORMANT_DEEPSHELF.get(), new Item.Properties()));

        public static final Holder<Item> DRACONIC_ENDSHELF = R.item("draconic_endshelf", () -> new BlockItem(Ench.Blocks.DRACONIC_ENDSHELF.get(), new Item.Properties()));

        public static final Holder<Item> ECHOING_DEEPSHELF = R.item("echoing_deepshelf", () -> new BlockItem(Ench.Blocks.ECHOING_DEEPSHELF.get(), new Item.Properties()));

        public static final Holder<Item> ECHOING_SCULKSHELF = R.item("echoing_sculkshelf", () -> new BlockItem(Ench.Blocks.ECHOING_SCULKSHELF.get(), new Item.Properties()));

        public static final Holder<Item> ENDER_LIBRARY = R.item("ender_library", () -> new BlockItem(Ench.Blocks.ENDER_LIBRARY.get(), new Item.Properties()));

        public static final Holder<Item> ENDSHELF = R.item("endshelf", () -> new BlockItem(Ench.Blocks.ENDSHELF.get(), new Item.Properties()));

        public static final Holder<Item> EXTRACTION_TOME = R.item("extraction_tome", ExtractionTomeItem::new, p -> p.rarity(Rarity.EPIC));

        public static final Holder<Item> FISHING_TOME = R.item("fishing_tome", () -> new TomeItem(Items.FISHING_ROD));

        public static final Holder<Item> GLOWING_HELLSHELF = R.item("glowing_hellshelf", () -> new BlockItem(Ench.Blocks.GLOWING_HELLSHELF.get(), new Item.Properties()));

        public static final Holder<Item> HEART_SEASHELF = R.item("heart_seashelf", () -> new BlockItem(Ench.Blocks.HEART_SEASHELF.get(), new Item.Properties()));

        public static final Holder<Item> HELLSHELF = R.item("hellshelf", () -> new BlockItem(Ench.Blocks.HELLSHELF.get(), new Item.Properties()));

        public static final Holder<Item> HELMET_TOME = R.item("helmet_tome", () -> new TomeItem(Items.DIAMOND_HELMET));

        public static final Holder<Item> IMPROVED_SCRAP_TOME = R.item("improved_scrap_tome", ImprovedScrappingTomeItem::new, p -> p.rarity(Rarity.RARE));

        public static final Holder<Item> INERT_TRIDENT = R.item("inert_trident", () -> new Item(new Item.Properties().stacksTo(1)));

        public static final Holder<Item> INFUSED_BREATH = R.item("infused_breath", () -> new Item(new Item.Properties().rarity(net.minecraft.world.item.Rarity.EPIC)));

        public static final Holder<Item> INFUSED_HELLSHELF = R.item("infused_hellshelf", () -> new GlowyBlockItem(Ench.Blocks.INFUSED_HELLSHELF.get(), new Item.Properties()));

        public static final Holder<Item> INFUSED_SEASHELF = R.item("infused_seashelf", () -> new GlowyBlockItem(Ench.Blocks.INFUSED_SEASHELF.get(), new Item.Properties()));

        public static final Holder<Item> LEGGINGS_TOME = R.item("leggings_tome", () -> new TomeItem(net.minecraft.world.item.Items.DIAMOND_LEGGINGS));

        public static final Holder<Item> LIBRARY = R.item("library", () -> new BlockItem(Ench.Blocks.LIBRARY.get(), new Item.Properties()));

        public static final Holder<Item> MELONSHELF = R.item("melonshelf", () -> new BlockItem(Ench.Blocks.MELONSHELF.get(), new Item.Properties()));

        public static final Holder<Item> OTHER_TOME = R.item("other_tome", () -> new TomeItem(net.minecraft.world.item.Items.AIR));

        public static final Holder<Item> PEARL_ENDSHELF = R.item("pearl_endshelf", () -> new BlockItem(Ench.Blocks.PEARL_ENDSHELF.get(), new Item.Properties()));

        public static final Holder<Item> PICKAXE_TOME = R.item("pickaxe_tome", () -> new TomeItem(net.minecraft.world.item.Items.DIAMOND_PICKAXE));

        public static final Holder<Item> PRISMATIC_WEB = R.item("prismatic_web", () -> new Item(new Item.Properties()));

        public static final Holder<Item> SCRAP_TOME = R.item("scrap_tome", () -> new ScrappingTomeItem(new Item.Properties().rarity(Rarity.UNCOMMON)));

        public static final Holder<Item> SEASHELF = R.item("seashelf", () -> new BlockItem(Ench.Blocks.SEASHELF.get(), new Item.Properties()));

        public static final Holder<Item> SIGHTSHELF = R.item("sightshelf", () -> new BlockItem(Ench.Blocks.SIGHTSHELF.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));

        public static final Holder<Item> SIGHTSHELF_T2 = R.item("sightshelf_t2", () -> new BlockItem(Ench.Blocks.SIGHTSHELF_T2.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));

        public static final Holder<Item> SOUL_TOUCHED_DEEPSHELF = R.item("soul_touched_deepshelf", () -> new BlockItem(Ench.Blocks.SOUL_TOUCHED_DEEPSHELF.get(), new Item.Properties()));

        public static final Holder<Item> SOUL_TOUCHED_SCULKSHELF = R.item("soul_touched_sculkshelf", () -> new BlockItem(Ench.Blocks.SOUL_TOUCHED_SCULKSHELF.get(), new Item.Properties()));

        public static final Holder<Item> STONESHELF = R.item("stoneshelf", () -> new BlockItem(Ench.Blocks.STONESHELF.get(), new Item.Properties()));

        public static final Holder<Item> WARDEN_TENDRIL = R.item("warden_tendril", () -> new Item(new Item.Properties()));

        public static final Holder<Item> WEAPON_TOME = R.item("weapon_tome", () -> new TomeItem(net.minecraft.world.item.Items.DIAMOND_SWORD));

        public static final Holder<Item> FILTERING_SHELF = R.item("filtering_shelf", () -> new BlockItem(Ench.Blocks.FILTERING_SHELF.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));

        public static final Holder<Item> TREASURE_SHELF = R.item("treasure_shelf", () -> new BlockItem(Ench.Blocks.TREASURE_SHELF.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));

        public static final Holder<Item> GEODE_SHELF = R.item("geode_shelf", () -> new BlockItem(Ench.Blocks.GEODE_SHELF.get(), new Item.Properties().rarity(Rarity.UNCOMMON)));

        private static void bootstrap() {}

    }

    // public static final class Enchantments {
    //
    // public static final Supplier<BerserkersFuryEnchant> BERSERKERS_FURY = R.enchant("berserkers_fury", BerserkersFuryEnchant::new);
    //
    // public static final Supplier<ChainsawEnchant> CHAINSAW = R.enchant("chainsaw", ChainsawEnchant::new);
    //
    // public static final Supplier<ChromaticEnchant> CHROMATIC = R.enchant("chromatic", ChromaticEnchant::new);
    //
    // public static final Supplier<CrescendoEnchant> CRESCENDO = R.enchant("crescendo", CrescendoEnchant::new);
    //
    // public static final Supplier<EarthsBoonEnchant> EARTHS_BOON = R.enchant("earths_boon", EarthsBoonEnchant::new);
    //
    // public static final Supplier<EndlessQuiverEnchant> ENDLESS_QUIVER = R.enchant("endless_quiver", EndlessQuiverEnchant::new);
    //
    // public static final Supplier<ExploitationEnchant> EXPLOITATION = R.enchant("exploitation", ExploitationEnchant::new);
    //
    // public static final Supplier<GrowthSerumEnchant> GROWTH_SERUM = R.enchant("growth_serum", GrowthSerumEnchant::new);
    //
    // public static final Supplier<IcyThornsEnchant> ICY_THORNS = R.enchant("icy_thorns", IcyThornsEnchant::new);
    //
    // public static final Supplier<InertEnchantment> INFUSION = R.enchant("infusion", InertEnchantment::new);
    //
    // public static final Supplier<KnowledgeEnchant> KNOWLEDGE = R.enchant("knowledge", KnowledgeEnchant::new);
    //
    // public static final Supplier<LifeMendingEnchant> LIFE_MENDING = R.enchant("life_mending", LifeMendingEnchant::new);
    //
    // public static final Supplier<MinersFervorEnchant> MINERS_FERVOR = R.enchant("miners_fervor", MinersFervorEnchant::new);
    //
    // public static final Supplier<NaturesBlessingEnchant> NATURES_BLESSING = R.enchant("natures_blessing", NaturesBlessingEnchant::new);
    //
    // public static final Supplier<ReboundingEnchant> REBOUNDING = R.enchant("rebounding", ReboundingEnchant::new);
    //
    // public static final Supplier<ReflectiveEnchant> REFLECTIVE = R.enchant("reflective", ReflectiveEnchant::new);
    //
    // public static final Supplier<ScavengerEnchant> SCAVENGER = R.enchant("scavenger", ScavengerEnchant::new);
    //
    // public static final Supplier<ShieldBashEnchant> SHIELD_BASH = R.enchant("shield_bash", ShieldBashEnchant::new);
    //
    // public static final Supplier<SpearfishingEnchant> SPEARFISHING = R.enchant("spearfishing", SpearfishingEnchant::new);
    //
    // public static final Supplier<StableFootingEnchant> STABLE_FOOTING = R.enchant("stable_footing", StableFootingEnchant::new);
    //
    // public static final Supplier<TemptingEnchant> TEMPTING = R.enchant("tempting", TemptingEnchant::new);
    //
    // private static void bootstrap() {}
    //
    // }

    public static class EnchantEffects {

        /**
         * The tempting effect causes animals to follow the item that has the effect.
         */
        public static final Supplier<DataComponentType<Unit>> TEMPTING = R.enchantmentEffect("tempting", b -> b.persistent(Unit.CODEC));

        /**
         * The stable footing effect causes the break speed penalty for flying to be ignored.
         */
        public static final Supplier<DataComponentType<Unit>> STABLE_FOOTING = R.enchantmentEffect("stable_footing", b -> b.persistent(Unit.CODEC));

        /**
         * Component used by Berserker's Fury. Allows configuring the mob effects, health cost, and cooldown.
         */
        public static final DeferredHolder<DataComponentType<?>, DataComponentType<BerserkingComponent>> BERSERKING = R.enchantmentEffect("berserking", b -> b.persistent(BerserkingComponent.CODEC));

        /**
         * The chainsaw effect causes whole trees to break when a log is broken.
         */
        public static final Supplier<DataComponentType<Unit>> CHAINSAW = R.enchantmentEffect("chainsaw", b -> b.persistent(Unit.CODEC));

        /**
         * The crescendo effect causes the crossbow to have an additional number of shots per consumed ammunition, without having to reload between them.
         */
        public static final Supplier<DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>>> CRESCENDO = R.enchantmentEffect("crescendo",
            b -> b.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC, LootContextParamSets.ENCHANTED_ITEM).listOf()));

        /**
         * The boon component allows a chance at dropping a random item from a tag when any block from a target tag is broken.
         */
        public static final Supplier<DataComponentType<BoonComponent>> EARTHS_BOON = R.enchantmentEffect("earths_boon", b -> b.persistent(BoonComponent.CODEC));

        private static void bootstrap() {}
    }

    public static class Tabs {

        public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ENCH = R.creativeTab("ench", b -> b.title(TooltipUtil.lang("creative_tab", "all")).icon(() -> Items.HELLSHELF.value().getDefaultInstance()));

        private static void bootstrap() {}
    }

    public static class Tiles {

        public static final Supplier<BlockEntityType<FilteringShelfTile>> FILTERING_SHELF = R.blockEntity("filtering_shelf", FilteringShelfTile::new, () -> ImmutableSet.of(Blocks.FILTERING_SHELF.get()));

        public static final Supplier<BlockEntityType<BasicLibraryTile>> LIBRARY = R.blockEntity("library", BasicLibraryTile::new, () -> ImmutableSet.of(Blocks.LIBRARY.get()));

        public static final Supplier<BlockEntityType<EnderLibraryTile>> ENDER_LIBRARY = R.blockEntity("ender_library", EnderLibraryTile::new, () -> ImmutableSet.of(Blocks.ENDER_LIBRARY.get()));

        private static void bootstrap() {}
    }

    public static class Menus {

        public static final Supplier<MenuType<ApothEnchantmentMenu>> ENCHANTING_TABLE = R.menu("enchanting_table", ApothEnchantmentMenu::new);

        public static final Supplier<MenuType<EnchLibraryContainer>> LIBRARY = R.menuWithPos("library", EnchLibraryContainer::new);

        private static void bootstrap() {}
    }

    public static class Colors {
        private static int[] _LIGHT_BLUE_FLASH = { 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff,
            0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff,
            0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff,
            0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x00b3ff, 0x0bb5ff,
            0x17b8ff, 0x22bbff, 0x2dbdff, 0x39c0ff, 0x44c3ff, 0x4fc6ff, 0x5bc9ff, 0x66ccff };

        public static GradientColor LIGHT_BLUE_FLASH = new GradientColor(MiscUtil.doubleUpGradient(_LIGHT_BLUE_FLASH), "light_blue_flash");
    }

    public static class Particles {
        public static final Supplier<SimpleParticleType> ENCHANT_FIRE = R.simpleParticle("enchant_fire", false);
        public static final Supplier<SimpleParticleType> ENCHANT_WATER = R.simpleParticle("enchant_water", false);
        public static final Supplier<SimpleParticleType> ENCHANT_SCULK = R.simpleParticle("enchant_sculk", false);
        public static final Supplier<SimpleParticleType> ENCHANT_END = R.simpleParticle("enchant_end", false);

        private static void bootstrap() {}
    }

    public static class RecipeTypes {
        public static final Supplier<RecipeType<InfusionRecipe>> INFUSION = R.recipe("infusion");

        private static void bootstrap() {}
    }

    public static class Components {

        /**
         * Used when Crescendo of Bolts is active to track the number of remaining bonus shots.
         */
        public static final Supplier<DataComponentType<Integer>> CRESCENDO_SHOTS = R.component("crescendo_shots", b -> b.persistent(Codec.intRange(1, 1024)).networkSynchronized(ByteBufCodecs.VAR_INT));

        /**
         * Keeps a copy of the original {@link ChargedProjectiles} when crescendo is present, so they can be re-charged after firing.
         */
        public static final Supplier<DataComponentType<ChargedProjectiles>> CRESCENDO_PROJECTILES = R.component("crescendo_projectiles",
            b -> b.persistent(ChargedProjectiles.CODEC).networkSynchronized(ChargedProjectiles.STREAM_CODEC).cacheEncoding());

        private static void bootstrap() {}
    }

    public static final class Tags {
        public static final TagKey<Item> BOON_DROPS = ItemTags.create(ApothicEnchanting.loc("boon_drops"));
        public static final TagKey<Item> SPEARFISHING_DROPS = ItemTags.create(ApothicEnchanting.loc("spearfishing_drops"));
    }

    public static final class DamageTypes {
        public static final ResourceKey<DamageType> CORRUPTED = ResourceKey.create(Registries.DAMAGE_TYPE, ApothicEnchanting.loc("corrupted"));
    }

    static {
        R.recipeSerializer("infusion", () -> InfusionRecipe.SERIALIZER);
        R.recipeSerializer("keep_nbt_infusion", () -> NBTInfusionRecipe.SERIALIZER);
        R.custom("warden_tendril", NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, () -> WardenLootModifier.CODEC);
        R.custom("enchantment_table_item_handler", NeoForgeRegistries.Keys.ATTACHMENT_TYPES, () -> EnchantmentTableItemHandler.TYPE);
    }

    public static void bootstrap(IEventBus bus) {
        Blocks.bootstrap();
        Items.bootstrap();
        EnchantEffects.bootstrap();
        Tabs.bootstrap();
        Tiles.bootstrap();
        Particles.bootstrap();
        Menus.bootstrap();
        RecipeTypes.bootstrap();
        Components.bootstrap();
        bus.register(R);
    }

}
