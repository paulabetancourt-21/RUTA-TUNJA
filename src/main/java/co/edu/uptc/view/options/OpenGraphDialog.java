package co.edu.uptc.view.options;

import co.edu.uptc.i18n.LanguageService;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class OpenGraphDialog {

    private final JFileChooser fileChooser;

    public OpenGraphDialog(LanguageService languageService) {
        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(languageService.getText("dialog.openGraph.title"));
        fileChooser.setFileFilter(new FileNameExtensionFilter(
                languageService.getText("dialog.openGraph.filter"), "csv"));
    }

    public File showOpenDialog(java.awt.Component parent) {
        int result = fileChooser.showOpenDialog(parent);
        return (result == JFileChooser.APPROVE_OPTION) ? fileChooser.getSelectedFile() : null;
    }
}
