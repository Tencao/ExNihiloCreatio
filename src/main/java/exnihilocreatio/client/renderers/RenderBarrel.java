package exnihilocreatio.client.renderers;

import exnihilocreatio.barrel.modes.block.BarrelModeBlock;
import exnihilocreatio.barrel.modes.compost.BarrelModeCompost;
import exnihilocreatio.barrel.modes.fluid.BarrelModeFluid;
import exnihilocreatio.barrel.modes.transform.BarrelModeFluidTransform;
import exnihilocreatio.blocks.BlockBarrel;
import exnihilocreatio.blocks.BlockCrucibleBase;
import exnihilocreatio.texturing.Color;
import exnihilocreatio.texturing.SpriteColor;
import exnihilocreatio.tiles.TileBarrel;
import exnihilocreatio.tiles.TileCrucibleBase;
import exnihilocreatio.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.animation.FastTESR;
import org.lwjgl.opengl.GL11;

public class RenderBarrel extends FastTESR<TileBarrel> {
    private static ModelVertex[] model = new ModelVertex[4];
    static {
        model[0] = new ModelVertex( EnumFacing.UP, 0.125, 0.875, 0.125, 0, 0, 0, 0 );
        model[1] = new ModelVertex( EnumFacing.UP, 0.875, 0.875, 0.125, 1, 0, 0, 0 );
        model[2] = new ModelVertex( EnumFacing.UP, 0.875, 0.875, 0.875, 1, 1, 0, 0 );
        model[3] = new ModelVertex( EnumFacing.UP, 0.125, 0.875, 0.875, 0, 1, 0, 0 );
    }

    @Override
    public void renderTileEntityFast(TileBarrel te, double x, double y, double z, float partialTicks, int destroyStage, float partial, BufferBuilder buffer){
        if(te.getMode() == null) return;

        // Fill Level
        float fill = te.getMode().getFilledLevelForRender(te);

        if(fill==0) return;

        final SpriteColor sprite = te.getMode().getSpriteColor(te);
        buffer.setTranslation(x, y, z);
        addSpriteColor(te, sprite, buffer, te.getMode().getFilledLevelForRender(te));
        buffer.setTranslation(0, 0, 0);

    }
    private void addSpriteColor(TileBarrel te, SpriteColor sprite, BufferBuilder buffer, float fill) {
        if (sprite == null) return;

        final BlockPos pos = te.getPos();
        // Light levels
        final int mixedBrightness = te.getWorld().getBlockState(pos).getPackedLightmapCoords(te.getWorld(), te.getPos());
        final int skyLight = mixedBrightness >> 16 & 0xFFFF;
        final int blockLight = mixedBrightness & 0xFFFF;
        // Texturing
        TextureAtlasSprite icon = sprite.getSprite();
        Color color = sprite.getColor();

        // Draw
        for (final ModelVertex vert : model) {
            for (final VertexFormatElement e : buffer.getVertexFormat().getElements()) {
                switch (e.getUsage()) {
                    case COLOR:
                        buffer.color(color.r, color.g, color.b, color.a);
                        break;

                    case NORMAL:
                        buffer.normal(vert.face.getFrontOffsetX(), vert.face.getFrontOffsetY(), vert.face.getFrontOffsetZ());
                        break;

                    case POSITION:
                        final double vertX = vert.x;
                        final double vertY = fill;
                        final double vertZ = vert.z;

                        buffer.pos(vertX, vertY, vertZ);
                        break;

                    case UV:
                        if (e.getIndex() == 1) {
                            buffer.lightmap(skyLight, blockLight);
                        } else {
                            buffer.tex(icon.getInterpolatedU(vert.u + vert.uMultiplier), icon.getInterpolatedV(16.0 - (vert.v + vert.vMultiplier)));
                        }
                        break;

                    default:
                        break;
                }
            }
            buffer.endVertex();
        }
    }
}
