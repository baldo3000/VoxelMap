package com.mamiyaotaru.voxelmap.gui.overridden;

import com.mamiyaotaru.voxelmap.VoxelConstants;
import com.mamiyaotaru.voxelmap.util.OpenGL;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;

public abstract class GuiSlotMinimap extends AbstractSelectionList {
    protected int slotWidth = 220;
    private boolean showTopBottomBG = true;
    private boolean showSlotBG = true;
    private boolean hasListHeader;
    protected long lastClicked;
    public boolean doubleclick;
    private int bottom;
    private int fullheight;

    protected GuiSlotMinimap(int width, int height, int top, int bottom, int itemHeight) {
        super(VoxelConstants.getMinecraft(), width, bottom - top, top, itemHeight);
        this.bottom = bottom;
        this.fullheight = height;
    }

    public void setShowTopBottomBG(boolean showTopBottomBG) {
        this.showTopBottomBG = showTopBottomBG;
    }

    public void setShowSlotBG(boolean showSlotBG) {
        this.showSlotBG = showSlotBG;
    }

    public void renderWidget(GuiGraphics drawContext, int mouseX, int mouseY, float delta) {
        int scrollBarLeft = getScrollbarPositionX();
        int scrollBarRight = scrollBarLeft + 6;

        setScrollAmount(getScrollAmount());

        Tesselator tessellator = Tesselator.getInstance();


        if (this.showSlotBG) {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, VoxelConstants.getOptionsBackgroundTexture());
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            float f = 32.0f;
            BufferBuilder vertexBuffer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            vertexBuffer.addVertex(this.getX(), bottom, 0.0F).setUv(this.getX() / f, (bottom + (int) getScrollAmount()) / f).setColor(32, 32, 32, 255);
            vertexBuffer.addVertex(this.getRight(), bottom, 0.0F).setUv(this.getRight() / f, (bottom + (int) getScrollAmount()) / f).setColor(32, 32, 32, 255);
            vertexBuffer.addVertex(this.getRight(), this.getY(), 0.0F).setUv(this.getRight() / f, (this.getY() + (int) getScrollAmount()) / f).setColor(32, 32, 32, 255);
            vertexBuffer.addVertex(this.getX(), this.getY(), 0.0F).setUv(this.getX() / f, (this.getY() + (int) getScrollAmount()) / f).setColor(32, 32, 32, 255);

            BufferUploader.drawWithShader(vertexBuffer.buildOrThrow());
        }

        int leftEdge = this.getX() + width / 2 - getRowWidth() / 2 + 2;
        int topOfListYPos = this.getY() + 4 - (int) getScrollAmount();

        if (this.hasListHeader) renderHeader(drawContext, leftEdge, topOfListYPos);

        renderListItems(drawContext, mouseX, mouseY, delta);
        OpenGL.glDisable(OpenGL.GL11_GL_DEPTH_TEST);

        byte topBottomFadeHeight = 4;

        if (this.showTopBottomBG) {
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, VoxelConstants.getOptionsBackgroundTexture());
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(OpenGL.GL11_GL_ALWAYS);

            BufferBuilder vertexBuffer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            vertexBuffer.addVertex(this.getX(), this.getY(), -100.0F).setUv(0.0F, this.getY() / 32.0F).setColor(64, 64, 64, 255);
            vertexBuffer.addVertex(this.getX() + width, this.getY(), -100.0F).setUv(width / 32.0F, this.getY() / 32.0F).setColor(64, 64, 64, 255);
            vertexBuffer.addVertex(this.getX() + width, 0.0F, -100.0F).setUv(width / 32.0F, 0.0F).setColor(64, 64, 64, 255);
            vertexBuffer.addVertex(this.getX(), 0.0F, -100.0F).setUv(0.0F, 0.0F).setColor(64, 64, 64, 255);
            vertexBuffer.addVertex(this.getX(), fullheight, -100.0F).setUv(0.0F, fullheight / 32.0F).setColor(64, 64, 64, 255);
            vertexBuffer.addVertex(this.getX() + width, fullheight, -100.0F).setUv(width / 32.0F, fullheight / 32.0F).setColor(64, 64, 64, 255);
            vertexBuffer.addVertex(this.getX() + width, bottom, -100.0F).setUv(width / 32.0F, bottom / 32.0F).setColor(64, 64, 64, 255);
            vertexBuffer.addVertex(this.getX(), bottom, -100.0F).setUv(0.0F, bottom / 32.0F).setColor(64, 64, 64, 255);

            BufferUploader.drawWithShader(vertexBuffer.buildOrThrow());

