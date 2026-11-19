package com.mycompany.campusconnect_final;

import com.mycompany.campusconnect.view.MainFrame;
import javax.swing.SwingUtilities;

public class CampusConnect_Final {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
