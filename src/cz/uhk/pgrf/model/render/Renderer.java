package cz.uhk.pgrf.model.render;

import cz.uhk.pgrf.model.geometry.Scene;
import cz.uhk.pgrf.model.geometry.Solid;
import cz.uhk.pgrf.transforms.Mat4;
import cz.uhk.pgrf.transforms.Mat4Identity;
import cz.uhk.pgrf.transforms.Point3D;
import cz.uhk.pgrf.transforms.Vec3D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;


public class Renderer implements Renderable {

    private BufferedImage image;
    private ZBuffer zBuffer;
    private Mat4 view;
    private Mat4 projection;
    private boolean isFilling = true;
    private int color;

    @Override
    public void setBufferedImage(BufferedImage img) {
        this.image = img;
        zBuffer = new ZBuffer(img.getWidth(), img.getHeight());
        zBuffer.setClearValue(1);
        zBuffer.clear();
    }

    @Override
    public void setView(Mat4 view) {
        this.view = view;
    }

    @Override
    public void setProjection(Mat4 projection) {
        this.projection = projection;
    }

    @Override
    public void render(List<Scene> solids) {
        for (Scene scene : solids) {
            render(scene);
        }
        zBuffer.clear();
    }

    @Override
    public void render(Scene scene) {
        for (int i = 0; i < scene.getCount(); i++) {
            render(scene.get(i), scene.getModel());
        }
    }

    @Override
    public void render(Solid solid, Mat4 model) {
        int count = 0;

        Mat4 modelNew;
        if (solid.isTransferable()) {
            modelNew = solid.getModel().mul(model);
        } else {
            modelNew = new Mat4Identity();
        }

        final Mat4 finTransform = modelNew.mul(view).mul(projection);
        List<Integer> ints = solid.getIndices();

        if (!solid.isTransferable()) {
            for (int i = 0; i < ints.size(); i += 2) {
                renderAxes(solid, i, finTransform);
            }
        } else {

            for (int i = 0; i < ints.size(); i += 3) {
                color = solid.getColor(i - count * 2);
                count++;

                int indexA = solid.getIndices().get(i);
                int indexB = solid.getIndices().get(i + 1);
                int indexC = solid.getIndices().get(i + 2);

                Point3D vertexA = solid.getVertices().get(indexA);
                Point3D vertexB = solid.getVertices().get(indexB);
                Point3D vertexC = solid.getVertices().get(indexC);

                vertexA = vertexA.mul(finTransform);
                vertexB = vertexB.mul(finTransform);
                vertexC = vertexC.mul(finTransform);

                if (vertexA.getW() < vertexB.getW()) {
                    Point3D temp = vertexA;
                    vertexA = vertexB;
                    vertexB = temp;
                }
                if (vertexB.getW() < vertexC.getW()) {
                    Point3D temp = vertexB;
                    vertexB = vertexC;
                    vertexC = temp;
                }
                if (vertexA.getW() < vertexB.getW()) {
                    Point3D temp = vertexA;
                    vertexA = vertexB;
                    vertexB = temp;
                }

                if (!(vertexA.getW() < zBuffer.getClearValue())) {
                    if (vertexB.getW() < zBuffer.getClearValue()) {
                        double t = (vertexA.getW() - zBuffer.getClearValue()) / (vertexA.getW() - vertexB.getW());
                        Point3D a = vertexA.mul(1 - t).add(vertexB.mul(t));

                        t = (vertexA.getW() - zBuffer.getClearValue()) / (vertexA.getW() - vertexC.getW());
                        Point3D b = vertexA.mul(1 - t).add(vertexC.mul(t));
                        drawTriangle(vertexA, a, b);
                    } else if (vertexC.getW() < zBuffer.getClearValue()) {
                        double t = (vertexB.getW() - zBuffer.getClearValue()) / (vertexB.getW() - vertexC.getW());
                        Point3D a = vertexB.mul(1 - t).add(vertexC.mul(t));

                        t = (vertexA.getW() - zBuffer.getClearValue()) / (vertexA.getW() - vertexC.getW());
                        Point3D b = vertexA.mul(1 - t).add(vertexC.mul(t));

                        drawTriangle(vertexA, a, b);
                        drawTriangle(vertexA, vertexB, a);
                    } else {
                        drawTriangle(vertexA, vertexB, vertexC);
                    }
                }
            }
        }
    }

