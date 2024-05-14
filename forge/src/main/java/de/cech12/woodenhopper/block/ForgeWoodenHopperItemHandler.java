package de.cech12.woodenhopper.block;

import de.cech12.woodenhopper.blockentity.WoodenHopperBlockEntity;
import de.cech12.woodenhopper.platform.Services;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.NotNull;

public class ForgeWoodenHopperItemHandler extends InvWrapper
{
    private final WoodenHopperBlockEntity hopper;

    public ForgeWoodenHopperItemHandler(WoodenHopperBlockEntity hopper) {
        super(hopper);
        this.hopper = hopper;
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate)
    {
        if (simulate) {
            return super.insertItem(slot, stack, true);
        } else {
            boolean wasEmpty = getInv().isEmpty();
            int originalStackSize = stack.getCount();
            stack = super.insertItem(slot, stack, false);
            if (wasEmpty && originalStackSize > stack.getCount())
            {
                if (hopper.mayNotTransfer())
                {
                    hopper.setTransferCooldown(Services.CONFIG.getCooldown());
                }
            }
            return stack;
        }
    }
}
