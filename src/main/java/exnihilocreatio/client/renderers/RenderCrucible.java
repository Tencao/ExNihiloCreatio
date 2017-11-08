package exnihilocreatio.client.renderers;

import exnihilocreatio.blocks.BlockCrucibleBase;
import exnihilocreatio.blocks.BlockInfestedLeaves;
import exnihilocreatio.texturing.Color;
import exnihilocreatio.texturing.SpriteColor;
import exnihilocreatio.tiles.TileCrucibleBase;
import exnihilocreatio.tiles.TileInfestedLeaves;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
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

import static java.lang.Math.round;

public class RenderCrucible extends FastTESR<TileCrucibleBase> {
    private static ModelVertex[] model = new ModelVertex[4];
    static {
        model[0] = new ModelVertex( EnumFacing.UP, 0.125, 0.6875, 0.125, 0, 0, 0, 0 );
        model[1] = new ModelVertex( EnumFacing.UP, 0.875, 0.6875, 0.125, 1, 0, 0, 0 );
        model[2] = new ModelVertex( EnumFacing.UP, 0.875, 0.6875, 0.875, 1, 1, 0, 0 );
        model[3] = new ModelVertex( EnumFacing.UP, 0.125, 0.6875, 0.875, 0, 1, 0, 0 );
    }
    @Override
    public void renderTileEntityFast(TileCrucibleBase te, double x, double y, double z, float partialTicks, int destroyStage, float partial, BufferBuilder buffer){
        if(te==null) return;

        // Select either the fluidstack or the itemstack in the crucible
        TextureAtlasSprite sprite;
        final float fluidFill = te.getFluidProportion();
        final float itemFill = te.getSolidProportion();


        final BlockPos pos = te.getPos();
        final Block block = getWorld().getBlockState(pos).getBlock();

        // Check because sometimes the renderer gets called while the block is breaking/decaying
        if(!(block instanceof BlockCrucibleBase)) return;

        // Light levels
        final int mixedBrightness = te.getWorld().getBlockState( pos ).getPackedLightmapCoords( te.getWorld(), te.getPos() );
        final int skyLight = mixedBrightness >> 16 & 65535;
        final int blockLight = mixedBrightness & 65535;

        if(itemFill == 0.0 && fluidFill == 0.0) return;

        IBlockState state;
        float[] fillLevels = new float[2];
        fillLevels[0] = fluidFill;
        fillLevels[1] = itemFill;
        int color;
        int rColor;
        int gColor;
        int bColor;
        int aColor;
        for(int i = 0; i < 2; i++){
            if(fillLevels[i] != 0){

                if(i == 0){
                    state = te.getTank().getFluid().getFluid().getBlock().getDefaultState(); // Fluid
                    color = te.getTank().getFluid().getFluid().getColor();
                    rColor = color >> 16 & 0xff;
                    gColor = color >> 8 & 0xff;
                    bColor = color & 0xff;
                    aColor = color >> 24 & 0xff;
                }
                else{
                    state = Block.getBlockFromItem(te.getCurrentItem().getItem()).getDefaultState(); //Item
                    if(state.getBlock() instanceof BlockLeaves){
                        color = Minecraft.getMinecraft().getBlockColors().getColor(state, te.getWorld(), te.getPos());
                        rColor = color >> 16 & 0xff;
                        gColor = color >> 8 & 0xff;
                        bColor = color & 0xff;
                        aColor = 255;
                    }
                    else {
                        rColor = 255;
                        gColor = 255;
                        bColor = 255;
                        aColor = 255;
                    }
                }
                sprite = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);

                buffer.setTranslation(x-pos.getX(), y-pos.getY(), z-pos.getZ());
                for ( final ModelVertex vert : model )
                {
                    for ( final VertexFormatElement e : buffer.getVertexFormat().getElements() )
                    {
                        switch ( e.getUsage() )
                        {
                            case COLOR:
                                buffer.color( rColor, gColor, bColor, aColor );
                                break;

                            case NORMAL:
                                buffer.normal( vert.face.getFrontOffsetX(), vert.face.getFrontOffsetY(), vert.face.getFrontOffsetZ() );
                                break;

                            case POSITION:
                                final double vertX = pos.getX() + vert.x;
                                final double vertY = pos.getY() + vert.yMultiplier * fillLevels[i] + 0.25;
                                final double vertZ = pos.getZ() + vert.z;

                                buffer.pos( vertX, vertY, vertZ );
                                break;

                            case UV:
                                if ( e.getIndex() == 1 )
                                {
                                    buffer.lightmap( skyLight, blockLight );
                                }
                                else
                                {
                                    buffer.tex( sprite.getInterpolatedU( vert.u + vert.uMultiplier ), sprite.getInterpolatedV( 16.0 - ( vert.v + vert.vMultiplier ) ) );
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

        // Don't screw everyone else up
        buffer.setTranslation(0, 0, 0);
    }

/*    @Override
    public void render(TileCrucibleBase te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        Tessellator tes = Tessellator.getInstance();
        BufferBuilder wr = tes.getBuffer();


        RenderHelper.disableStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        SpriteColor[] sprite = te.getSpriteAndColor();

        if (sprite != null && (sprite[0] != null || sprite[1] != null)) {
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            wr.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
            // Makes the fluid have the correct transparency
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            addSpriteColor(te, sprite[0], wr, false);
            addSpriteColor(te, sprite[1], wr, true);

            tes.draw();
        }

        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
        RenderHelper.enableStandardItemLighting();

    }

    public void addSpriteColor(TileCrucibleBase te, SpriteColor sprite, BufferBuilder wr, boolean isFluid) {
        if (sprite != null) {
            TextureAtlasSprite icon = sprite.getSprite();
            double minU = (double) icon.getMinU();
            double maxU = (double) icon.getMaxU();
            double minV = (double) icon.getMinV();
            double maxV = (double) icon.getMaxV();

            // determine the tint for the fluid/block
            Color color = sprite.getColor();


            //wr.begin(GL11.GL_QUADS, new VertexFormat().addElement(DefaultVertexFormats.POSITION_3F).addElement(DefaultVertexFormats.COLOR_4UB).addElement(DefaultVertexFormats.NORMAL_3B));
            // Offset by bottome of crucibleStone, which is 4 pixels above the base of the block (and make it stop on pixel below the top)
            float fillAmount;
            if (isFluid) {
                fillAmount = ((12F / 16F) * te.getFluidProportion() + (4F / 16F)) * 0.9375F;
            } else {
                fillAmount = ((12F / 16F) * te.getSolidProportion() + (4F / 16F)) * 0.9375F;
            }

            wr.pos(0.0625F, fillAmount, 0.0625F).tex(minU, minV).color(color.r, color.g, color.b, color.a).normal(0, 1, 0).endVertex();
            wr.pos(0.0625F, fillAmount, 0.9375F).tex(minU, maxV).color(color.r, color.g, color.b, color.a).normal(0, 1, 0).endVertex();
            wr.pos(0.9375F, fillAmount, 0.9375F).tex(maxU, maxV).color(color.r, color.g, color.b, color.a).normal(0, 1, 0).endVertex();
            wr.pos(0.9375F, fillAmount, 0.0625F).tex(maxU, minV).color(color.r, color.g, color.b, color.a).normal(0, 1, 0).endVertex();
        }
    }*/

}