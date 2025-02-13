package net.roguelogix.biggerreactors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.phosphophyllite.client.gui.RenderHelper;
import net.roguelogix.phosphophyllite.client.gui.elements.RenderedElement;

import javax.annotation.Nonnull;
import java.util.Locale;

@OnlyIn(Dist.CLIENT)
public class CommonRender {

    // This is a separate atlas with textures used by several things. For example, RF and fluid level markers.
    // Gauge-specific textures should still be contained alongside the screen it's used in. If it's used in multiple
    // screens though, then it's probably a good candidate for the common texture.
    public static final ResourceLocation COMMON_RESOURCE_TEXTURE = new ResourceLocation(BiggerReactors.modid, "textures/screen/common.png");

    /**
     * Render an energy gauge.
     * Such a gauge is assumed to be 18x64 pixels in size. Other sizes may be used, but YMMV.
     * This element renders using the common resource texture.
     *
     * @param poseStack      The current pose stack.
     * @param symbol         The symbol to draw as.
     * @param energyStored   The amount of energy to draw.
     * @param energyCapacity The max energy capacity that can be displayed.
     */
    public static <T extends AbstractContainerMenu> void renderEnergyGauge(@Nonnull PoseStack poseStack, @Nonnull RenderedElement<T> symbol, long energyStored, long energyCapacity) {
        // Preserve the previously selected texture and bind the common texture.
        ResourceLocation preservedResource = RenderHelper.getCurrentResource();
        RenderHelper.bindTexture(COMMON_RESOURCE_TEXTURE);
        // If there's no energy, there's no need to draw.
        if (energyCapacity > 0) {
            // Calculate how much needs to be rendered.
            int renderSize = (int) ((symbol.height * energyStored) / energyCapacity);
            // Render energy.
            symbol.blit(poseStack, 54, 0);
            // Render backdrop/mask away extra energy.
            symbol.blit(poseStack, symbol.width, symbol.height - renderSize, 36, 0);
        }
        // Draw frame.
        symbol.blit(poseStack, 0, 0);
        // Update tooltip.
        symbol.tooltip = Component.literal(String.format("%s/%s",
                RenderHelper.formatValue(energyStored, "RF"),
                RenderHelper.formatValue(energyCapacity, "RF")));
        // Reset color and restore the previously bound texture.
        RenderHelper.clearRenderColor();
        RenderHelper.bindTexture(preservedResource);
    }

    /**
     * Render a fluid gauge.
     * Such a gauge is assumed to be 18x64 pixels in size. Other sizes may be used, but YMMV.
     * This element renders using the common resource texture.
     *
     * @param poseStack     The current pose stack.
     * @param symbol        The symbol to draw as.
     * @param fluidStored   The amount of fluid to draw.
     * @param fluidCapacity The max fluid capacity that can be displayed.
     * @param fluid         The fluid to use.
     */
    public static <T extends AbstractContainerMenu> void renderFluidGauge(@Nonnull PoseStack poseStack, @Nonnull RenderedElement<T> symbol, long fluidStored, long fluidCapacity, Fluid fluid) {
        // Preserve the previously selected texture and bind the common texture.
        ResourceLocation preservedResource = RenderHelper.getCurrentResource();
        RenderHelper.bindTexture(COMMON_RESOURCE_TEXTURE);
        // If there's no fluid, there's no need to draw.
        if (fluidCapacity > 0) {
            // Calculate how much needs to be rendered.
            int renderSize = (int) ((symbol.height * fluidStored) / fluidCapacity);
            // Render fluid. Offset by 1, otherwise it doesn't align with the frame.
            RenderHelper.drawFluidGrid(poseStack, symbol.x + 1, symbol.y, symbol.getBlitOffset(), 16, 16, fluid, 1, 4);
            // Render backdrop/mask away extra fluid.
            symbol.blit(poseStack, symbol.width, symbol.height - renderSize, 18, 0);
        }
        // Draw frame.
        symbol.blit(poseStack, 0, 0);
        // Draw level marks.
        symbol.blit(poseStack, 72, 0);
        // Update tooltip.
        symbol.tooltip = Component.literal(String.format("%s/%s of %s",
                RenderHelper.formatValue((fluidStored / 1000.0), "B", true),
                RenderHelper.formatValue((fluidCapacity / 1000.0), "B", true),
                new FluidStack(fluid, 1).getDisplayName().getString().toLowerCase(Locale.US)));
        // Reset color and restore the previously bound texture.
        RenderHelper.clearRenderColor();
        RenderHelper.bindTexture(preservedResource);
    }
}
