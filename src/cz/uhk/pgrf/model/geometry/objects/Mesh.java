package cz.uhk.pgrf.model.geometry.objects;

import java.awt.Color;
import java.util.ArrayList;

import cz.uhk.pgrf.model.geometry.Solid;
import cz.uhk.pgrf.transforms.Bicubic;
import cz.uhk.pgrf.transforms.Cubic;
import cz.uhk.pgrf.transforms.Mat4;
import cz.uhk.pgrf.transforms.Point3D;


public class Mesh extends Solid {

    public static final int points = 30;
    public static final Point3D p11 = new Point3D(-2, 2, 5);
    public static final Point3D p12 = new Point3D(-1, 3, 5);
    public static final Point3D p13 = new Point3D(1, 3, 5);
    public static final Point3D p14 = new Point3D(2, 2, 5);

    public static final Point3D p21 = new Point3D(-3, 1, 5);
    public static final Point3D p22 = new Point3D(-10, 10, -5);
    public static final Point3D p23 = new Point3D(10, 10, -5);
    public static final Point3D p24 = new Point3D(3, 1, 5);

    public static final Point3D p31 = new Point3D(-3, -1, 5);
    public static final Point3D p32 = new Point3D(-10, -10, -5);
    public static final Point3D p33 = new Point3D(10, -10, -5);
    public static final Point3D p34 = new Point3D(3, -1, 5);

    public static final Point3D p41 = new Point3D(-2, -2, 5);
    public static final Point3D p42 = new Point3D(-1, -3, 5);
    public static final Point3D p43 = new Point3D(1, -3, 5);
    public static final Point3D p44 = new Point3D(2, -2, 5);

    public Mesh() {
        Mat4 curve = Cubic.COONS;
        Bicubic mesh = new Bicubic(curve, p11, p12, p13, p14, p21, p22, p23, p24, p31, p32, p33, p34, p41, p42, p43, p44);
        vertices = new ArrayList<>();
        indices = new ArrayList<>();
        colors = new ArrayList<>();

        for (int i = 0; i <= points; i++) {
            for (int j = 0; j <= points; j++) {
                vertices.add(mesh.compute((double) i / points, (double) j / points));
            }
        }

        for (int i = 0; i < points; i++) {
            for (int j = 0; j < points; j++) {
                indices.add(i * (points + 1) + j);
                indices.add(i * (points + 1) + j + 1);
                indices.add((i + 1) * (points + 1) + j);

                indices.add(i * (points + 1) + j + 1);
                indices.add((i + 1) * (points + 1) + j);
                indices.add((i + 1) * (points + 1) + j + 1);
            }
        }

        for (int i = 0; i < indices.size(); i++) {
            int r = (int) (Math.random() * (255));
            int g = (int) (Math.random() * (255));
            int b = (int) (Math.random() * (255));
            colors.add(new Color(r, g, b).getRGB());
        }
    }
}
