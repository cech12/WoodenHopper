package cech12.woodenhopper.client;

import cech12.woodenhopper.WoodenHopperMod;
import cech12.woodenhopper.inventory.WoodenHopperContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class WoodenHopperScreen extends ContainerScreen<WoodenHopperContainer> {
    /** The ResourceLocation containing the gui texture for the hopper */
    private static final ResourceLocation HOPPER_GUI_TEXTURE = new ResourceLocation(WoodenHopperMod.MOD_ID, "textures/gui/container/wooden_hopper.png");

    public WoodenHopperScreen(WoodenHopperContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.passEvents = false;
        this.ySize = 133;
        this.playerInventoryTitleY = this.ySize - 94;
    }

    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.func_230459_a_(matrixStack, mouseX, mouseY);
    }

    protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrixStack, float partialTicks, int x, int y) {
        if (this.minecraft != null) {
            this.minecraft.getTextureManager().bindTexture(HOPPER_GUI_TEXTURE);
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
        }
    }
}
