package co.edu.uptc.view.help;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;

public class LoadLanguageDialog extends JDialog {

    private JTextField langNameField;
    private JTextArea yamlArea;

    public LoadLanguageDialog(JFrame parent) {
        super(parent, "Cargar Idioma Extra", true);
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(14, 18, 14, 18));
        
        JPanel topPanel = new JPanel(new BorderLayout(6, 6));
        topPanel.add(new JLabel("Nombre del idioma:"), BorderLayout.NORTH);
        langNameField = new JTextField(15);
        topPanel.add(langNameField, BorderLayout.SOUTH);
        
        yamlArea = new JTextArea(8, 25);
        JScrollPane scrollPane = new JScrollPane(yamlArea);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        JButton loadFileBtn = new JButton("Desde archivo...");
        JButton addBtn = new JButton("Agregar");
        JButton cancelBtn = new JButton("Cancelar");
        
        loadFileBtn.addActionListener(e -> selectFile());
        addBtn.addActionListener(e -> dispose());
        cancelBtn.addActionListener(e -> dispose());
        
        panel.add(loadFileBtn);
        panel.add(addBtn);
        panel.add(cancelBtn);
        return panel;
    }

    private void selectFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos YAML (*.yml)", "yml"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            langNameField.setText(chooser.getSelectedFile().getName());
        }
    }
}