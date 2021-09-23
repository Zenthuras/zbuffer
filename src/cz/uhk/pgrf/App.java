package cz.uhk.pgrf;

import cz.uhk.pgrf.controller.MainController;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        MainController mainController = new MainController(800, 800);
        SwingUtilities.invokeLater(mainController::start);
    }
}
