package dev.shadowsoffire.apothic_enchanting.mixin.client;

import org.spongepowered.asm.mixin.Mixin;

import dev.shadowsoffire.apothic_enchanting.table.ApothEnchantScreen.SuperRender;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.EnchantmentScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.EnchantmentMenu;

@Mixin(value = EnchantmentScreen.class, remap = false)
public abstract class EnchantmentScreenMixin extends AbstractContainerScreen<EnchantmentMenu> implements SuperRender {

    public EnchantmentScreenMixin(EnchantmentMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    public void apoth_superRender(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    }

}