    @Override
    public void drawTriangle(Point3D vertexA, Point3D vertexB, Point3D vertexC) {
        Vec3D vA = vertexA.dehomog().get();
        Vec3D vB = vertexB.dehomog().get();
        Vec3D vC = vertexC.dehomog().get();

        if (vA == null || vB == null || vC == null) {
            return;
        }

        if (Math.min(Math.min(vA.getX(), vB.getX()), vC.getX()) > 1.0
                || Math.max(Math.max(vA.getX(), vB.getX()), vC.getX()) < -1.0
                || Math.min(Math.min(vA.getY(), vB.getY()), vC.getY()) > 1.0
                || Math.max(Math.max(vA.getY(), vB.getY()), vC.getY()) < -1.0
                || Math.min(Math.min(vA.getZ(), vB.getZ()), vC.getZ()) > 1.0
                || Math.max(Math.max(vA.getZ(), vB.getZ()), vC.getZ()) < 0.0) {
            return;
        }

        vA = vA.mul(new Vec3D(1.0, -1.0, 1.0)).add(new Vec3D(1.0, 1.0, 0.0))
                .mul(new Vec3D((image.getWidth() - 1) / 2, (image.getHeight() - 1) / 2, 1.0));
        vB = vB.mul(new Vec3D(1.0, -1.0, 1.0)).add(new Vec3D(1.0, 1.0, 0.0))
                .mul(new Vec3D((image.getWidth() - 1) / 2, (image.getHeight() - 1) / 2, 1.0));
        vC = vC.mul(new Vec3D(1.0, -1.0, 1.0)).add(new Vec3D(1.0, 1.0, 0.0))
                .mul(new Vec3D((image.getWidth() - 1) / 2, (image.getHeight() - 1) / 2, 1.0));

        if (isFilling) {

            if (vA.getY() > vB.getY()) {
                Vec3D temp = vA;
                vA = vB;
                vB = temp;
            }
            if (vB.getY() > vC.getY()) {
                Vec3D temp = vB;
                vB = vC;
                vC = temp;
            }
            if (vA.getY() > vB.getY()) {
                Vec3D temp = vA;
                vA = vB;
                vB = temp;
            }

            for (int y = Math.max((int) vA.getY() + 1, 0); y <= Math.min((int) vB.getY(),
                    zBuffer.getHeight() - 1); y++) {
                double t = ((double) y - vA.getY()) / (vB.getY() - vA.getY());
                double x1 = vA.getX() * (1.0 - t) + vB.getX() * t;
                double z1 = vA.getZ() * (1.0 - t) + vB.getZ() * t;

                double t2 = ((double) y - vA.getY()) / (vC.getY() - vA.getY());
                double x2 = vA.getX() * (1.0 - t2) + vC.getX() * t2;
                double z2 = vA.getZ() * (1.0 - t2) + vC.getZ() * t2;

                if (x1 > x2) {
                    double temp = x1;
                    x1 = x2;
                    x2 = temp;
                    temp = z1;
                    z1 = z2;
                    z2 = temp;
                }

                for (int x = Math.max((int) x1 + 1, 0); x <= Math.min(x2, zBuffer.getWidth() - 1); x++) {
                    double t3 = ((double) x - x1) / (x2 - x1);
                    double z3 = z1 * (1.0 - t3) + z2 * t3;
                    if (zBuffer.getElement(x, y) >= z3 && z3 >= 0.0) {
                        zBuffer.setElement(x, y, z3);
                        image.setRGB(x, y, color);
                    }
                }
            }

            for (int y = Math.max((int) vB.getY() + 1, 0); y <= Math.min(vC.getY(), zBuffer.getHeight() - 1); y++) {
                double t = ((double) y - vB.getY()) / (vC.getY() - vB.getY());
                double x1 = vB.getX() * (1.0 - t) + vC.getX() * t;
                double z1 = vB.getZ() * (1.0 - t) + vC.getZ() * t;

                double t2 = ((double) y - vA.getY()) / (vC.getY() - vA.getY());
                double x2 = vA.getX() * (1.0 - t2) + vC.getX() * t2;
                double z2 = vA.getZ() * (1.0 - t2) + vC.getZ() * t2;

                if (x1 > x2) {
                    double temp = x1;
                    x1 = x2;
                    x2 = temp;
                    temp = z1;
                    z1 = z2;
                    z2 = temp;
                }

                for (int x = Math.max((int) x1 + 1, 0); x <= Math.min(x2, zBuffer.getWidth() - 1); x++) {
                    double t3 = ((double) x - x1) / (x2 - x1);
                    double z3 = z1 * (1.0 - t3) + z2 * t3;
                    if (zBuffer.getElement(x, y) > z3 && z3 >= 0.0) {
                        zBuffer.setElement(x, y, z3);
                        image.setRGB(x, y, color);
                    }
                }
            }
        } else {
            Graphics g = image.getGraphics();
            g.setColor(new Color(color));
            g.drawLine((int) vA.getX(), (int) vA.getY(), (int) vB.getX(), (int) vB.getY());
            g.drawLine((int) vA.getX(), (int) vA.getY(), (int) vC.getX(), (int) vC.getY());
            g.drawLine((int) vC.getX(), (int) vC.getY(), (int) vB.getX(), (int) vB.getY());
        }
    }

