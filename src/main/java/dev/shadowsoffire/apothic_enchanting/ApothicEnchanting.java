package dev.shadowsoffire.apothic_enchanting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.shadowsoffire.apothic_attributes.ApothicAttributes;
import dev.shadowsoffire.apothic_enchanting.EnchantmentInfo.PowerFunc;
import dev.shadowsoffire.apothic_enchanting.asm.EnchHooks;
import dev.shadowsoffire.apothic_enchanting.data.ApothEnchantmentProvider;
import dev.shadowsoffire.apothic_enchanting.data.EnchTagsProvider;
import dev.shadowsoffire.apothic_enchanting.data.LootProvider;
import dev.shadowsoffire.apothic_enchanting.library.EnchLibraryTile;
import dev.shadowsoffire.apothic_enchanting.objects.TomeItem;
import dev.shadowsoffire.apothic_enchanting.payloads.CluePayload;
import dev.shadowsoffire.apothic_enchanting.payloads.StatsPayload;
import dev.shadowsoffire.apothic_enchanting.table.ApothEnchantingTableBlock;
import dev.shadowsoffire.apothic_enchanting.table.EnchantingStatRegistry;
import dev.shadowsoffire.apothic_enchanting.util.MiscDatagen;
import dev.shadowsoffire.placebo.config.Configuration;
import dev.shadowsoffire.placebo.events.ResourceReloadEvent;
import dev.shadowsoffire.placebo.network.PayloadHelper;
import dev.shadowsoffire.placebo.tabs.ITabFiller;
import dev.shadowsoffire.placebo.tabs.TabFillingRegistry;
import dev.shadowsoffire.placebo.util.PlaceboUtil;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.dispenser.ShearsDispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTab.TabVisibility;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod(ApothicEnchanting.MODID)
public class ApothicEnchanting {

    public static final String MODID = "apothic_enchanting";
    public static final Logger LOGGER = LogManager.getLogger("Apotheosis : Enchantment");

    public static final Map<Enchantment, EnchantmentInfo> ENCHANTMENT_INFO = new HashMap<>();
    public static final Object2IntMap<Enchantment> ENCH_HARD_CAPS = new Object2IntOpenHashMap<>();
    public static final String ENCH_HARD_CAP_IMC = "set_ench_hard_cap";
    public static final List<TomeItem> TYPED_BOOKS = new ArrayList<>();
    static Configuration enchInfoConfig;

    public ApothicEnchanting(IEventBus bus) {
        Ench.bootstrap(bus);
        bus.register(this);
    }

