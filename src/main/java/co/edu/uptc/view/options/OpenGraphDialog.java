package co.edu.uptc.view.options;

import co.edu.uptc.i18n.LanguageService;
import javax.swing.JFileChooser;
import java.io.File;

public class OpenGraphDialog {

    private final JFileChooser fileChooser;

    public OpenGraphDialog(LanguageService languageService) {
        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(languageService.getText("dialog.openGraph.title"));
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
    }

    public File showOpenDialog(java.awt.Component parent) {
        int result = fileChooser.showOpenDialog(parent);
        return (result == JFileChooser.APPROVE_OPTION) ? fileChooser.getSelectedFile() : null;
    }
}