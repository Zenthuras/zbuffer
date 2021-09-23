package cz.uhk.pgrf.model.geometry;

import cz.uhk.pgrf.transforms.Mat4;
import cz.uhk.pgrf.transforms.Mat4Identity;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    protected List<Solid> solids = new ArrayList<>();
    protected Mat4 model = new Mat4Identity();

    public Scene(Solid solid) {
        this.solids.add(solid);
    }

    public Mat4 getModel() {
        return model;
    }

    public void setModel(Mat4 model) {
        this.model = model;
    }

    public Solid get(int i) {
        if (i >= 0 && i < solids.size()) {
            return solids.get(i);
        }
        return null;
    }

    public int getCount() {
        return solids.size();
    }

    public void add(Solid solid) {
        this.solids.add(solid);
    }

    public List<Solid> getSolids() {
        return solids;
    }

    public void setSolids(List<Solid> solids) {
        this.solids = solids;
    }
}