    @Override
    public BufferedImage getBufferedImage() {
        return image;
    }

    public void setIsFilling(boolean isFilling) {
        this.isFilling = isFilling;
    }

    private void renderAxes(Solid solid, int i, Mat4 finTransform) {
        int indexA = solid.getIndices().get(i);
        int indexB = solid.getIndices().get(i + 1);

        Point3D vertexA = solid.getVertices().get(indexA);
        Point3D vertexB = solid.getVertices().get(indexB);

        vertexA = vertexA.mul(finTransform);
        vertexB = vertexB.mul(finTransform);

        Vec3D vecA = null;
        Vec3D vecB = null;

        if (vertexA.getW() > zBuffer.getClearValue()) {
            Point3D temp = vertexA;
            vertexA = vertexB;
            vertexB = temp;
        }

        if (vertexB.getW() < zBuffer.getClearValue()) {
            return;
        }

        if (vertexA.getW() < zBuffer.getClearValue()) {
            double t = (zBuffer.getClearValue() - vertexA.getW()) / (vertexB.getW() - vertexA.getW());
            vertexA = vertexA.mul(1 - t).add(vertexB.mul(t));
        }

        if (vertexA.dehomog().isPresent()) {
            vecA = vertexA.dehomog().get();
        }

        if (vertexA.dehomog().isPresent()) {
            vecB = vertexB.dehomog().get();
        }

        if (vecA == null || vecB == null) {
            return;
        }
        if (Math.min(vecA.getX(), vecB.getX()) > 1.0 || Math.max(vecA.getX(), vecB.getX()) < -1.0
                || Math.min(vecA.getY(), vecB.getY()) > 1.0 || Math.max(vecA.getY(), vecB.getY()) < -1.0
                || Math.min(vecA.getZ(), vecB.getZ()) > 1.0 || Math.max(vecA.getZ(), vecB.getZ()) < -1.0) {
            return;
        }

        vecA = vecA.mul(new Vec3D(1.0, -1.0, 1.0)).add(new Vec3D(1.0, 1.0, 0.0))
                .mul(new Vec3D((image.getWidth() - 1) / 2, (image.getHeight() - 1) / 2, 1.0));
        vecB = vecB.mul(new Vec3D(1.0, -1.0, 1.0)).add(new Vec3D(1.0, 1.0, 0.0))
                .mul(new Vec3D((image.getWidth() - 1) / 2, (image.getHeight() - 1) / 2, 1.0));
        int x1 = (int) vecA.getX();
        int y1 = (int) vecA.getY();
        double z1 = vecA.getZ();

        int x2 = (int) vecB.getX();
        int y2 = (int) vecB.getY();
        double z2 = vecB.getZ();

        double dx = x2 - x1;
        double dy = y2 - y1;

        if (Math.abs(y2 - y1) <= Math.abs(x2 - x1)) {

            if (x2 < x1) {
                int temp = x2;
                x2 = x1;
                x1 = temp;

                temp = y2;
                y2 = y1;
                y1 = temp;

                double tempZ = z2;
                z2 = z1;
                z1 = tempZ;
            }

            double k = dy / dx;
            int yNew;
            double y = y1;

            for (int x = x1; x <= x2; x++) {
                yNew = (int) Math.round(y);

                if ((x > 0 && x < image.getWidth() - 1) && (yNew > 0 && yNew < image.getHeight() - 1)) {
                    double t3 = ((double) x - x1) / (x2 - x1);
                    double z3 = z1 * (1.0 - t3) + z2 * t3;
                    if (zBuffer.getElement(x, yNew) > z3 && z3 >= 0.0) {
                        zBuffer.setElement(x, yNew, z3);
                        image.setRGB(x, yNew, solid.getColor(i / 2));
                    }
                }
                y += k;
            }
        } else {

            if (y2 < y1) {
                int temp = x2;
                x2 = x1;
                x1 = temp;

                temp = y2;
                y2 = y1;
                y1 = temp;
            }

            double k = dx / dy;
            int xNew;
            double x = x1;

            for (int y = y1; y <= y2; y++) {
                xNew = (int) Math.round(x);

                if ((xNew > 0 && xNew < image.getWidth() - 1) && (y > 0 && y < image.getHeight() - 1)) {
                    double t3 = ((double) y - y1) / (y2 - y1);
                    double z3 = z2 * (1.0 - t3) + z1 * t3;
                    if (zBuffer.getElement(xNew, y) > z3 && z3 >= 0.0) {
                        zBuffer.setElement(xNew, y, z3);
                        image.setRGB(xNew, y, solid.getColor(i / 2));
                    }
                }
                x += k;
            }
        }
    }
}