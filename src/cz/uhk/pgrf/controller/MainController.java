package cz.uhk.pgrf.controller;

import cz.uhk.pgrf.model.geometry.Scene;
import cz.uhk.pgrf.model.geometry.objects.*;
import cz.uhk.pgrf.model.render.Renderer;
import cz.uhk.pgrf.transforms.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


public class MainController {

    private static int CLEAR_COLOR = 0xffffff;
    private JPanel panel;
    private BufferedImage image;
    private List<Scene> solids;
    private Renderer renderer;
    private Camera camera;
    private Mat4 view;
    private Mat4 projection;
    private Mat4 model;
    private Scene cube;
    private Scene tetrahedron;
    private Scene triangle;
    private Scene mesh;
    private final double step = 0.5;
    private JRadioButton jPers;
    private JRadioButton jFill;
    private JRadioButton jWire;
    private JCheckBox jCube;
    private JCheckBox jTetra;
    private JCheckBox jTriangle;
    private JCheckBox jMesh;
    private double scale = 1;
    private int endX;
    private int endY;
    private int moveX;
    private int moveY;

    public MainController(int width, int height) {
        JFrame frame = new JFrame();
        frame.setTitle("PGRF2 - Liška");
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        panel = new JPanel();
        panel.setPreferredSize(new Dimension(width, height));

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

        JToolBar tb = new JToolBar();
        tb.setFocusable(false);
        frame.add(tb, BorderLayout.NORTH);
        initButtons(tb);

        solids = new ArrayList<>();
        renderer = new Renderer();
        renderer.setBufferedImage(image);

        model = new Mat4Identity();
        projection = new Mat4PerspRH(Math.PI / 4, 1, 0.1, 200);
        camera = new Camera(new Vec3D(-15, 0, 0), 0, 0, 1, true);
        view = camera.getViewMatrix();

        solids.add(new Scene(new Arrow()));
        cube = new Scene(new Cube());
        tetrahedron = new Scene(new Tetrahedron());
        triangle = new Scene(new Triangle());
        mesh = new Scene(new Mesh());
        draw();

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                endX = e.getX();
                endY = e.getY();
            }
        };

        panel.addMouseListener(mouse);

        MouseAdapter mouseMove = new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                moveX = endX;
                moveY = endY;

                endX = e.getX();
                endY = e.getY();

                int moveX = endX - MainController.this.moveX;
                int moveY = endY - MainController.this.moveY;
                switch (e.getModifiers()) {
                    case MouseEvent.BUTTON1_MASK:
                        camera = camera.addAzimuth((double) -moveX * Math.PI / 720);
                        camera = camera.addZenith((double) -moveY * Math.PI / 720);
                        break;
                    case MouseEvent.BUTTON3_MASK:
                        Mat4 rot = new Mat4RotXYZ(0, -moveY * Math.PI / 180, moveX * Math.PI / 180);
                        model = model.mul(rot);
                        break;
                }
                draw();
            }

        };

        MouseAdapter mouseWheel = new MouseAdapter() {
            // Přiblížení/oddálení
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.getWheelRotation() < 0) {
                    camera = camera.forward(step);
                } else {
                    camera = camera.backward(step);
                }
                draw();
            }
        };

        panel.addMouseMotionListener(mouseMove);
        panel.addMouseWheelListener(mouseWheel);

        KeyAdapter key = new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W:
                        // vpřed
                        camera = camera.forward(step);
                        break;
                    case KeyEvent.VK_S:
                        // vzad
                        camera = camera.backward(step);
                        break;
                    case KeyEvent.VK_A:
                        // levá
                        camera = camera.left(step);
                        break;
                    case KeyEvent.VK_D:
                        // pravá
                        camera = camera.right(step);
                        break;
                    case KeyEvent.VK_Q:
                        // nahoru
                        camera = camera.up(step);
                        break;
                    case KeyEvent.VK_E:
                        // dolu
                        camera = camera.down(step);
                        break;
                    case KeyEvent.VK_R:
                        // zmenšit
                        scale = scale * step;
                        Mat4 scale = new Mat4Scale(MainController.this.scale, MainController.this.scale, MainController.this.scale);
                        model = model.mul(scale);
                        break;
                    case KeyEvent.VK_T:
                        // zvětšit
                        MainController.this.scale = MainController.this.scale * (1 + step);
                        Mat4 scale1 = new Mat4Scale(MainController.this.scale, MainController.this.scale, MainController.this.scale);
                        model = model.mul(scale1);
                }
                draw();
            }
        };
        frame.addKeyListener(key);

    }

    private void initButtons(JToolBar container) {
        JButton reset = new JButton("Reset");
        reset.setFocusable(false);
        container.add(reset);

        jPers = new JRadioButton("Perspektivní");
        jPers.setSelected(true);
        JRadioButton jOrth = new JRadioButton("Ortogonální");

        ButtonGroup group = new ButtonGroup();
        group.add(jPers);
        group.add(jOrth);

        jPers.setFocusable(false);
        jOrth.setFocusable(false);

        jFill = new JRadioButton("Vyplněný");
        jFill.setSelected(true);
        jWire = new JRadioButton("Drátěný");

        ButtonGroup group1 = new ButtonGroup();
        group1.add(jFill);
        group1.add(jWire);

        jFill.setFocusable(false);
        jWire.setFocusable(false);

        jCube = new JCheckBox("Krychle");
        jTetra = new JCheckBox("Jehlan");
        jTriangle = new JCheckBox("Trojuhelník");
        jMesh = new JCheckBox("Bicubic");

        jCube.setFocusable(false);
        jTetra.setFocusable(false);
        jTriangle.setFocusable(false);
        jMesh.setFocusable(false);

        container.add(jPers);
        container.add(jOrth);
        container.add(jFill);
        container.add(jWire);
        container.add(jCube);
        container.add(jTetra);
        container.add(jTriangle);
        container.add(jMesh);

        reset.addActionListener(e -> reset());
        jPers.addActionListener(e -> choosePers());
        jOrth.addActionListener(e -> chooseOrth());
        jFill.addActionListener(e -> chooseFill());
        jWire.addActionListener(e -> chooseWire());
        jCube.addActionListener(e -> chooseCube());
        jTetra.addActionListener(e -> chooseTetra());
        jTriangle.addActionListener(e -> chooseTriangle());
        jMesh.addActionListener(e -> chooseMesh());
    }

    private void chooseCube() {
        if (jCube.isSelected()) {
            solids.add(cube);
            draw();
        } else {
            solids.remove(cube);
            draw();
        }
    }

    private void chooseTetra() {
        if (jTetra.isSelected()) {
            solids.add(tetrahedron);
            draw();
        } else {
            solids.remove(tetrahedron);
            draw();
        }
    }

    private void chooseTriangle() {
        if (jTriangle.isSelected()) {
            solids.add(triangle);
            draw();
        } else {
            solids.remove(triangle);
            draw();
        }
    }

    private void chooseMesh() {
        if (jMesh.isSelected()) {
            solids.add(mesh);
            draw();
        } else {
            solids.remove(mesh);
            draw();
        }
    }

    private void chooseFill() {
        jFill.setSelected(true);
        renderer.setIsFilling(true);
        draw();

    }

    private void chooseWire() {
        jWire.setSelected(true);
        renderer.setIsFilling(false);
        draw();

    }

    private void chooseOrth() {
        projection = new Mat4OrthoRH(20, 20, 0.1, 200);
        draw();
    }

    private void choosePers() {
        projection = new Mat4PerspRH(Math.PI / 4, 1, 0.1, 200);
        draw();
    }

    private void reset() {
        scale = 1;
        model = new Mat4Identity();
        camera = new Camera(new Vec3D(-20, 0, 0), 0, 0, 1, true);
        view = camera.getViewMatrix();
        projection = new Mat4PerspRH(Math.PI / 4, 1, 0.1, 200);
        jPers.setSelected(true);
        jFill.setSelected(true);
        draw();
    }

    public void clear(int color) {
        Graphics graphics = image.getGraphics();
        graphics.setColor(new Color(color));
        graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
    }

    public void present() {
        if (panel.getGraphics() != null)
            panel.getGraphics().drawImage(renderer.getBufferedImage(), 0, 0, null);
    }

    public void draw() {
        clear(CLEAR_COLOR);
        for (Scene scene : solids) {
            scene.setModel(model);
        }
        renderer.setView(camera.getViewMatrix());
        renderer.setProj(projection);
        renderer.render(solids);
        present();
        drawHelp();
    }

    public void start() {
        clear(CLEAR_COLOR);
        draw();
        present();
        drawHelp();
        Graphics start = panel.getGraphics();
        start.setColor(Color.BLACK);
        start.setFont(new Font("Arial", Font.PLAIN, 40));
    }

    public void drawHelp() {
        Graphics help = panel.getGraphics();
        help.setColor(Color.RED);
        help.drawString("NÁPOVĚDA ", 10, 15);
        help.drawString("A,W,S,D -> pohyb ←↑↓→ po ose XY ", 10, 30);
        help.drawString("Q,E -> pohyb ↑↓ po ose Z ", 10, 45);
        help.drawString("R,T-> zvětšení/zmenšení o polovinu ", 10, 60);
        help.drawString("Mouse_BTN1 -> otáčení kamerou ", 10, 75);
        help.drawString("Mouse_BTN3 -> otáčení objekty ", 10, 90);
        help.drawString("Mouse_Wheel -> zooming ", 10, 105);
    }

}
