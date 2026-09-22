package de.cech12.woodenhopper.mixin;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.llamalad7.mixinextras.sugar.Local;

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
            ItemStack stack,
            int startIndex,
            int endIndex,
            boolean reverseDirection,
            CallbackInfoReturnable<Boolean> callbackInfo,
            @Local Slot slot,
            @Local(ordinal = 1) ItemStack itemstack
    ) {
        slot.set(itemstack);
    }

}
