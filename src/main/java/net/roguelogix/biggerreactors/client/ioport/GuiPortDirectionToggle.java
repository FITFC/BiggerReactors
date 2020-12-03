package net.roguelogix.biggerreactors.client.ioport;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.roguelogix.biggerreactors.BiggerReactors;
import net.roguelogix.phosphophyllite.gui.GuiSync;
import net.roguelogix.phosphophyllite.gui.old.client.GuiPartBase;
import net.roguelogix.phosphophyllite.gui.old.client.GuiRenderHelper;
import net.roguelogix.phosphophyllite.gui.old.client.api.IHasTooltip;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GuiPortDirectionToggle<T extends Container> extends GuiPartBase<T> implements IHasTooltip {

    private final ResourceLocation texture = new ResourceLocation(BiggerReactors.modid, "textures/screen/parts/gui_symbols.png");
    private boolean debounce = false;
    private boolean inputState;

    /**
     * @param screen The screen this instance belongs to.
     * @param xPos   The X position of the part.
     * @param yPos   The Y position of the part.
     * @param xSize  The width of the part.
     * @param ySize  The height of the part.
     */
    public GuiPortDirectionToggle(ContainerScreen<T> screen, int xPos, int yPos, int xSize, int ySize) {
        super(screen, xPos, yPos, xSize, ySize);
    }

    public void updateState(boolean inputState) {
        this.inputState = inputState;
    }

    /**
     * Render this element.
     */
    @Override
    public void drawPart(MatrixStack mStack) {
        // Reset and bind texture.
        super.drawPart(mStack);
        GuiRenderHelper.setTexture(this.texture);

        // Draw button.
        if (this.debounce) {
            GuiRenderHelper.setTextureOffset(16, 64);
        } else {
            GuiRenderHelper.setTextureOffset(0, 64);
        }
        GuiRenderHelper.draw(mStack, this.xPos, this.yPos, this.screen.getBlitOffset(), this.xSize, this.ySize);
    }

    @Override
    public void drawTooltip(MatrixStack mStack, int mouseX, int mouseY) {
        if (this.isMouseOver(mouseX, mouseY)) {
            this.screen.func_243308_b(mStack, Arrays.stream(new TranslationTextComponent("tooltip.biggerreactors.buttons.io").getString().split("\\n")).map(StringTextComponent::new).collect(Collectors.toList()), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else {
            ((GuiSync.IGUIPacketProvider) this.screen.getContainer()).executeRequest("setDirection", !inputState);
            assert this.screen.getMinecraft().player != null;
            this.screen.getMinecraft().player.playSound(SoundEvents.UI_BUTTON_CLICK, this.screen.getMinecraft().gameSettings.getSoundLevel(SoundCategory.MASTER), 1.0F);
            return true;
        }
    }
}