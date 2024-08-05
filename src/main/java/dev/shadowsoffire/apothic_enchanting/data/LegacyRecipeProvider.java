package dev.shadowsoffire.apothic_enchanting.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.mojang.serialization.Codec;

import dev.shadowsoffire.apothic_enchanting.ApothicEnchanting;
import dev.shadowsoffire.apothic_enchanting.Ench;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.ItemValue;
import net.minecraft.world.item.crafting.Ingredient.TagValue;
import net.minecraft.world.item.crafting.Ingredient.Value;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;

/**
 * Data provider for crafting recipes using the legacy syntax used by RecipeHelper.
 * <p>
 * Shaped recipes are written out as the output, width, height, and then a row-major vararg array of the actual inputs.
 * The pattern will be inferred from the inputs.
 */
public class LegacyRecipeProvider implements DataProvider {

    private final Path outputDir;
    private CachedOutput cachedOutput;
    private List<CompletableFuture<?>> futures = new ArrayList<>();
    private CompletableFuture<HolderLookup.Provider> registries;

    public LegacyRecipeProvider(Path outputDir, CompletableFuture<HolderLookup.Provider> registries) {
        this.outputDir = outputDir;
        this.registries = registries;
    }

    private void genRecipes() {
        addShaped(Ench.Blocks.HELLSHELF, 3, 3,
            Blocks.NETHER_BRICKS, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICKS,
            Items.BLAZE_ROD, Tags.Items.BOOKSHELVES, potionIngredient(Potions.REGENERATION),
            Blocks.NETHER_BRICKS, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICKS);

        addShaped(Ench.Items.PRISMATIC_WEB, 3, 3,
            null, Items.PRISMARINE_SHARD, null,
            Items.PRISMARINE_SHARD, Blocks.COBWEB, Items.PRISMARINE_SHARD,
            null, Items.PRISMARINE_SHARD, null);

        ItemStack book = new ItemStack(Items.BOOK);
        ItemStack stick = new ItemStack(Items.STICK);
        ItemStack blaze = new ItemStack(Items.BLAZE_ROD);
        addShaped(new ItemStack(Ench.Items.HELMET_TOME, 5), 3, 2, book, book, book, book, blaze, book);
        addShaped(new ItemStack(Ench.Items.CHESTPLATE_TOME, 8), 3, 3, book, blaze, book, book, book, book, book, book, book);
        addShaped(new ItemStack(Ench.Items.LEGGINGS_TOME, 7), 3, 3, book, null, book, book, blaze, book, book, book, book);
        addShaped(new ItemStack(Ench.Items.BOOTS_TOME, 4), 3, 2, book, null, book, book, blaze, book);
        addShaped(new ItemStack(Ench.Items.WEAPON_TOME, 2), 1, 3, book, book, new ItemStack(Items.BLAZE_POWDER));
        addShaped(new ItemStack(Ench.Items.PICKAXE_TOME, 3), 3, 3, book, book, book, null, blaze, null, null, stick, null);
        addShaped(new ItemStack(Ench.Items.FISHING_TOME, 2), 3, 3, null, null, blaze, null, stick, book, stick, null, book);
        addShaped(new ItemStack(Ench.Items.BOW_TOME, 3), 3, 3, null, stick, book, blaze, null, book, null, stick, book);
        addShapeless(new ItemStack(Ench.Items.OTHER_TOME, 6), book, book, book, book, book, book, blaze);
        addShaped(new ItemStack(Ench.Items.SCRAP_TOME, 8), 3, 3, book, book, book, book, Blocks.ANVIL, book, book, book, book);

        addShaped(Ench.Blocks.BLAZING_HELLSHELF, 3, 3,
            null, Items.FIRE_CHARGE, null,
            Items.FIRE_CHARGE, Ench.Blocks.INFUSED_HELLSHELF, Items.FIRE_CHARGE,
            Items.BLAZE_POWDER, Items.BLAZE_POWDER, Items.BLAZE_POWDER);

        addShaped(Ench.Blocks.GLOWING_HELLSHELF, 3, 3,
            null, Blocks.GLOWSTONE, null,
            null, Ench.Blocks.INFUSED_HELLSHELF, null,
            Blocks.GLOWSTONE, null, Blocks.GLOWSTONE);

        addShaped(Ench.Blocks.SEASHELF, 3, 3,
            Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICKS,
            potionIngredient(Potions.WATER), Tags.Items.BOOKSHELVES, Items.PUFFERFISH,
            Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICKS);

        addShaped(Ench.Blocks.CRYSTAL_SEASHELF, 3, 3,
            null, Items.PRISMARINE_CRYSTALS, null,
            null, Ench.Blocks.INFUSED_SEASHELF, null,
            Items.PRISMARINE_CRYSTALS, null, Items.PRISMARINE_CRYSTALS);

        addShaped(Ench.Blocks.HEART_SEASHELF, 3, 3,
            null, Items.HEART_OF_THE_SEA, null,
            Items.PRISMARINE_SHARD, Ench.Blocks.INFUSED_SEASHELF, Items.PRISMARINE_SHARD,
            Items.PRISMARINE_SHARD, Items.PRISMARINE_SHARD, Items.PRISMARINE_SHARD);

        addShaped(Ench.Blocks.BEESHELF, 3, 3,
            Items.HONEYCOMB, Items.BEEHIVE, Items.HONEYCOMB,
            Items.HONEY_BLOCK, Tags.Items.BOOKSHELVES, Items.HONEY_BLOCK,
            Items.HONEYCOMB, Items.BEEHIVE, Items.HONEYCOMB);

        addShaped(Ench.Blocks.MELONSHELF, 3, 3,
            Items.MELON, Items.MELON, Items.MELON,
            Items.GLISTERING_MELON_SLICE, Tags.Items.BOOKSHELVES, Items.GLISTERING_MELON_SLICE,
            Items.MELON, Items.MELON, Items.MELON);

        addShaped(Ench.Blocks.GEODE_SHELF, 3, 3,
            Items.CALCITE, Items.CALCITE, Items.CALCITE,
            Items.CALCITE, Tags.Items.BOOKSHELVES, Items.CALCITE,
            Items.CALCITE, Items.BUDDING_AMETHYST, Items.CALCITE);

        addShaped(Ench.Blocks.SIGHTSHELF, 3, 3,
            Tags.Items.STORAGE_BLOCKS_GOLD, Ench.Items.INFUSED_HELLSHELF, Tags.Items.STORAGE_BLOCKS_GOLD,
            potionIngredient(Potions.NIGHT_VISION), Items.ENDER_EYE, Items.SPYGLASS,
            Tags.Items.STORAGE_BLOCKS_GOLD, Ench.Items.INFUSED_HELLSHELF, Tags.Items.STORAGE_BLOCKS_GOLD);

        Ingredient nightVisPot = potionIngredient(Potions.LONG_NIGHT_VISION);
        addShaped(Ench.Blocks.SIGHTSHELF_T2, 3, 3,
            Items.EMERALD_BLOCK, Tags.Items.INGOTS_NETHERITE, Items.EMERALD_BLOCK,
            nightVisPot, Ench.Items.SIGHTSHELF, nightVisPot,
            Items.EMERALD_BLOCK, Tags.Items.INGOTS_NETHERITE, Items.EMERALD_BLOCK);

        addShaped(Ench.Blocks.ENDSHELF, 3, 3,
            Items.END_STONE_BRICKS, Items.END_STONE_BRICKS, Items.END_STONE_BRICKS,
            Ench.Items.INFUSED_BREATH, Tags.Items.BOOKSHELVES, Tags.Items.ENDER_PEARLS,
            Items.END_STONE_BRICKS, Items.END_STONE_BRICKS, Items.END_STONE_BRICKS);

        addShaped(Ench.Blocks.PEARL_ENDSHELF, 3, 3,
            Items.END_ROD, null, Items.END_ROD,
            Tags.Items.ENDER_PEARLS, Ench.Items.ENDSHELF, Tags.Items.ENDER_PEARLS,
            Items.END_ROD, null, Items.END_ROD);

        addShaped(Ench.Blocks.DRACONIC_ENDSHELF, 3, 3,
            null, Items.DRAGON_HEAD, null,
            Tags.Items.ENDER_PEARLS, Ench.Items.ENDSHELF, Tags.Items.ENDER_PEARLS,
            Tags.Items.ENDER_PEARLS, Tags.Items.ENDER_PEARLS, Tags.Items.ENDER_PEARLS);

        addShaped(Items.COBWEB, 3, 3,
            Tags.Items.STRINGS, Tags.Items.STRINGS, Tags.Items.STRINGS,
            Tags.Items.STRINGS, Items.HONEYCOMB, Tags.Items.STRINGS,
            Tags.Items.STRINGS, Tags.Items.STRINGS, Tags.Items.STRINGS);

        addShaped(Ench.Items.TREASURE_SHELF, 3, 3,
            Tags.Items.STORAGE_BLOCKS_GOLD, Ench.Items.DEEPSHELF, Tags.Items.STORAGE_BLOCKS_GOLD,
            Tags.Items.GEMS_DIAMOND, Tags.Items.STORAGE_BLOCKS_EMERALD, Tags.Items.GEMS_DIAMOND,
            Tags.Items.STORAGE_BLOCKS_GOLD, Ench.Items.DEEPSHELF, Tags.Items.STORAGE_BLOCKS_GOLD);

        addShaped(Ench.Items.INERT_TRIDENT, 3, 3,
            Items.NAUTILUS_SHELL, Items.NAUTILUS_SHELL, Items.NAUTILUS_SHELL,
            null, Items.HEART_OF_THE_SEA, null,
            null, Tags.Items.INGOTS_IRON, null);

        ItemStack pufferfish = new ItemStack(Items.PUFFERFISH);
        pufferfish.set(DataComponents.CUSTOM_NAME, Component.translatable("\"%s\"", pufferfish.getHoverName()).withStyle(Style.EMPTY.withItalic(false)));
        addShaped(pufferfish, 3, 3,
            null, Items.BAMBOO, null,
            Items.BAMBOO, ItemTags.FISHES, Items.BAMBOO,
            null, Items.BAMBOO, null);
    }

