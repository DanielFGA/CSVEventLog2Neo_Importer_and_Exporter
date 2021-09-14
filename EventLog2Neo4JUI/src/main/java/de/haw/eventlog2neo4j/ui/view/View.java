package de.haw.eventlog2neo4j.ui.view;

import de.haw.eventlog2neo4j.ui.controller.Controller;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import static de.haw.eventlog2neo4j.ui.controller.Controller.CONFIG_ERROR_MESSAGE;

public class View extends JFrame {

    private final JLabel resultOutput = new JLabel("Select an option and a file.");
    private final JRadioButton loadButton = new JRadioButton("import");

    public View(Controller controller) {
        JPanel jPanel = new JPanel();
        JButton button = new JButton("Upload Configuration File");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(new File("."));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("YML", "yml");
            chooser.setFileFilter(filter);
            int result = chooser.showOpenDialog(this);

            switch (result) {
                case JFileChooser.APPROVE_OPTION:
                    if (loadButton.isSelected()) {
                        controller.loadCSVEventLogToNeo4j(chooser.getSelectedFile().getAbsolutePath());
                    } else {
                        controller.extractCSVEventLogFromNeo4j(chooser.getSelectedFile().getAbsolutePath());
                    }
                    this.setOutput("Success");
                    break;
                case JFileChooser.ERROR_OPTION:
                    this.setOutput(CONFIG_ERROR_MESSAGE);
                    break;
                default:
                    break;
            }
        });
        JPanel radioButtonPanel = new JPanel(new GridLayout(0, 1));
        ButtonGroup group = new ButtonGroup();
        this.loadButton.setSelected(true);
        group.add(this.loadButton);
        JRadioButton extractButton = new JRadioButton("extract");
        group.add(extractButton);
        radioButtonPanel.add(extractButton);
        radioButtonPanel.add(this.loadButton);

        jPanel.setSize(300, 110);
        jPanel.setLayout(new FlowLayout());
        jPanel.add(button);
        jPanel.add(radioButtonPanel);
        jPanel.add(this.resultOutput);
        this.add(jPanel);
        this.setSize(310, 120);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });
        this.setTitle("CSV2Neo4j Importer/Exporter");
    }

    public void setOutput(String message) {
        this.resultOutput.setText(message);
    }

}
