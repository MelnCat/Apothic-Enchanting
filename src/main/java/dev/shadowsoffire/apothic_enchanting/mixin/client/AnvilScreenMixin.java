package dev.shadowsoffire.apothic_enchanting.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import net.minecraft.client.gui.screens.inventory.AnvilScreen;

@Mixin(value = AnvilScreen.class, remap = false)
public class AnvilScreenMixin {

    @ModifyConstant(method = "renderLabels(Lnet/minecraft/client/gui/GuiGraphics;II)V", constant = @Constant(intValue = 40))
    public int apoth_removeLevelCap(int old) {
        return Integer.MAX_VALUE;
    }

}
