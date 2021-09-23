package cz.uhk.pgrf.model.geometry;

import cz.uhk.pgrf.transforms.Mat4;
import cz.uhk.pgrf.transforms.Mat4Identity;
import cz.uhk.pgrf.transforms.Point3D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Solid {

    protected int defaultColor = Color.BLACK.getRGB();
    protected boolean transferable = true;

    protected List<Point3D> vertices;
    protected List<Integer> indices;
    protected List<Integer> colors;
    protected Mat4 model = new Mat4Identity();

    public List<Point3D> getVertices() {
        return new ArrayList<>(vertices);
    }

    public List<Integer> getIndices() {
        return new ArrayList<>(indices);
    }

    public int getColor(int i) {
        if (colors != null) {
            if (colors.size() > i && i >= 0) {
                return colors.get(i);
            }
        }
        return defaultColor;
    }

    public Mat4 getModel() {
        return model;
    }

    public void setModel(Mat4 model) {
        this.model = model;
    }

    public boolean isTransferable() {
        return transferable;
    }
}
