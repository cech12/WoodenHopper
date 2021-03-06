package cech12.woodenhopper.block;

import cech12.woodenhopper.config.ServerConfig;
import cech12.woodenhopper.tileentity.WoodenHopperTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;

public class WoodenHopperItemHandler extends InvWrapper
{
    private final WoodenHopperTileEntity hopper;

    public WoodenHopperItemHandler(WoodenHopperTileEntity hopper)
    {
        super(hopper);
        this.hopper = hopper;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (simulate) {
            return super.insertItem(slot, stack, true);
        } else {
            boolean wasEmpty = getInv().isEmpty();
            int originalStackSize = stack.getCount();
            stack = super.insertItem(slot, stack, false);
            if (wasEmpty && originalStackSize > stack.getCount())
            {
                if (!hopper.mayTransfer())
                {
                    hopper.setTransferCooldown(ServerConfig.WOODEN_HOPPER_COOLDOWN.get());
                }
            }
            return stack;
        }
    }
}
