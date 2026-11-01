package co.edu.uptc.view.help;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class HowItWorksDialog extends JDialog {

    public HowItWorksDialog(JFrame parent) {
        super(parent, "¿Cómo funciona?", true);
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
        JPanel panel = new JPanel(new GridLayout(5, 1, 8, 8));
        panel.add(new JLabel("<html><b>Pasos para construir el grafo y calcular rutas:</b></html>"));
        panel.add(new JLabel("1. Vía > Crear nuevo nodo: Haz clic en el mapa para ubicar una esquina."));
        panel.add(new JLabel("2. Vía > Crear nueva arista: Selecciona origen y destino para conectar vías."));
        panel.add(new JLabel("3. Ruta > Calcular ruta: Marca origen y destino para hallar la vía más corta."));
        panel.add(new JLabel("4. Presiona Esc en cualquier momento para cancelar la acción en curso."));
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