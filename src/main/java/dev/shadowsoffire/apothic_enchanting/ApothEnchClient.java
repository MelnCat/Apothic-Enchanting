package dev.shadowsoffire.apothic_enchanting;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import dev.shadowsoffire.apothic_enchanting.Ench.Particles;
import dev.shadowsoffire.apothic_enchanting.api.IEnchantingBlock;
import dev.shadowsoffire.apothic_enchanting.client.DrawsOnLeft;
import dev.shadowsoffire.apothic_enchanting.library.EnchLibraryScreen;
import dev.shadowsoffire.apothic_enchanting.table.ApothEnchantmentScreen;
import dev.shadowsoffire.apothic_enchanting.table.EnchantingStatRegistry;
import dev.shadowsoffire.apothic_enchanting.util.TooltipUtil;
import dev.shadowsoffire.placebo.util.EnchantmentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.particle.EnchantmentTableParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod.EventBusSubscriber;
import net.neoforged.fml.common.Mod.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = ApothicEnchanting.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class ApothEnchClient {

    private static final BlockHitResult MISS = BlockHitResult.miss(Vec3.ZERO, Direction.NORTH, BlockPos.ZERO);

    @SubscribeEvent
    public static void client(FMLClientSetupEvent e) {
        NeoForge.EVENT_BUS.register(new ForgeBusEvents());
    }

    @SubscribeEvent
    public static void screens(RegisterMenuScreensEvent e) {
        e.register(Ench.Menus.ENCHANTING_TABLE.get(), ApothEnchantmentScreen::new);
        e.register(Ench.Menus.LIBRARY.get(), EnchLibraryScreen::new);
    }

    @SubscribeEvent
    public static void particleFactories(RegisterParticleProvidersEvent e) {
        e.registerSpriteSet(Particles.ENCHANT_FIRE.get(), EnchantmentTableParticle.Provider::new);
        e.registerSpriteSet(Particles.ENCHANT_WATER.get(), EnchantmentTableParticle.Provider::new);
        e.registerSpriteSet(Particles.ENCHANT_SCULK.get(), EnchantmentTableParticle.Provider::new);
        e.registerSpriteSet(Particles.ENCHANT_END.get(), EnchantmentTableParticle.Provider::new);
    }

    public static class ForgeBusEvents {

        @SubscribeEvent
        public void tooltips(ItemTooltipEvent e) {
            Item i = e.getItemStack().getItem();
            List<Component> tooltip = e.getToolTip();
            if (i == Items.COBWEB) tooltip.add(TooltipUtil.lang("info", "cobweb").withStyle(ChatFormatting.GRAY));
            else if (i == Ench.Items.PRISMATIC_WEB.get()) tooltip.add(TooltipUtil.lang("info", "prismatic_cobweb").withStyle(ChatFormatting.GRAY));
            else if (i instanceof BlockItem) {
                Block block = ((BlockItem) i).getBlock();
                Level world = Minecraft.getInstance().level;
                if (world == null || Minecraft.getInstance().player == null) return;
                BlockPlaceContext ctx = new BlockPlaceContext(world, Minecraft.getInstance().player, InteractionHand.MAIN_HAND, e.getItemStack(), MISS);
                BlockState state = null;
                try {
                    state = block.getStateForPlacement(ctx);
                }
                catch (Exception ex) {
                    ApothicEnchanting.LOGGER.debug(ex.getMessage());
                    StackTraceElement[] trace = ex.getStackTrace();
                    for (StackTraceElement traceElement : trace)
                        ApothicEnchanting.LOGGER.debug("\tat " + traceElement);
                }

                if (state == null) state = block.defaultBlockState();
                float maxEterna = EnchantingStatRegistry.getMaxEterna(state, world, BlockPos.ZERO);
                float eterna = EnchantingStatRegistry.getEterna(state, world, BlockPos.ZERO);
                float quanta = EnchantingStatRegistry.getQuanta(state, world, BlockPos.ZERO);
                float arcana = EnchantingStatRegistry.getArcana(state, world, BlockPos.ZERO);
                float rectification = EnchantingStatRegistry.getQuantaRectification(state, world, BlockPos.ZERO);
                int clues = EnchantingStatRegistry.getBonusClues(state, world, BlockPos.ZERO);
                boolean treasure = ((IEnchantingBlock) state.getBlock()).allowsTreasure(state, world, BlockPos.ZERO);
                if (eterna != 0 || quanta != 0 || arcana != 0 || rectification != 0 || clues != 0) {
                    tooltip.add(TooltipUtil.lang("info", "ench_stats").withStyle(ChatFormatting.GOLD));
                }
                if (eterna != 0) {
                    if (eterna > 0) {
                        tooltip.add(TooltipUtil.lang("info", "eterna.p", String.format("%.2f", eterna), String.format("%.2f", maxEterna)).withStyle(ChatFormatting.GREEN));
                    }
                    else tooltip.add(TooltipUtil.lang("info", "eterna", String.format("%.2f", eterna)).withStyle(ChatFormatting.GREEN));
                }
                if (quanta != 0) {
                    tooltip.add(TooltipUtil.lang("info", "quanta" + (quanta > 0 ? ".p" : ""), String.format("%.2f", quanta)).withStyle(ChatFormatting.RED));
                }
                if (arcana != 0) {
                    tooltip.add(TooltipUtil.lang("info", "arcana" + (arcana > 0 ? ".p" : ""), String.format("%.2f", arcana)).withStyle(ChatFormatting.DARK_PURPLE));
                }
                if (rectification != 0) {
                    tooltip.add(TooltipUtil.lang("info", "rectification" + (rectification > 0 ? ".p" : ""), String.format("%.2f", rectification)).withStyle(ChatFormatting.YELLOW));
                }
                if (clues != 0) {
                    tooltip.add(TooltipUtil.lang("info", "clues" + (clues > 0 ? ".p" : ""), String.format("%d", clues)).withStyle(ChatFormatting.DARK_AQUA));
                }
                if (treasure) {
                    tooltip.add(TooltipUtil.lang("info", "allows_treasure").withStyle(ChatFormatting.GOLD));
                }
                Set<Enchantment> blacklist = ((IEnchantingBlock) state.getBlock()).getBlacklistedEnchantments(state, world, BlockPos.ZERO);
                if (blacklist.size() > 0) {
                    tooltip.add(TooltipUtil.lang("info", "filter").withStyle(s -> s.withColor(0x58B0CC)));
                    for (Enchantment ench : blacklist) {
                        MutableComponent name = (MutableComponent) ench.getFullname(1);
                        name.getSiblings().clear();
                        name.withStyle(s -> s.withColor(0x5878AA));
                        tooltip.add(Component.literal(" - ").append(name).withStyle(s -> s.withColor(0x5878AA)));
                    }
                }
            }
            else if (i == Items.ENCHANTED_BOOK) {
                ItemStack stack = e.getItemStack();
                var enchMap = EnchantmentHelper.getEnchantments(stack);
                if (enchMap.size() == 1) {
                    var ench = enchMap.keySet().iterator().next();
                    int lvl = enchMap.values().iterator().next();
                    if (!ModList.get().isLoaded("enchdesc")) {
                        if (ApothicEnchanting.MODID.equals(BuiltInRegistries.ENCHANTMENT.getKey(ench).getNamespace())) {
                            tooltip.add(Component.translatable(ench.getDescriptionId() + ".desc").withStyle(ChatFormatting.DARK_GRAY));
                        }
                    }
                    if (ApothEnchConfig.showEnchantedBookMetadata) {
                        var info = ApothicEnchanting.getEnchInfo(ench);
                        Object[] args = new Object[4];
                        args[0] = boolComp("discoverable", info.isDiscoverable());
                        args[1] = boolComp("lootable", info.isLootable());
                        args[2] = boolComp("tradeable", info.isTradeable());
                        args[3] = boolComp("treasure", info.isTreasure());
                        if (e.getFlags().isAdvanced()) {
                            tooltip.add(Component.translatable("%s \u2507 %s \u2507 %s \u2507 %s", args[0], args[1], args[2], args[3]).withStyle(ChatFormatting.DARK_GRAY));
                            tooltip.add(TooltipUtil.lang("info", "book_range", info.getMinPower(lvl), info.getMaxPower(lvl)).withStyle(ChatFormatting.GREEN));
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        public void drawAnvilCostBlob(ScreenEvent.Render.Post e) {
            if (e.getScreen() instanceof AnvilScreen anv) {
                int level = anv.getMenu().getCost();
                if (level <= 0 || !anv.getMenu().getSlot(anv.getMenu().getResultSlot()).hasItem()) return;
                List<Component> list = new ArrayList<>();
                list.add(TooltipUtil.lang("info", "anvil_at", level).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.GREEN));
                int expCost = EnchantmentUtils.getTotalExperienceForLevel(level);
                list.add(TooltipUtil.lang("info", "anvil_xp_cost", Component.literal("" + expCost).withStyle(ChatFormatting.GREEN),
                    Component.literal("" + level).withStyle(ChatFormatting.GREEN)));
                DrawsOnLeft.draw(anv, e.getGuiGraphics(), list, anv.getGuiTop() + 28);
            }
        }

        private static Component boolComp(String key, boolean flag) {
            return TooltipUtil.lang("info", key + (flag ? "" : ".not")).withStyle(Style.EMPTY.withColor(flag ? 0x108810 : 0xAA1616));
        }

    }

}
