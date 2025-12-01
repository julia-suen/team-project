package view;

import javax.swing.*;
import java.awt.*;

public class MarkerInfoPanel extends JPanel {

    private final JLabel latLabel = new JLabel();
    private final JLabel lonLabel = new JLabel();
    private final JLabel sizeLabel = new JLabel();
    private final JLabel frpLabel = new JLabel();
    private final JLabel dateLabel = new JLabel();

    public MarkerInfoPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(255, 255, 255, 200));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.add(latLabel);
        this.add(lonLabel);
        this.add(sizeLabel);
        this.add(frpLabel);
        this.add(dateLabel);

        setVisible(false); // start hidden
    }

    public void update(double lat, double lon, int size, String date, double frp) {
        latLabel.setText(" Lat: " + lat);
        lonLabel.setText(" Lon: " + lon);
        sizeLabel.setText(" Size: " + size);
        frpLabel.setText(" FRP: " + frp);
        dateLabel.setText(" Date: " + date);

        setVisible(true);
        repaint();
    }

}
