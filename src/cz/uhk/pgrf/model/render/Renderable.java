package cz.uhk.pgrf.model.render;

import cz.uhk.pgrf.model.geometry.Scene;
import cz.uhk.pgrf.model.geometry.Solid;
import cz.uhk.pgrf.transforms.Mat4;
import cz.uhk.pgrf.transforms.Point3D;

import java.awt.image.BufferedImage;
import java.util.List;

public interface Renderable {

    void setBufferedImage(BufferedImage img);

    BufferedImage getBufferedImage();

    void setView(Mat4 mat);

    void setProj(Mat4 mat);

    void render(Scene scene);

    void render(Solid solid, Mat4 model);

    void render(List<Scene> solids);

    void drawTriangle(Point3D vertexA, Point3D vertexB, Point3D vertexC);

    void setIsFilling(boolean isFilling);


}
