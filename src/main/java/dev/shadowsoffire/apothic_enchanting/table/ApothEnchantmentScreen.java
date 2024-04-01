package dev.shadowsoffire.apothic_enchanting.table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import dev.shadowsoffire.apothic_enchanting.ApothicEnchanting;
import dev.shadowsoffire.apothic_enchanting.Ench;
import dev.shadowsoffire.apothic_enchanting.client.DrawsOnLeft;
import dev.shadowsoffire.apothic_enchanting.table.ApothEnchantmentMenu.Arcana;
import dev.shadowsoffire.apothic_enchanting.table.infusion.InfusionRecipe;
import dev.shadowsoffire.apothic_enchanting.util.MiscUtil;
import dev.shadowsoffire.apothic_enchanting.util.TooltipUtil;
import dev.shadowsoffire.placebo.util.EnchantmentUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EnchantmentNames;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

public class ApothEnchantmentScreen extends EnchantmentScreen implements DrawsOnLeft {

    public static final ResourceLocation TEXTURES = ApothicEnchanting.loc("textures/gui/enchanting_table.png");

    protected final ApothEnchantmentMenu menu;
    protected final Int2ObjectMap<List<EnchantmentInstance>> clues = new Int2ObjectOpenHashMap<>();

    protected float eterna = 0, lastEterna = 0, quanta = 0, lastQuanta = 0, arcana = 0, lastArcana = 0;
    protected boolean[] hasAllClues = { false, false, false };

    // menu type is weak due to weird generic stuff regarding screen registration.
    public ApothEnchantmentScreen(EnchantmentMenu container, Inventory inv, Component title) {
        super(container, inv, title);
        this.menu = (ApothEnchantmentMenu) container;
        this.imageHeight = 197;
        this.clues.defaultReturnValue(new ArrayList<>());
    }

    @Override
    protected void renderLabels(GuiGraphics gfx, int mouseX, int mouseY) {
        gfx.drawString(this.font, this.title, 12, 5, 4210752, false);
        gfx.drawString(this.font, this.playerInventoryTitle, 7, this.imageHeight - 96 + 4, 4210752, false);
        gfx.drawString(this.font, TooltipUtil.lang("gui", "enchant.eterna"), 19, 74, 0x3DB53D, false);
        gfx.drawString(this.font, TooltipUtil.lang("gui", "enchant.quanta"), 19, 84, 0xFC5454, false);
        gfx.drawString(this.font, TooltipUtil.lang("gui", "enchant.arcana"), 19, 94, 0xA800A8, false);
    }

    @Override
    public void containerTick() {
        super.containerTick();
        float current = this.menu.stats.eterna();
        if (current != this.eterna) {
            if (current > this.eterna) this.eterna += Math.min(current - this.eterna, Math.max(0.16F, (current - this.eterna) * 0.1F));
            else this.eterna = Math.max(this.eterna - this.lastEterna * 0.075F, current);
        }
        if (current > 0) this.lastEterna = current;

        current = this.menu.stats.quanta();
        if (current != this.quanta) {
            if (current > this.quanta) this.quanta += Math.min(current - this.quanta, Math.max(0.04F, (current - this.quanta) * 0.1F));
            else this.quanta = Math.max(this.quanta - this.lastQuanta * 0.075F, current);
        }
        if (current > 0) this.lastQuanta = current;

        current = this.menu.stats.arcana();
        if (current != this.arcana) {
            if (current > this.arcana) this.arcana += Math.min(current - this.arcana, Math.max(0.04F, (current - this.arcana) * 0.1F));
            else this.arcana = Math.max(this.arcana - this.lastArcana * 0.075F, current);
        }
        if (current > 0) this.lastArcana = current;
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;

        for (int k = 0; k < 3; ++k) {
            double d0 = pMouseX - (i + 60);
            double d1 = pMouseY - (j + 14 + 19 * k);
            if (d0 >= 0.0D && d1 >= 0.0D && d0 < 108.0D && d1 < 19.0D && this.menu.clickMenuButton(this.minecraft.player, k)) {
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, k);
                return true;
            }
        }

