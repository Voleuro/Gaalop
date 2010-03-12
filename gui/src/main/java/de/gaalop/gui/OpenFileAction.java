package de.gaalop.gui;

import de.gaalop.CodeParserPlugin;
import de.gaalop.gui.util.PluginIconUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Open a file open dialog and create a new tab if successfullly opened a file.
 */
public class OpenFileAction extends AbstractAction {

    private Log log = LogFactory.getLog(OpenFileAction.class);

    private CodeParserPlugin plugin;

    private JTabbedPane tabbedPane;

    public OpenFileAction(CodeParserPlugin plugin, JTabbedPane tabbedPanel) {
        super(plugin.getName(), PluginIconUtil.getSmallIcon(plugin));
        this.plugin = plugin;
        this.tabbedPane = tabbedPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        try {
            if (fileChooser.showOpenDialog(tabbedPane.getParent()) == JFileChooser.APPROVE_OPTION) {
                String content = readFile(fileChooser.getSelectedFile());

                SourceFilePanel filePanel = new SourceFilePanel(plugin, fileChooser.getSelectedFile(), content);
                tabbedPane.addTab("", filePanel);
                int indexOfPanel = tabbedPane.indexOfComponent(filePanel);
                tabbedPane.setTabComponentAt(indexOfPanel, filePanel.getTabLabel());
                tabbedPane.setSelectedIndex(indexOfPanel);

            }
        } catch (IOException ex) {
            log.error(ex);
            JOptionPane.showMessageDialog(tabbedPane.getParent(), "Unable to open file " +
                    fileChooser.getSelectedFile() + ".\n" + ex, "Error", JFileChooser.ERROR);
        }
    }

    private String readFile(File file) throws IOException {
        StringBuilder result = new StringBuilder();
        BufferedReader in = new BufferedReader(new FileReader(file));
        try {
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
                result.append('\n');
            }
        } finally {
            in.close();
        }
        return result.toString();
    }
}
