import javax.swing.*;
public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            UIManager.put("control", new java.awt.Color(50, 50, 50));
            UIManager.put("info", new java.awt.Color(50, 50, 50));
            UIManager.put("nimbusBase", new java.awt.Color(18, 30, 49));
            UIManager.put("nimbusAlertYellow", new java.awt.Color(248, 187, 0));
            UIManager.put("nimbusDisabledText", new java.awt.Color(128, 128, 128));
            UIManager.put("nimbusFocus", new java.awt.Color(115, 164, 209));
            UIManager.put("nimbusGreen", new java.awt.Color(176, 179, 50));
            UIManager.put("nimbusInfoBlue", new java.awt.Color(66, 139, 221));
            UIManager.put("nimbusLightBackground", new java.awt.Color(30, 30, 30));
            UIManager.put("nimbusOrange", new java.awt.Color(191, 98, 4));
            UIManager.put("nimbusRed", new java.awt.Color(169, 46, 34));
            UIManager.put("nimbusSelectedText", new java.awt.Color(255, 255, 255));
            UIManager.put("nimbusSelectionBackground", new java.awt.Color(104, 93, 156));
            UIManager.put("text", new java.awt.Color(230, 230, 230));
        } catch (Exception e) {
            e.printStackTrace();
        }
        javax.swing.SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