            RenderSystem.depthFunc(OpenGL.GL11_GL_LEQUAL);
            RenderSystem.disableDepthTest();
            OpenGL.glEnable(OpenGL.GL11_GL_BLEND);
            RenderSystem.blendFuncSeparate(OpenGL.GL11_GL_SRC_ALPHA, OpenGL.GL11_GL_ONE_MINUS_SRC_ALPHA, 0, 1);
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, VoxelConstants.getOptionsBackgroundTexture());

            vertexBuffer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            vertexBuffer.addVertex(this.getX(), this.getY() + topBottomFadeHeight, 0.0F).setUv(0.0F, 1.0F).setColor(0, 0, 0, 0);
            vertexBuffer.addVertex(this.getRight(), this.getY() + topBottomFadeHeight, 0.0F).setUv(1.0F, 1.0F).setColor(0, 0, 0, 0);
            vertexBuffer.addVertex(this.getRight(), this.getY(), 0.0F).setUv(1.0F, 0.0F).setColor(0, 0, 0, 255);
            vertexBuffer.addVertex(this.getX(), this.getY(), 0.0F).setUv(0.0F, 0.0F).setColor(0, 0, 0, 255);
            vertexBuffer.addVertex(this.getX(), bottom, 0.0F).setUv(0.0F, 1.0F).setColor(0, 0, 0, 255);
            vertexBuffer.addVertex(this.getRight(), bottom, 0.0F).setUv(1.0F, 1.0F).setColor(0, 0, 0, 255);
            vertexBuffer.addVertex(this.getRight(), bottom - topBottomFadeHeight, 0.0F).setUv(1.0F, 0.0F).setColor(0, 0, 0, 0);
            vertexBuffer.addVertex(this.getX(), bottom - topBottomFadeHeight, 0.0F).setUv(0.0F, 0.0F).setColor(0, 0, 0, 0);

            BufferUploader.drawWithShader(vertexBuffer.buildOrThrow());
        }

        int maxScroll = getMaxScroll();

        if (maxScroll > 0) {
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            int k1 = Mth.clamp((this.getBottom() - this.getY()) * (this.getBottom() - this.getY()) / getMaxPosition(), 32, this.getBottom() - this.getY() - 8);
            int l1 = (int) getScrollAmount() * (this.getBottom() - this.getY() - k1) / maxScroll + this.getY();

            if (l1 < this.getY()) l1 = this.getY();

            BufferBuilder vertexBuffer = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
            vertexBuffer.addVertex(scrollBarLeft, this.getBottom(), 0.0F).setColor(0, 0, 0, 255);
            vertexBuffer.addVertex(scrollBarRight, this.getBottom(), 0.0F).setColor(0, 0, 0, 255);
            vertexBuffer.addVertex(scrollBarRight, this.getY(), 0.0F).setColor(0, 0, 0, 255);
            vertexBuffer.addVertex(scrollBarLeft, this.getY(), 0.0F).setColor(0, 0, 0, 255);
            vertexBuffer.addVertex(scrollBarLeft, l1 + k1, 0.0F).setColor(128, 128, 128, 255);
            vertexBuffer.addVertex(scrollBarRight, l1 + k1, 0.0F).setColor(128, 128, 128, 255);
            vertexBuffer.addVertex(scrollBarRight, l1, 0.0F).setColor(128, 128, 128, 255);
            vertexBuffer.addVertex(scrollBarLeft, l1, 0.0F).setColor(128, 128, 128, 255);
            vertexBuffer.addVertex(scrollBarLeft, l1 + k1 - 1, 0.0F).setColor(192, 192, 192, 255);
            vertexBuffer.addVertex(scrollBarRight - 1, l1 + k1 - 1, 0.0F).setColor(192, 192, 192, 255);
            vertexBuffer.addVertex(scrollBarRight - 1, l1, 0.0F).setColor(192, 192, 192, 255);
            vertexBuffer.addVertex(scrollBarLeft, l1, 0.0F).setColor(192, 192, 192, 255);

            BufferUploader.drawWithShader(vertexBuffer.buildOrThrow());
        }

        renderDecorations(drawContext, mouseX, mouseY);

        OpenGL.glDisable(OpenGL.GL11_GL_BLEND);
    }

    public int getRowWidth() {
        return slotWidth;
    }

    public void setSlotWidth(int slotWidth) {
        this.slotWidth = slotWidth;
    }

    protected int getScrollbarPositionX() {
        return slotWidth >= 220 ? width / 2 + 124 : this.getRight() - 6;
    }

    public void setLeftPos(int left) {
        this.setX(left);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.doubleclick = System.currentTimeMillis() - this.lastClicked < 250L;
        this.lastClicked = System.currentTimeMillis();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void updateWidgetNarration(NarrationElementOutput builder) {
    }
}