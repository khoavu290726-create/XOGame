package view.component;

import config.AppConfig;
import util.DrawUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Nút bấm tùy chỉnh với bo góc và hiệu ứng hover/press.
 * Kế thừa JButton nhưng override paintComponent để có giao diện riêng.
 *
 * @author SinhVienCNTT
 */
public class RoundButton extends JButton {

    private final Color baseColor;
    private Color currentColor;
    private boolean hovered  = false;
    private boolean pressed  = false;

    public RoundButton(String text, Color baseColor) {
        super(text);
        this.baseColor    = baseColor;
        this.currentColor = baseColor;

        // Tắt rendering mặc định của Swing
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);

        setFont(AppConfig.FONT_BODY.deriveFont(Font.BOLD));
        setForeground(AppConfig.COLOR_TEXT_MAIN);
        setPreferredSize(new Dimension(AppConfig.BTN_WIDTH, AppConfig.BTN_HEIGHT));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListeners();
    }

    private void addMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                hovered = true;
                currentColor = baseColor.brighter();
                repaint();
            }
            @Override public void mouseExited(MouseEvent e) {
                hovered  = false;
                pressed  = false;
                currentColor = baseColor;
                repaint();
            }
            @Override public void mousePressed(MouseEvent e) {
                pressed = true;
                currentColor = baseColor.darker();
                repaint();
            }
            @Override public void mouseReleased(MouseEvent e) {
                pressed = false;
                currentColor = hovered ? baseColor.brighter() : baseColor;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        DrawUtil.enableAntiAlias(g2);

        // Vẽ shadow nhẹ
        g2.setColor(new Color(0, 0, 0, 50));
        g2.fillRoundRect(2, 3, getWidth() - 2, getHeight() - 2,
                         AppConfig.BTN_ARC, AppConfig.BTN_ARC);

        // Vẽ nền nút
        g2.setColor(currentColor);
        g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2,
                         AppConfig.BTN_ARC, AppConfig.BTN_ARC);

        // Vẽ viền nhẹ phía trên (highlight)
        g2.setColor(new Color(255, 255, 255, 30));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 3,
                         AppConfig.BTN_ARC, AppConfig.BTN_ARC);

        g2.dispose();

        // Vẽ chữ mặc định của JButton
        super.paintComponent(g);
    }
}