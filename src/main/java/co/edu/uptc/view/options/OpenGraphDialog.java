package co.edu.uptc.view.options;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class OpenGraphDialog {

    private final JFileChooser fileChooser;

    public OpenGraphDialog() {
        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Abrir grafo");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Grafos (*.csv)", "csv"));
    }

    public File showOpenDialog(java.awt.Component parent) {
        int result = fileChooser.showOpenDialog(parent);
        return (result == JFileChooser.APPROVE_OPTION) ? fileChooser.getSelectedFile() : null;
    }
}