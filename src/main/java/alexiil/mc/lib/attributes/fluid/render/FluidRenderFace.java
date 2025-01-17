package alexiil.mc.lib.attributes.fluid.render;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.util.math.Direction;

public final class FluidRenderFace {
    public final double x0, y0, z0, u0, v0;
    public final double x1, y1, z1, u1, v1;
    public final double x2, y2, z2, u2, v2;
    public final double x3, y3, z3, u3, v3;

    public FluidRenderFace(//
        double _x0, double _y0, double _z0, double _u0, double _v0, //
        double _x1, double _y1, double _z1, double _u1, double _v1, //
        double _x2, double _y2, double _z2, double _u2, double _v2, //
        double _x3, double _y3, double _z3, double _u3, double _v3 //
    ) {
        x0 = _x0;
        y0 = _y0;
        z0 = _z0;
        u0 = _u0;
        v0 = _v0;

        x1 = _x1;
        y1 = _y1;
        z1 = _z1;
        u1 = _u1;
        v1 = _v1;

        x2 = _x2;
        y2 = _y2;
        z2 = _z2;
        u2 = _u2;
        v2 = _v2;

        x3 = _x3;
        y3 = _y3;
        z3 = _z3;
        u3 = _u3;
        v3 = _v3;
    }

    public static void appendCuboid(double x0, double y0, double z0, double x1, double y1, double z1,
        double textureScale, EnumSet<Direction> faces, List<FluidRenderFace> to) {
        for (Direction face : faces) {
            to.add(createFlatFace(x0, y0, z0, x1, y1, z1, textureScale, face));
        }
    }

    public static FluidRenderFace createFlatFaceX(double x0, double y0, double z0, double x1, double y1, double z1,
        double textureScale, boolean positive) {
        final double s = textureScale;
        if (positive) {
            return new FluidRenderFace(//
                x1, y0, z0, z0 * s, y0 * s, //
                x1, y1, z0, z1 * s, y0 * s, //
                x1, y1, z1, z1 * s, y1 * s, //
                x1, y0, z1, z0 * s, y1 * s//
            );
        } else {
            return new FluidRenderFace(//
                x0, y0, z0, z0 * s, y0 * s, //
                x0, y0, z1, z0 * s, y1 * s, //
                x0, y1, z1, z1 * s, y1 * s, //
                x0, y1, z0, z1 * s, y0 * s //
            );
        }
    }

    public static FluidRenderFace createFlatFaceY(double x0, double y0, double z0, double x1, double y1, double z1,
        double textureScale, boolean positive) {
        final double s = textureScale;
        if (positive) {
            return new FluidRenderFace(//
                x0, y1, z0, x0 * s, z0 * s, //
                x0, y1, z1, x0 * s, z1 * s, //
                x1, y1, z1, x1 * s, z1 * s, //
                x1, y1, z0, x1 * s, z0 * s//
            );
        } else {
            return new FluidRenderFace(//
                x0, y0, z0, x0 * s, z0 * s, //
                x1, y0, z0, x1 * s, z0 * s, //
                x1, y0, z1, x1 * s, z1 * s, //
                x0, y0, z1, x0 * s, z1 * s//
            );
        }
    }

    public static FluidRenderFace createFlatFaceZ(double x0, double y0, double z0, double x1, double y1, double z1,
        double textureScale, boolean positive) {
        final double s = textureScale;
        if (positive) {
            return new FluidRenderFace(//
                x0, y0, z1, x0 * s, y0 * s, //
                x1, y0, z1, x1 * s, y0 * s, //
                x1, y1, z1, x1 * s, y1 * s, //
                x0, y1, z1, x0 * s, y1 * s//
            );
        } else {
            return new FluidRenderFace(//
                x0, y0, z0, x0 * s, y0 * s, //
                x0, y1, z0, x0 * s, y1 * s, //
                x1, y1, z0, x1 * s, y1 * s, //
                x1, y0, z0, x1 * s, y0 * s //
            );
        }
    }

    public static FluidRenderFace createFlatFace(double x0, double y0, double z0, double x1, double y1, double z1,
        double textureScale, Direction face) {
        switch (face) {
            case DOWN:
                return createFlatFaceY(x0, y0, z0, x1, y1, z1, textureScale, false);
            case UP:
                return createFlatFaceY(x0, y0, z0, x1, y1, z1, textureScale, true);
            case NORTH:
                return createFlatFaceZ(x0, y0, z0, x1, y1, z1, textureScale, false);
            case SOUTH:
                return createFlatFaceZ(x0, y0, z0, x1, y1, z1, textureScale, true);
            case WEST:
                return createFlatFaceX(x0, y0, z0, x1, y1, z1, textureScale, false);
            case EAST:
                return createFlatFaceX(x0, y0, z0, x1, y1, z1, textureScale, true);
            default: {
                throw new IllegalStateException("Unknown Direction " + face);
            }
        }
    }

    @Override
    public String toString() {
        return "FluidRenderFace {"//
            + "\n  " + x0 + " " + y0 + " " + z0 + " " + u0 + " " + v0//
            + "\n  " + x1 + " " + y1 + " " + z1 + " " + u1 + " " + v1//
            + "\n  " + x2 + " " + y2 + " " + z2 + " " + u2 + " " + v2//
            + "\n  " + x3 + " " + y3 + " " + z3 + " " + u3 + " " + v3//
            + "\n}"//
        ;
    }
}