    @SubscribeEvent
    public void init(FMLCommonSetupEvent e) {
        this.reload(null);

        NeoForge.EVENT_BUS.register(new ApothEnchEvents());
        NeoForge.EVENT_BUS.addListener(this::reload);
        e.enqueueWork(() -> {
            DispenserBlock.registerBehavior(Items.SHEARS, new ShearsDispenseItemBehavior());

            TabFillingRegistry.register(Ench.Tabs.ENCH.getKey(), Ench.Items.HELLSHELF, Ench.Items.INFUSED_HELLSHELF, Ench.Items.BLAZING_HELLSHELF, Ench.Items.GLOWING_HELLSHELF, Ench.Items.SEASHELF, Ench.Items.INFUSED_SEASHELF,
                Ench.Items.CRYSTAL_SEASHELF, Ench.Items.HEART_SEASHELF, Ench.Items.DORMANT_DEEPSHELF, Ench.Items.DEEPSHELF, Ench.Items.ECHOING_DEEPSHELF, Ench.Items.SOUL_TOUCHED_DEEPSHELF, Ench.Items.ECHOING_SCULKSHELF,
                Ench.Items.SOUL_TOUCHED_SCULKSHELF, Ench.Items.ENDSHELF, Ench.Items.PEARL_ENDSHELF, Ench.Items.DRACONIC_ENDSHELF, Ench.Items.BEESHELF, Ench.Items.MELONSHELF, Ench.Items.STONESHELF, Ench.Items.SIGHTSHELF,
                Ench.Items.SIGHTSHELF_T2, Ench.Items.FILTERING_SHELF, Ench.Items.TREASURE_SHELF, Ench.Items.GEODE_SHELF, Ench.Items.LIBRARY, Ench.Items.ENDER_LIBRARY);

            TabFillingRegistry.register(Ench.Tabs.ENCH.getKey(), Ench.Items.HELMET_TOME, Ench.Items.CHESTPLATE_TOME, Ench.Items.LEGGINGS_TOME, Ench.Items.BOOTS_TOME, Ench.Items.WEAPON_TOME, Ench.Items.BOW_TOME, Ench.Items.PICKAXE_TOME,
                Ench.Items.FISHING_TOME, Ench.Items.OTHER_TOME, Ench.Items.SCRAP_TOME, Ench.Items.IMPROVED_SCRAP_TOME, Ench.Items.EXTRACTION_TOME);

            TabFillingRegistry.register(Ench.Tabs.ENCH.getKey(), Ench.Items.PRISMATIC_WEB, Ench.Items.INERT_TRIDENT, Ench.Items.WARDEN_TENDRIL, Ench.Items.INFUSED_BREATH);

            fill(Ench.Tabs.ENCH.getKey(), Ench.Enchantments.BERSERKERS_FURY, Ench.Enchantments.CHAINSAW, Ench.Enchantments.CHROMATIC, Ench.Enchantments.CRESCENDO_OF_BOLTS, Ench.Enchantments.EARTHS_BOON,
                Ench.Enchantments.ENDLESS_QUIVER, Ench.Enchantments.WORKER_EXPLOITATION, Ench.Enchantments.GROWTH_SERUM, Ench.Enchantments.ICY_THORNS, Ench.Enchantments.KNOWLEDGE_OF_THE_AGES, Ench.Enchantments.LIFE_MENDING,
                Ench.Enchantments.MINERS_FERVOR, Ench.Enchantments.NATURES_BLESSING, Ench.Enchantments.REBOUNDING, Ench.Enchantments.REFLECTIVE_DEFENSES, Ench.Enchantments.SCAVENGER, Ench.Enchantments.SHIELD_BASH,
                Ench.Enchantments.STABLE_FOOTING, Ench.Enchantments.TEMPTING);

            PlaceboUtil.registerCustomColor(Ench.Colors.LIGHT_BLUE_FLASH);
        });

        EnchantingStatRegistry.INSTANCE.registerToBus();

        PayloadHelper.registerPayload(new CluePayload.Provider());
        PayloadHelper.registerPayload(new StatsPayload.Provider());
    }

    /**
     * This handles IMC events for the enchantment module. <br>
     * Currently only one type is supported. A mod may pass a single {@link EnchantmentInstance} indicating the hard capped max level for an enchantment. <br>
     * That pair must use the method {@link ENCH_HARD_CAP_IMC}.
     */
    @SubscribeEvent
    public void handleIMC(InterModProcessEvent e) {
        e.getIMCStream(ENCH_HARD_CAP_IMC::equals).forEach(msg -> {
            try {
                EnchantmentInstance data = (EnchantmentInstance) msg.messageSupplier().get();
                if (data != null && data.enchantment != null && data.level > 0) {
                    ENCH_HARD_CAPS.put(data.enchantment, data.level);
                }
                else LOGGER.error("Failed to process IMC message with method {} from {} (invalid values passed).", msg.method(), msg.senderModId());
            }
            catch (Exception ex) {
                LOGGER.error("Exception thrown during IMC message with method {} from {}.", msg.method(), msg.senderModId());
                ex.printStackTrace();
            }
        });
    }

    @SubscribeEvent
    public void caps(RegisterCapabilitiesEvent e) {
        e.registerBlockEntity(ItemHandler.BLOCK, Ench.Tiles.LIBRARY.get(), EnchLibraryTile::getItemHandler);
        e.registerBlockEntity(ItemHandler.BLOCK, Ench.Tiles.ENDER_LIBRARY.get(), EnchLibraryTile::getItemHandler);
        e.registerBlockEntity(ItemHandler.BLOCK, BlockEntityType.ENCHANTING_TABLE, ApothEnchantingTableBlock::getItemHandler);
    }

    @SubscribeEvent
    public void data(GatherDataEvent e) {
        PackOutput output = e.getGenerator().getPackOutput();
        MiscDatagen gen = new MiscDatagen(output.getOutputFolder(Target.DATA_PACK).resolve(MODID));
        e.getGenerator().addProvider(true, gen);

        RegistrySetBuilder regSet = new RegistrySetBuilder()
            .add(Registries.ENCHANTMENT, ApothEnchantmentProvider::bootstrap);

        e.getGenerator().addProvider(true, new DatapackBuiltinEntriesProvider(output, e.getLookupProvider(), regSet, Set.of(MODID, "minecraft")));
        e.getGenerator().addProvider(true, LootProvider.create(output, e.getLookupProvider()));
        e.getGenerator().addProvider(true, new EnchTagsProvider(output, e.getLookupProvider(), e.getExistingFileHelper()));
    }

