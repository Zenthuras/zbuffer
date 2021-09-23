package cz.uhk.pgrf.model.geometry.objects;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import cz.uhk.pgrf.model.geometry.Solid;
import cz.uhk.pgrf.transforms.Point3D;

public class Tetrahedron extends Solid {

    public Tetrahedron() {
        Integer[] ints = new Integer[]{0, 1, 2, 2, 0, 3, 2, 3, 1, 3, 0, 1};
        indices = new ArrayList<>(Arrays.asList(ints));

        vertices = new ArrayList<>();
        vertices.add(new Point3D(-2, -2, -2));
        vertices.add(new Point3D(2, -2, 2));
        vertices.add(new Point3D(-2, 2, 2));
        vertices.add(new Point3D(2, 2, -2));

        colors = new ArrayList<>();
        for (int i = 0; i < indices.size(); i++) {
            int r = (int) (Math.random() * (255));
            int g = (int) (Math.random() * (255));
            int b = (int) (Math.random() * (255));
            colors.add(new Color(r, g, b).getRGB());
        }
    }
}
