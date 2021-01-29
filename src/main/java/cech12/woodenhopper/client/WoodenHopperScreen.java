package cech12.woodenhopper.client;

import cech12.woodenhopper.WoodenHopperMod;
import cech12.woodenhopper.inventory.WoodenHopperContainer;
//import com.mojang.blaze3d.matrix.MatrixStack; //1.16
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

//import javax.annotation.Nonnull; //1.16

@OnlyIn(Dist.CLIENT)
public class WoodenHopperScreen extends ContainerScreen<WoodenHopperContainer> {
    /** The ResourceLocation containing the gui texture for the hopper */
    private static final ResourceLocation HOPPER_GUI_TEXTURE = new ResourceLocation(WoodenHopperMod.MOD_ID, "textures/gui/container/wooden_hopper.png");

    public WoodenHopperScreen(WoodenHopperContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.passEvents = false;
        this.ySize = 133;
        //this.playerInventoryTitleY = this.ySize - 94; //1.16
    }

    /**
     * 1.15
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        this.font.drawString(this.title.getFormattedText(), 8.0F, 6.0F, 4210752);
        this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0F, (float)(this.ySize - 96 + 2), 4210752);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) { //1.15
        this.renderBackground(); //1.15
        super.render(mouseX, mouseY, partialTicks); //1.15
        this.renderHoveredToolTip(mouseX, mouseY); //1.15
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int x, int y) { //1.15
        if (this.minecraft != null) {
            this.minecraft.getTextureManager().bindTexture(HOPPER_GUI_TEXTURE);
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            this.blit(i, j, 0, 0, this.xSize, this.ySize); //1.15
        }
    }
}
