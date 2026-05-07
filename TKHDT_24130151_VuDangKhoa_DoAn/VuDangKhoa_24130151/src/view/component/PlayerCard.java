package view.component;

import config.AppConfig;
import util.DrawUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Component hiển thị thông tin một người chơi:
 * tên, ký hiệu (X/O), điểm số, và highlight khi đến lượt.
 *
 * @author SinhVienCNTT
 */
public class PlayerCard extends JPanel {

    private final Color symbolColor;
    private JLabel symbolLabel;
    private JLabel nameLabel;
    private JLabel scoreLabel;
    private JLabel winsLabel;

    private boolean active = false;
    private boolean aiThinking = false;

    public PlayerCard(String symbol, Color symbolColor) {
        this.symbolColor = symbolColor;
        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        setPreferredSize(new Dimension(160, 110));
        buildUI(symbol);
    }

    private void buildUI(String symbol) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(1, 0, 1, 0);

        // Ký hiệu lớn
        symbolLabel = new JLabel(symbol, SwingConstants.CENTER);
        symbolLabel.setFont(AppConfig.FONT_SYMBOL);
        symbolLabel.setForeground(symbolColor);
        gbc.gridy = 0;
        add(symbolLabel, gbc);

        // Điểm số
        scoreLabel = new JLabel("0", SwingConstants.CENTER);
        scoreLabel.setFont(AppConfig.FONT_SCORE);
        scoreLabel.setForeground(AppConfig.COLOR_TEXT_MAIN);
        gbc.gridy = 1;
        add(scoreLabel, gbc);

        // Tên người chơi
        nameLabel = new JLabel("...", SwingConstants.CENTER);
        nameLabel.setFont(AppConfig.FONT_SMALL);
        nameLabel.setForeground(AppConfig.COLOR_TEXT_DIM);
        gbc.gridy = 2;
        add(nameLabel, gbc);

        // Số ván thắng
        winsLabel = new JLabel("", SwingConstants.CENTER);
        winsLabel.setFont(AppConfig.FONT_SMALL.deriveFont(10f));
        winsLabel.setForeground(AppConfig.COLOR_TEXT_MUTED);
        gbc.gridy = 3;
        add(winsLabel, gbc);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        DrawUtil.enableAntiAlias(g2);

        Color bg     = active ? AppConfig.COLOR_ACTIVE_BG : AppConfig.COLOR_BG_CARD;
        Color border = active ? symbolColor : new Color(40, 50, 80);
        float bWidth = active ? 2.5f : 1f;

        // Shadow
        g2.setColor(new Color(0, 0, 0, 60));
        g2.fillRoundRect(3, 4, getWidth() - 4, getHeight() - 4, 18, 18);

        // Nền
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 3, 18, 18);

        // Viền
        g2.setColor(border);
        g2.setStroke(new BasicStroke(bWidth));
        g2.drawRoundRect(0, 0, getWidth() - 2, getHeight() - 3, 18, 18);

        // Highlight phía trên nếu đang lượt
        if (active) {
            g2.setColor(DrawUtil.withAlpha(symbolColor, 40));
            g2.fillRoundRect(0, 0, getWidth() - 2, 8, 18, 18);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    // ==================== Cập nhật data ====================

    public void setPlayerName(String name)   { nameLabel.setText(name);               }
    public void setScore(int score)          { scoreLabel.setText(String.valueOf(score)); }
    public void setWinStats(int w, int d, int l) {
        winsLabel.setText("W:" + w + " D:" + d + " L:" + l);
    }

    public void setActive(boolean active) {
        this.active = active;
        symbolLabel.setText(active && aiThinking ? "..." : symbolLabel.getText().contains("X") ? "X" : "O");
        repaint();
    }

    public void setAIThinking(boolean thinking) {
        this.aiThinking = thinking;
        if (thinking) symbolLabel.setText("...");
        else symbolLabel.setText(symbolColor.equals(AppConfig.COLOR_X) ? "X" : "O");
        repaint();
    }

    public void setSymbolText(String sym) { symbolLabel.setText(sym); }
}