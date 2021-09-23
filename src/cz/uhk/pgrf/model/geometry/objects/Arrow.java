package cz.uhk.pgrf.model.geometry.objects;

import cz.uhk.pgrf.model.geometry.Solid;
import cz.uhk.pgrf.transforms.Point3D;

import java.util.ArrayList;
import java.util.Arrays;

public class Arrow extends Solid {

    public Arrow() {
        transferable = false;

        Integer[] ints = new Integer[]{0, 1, 2, 3, 4, 5};
        indices = new ArrayList<>(Arrays.asList(ints));

        vertices = new ArrayList<>();
        vertices.add(new Point3D(10, 0, 0));
        vertices.add(new Point3D(-10, 0, 0));
        vertices.add(new Point3D(0, -10, 0));
        vertices.add(new Point3D(0, 10, 0));
        vertices.add(new Point3D(0, 0, 10));
        vertices.add(new Point3D(0, 0, -10));

        colors = new ArrayList<>();
        colors.add(0xff0000); // červená
        colors.add(0x00ff00); // zelená
        colors.add(0x0000ff); // modrá
    }
}
