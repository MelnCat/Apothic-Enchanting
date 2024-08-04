package dev.shadowsoffire.apothic_enchanting.util;

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
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;

public class MiscDatagen implements DataProvider {

    private final Path outputDir;
    private CachedOutput cachedOutput;
    private List<CompletableFuture<?>> futures = new ArrayList<>();
    private CompletableFuture<HolderLookup.Provider> registries;

    public MiscDatagen(Path outputDir, CompletableFuture<HolderLookup.Provider> registries) {
        this.outputDir = outputDir;
        this.registries = registries;
    }

    private void genRecipes() {
        Ingredient pot = potionIngredient(Potions.REGENERATION);
        addShaped(Ench.Blocks.HELLSHELF.value(), 3, 3, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICKS, Items.BLAZE_ROD, "c:bookshelves", pot, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICKS,
            Blocks.NETHER_BRICKS);
        addShaped(Ench.Items.PRISMATIC_WEB, 3, 3, null, Items.PRISMARINE_SHARD, null, Items.PRISMARINE_SHARD, Blocks.COBWEB, Items.PRISMARINE_SHARD, null, Items.PRISMARINE_SHARD, null);
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
        Ingredient maxHellshelf = Ingredient.of(Ench.Blocks.INFUSED_HELLSHELF.value());
        addShaped(Ench.Blocks.BLAZING_HELLSHELF.value(), 3, 3, null, Items.FIRE_CHARGE, null, Items.FIRE_CHARGE, maxHellshelf, Items.FIRE_CHARGE, Items.BLAZE_POWDER, Items.BLAZE_POWDER, Items.BLAZE_POWDER);
        addShaped(Ench.Blocks.GLOWING_HELLSHELF.value(), 3, 3, null, Blocks.GLOWSTONE, null, null, maxHellshelf, null, Blocks.GLOWSTONE, null, Blocks.GLOWSTONE);
        addShaped(Ench.Blocks.SEASHELF.value(), 3, 3, Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICKS, potionIngredient(Potions.WATER), "c:bookshelves", Items.PUFFERFISH,
            Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICKS, Blocks.PRISMARINE_BRICKS);
        Ingredient maxSeashelf = Ingredient.of(Ench.Blocks.INFUSED_SEASHELF.value());
        addShaped(Ench.Blocks.CRYSTAL_SEASHELF.value(), 3, 3, null, Items.PRISMARINE_CRYSTALS, null, null, maxSeashelf, null, Items.PRISMARINE_CRYSTALS, null, Items.PRISMARINE_CRYSTALS);
        addShaped(Ench.Blocks.HEART_SEASHELF.value(), 3, 3, null, Items.HEART_OF_THE_SEA, null, Items.PRISMARINE_SHARD, maxSeashelf, Items.PRISMARINE_SHARD, Items.PRISMARINE_SHARD, Items.PRISMARINE_SHARD,
            Items.PRISMARINE_SHARD);
        addShaped(Ench.Blocks.PEARL_ENDSHELF.value(), 3, 3, Items.END_ROD, null, Items.END_ROD, Items.ENDER_PEARL, Ench.Blocks.ENDSHELF.value(), Items.ENDER_PEARL, Items.END_ROD, null, Items.END_ROD);
        addShaped(Ench.Blocks.DRACONIC_ENDSHELF.value(), 3, 3, null, Items.DRAGON_HEAD, null, Items.ENDER_PEARL, Ench.Blocks.ENDSHELF.value(), Items.ENDER_PEARL, Items.ENDER_PEARL, Items.ENDER_PEARL, Items.ENDER_PEARL);
        addShaped(Ench.Blocks.BEESHELF.value(), 3, 3, Items.HONEYCOMB, Items.BEEHIVE, Items.HONEYCOMB, Items.HONEY_BLOCK, "c:bookshelves", Items.HONEY_BLOCK, Items.HONEYCOMB, Items.BEEHIVE, Items.HONEYCOMB);
        addShaped(Ench.Blocks.MELONSHELF.value(), 3, 3, Items.MELON, Items.MELON, Items.MELON, Items.GLISTERING_MELON_SLICE, "c:bookshelves", Items.GLISTERING_MELON_SLICE, Items.MELON, Items.MELON, Items.MELON);
        addShaped(Ench.Blocks.GEODE_SHELF.value(), 3, 3, Items.CALCITE, Items.CALCITE, Items.CALCITE, Items.CALCITE, "c:bookshelves", Items.CALCITE, Items.CALCITE, Items.BUDDING_AMETHYST, Items.CALCITE);
    }

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