        if (this.menu.getSlot(0).hasItem() && this.isHovering(145, -15, 27, 15, pMouseX, pMouseY) && Arrays.stream(this.menu.enchantClue).boxed().map(Enchantment::byId).allMatch(Predicates.notNull())) {
            Minecraft.getInstance().pushGuiLayer(new EnchantingInfoScreen(this));
        }

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    @SuppressWarnings("deprecation")
    @Override
    protected void renderBg(GuiGraphics gfx, float partialTicks, int mouseX, int mouseY) {
        int xCenter = (this.width - this.imageWidth) / 2;
        int yCenter = (this.height - this.imageHeight) / 2;
        gfx.blit(TEXTURES, xCenter, yCenter, 0, 0, this.imageWidth, this.imageHeight);
        this.renderBook(gfx, xCenter, yCenter, partialTicks);

        EnchantmentNames.getInstance().initSeed(this.menu.getEnchantmentSeed());
        int lapis = this.menu.getGoldCount();

        for (int slot = 0; slot < 3; ++slot) {
            int j1 = xCenter + 60;
            int k1 = j1 + 20;
            int level = this.menu.costs[slot];
            if (level == 0) {
                gfx.blit(TEXTURES, j1, yCenter + 14 + 19 * slot, 148, 218, 108, 19);
            }
            else {
                String s = "" + level;
                int width = 86 - this.font.width(s);
                FormattedText itextproperties = EnchantmentNames.getInstance().getRandomName(this.font, width);
                int color = 6839882;
                if ((lapis < slot + 1 || this.minecraft.player.experienceLevel < level) && !this.minecraft.player.getAbilities().instabuild || this.menu.enchantClue[slot] == -1) { // Forge: render buttons as disabled when enchantable
                                                                                                                                                                                    // but enchantability not met on lower levels
                    gfx.blit(TEXTURES, j1, yCenter + 14 + 19 * slot, 148, 218, 108, 19);
                    gfx.blit(TEXTURES, j1 + 1, yCenter + 15 + 19 * slot, 16 * slot, 239, 16, 16);
                    gfx.drawWordWrap(this.font, itextproperties, k1, yCenter + 16 + 19 * slot, width, (color & 16711422) >> 1);
                    color = 4226832;
                }
                else {
                    int k2 = mouseX - (xCenter + 60);
                    int l2 = mouseY - (yCenter + 14 + 19 * slot);
                    if (k2 >= 0 && l2 >= 0 && k2 < 108 && l2 < 19) {
                        gfx.blit(TEXTURES, j1, yCenter + 14 + 19 * slot, 148, 237, 108, 19);
                        color = 16777088;
                    }
                    else {
                        gfx.blit(TEXTURES, j1, yCenter + 14 + 19 * slot, 148, 199, 108, 19);
                    }

                    gfx.blit(TEXTURES, j1 + 1, yCenter + 15 + 19 * slot, 16 * slot, 223, 16, 16);
                    gfx.drawWordWrap(this.font, itextproperties, k1, yCenter + 16 + 19 * slot, width, color);
                    color = 8453920;
                }

                gfx.drawString(this.font, s, k1 + 86 - this.font.width(s), yCenter + 16 + 19 * slot + 7, color);
            }
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURES);
        if (this.eterna > 0) {
            gfx.blit(TEXTURES, xCenter + 59, yCenter + 75, 0, 197, getBarLength(this.eterna), 5);
        }
        if (this.quanta > 0) {
            gfx.blit(TEXTURES, xCenter + 59, yCenter + 85, 0, 202, getBarLength(this.quanta), 5);
        }
        if (this.arcana > 0) {
            gfx.blit(TEXTURES, xCenter + 59, yCenter + 95, 0, 207, getBarLength(this.arcana), 5);
        }

        if (this.menu.getSlot(0).hasItem() && Arrays.stream(this.menu.enchantClue).boxed().map(Enchantment::byId).allMatch(Predicates.notNull())) {
            int u = this.isHovering(145, -15, 27, 15, mouseX, mouseY) ? 15 : 0;
            gfx.blit(TEXTURES, xCenter + 145, yCenter - 15, this.imageWidth, u, 27, 15);
        }
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTicks) {
        partialTicks = this.minecraft.getFrameTime();
        ((SuperRender) this).apoth_superRender(gfx, mouseX, mouseY, partialTicks);
        this.renderTooltip(gfx, mouseX, mouseY);
        boolean creative = this.minecraft.player.getAbilities().instabuild;
        int lapis = this.menu.getGoldCount();

        for (int slot = 0; slot < 3; ++slot) {
            int level = this.menu.costs[slot];
            Enchantment enchantment = Enchantment.byId(this.menu.enchantClue[slot]);
            int cost = slot + 1;
            if (this.isHovering(60, 14 + 19 * slot, 108, 18, mouseX, mouseY) && level > 0) {
                List<Component> list = Lists.newArrayList();
                boolean isFailedInfusion = slot == 2 && enchantment == null && InfusionRecipe.findItemMatch(this.minecraft.level, this.menu.getSlot(0).getItem()) != null;

                if (enchantment != null) {
                    if (!this.clues.get(slot).isEmpty()) {
                        list.add(TooltipUtil.lang("info", "runes" + (this.hasAllClues[slot] ? "_all" : "")).withStyle(ChatFormatting.YELLOW, ChatFormatting.UNDERLINE));
                        for (EnchantmentInstance i : this.clues.get(slot)) {
                            list.add(i.enchantment.getFullname(i.level));
                        }
                    }
                    else {
                        list.add(TooltipUtil.lang("info", "no_clue").withStyle(ChatFormatting.DARK_RED, ChatFormatting.UNDERLINE));
                    }
                }
                else if (isFailedInfusion) {
                    list.add(Ench.Enchantments.INFUSION.get().getFullname(1).copy().withStyle(ChatFormatting.ITALIC));
                    Collections.addAll(list, Component.literal(""), TooltipUtil.lang("info", "infusion_failed").withStyle(ChatFormatting.RED));
                }
                else {
                    list.add(Component.translatable("container.enchant.clue", "").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
                    Collections.addAll(list, Component.literal(""), Component.translatable("neoforge.container.enchant.limitedEnchantability").withStyle(ChatFormatting.RED));
                }

                if (enchantment != null && !creative) {
                    list.add(Component.literal(""));
                    if (this.minecraft.player.experienceLevel < level) {
                        list.add(Component.translatable("container.enchant.level.requirement", this.menu.costs[slot]).withStyle(ChatFormatting.RED));
                    }
                    else {
                        String s;
                        if (cost == 1) {
                            s = I18n.get("container.enchant.lapis.one");
                        }
                        else {
                            s = I18n.get("container.enchant.lapis.many", cost);
                        }

                        ChatFormatting textformatting = lapis >= cost ? ChatFormatting.GRAY : ChatFormatting.RED;
                        list.add(Component.literal(s).withStyle(textformatting));
                        if (cost == 1) {
                            s = I18n.get("container.enchant.level.one");
                        }
                        else {
                            s = I18n.get("container.enchant.level.many", cost);
                        }

                        list.add(Component.literal(s).withStyle(ChatFormatting.GRAY));
                    }
                }
                gfx.renderComponentTooltip(this.font, list, mouseX, mouseY);
                break;
            }
        }

        if (this.isHovering(60, 14 + 19 * 3 + 5, 110, 5, mouseX, mouseY)) {
            List<Component> list = Lists.newArrayList();
            list.add(eterna().append(TooltipUtil.lang("gui", "enchant.eterna.desc")));
            list.add(TooltipUtil.lang("gui", "enchant.eterna.desc2").withStyle(ChatFormatting.GRAY));
            if (this.menu.stats.eterna() > 0) {
                list.add(Component.literal(""));
                list.add(TooltipUtil.lang("gui", "enchant.eterna.desc3", f(this.menu.stats.eterna()), 100).withStyle(ChatFormatting.GRAY));
            }
            gfx.renderComponentTooltip(this.font, list, mouseX, mouseY);
        }
        else if (this.isHovering(60, 14 + 19 * 3 + 15, 110, 5, mouseX, mouseY)) {
            List<Component> list = Lists.newArrayList();
            list.add(quanta().append(TooltipUtil.lang("gui", "enchant.quanta.desc")));
            list.add(TooltipUtil.lang("gui", "enchant.quanta.desc2").withStyle(ChatFormatting.GRAY));
            list.add(rectification().append(TooltipUtil.lang("gui", "enchant.quanta.desc3").withStyle(ChatFormatting.GRAY)));
            if (this.menu.stats.quanta() > 0) {
                list.add(CommonComponents.EMPTY);
                list.add(TooltipUtil.lang("gui", "enchant.quanta.desc4", f(this.menu.stats.quanta())).withStyle(ChatFormatting.GRAY));
                // list.add(TooltipUtil.lang("info", "gui_rectification", f(this.menu.stats.rectification())).withStyle(ChatFormatting.YELLOW));
            }
            gfx.renderComponentTooltip(this.font, list, mouseX, mouseY);
            float quanta = this.menu.stats.quanta();
            if (quanta > 0) {
                list.clear();
                list.add(TooltipUtil.lang("info", "quanta_buff").withStyle(ChatFormatting.UNDERLINE, ChatFormatting.RED));
                // list.add(TooltipUtil.lang("info", "quanta_reduc", f(-quanta + quanta * rectification / 100F)).withStyle(ChatFormatting.DARK_RED));
                list.add(TooltipUtil.lang("info", "quanta_growth", f(quanta)).withStyle(ChatFormatting.BLUE));
                this.drawOnLeft(gfx, list, this.getGuiTop() + 29);
            }
        }
        else if (this.isHovering(60, 14 + 19 * 3 + 25, 110, 5, mouseX, mouseY)) {
            List<Component> list = Lists.newArrayList();
            PoseStack stack = gfx.pose();
            stack.pushPose();
            stack.translate(0, 0, 4);
            list.add(arcana().append(TooltipUtil.lang("gui", "enchant.arcana.desc")));
            list.add(TooltipUtil.lang("gui", "enchant.arcana.desc2").withStyle(ChatFormatting.GRAY));
            list.add(TooltipUtil.lang("gui", "enchant.arcana.desc3").withStyle(ChatFormatting.GRAY));
            if (this.menu.stats.arcana() > 0) {
                list.add(Component.literal(""));
                float ench = this.menu.getSlot(0).getItem().getEnchantmentValue() / 2F;
                list.add(TooltipUtil.lang("gui", "enchant.arcana.desc4", f(this.menu.stats.arcana() - ench)).withStyle(ChatFormatting.GRAY));
                list.add(TooltipUtil.lang("info", "ench_bonus", f(ench)).withStyle(ChatFormatting.YELLOW));
                list.add(TooltipUtil.lang("gui", "enchant.arcana.desc5", f(this.menu.stats.arcana())).withStyle(ChatFormatting.GOLD));
            }
            gfx.renderComponentTooltip(this.font, list, mouseX, mouseY);
            stack.popPose();
            if (this.menu.stats.arcana() > 0) {
                list.clear();
                Arcana a = Arcana.getForThreshold(this.menu.stats.arcana());
                list.add(TooltipUtil.lang("info", "arcana_bonus").withStyle(ChatFormatting.UNDERLINE, ChatFormatting.DARK_PURPLE));
                if (a != Arcana.EMPTY) list.add(TooltipUtil.lang("info", "weights_changed").withStyle(ChatFormatting.BLUE));
                int minEnchants = this.menu.stats.arcana() > 75F ? 3 : this.menu.stats.arcana() > 25F ? 2 : 0;
                if (minEnchants > 0) list.add(TooltipUtil.lang("info", "min_enchants", minEnchants).withStyle(ChatFormatting.BLUE));

                this.drawOnLeft(gfx, list, this.getGuiTop() + 29);
                int offset = 20 + list.size() * this.minecraft.font.lineHeight;
                list.clear();
                list.add(TooltipUtil.lang("info", "rel_weights").withStyle(ChatFormatting.UNDERLINE, ChatFormatting.YELLOW));
                list.add(TooltipUtil.lang("info", "weight", I18n.get("rarity.enchantment.common"), a.rarities[0]).withStyle(ChatFormatting.GRAY));
                list.add(TooltipUtil.lang("info", "weight", I18n.get("rarity.enchantment.uncommon"), a.rarities[1]).withStyle(ChatFormatting.GREEN));
                list.add(TooltipUtil.lang("info", "weight", I18n.get("rarity.enchantment.rare"), a.rarities[2]).withStyle(ChatFormatting.BLUE));
                list.add(TooltipUtil.lang("info", "weight", I18n.get("rarity.enchantment.very_rare"), a.rarities[3]).withStyle(ChatFormatting.GOLD));
                this.drawOnLeft(gfx, list, this.getGuiTop() + 29 + offset);
            }
        }
        else if (this.menu.getSlot(0).hasItem() && this.isHovering(145, -15, 27, 15, mouseX, mouseY) && Arrays.stream(this.menu.enchantClue).boxed().map(Enchantment::byId).allMatch(Predicates.notNull())) {
            List<Component> list = Lists.newArrayList();
            list.add(TooltipUtil.lang("info", "all_available").withStyle(ChatFormatting.BLUE));
            gfx.renderComponentTooltip(this.font, list, mouseX, mouseY);
        }

        ItemStack enchanting = this.menu.getSlot(0).getItem();
        if (!enchanting.isEmpty() && this.menu.costs[2] > 0) {
            for (int slot = 0; slot < 3; slot++) {
                if (this.isHovering(60, 14 + 19 * slot, 108, 18, mouseX, mouseY)) {
                    List<Component> list = new ArrayList<>();
                    int level = this.menu.costs[slot];
                    list.add(TooltipUtil.lang("info", "ench_at", level).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.GREEN));
                    list.add(Component.literal(""));
                    int expCost = MiscUtil.getExpCostForSlot(level, slot);
                    list.add(TooltipUtil.lang("info", "xp_cost", Component.literal("" + expCost).withStyle(ChatFormatting.GREEN),
                        Component.literal("" + EnchantmentUtils.getLevelForExperience(expCost)).withStyle(ChatFormatting.GREEN)));
                    float quanta = this.menu.stats.quanta() / 100F;
                    int minPow = this.menu.stats.stable() ? level : Math.round(Mth.clamp(level - level * quanta, 1, 200));
                    int maxPow = Math.round(Mth.clamp(level + level * quanta, 1, 200));
                    list.add(TooltipUtil.lang("info", "power_range", Component.literal("" + minPow).withStyle(ChatFormatting.DARK_RED), Component.literal("" + maxPow).withStyle(ChatFormatting.BLUE)));
                    list.add(TooltipUtil.lang("info", "item_ench", Component.literal("" + enchanting.getEnchantmentValue()).withStyle(ChatFormatting.GREEN)));
                    list.add(TooltipUtil.lang("info", "num_clues", Component.literal("" + (1 + this.menu.stats.clues())).withStyle(ChatFormatting.DARK_AQUA)));
                    this.drawOnLeft(gfx, list, this.getGuiTop() + 29);
                    break;
                }
            }
        }
    }

    @Override
    public ApothEnchantmentMenu getMenu() {
        return this.menu;
    }

    public void acceptClues(int slot, List<EnchantmentInstance> clues, boolean all) {
        this.clues.put(slot, clues);
        this.hasAllClues[slot] = all;
    }

    /**
     * Gets the pixel length of an enchanting stat bar based on the current value.
     */
    public static int getBarLength(float stat) {
        return (int) (stat / 100 * 110);
    }

    private static MutableComponent eterna() {
        return TooltipUtil.lang("gui", "enchant.eterna").withStyle(ChatFormatting.GREEN);
    }

    private static MutableComponent quanta() {
        return TooltipUtil.lang("gui", "enchant.quanta").withStyle(ChatFormatting.RED);
    }

    private static MutableComponent arcana() {
        return TooltipUtil.lang("gui", "enchant.arcana").withStyle(ChatFormatting.DARK_PURPLE);
    }

    private static MutableComponent rectification() {
        return TooltipUtil.lang("gui", "enchant.rectification").withStyle(ChatFormatting.YELLOW);
    }

    private static String f(float f) {
        return String.format("%.2f", f);
    }

    public static interface SuperRender {
        public void apoth_superRender(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick);
    }
}
