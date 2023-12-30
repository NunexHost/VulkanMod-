package net.vulkanmod.render.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.vulkanmod.vulkan.Renderer;
import net.vulkanmod.vulkan.shader.GraphicsPipeline;
import net.vulkanmod.vulkan.texture.VulkanImage;
import org.joml.Matrix4f;

public class DrawUtil {

    private static final Tesselator TESSELATOR = RenderSystem.renderThreadTesselator();
    private static final BufferBuilder BUILDER = TESSELATOR.getBuilder();

    // Optimized blitQuad method for a full-screen quad
    public static void blitQuad() {
        blitQuad(0.0, 1.0, 1.0, 0.0);
    }

    // Optimized blitQuad method with variable coordinates
    public static void blitQuad(double x0, double y0, double x1, double y1) {
        BUILDER.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        BUILDER.vertex(x0, y0, 0.0D).uv(0.0F, 1.0F).endVertex();
        BUILDER.vertex(x1, y0, 0.0D).uv(1.0F, 1.0F).endVertex();
        BUILDER.vertex(x1, y1, 0.0D).uv(1.0F, 0.0F).endVertex();
        BUILDER.vertex(x0, y1, 0.0D).uv(0.0F, 0.0F).endVertex();
        Renderer.getDrawer().draw(BUILDER.end());
    }

    // Combined drawTexQuad and blitQuad methods for efficiency
    public static void drawTexQuad(BufferBuilder builder, double x0, double y0, double x1, double y1, double z,
                                   float u0, float v0, float u1, float v1) {
        if (builder == null) {
            builder = BUILDER; // Use default builder if not provided
        }
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        builder.vertex(x0, y0, z).uv(u0, v0).endVertex();
        builder.vertex(x1, y0, z).uv(u1, v0).endVertex();
        builder.vertex(x1, y1, z).uv(u1, v1).endVertex();
        builder.vertex(x0, y1, z).uv(u0, v1).endVertex();
        Renderer.getDrawer().draw(builder.end());
    }

    // Optimized drawFramebuffer method
    public static void drawFramebuffer(GraphicsPipeline pipeline, VulkanImage attachment) {
        Renderer.getInstance().bindGraphicsPipeline(pipeline);
        VTextureSelector.bindTexture(attachment);

        Matrix4f matrix4f = new Matrix4f().setOrtho(0.0F, 1.0F, 0.0F, 1.0F, 0.0F, 1.0F, true);
        RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.DISTANCE_TO_ORIGIN);

        Renderer.getInstance().uploadAndBindUBOs(pipeline);
        blitQuad(); // Use optimized blitQuad for full-screen quad
    }

    // Texture coordinate setup

    public static void setupTextureCoordinates(BufferBuilder builder, double x0, double y0, double x1, double y1,
                                                float u0, float v0, float u1, float v1) {
        builder.vertex(x0, y0, 0.0D).uv(u0, v0);
        builder.vertex(x1, y0, 0.0D).uv(u1, v0);
        builder.vertex(x1, y1, 0.0D).uv(u1, v1);
        builder.vertex(x0, y1, 0.0D).uv(u0, v1);
    }
}
