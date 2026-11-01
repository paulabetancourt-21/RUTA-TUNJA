package co.edu.uptc.view.help;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

public class AboutDialog extends JDialog {

    public AboutDialog(JFrame parent) {
        super(parent, "Acerca de...", true);
        initDialog();
        add(createContentPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private void initDialog() {
        setLayout(new BorderLayout(12, 12));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(16, 20, 16, 20));
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 4, 4));
        JLabel title = new JLabel("Mapa Interactivo Tunja", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        panel.add(title);
        panel.add(new JLabel("Versión 1.0.0 (Swing MVP)", JLabel.CENTER));
        panel.add(new JLabel("Paula Andrea Betancourt Matamoros", JLabel.CENTER));
        panel.add(new JLabel("Universidad Pedagógica y Tecnológica de Colombia", JLabel.CENTER));
        panel.add(new JLabel("Sistemas e Informática - 2026", JLabel.CENTER));
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        JButton okBtn = new JButton("Aceptar");
        okBtn.addActionListener(e -> dispose());
        panel.add(okBtn);
        return panel;
    }
}