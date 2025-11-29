package view.components;

// Reference: https://stackoverflow.com/questions/1573159/java-check-boxes-in-a-jcombobox
/* * The following code is adapted from Java Forums - JCheckBox in JComboBox URL: http://forum.java.sun.com/thread.jspa?forumID=257&threadID=364705 Date of Access: July 28 2005 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
            JCheckBox jcb = (JCheckBox) getSelectedItem();
            jcb.setSelected(!jcb.isSelected());
        }
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

    class ComboBoxRenderer implements ListCellRenderer {
        private JLabel defaultLabel;

        public ComboBoxRenderer() {
            setOpaque(true);
        }

        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            if (value instanceof Component) {
                Component c = (Component) value;
                if (isSelected) {
                    c.setBackground(list.getSelectionBackground());
                    c.setForeground(list.getSelectionForeground());
                } else {
                    c.setBackground(list.getBackground());
                    c.setForeground(list.getForeground());
                }
                return c;
            } else {
                if (defaultLabel == null) {
                    defaultLabel = new JLabel(value.toString());
                } else {
                    defaultLabel.setText("Multiple provinces selected");
                }
                return defaultLabel;
            }
        }
    }
}