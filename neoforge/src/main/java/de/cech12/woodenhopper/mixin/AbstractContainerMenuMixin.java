package de.cech12.woodenhopper.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import de.cech12.woodenhopper.inventory.WoodenHopperContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {

    @Inject(
            method = "moveItemStackTo",
            at = {
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/item/ItemStack;setCount(I)V",
                            ordinal = 1,
                            shift = At.Shift.AFTER
                    ),
                    @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/world/item/ItemStack;setCount(I)V",
                            ordinal = 2,
                            shift = At.Shift.AFTER
                    )
            }
    )
    private void woodenhopper$updateSlotAfterSetCount(
            ItemStack itemStack,
            int startSlot,
            int endSlot,
            boolean backwards,
            CallbackInfoReturnable<Boolean> callbackInfo,
            @Local(name = "slot") Slot slot,
            @Local(name = "itemstack") ItemStack itemstack
    ) {
        if ((Object) this instanceof WoodenHopperContainer) {
            // needed for NeoForge because the ItemStacksResourceHandler of the NeoForgeWoodenHopperBlockEntity returns
            // only copies of the containing item stacks in the getItems method, so the slot needs to be updated with the
            // new ItemStack after the count has been set
            slot.set(itemstack);
        }
    }

}
