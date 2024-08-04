package dev.shadowsoffire.apothic_enchanting.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

import net.minecraft.world.item.CrossbowItem;

@Pseudo
@Mixin(targets = "com.hollingsworth.arsnouveau.common.items.SpellCrossbow", remap = false)
public class SpellCrossbowMixin extends CrossbowItem {

    public SpellCrossbowMixin(Properties pProperties) {
        super(pProperties);
    }

//    @Inject(method = "m_7203_", at = @At(value = "INVOKE", target = "Lcom/hollingsworth/arsnouveau/common/items/SpellCrossbow;shootStoredProjectiles(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;FF)V", remap = false))
//    public void apoth_preFired(Level pLevel, Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> ci) {
//        CrescendoEnchant.preArrowFired(pPlayer.getItemInHand(pHand));
//    }
//
//    @Inject(method = "m_7203_", at = @At(value = "RETURN", ordinal = 0))
//    public void apoth_addCharges(Level pLevel, Player pPlayer, InteractionHand pHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> ci) {
//        CrescendoEnchant.onArrowFired(pPlayer.getItemInHand(pHand));
//    }
//
//    @Inject(method = "getArrow", at = @At(value = "RETURN"), remap = false)
//    private void apoth_markArrows(Level pLevel, LivingEntity pLivingEntity, ItemStack pCrossbowStack, ItemStack pAmmoStack, CallbackInfoReturnable<AbstractArrow> ci) {
//        CrescendoHooks.markGeneratedArrows(ci.getReturnValue(), pCrossbowStack);
//    }

}
