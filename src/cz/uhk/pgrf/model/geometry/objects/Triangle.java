package cz.uhk.pgrf.model.geometry.objects;

import cz.uhk.pgrf.model.geometry.Solid;
import cz.uhk.pgrf.transforms.Point3D;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Triangle extends Solid {

    public Triangle() {
        Integer[] ints = new Integer[]{0, 1, 2};
        indices = new ArrayList<>(Arrays.asList(ints));

        vertices = new ArrayList<>();
        vertices.add(new Point3D(4, 0, 0));
        vertices.add(new Point3D(0, 2, 0));
        vertices.add(new Point3D(0, 0, 1));

        colors = new ArrayList<>();
        for (int i = 0; i < indices.size(); i++) {
            int r = (int) (Math.random() * (255));
            int g = (int) (Math.random() * (255));
            int b = (int) (Math.random() * (255));
            colors.add(new Color(r, g, b).getRGB());
        }
    }
}
