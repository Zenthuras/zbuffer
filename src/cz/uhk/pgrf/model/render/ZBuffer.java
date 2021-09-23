package cz.uhk.pgrf.model.render;

public class ZBuffer {
    private double[][] zBuffer;
    private double clearValue;

    public ZBuffer(int width, int height) {
        zBuffer = new double[width][height];
        clearValue = 1;
    }

    public double getClearValue() {
        return clearValue;
    }

    public void setClearValue(double clearValue) {
        this.clearValue = clearValue;
    }

    public int getWidth() {
        return zBuffer.length;
    }

    public int getHeight() {
        return zBuffer[0].length;
    }

    public void setElement(int x, int y, Double z) {
        zBuffer[x][y] = z;
    }

    public Double getElement(int x, int y) {
        return zBuffer[x][y];
    }

    public void clear() {
        for (int i = 0; i < zBuffer.length; i++) {
            for (int y = 0; y < zBuffer[0].length; y++) {
                zBuffer[i][y] = 1.0;
            }
        }
    }
}
