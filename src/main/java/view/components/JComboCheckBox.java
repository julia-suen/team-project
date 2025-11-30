package view.components;

// Reference: https://stackoverflow.com/questions/1573159/java-check-boxes-in-a-jcombobox
/* * The following code is adapted from Java Forums - JCheckBox in JComboBox URL: http://forum.java.sun.com/thread.jspa?forumID=257&threadID=364705 Date of Access: July 28 2005 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class JComboCheckBox extends JComboBox<JCheckBox> {
    public JComboCheckBox(JCheckBox[] items) {
        super(items);
        addStuff();
    }

    private void addStuff() {
        setRenderer(new ComboBoxRenderer());
        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                itemSelected();
            }
        });
    }

    private void itemSelected() {
        if (getSelectedItem() instanceof JCheckBox) {
            final JCheckBox jcb = (JCheckBox) getSelectedItem();
            jcb.setSelected(!jcb.isSelected());
        }
    }

    // Helper method to retrieve all selected provinces
    public List<JCheckBox> getCheckedItems() {
        final List<JCheckBox> selected = new ArrayList<>();

        final ComboBoxModel<JCheckBox> model = getModel();
        for (int i = 0; i < model.getSize(); i++) {
            final Object obj = model.getElementAt(i);
            if (obj instanceof JCheckBox cb && cb.isSelected()) {
                selected.add(cb);
            }
        }
        return selected;
    }

    @Override
    public void setPopupVisible(boolean v) {
        // Only allow *show*, never allow *hide* triggered by item clicks
        if (v) {
            super.setPopupVisible(true);
        }
    }

    @Override
    public void processMouseEvent(MouseEvent e) {
        // Prevent JComboBox from auto-hiding on mouse release
        if (e.getID() != MouseEvent.MOUSE_RELEASED) {
            super.processMouseEvent(e);
        }
    }

    class ComboBoxRenderer implements ListCellRenderer<JCheckBox> {
        public ComboBoxRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends JCheckBox> list,
                                                      JCheckBox value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {

            if (index == -1) {
                // Collapsed combo box display (not the dropdown list)
                // Show summary text
                final int selectedCount = getCheckedItems().size();

                final JLabel label = new JLabel();
                if (selectedCount == 0) {
                    label.setText("Select provinces...");
                }
                else if (selectedCount == 1) {
                    label.setText(value.getText()); // only selected checkbox
                }
                else {
                    label.setText(selectedCount + " provinces selected");
                }
                return label;
            }

            // index >= 0 → this is one item in the dropdown list
            value.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            value.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            value.setSelected(value.isSelected());
            return value;
        }

    }
}