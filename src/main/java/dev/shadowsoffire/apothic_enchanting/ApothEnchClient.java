package dev.shadowsoffire.apothic_enchanting;

import java.util.ArrayList;
import java.util.List;

import dev.shadowsoffire.apothic_enchanting.Ench.Particles;
import dev.shadowsoffire.apothic_enchanting.client.DrawsOnLeft;
import dev.shadowsoffire.apothic_enchanting.library.EnchLibraryScreen;
import dev.shadowsoffire.apothic_enchanting.payloads.CluePayload;
import dev.shadowsoffire.apothic_enchanting.payloads.StatsPayload;
import dev.shadowsoffire.apothic_enchanting.table.ApothEnchantmentScreen;
import dev.shadowsoffire.apothic_enchanting.util.TooltipUtil;
import dev.shadowsoffire.placebo.util.EnchantmentUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.client.particle.FlyTowardsPositionParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
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
        e.registerSpriteSet(Particles.ENCHANT_FIRE.get(), FlyTowardsPositionParticle.EnchantProvider::new);
        e.registerSpriteSet(Particles.ENCHANT_WATER.get(), FlyTowardsPositionParticle.EnchantProvider::new);
        e.registerSpriteSet(Particles.ENCHANT_SCULK.get(), FlyTowardsPositionParticle.EnchantProvider::new);
        e.registerSpriteSet(Particles.ENCHANT_END.get(), FlyTowardsPositionParticle.EnchantProvider::new);
    }

    public static void handleCluePayload(CluePayload msg) {
        if (Minecraft.getInstance().screen instanceof ApothEnchantmentScreen es) {
            es.acceptClues(msg.slot(), msg.clues(), msg.all());
        }
    }

    public static void handleStatsPayload(StatsPayload msg) {
        if (Minecraft.getInstance().screen instanceof ApothEnchantmentScreen es) {
            es.getMenu().setStats(msg.stats());
        }
    }

    public static class ForgeBusEvents {

        @SubscribeEvent
        public void tooltips(ItemTooltipEvent e) {
            Item i = e.getItemStack().getItem();
            List<Component> tooltip = e.getToolTip();
            if (i == Ench.Items.PRISMATIC_WEB.value()) {
                tooltip.add(TooltipUtil.lang("info", "prismatic_cobweb").withStyle(ChatFormatting.GRAY));
            }
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
                    // Since we're calling with an invalid context, this may fail, and we need to handle that quietly.
                    ApothicEnchanting.LOGGER.trace(ex.getMessage());
                    StackTraceElement[] trace = ex.getStackTrace();
                    for (StackTraceElement traceElement : trace)
                        ApothicEnchanting.LOGGER.trace("\tat " + traceElement);
                }

                if (state == null) {
                    state = block.defaultBlockState();
                }

                TooltipUtil.appendBlockStats(world, state, BlockPos.ZERO, tooltip::add);
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

    }

}
