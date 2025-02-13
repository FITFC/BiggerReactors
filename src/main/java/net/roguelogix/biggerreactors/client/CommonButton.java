package net.roguelogix.biggerreactors.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.roguelogix.phosphophyllite.client.gui.RenderHelper;
import net.roguelogix.phosphophyllite.client.gui.screens.PhosphophylliteScreen;
import net.roguelogix.phosphophyllite.client.gui.elements.InteractiveElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class CommonButton<T extends AbstractContainerMenu> extends InteractiveElement<T> {

    /**
     * Default constructor.
     *
     * @param parent       The parent screen of this element.
     * @param x            The x position of this element.
     * @param y            The y position of this element.
     * @param width   The width of this element.
     * @param height  The height of this element.
     * @param u       The u offset to use when rendering this element (starting from the left, and moving right).
     * @param v       The v offset to use when rendering this element (starting from the top, and moving down).
     * @param tooltip      The tooltip for this element. If null, a tooltip will not render. If you set a tooltip later, use StringTextComponent.EMPTY.
     */
    public CommonButton(@Nonnull PhosphophylliteScreen<T> parent, int x, int y, int width, int height, int u, int v, @Nullable Component tooltip) {
        super(parent, x, y, width, height, u, v, tooltip);
    }

    /**
     * Render element.
     *
     * @param poseStack The current pose stack.
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     */
    @Override
    public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY) {
        // Check conditions.
        if (this.renderEnable) {
            // Preserve the previously selected texture and bind the common texture.
            ResourceLocation preservedResource = RenderHelper.getCurrentResource();
            RenderHelper.bindTexture(CommonRender.COMMON_RESOURCE_TEXTURE);
            // Check where the mouse is.
            if (this.isMouseOver(mouseX, mouseY)) {
                // Draw active/hovered button.
                if(this.actionEnable) {
                    this.blit(poseStack, this.u, this.v + this.height);
                } else {
                    this.blit(poseStack, this.u, this.v + (this.height * 2));
                }
            } else {
                // Draw inactive/non-hovered button.
                if(this.actionEnable) {
                    this.blit(poseStack, this.u, this.v);
                } else {
                    this.blit(poseStack, this.u, this.v + (this.height * 2));
                }
            }
            // Reset color and restore the previously bound texture.
            RenderHelper.clearRenderColor();
            RenderHelper.bindTexture(preservedResource);
            // Trigger user-defined render logic.
            if (this.onRender != null) {
                this.onRender.trigger(poseStack, mouseX, mouseY);
            }
        }
    }

    /**
     * Triggered when the mouse is released.
     *
     * @param mouseX The x position of the mouse.
     * @param mouseY The y position of the mouse.
     * @param button The button clicked.
     * @return Whether the event was consumed.
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // Check conditions.
        if (this.actionEnable && this.isMouseOver(mouseX, mouseY)) {
            // Play the selection sound.
            this.playSound(SoundEvents.UI_BUTTON_CLICK);
            // Trigger user-defined selection logic.
            if (this.onMouseReleased != null) {
                this.onMouseReleased.trigger(mouseX, mouseY, button);
            }
            // The event was consumed.
            return true;
        } else {
            // The event was not consumed.
            return false;
        }
    }
}
