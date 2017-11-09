package exnihilocreatio.client.renderers;

import exnihilocreatio.texturing.Color;
import exnihilocreatio.texturing.SpriteColor;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class ModelVertex {
    final EnumFacing face;
    final double x, yMultiplier, z;
    final double u, v;
    final double uMultiplier, vMultiplier;
    public ModelVertex(
            final EnumFacing side,
            final double x,
            final double y,
            final double z,
            final double u1,
            final double v1,
            final double u2,
            final double v2 )
    {
        face = side;
        this.x = x;
        yMultiplier = y;
        this.z = z;

        final double texMultiplier = 16;

        u = u1 * texMultiplier;
        v = v1 * texMultiplier;
        uMultiplier = u2 * texMultiplier;
        vMultiplier = v2 * texMultiplier;
    }
}