    @SuppressWarnings("deprecation")
    public static EnchantmentInfo getEnchInfo(Enchantment ench) {
        EnchantmentInfo info = ENCHANTMENT_INFO.get(ench);

        if (enchInfoConfig == null) { // Legitimate occurances can now happen, such as when vanilla calls fillItemGroup
            // LOGGER.error("A mod has attempted to access enchantment information before Apotheosis init, this should not happen.");
            // Thread.dumpStack();
            return new EnchantmentInfo(ench);
        }

        if (info == null) { // Should be impossible now.
            info = EnchantmentInfo.load(ench, enchInfoConfig);
            ENCHANTMENT_INFO.put(ench, info);
            if (enchInfoConfig.hasChanged()) enchInfoConfig.save();
            LOGGER.error("Had to late load enchantment info for {}, this is a bug in the mod {} as they are registering late!", BuiltInRegistries.ENCHANTMENT.getKey(ench), BuiltInRegistries.ENCHANTMENT.getKey(ench).getNamespace());
        }

        return info;
    }

    /**
     * Tries to find a max level for this enchantment. This is used to scale up default levels to the Apoth cap.
     * Single-Level enchantments are not scaled.
     * Barring that, enchantments are scaled using the {@link EnchantmentInfo#defaultMin(Enchantment)} until outside the default level space.
     */
    public static int getDefaultMax(Enchantment ench) {
        int level = ench.getMaxLevel();
        if (level == 1) return 1;
        PowerFunc minFunc = EnchantmentInfo.defaultMin(ench);
        int max = 200;
        int minPower = minFunc.getPower(level);
        if (minPower >= max) return level;
        int lastPower = minPower;
        while (minPower < max) {
            minPower = minFunc.getPower(++level);
            if (lastPower == minPower) return level;
            if (minPower > max) return level - 1;
            lastPower = minPower;
        }
        return level;
    }

    @SafeVarargs
    public static void fill(ResourceKey<CreativeModeTab> tab, ResourceKey<Enchantment>... enchants) {
        Arrays.stream(enchants).map(ApothicEnchanting::enchFiller).forEach(filler -> TabFillingRegistry.register(filler, tab));
    }

    public static ITabFiller enchFiller(ResourceKey<Enchantment> e) {
        return (tab, event) -> {
            Holder<Enchantment> ench = event.getParameters().holders().lookupOrThrow(Registries.ENCHANTMENT).get(e).orElse(null);
            if (ench == null) {
                return;
            }

            int maxLevel = EnchHooks.getMaxLevel(ench.value());
            event.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ench, maxLevel)), TabVisibility.PARENT_TAB_ONLY);
            for (int level = 1; level <= maxLevel; level++) {
                event.accept(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ench, level)), TabVisibility.SEARCH_TAB_ONLY);
            }
        };
    }

    public void reload(ResourceReloadEvent e) {
        enchInfoConfig = new Configuration(ApothicAttributes.getConfigFile("enchantments"));
        enchInfoConfig.setTitle("Apotheosis Enchantment Information");
        enchInfoConfig.setComment("This file contains configurable data for each enchantment.\nThe names of each category correspond to the registry names of every loaded enchantment.");
        ENCHANTMENT_INFO.clear();

        for (Enchantment ench : BuiltInRegistries.ENCHANTMENT) {
            ENCHANTMENT_INFO.put(ench, EnchantmentInfo.load(ench, enchInfoConfig));
        }

        for (Enchantment ench : BuiltInRegistries.ENCHANTMENT) {
            EnchantmentInfo info = ENCHANTMENT_INFO.get(ench);
            for (int i = 1; i <= info.getMaxLevel(); i++)
                if (info.getMinPower(i) > info.getMaxPower(i))
                    LOGGER.warn("Enchantment {} has min/max power {}/{} at level {}, making this level unobtainable except by combination.", BuiltInRegistries.ENCHANTMENT.getKey(ench), info.getMinPower(i), info.getMaxPower(i), i);
        }

        if (e == null && enchInfoConfig.hasChanged()) enchInfoConfig.save();
        ApothEnchConfig.load(new Configuration(ApothicAttributes.getConfigFile(MODID)));
    }

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