    /**
     * Transforms an object that could be converted into an {@link ItemStack} into one.
     * 
     * @param thing A potential candidate object. One of {@link ItemStack}, {@link ItemLike}, or a {@link Holder} containing an {@link ItemLike}.
     * @throws IllegalArgumentException if the type of object is unknown
     */
    public static ItemStack makeStack(Object thing) {
        if (thing instanceof ItemStack stack) return stack;
        if (thing instanceof ItemLike il) return new ItemStack(il);
        if (thing instanceof Holder<?> h) return new ItemStack((ItemLike) h.value());
        throw new IllegalArgumentException("Attempted to create an ItemStack from something that cannot be converted: " + thing);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static NonNullList<Ingredient> createInput(String modid, boolean allowEmpty, Object... inputArr) {
        NonNullList<Ingredient> inputL = NonNullList.create();
        for (int i = 0; i < inputArr.length; i++) {
            Object input = inputArr[i];
            if (input instanceof TagKey tag) inputL.add(i, Ingredient.of(tag));
            else if (input instanceof String str) inputL.add(i, Ingredient.of(ItemTags.create(ResourceLocation.parse(str))));
            else if (input instanceof ItemStack stack && !stack.isEmpty()) inputL.add(i, Ingredient.of(stack));
            else if (input instanceof ItemLike || input instanceof Holder) inputL.add(i, Ingredient.of(makeStack(input)));
            else if (input instanceof Ingredient ing) inputL.add(i, ing);
            else if (allowEmpty) inputL.add(i, Ingredient.EMPTY);
            else throw new UnsupportedOperationException("Attempted to add invalid recipe.  Complain to the author of " + modid + ". (Input " + input + " not allowed.)");
        }
        return inputL;
    }

    public static ShapedRecipePattern toPattern(int width, int height, NonNullList<Ingredient> input) {
        Map<Character, Ingredient> key = new HashMap<>();
        Map<Ingredient, Character> chars = new HashMap<>();
        List<String> rows = new ArrayList<>(height);
        for (int h = 0; h < height; h++) {
            String row = "";
            for (int w = 0; w < width; w++) {
                Ingredient ing = input.get(h * width + w);
                if (chars.containsKey(ing)) {
                    row += chars.get(ing);
                    continue;
                }
                else {
                    Character c = getFirstChar(chars.values(), ing);
                    key.put(c, ing);
                    chars.put(ing, c);
                    row += c;
                    continue;
                }
            }
            rows.add(row);
        }
        key.remove(' ');
        return ShapedRecipePattern.of(key, rows);
    }

    private static Character getFirstChar(Collection<Character> inUse, Ingredient ing) {
        String path;
        if (ing == Ingredient.EMPTY) {
            return ' ';
        }
        else if (ing.isCustom()) {
            ICustomIngredient custom = ing.getCustomIngredient();
            Item item = custom.getItems().findFirst().map(ItemStack::getItem).orElse(Items.AIR);
            path = BuiltInRegistries.ITEM.getKey(item).getPath();
        }
        else {
            Value v = ing.getValues()[0];
            if (v instanceof TagValue t) {
                path = t.tag().location().getPath();
            }
            else if (v instanceof ItemValue i) {
                path = BuiltInRegistries.ITEM.getKey(i.item().getItem()).getPath();
            }
            else {
                throw new UnsupportedOperationException("Unknown Ingredient$Value type: " + v.getClass().getCanonicalName());
            }
        }
        path = path.toUpperCase(Locale.ROOT);
        for (char c : path.toCharArray()) {
            if (!inUse.contains(c)) return c;
        }
        throw new UnsupportedOperationException("Failed to find any unused characters for ingredient: " + ing);
    }

    private ShapedRecipe genShaped(ItemStack output, int width, int height, Object... input) {
        if (width * height != input.length) throw new UnsupportedOperationException("Attempted to add invalid shaped recipe.");
        return new ShapedRecipe(ApothicEnchanting.MODID, CraftingBookCategory.MISC, toPattern(width, height, createInput(ApothicEnchanting.MODID, true, input)), output);
    }

    public void addShaped(Object output, int width, int height, Object... input) {
        ItemStack out = makeStack(output);
        ShapedRecipe recipe = this.genShaped(out, width, height, input);
        write(recipe, ShapedRecipe.CODEC, "recipe", BuiltInRegistries.ITEM.getKey(out.getItem()).getPath());
    }

    public void addShapeless(Object output, Object... inputs) {
        ItemStack out = makeStack(output);
        ShapelessRecipe recipe = new ShapelessRecipe(ApothicEnchanting.MODID, CraftingBookCategory.MISC, out, createInput(ApothicEnchanting.MODID, false, inputs));
        write(recipe, ShapelessRecipe.CODEC, "recipe", BuiltInRegistries.ITEM.getKey(out.getItem()).getPath());
    }

    private <T> void write(T object, Codec<T> codec, String type, String path) {
        this.futures.add(this.registries.thenCompose(regs -> DataProvider.saveStable(this.cachedOutput, regs, codec, object, outputDir.resolve(type + "/" + path + ".json"))));
    }

    public static Ingredient potionIngredient(Holder<Potion> type) {
        HolderSet<Item> items = HolderSet.direct(BuiltInRegistries.ITEM.wrapAsHolder(Items.POTION));
        DataComponentPredicate predicate = DataComponentPredicate.builder().expect(DataComponents.POTION_CONTENTS, new PotionContents(type)).build();
        return new Ingredient(new DataComponentIngredient(items, predicate, false));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        this.cachedOutput = pOutput;
        genRecipes();
        return CompletableFuture.allOf(this.futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return ApothicEnchanting.MODID;
    }
}
