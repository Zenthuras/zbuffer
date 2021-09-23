package cz.uhk.pgrf.model.geometry.objects;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

import cz.uhk.pgrf.model.geometry.Solid;
import cz.uhk.pgrf.transforms.Point3D;

public class Cube extends Solid {

    public Cube() {

        Integer[] ints = new Integer[]{0, 1, 2, 0, 2, 3, 0, 4, 5, 0, 5, 1, 3, 2, 6, 3, 6, 7, 4, 7, 6, 4, 6, 5, 1, 2,
                6, 1, 6, 5, 0, 3, 7, 0, 7, 4};
        indices = new ArrayList<>(Arrays.asList(ints));

        vertices = new ArrayList<>();
        vertices.add(new Point3D(-1, -1, 1));
        vertices.add(new Point3D(-1, 1, 1));
        vertices.add(new Point3D(1, 1, 1));
        vertices.add(new Point3D(1, -1, 1));

        vertices.add(new Point3D(-1, -1, -1));
        vertices.add(new Point3D(-1, 1, -1));
        vertices.add(new Point3D(1, 1, -1));
        vertices.add(new Point3D(1, -1, -1));

        colors = new ArrayList<>();
        for (int i = 0; i < indices.size(); i++) {
            int r = (int) (Math.random() * (255));
            int g = (int) (Math.random() * (255));
            int b = (int) (Math.random() * (255));
            colors.add(new Color(r, g, b).getRGB());
        }
    }
}
