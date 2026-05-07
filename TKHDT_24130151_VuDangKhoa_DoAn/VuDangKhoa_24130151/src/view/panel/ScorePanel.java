package view.panel;

import config.AppConfig;
import controller.GameController;
import controller.GameEventListener;
import model.*;
import view.component.PlayerCard;

import javax.swing.*;
import java.awt.*;

/**
 * Panel hiển thị thông tin điểm số và trạng thái lượt chơi.
 *
 * @author SinhVienCNTT
 */
public class ScorePanel extends JPanel implements GameEventListener {

    private final GameController controller;

    private PlayerCard cardX, cardO;
    private JLabel statusLabel;
    private JLabel gameCountLabel;
    private JLabel aiThinkingLabel;

    public ScorePanel(GameController controller) {
        this.controller = controller;
        controller.addListener(this);

        setOpaque(false);
        setLayout(new BorderLayout(0, 8));
        setBorder(BorderFactory.createEmptyBorder(14, 20, 10, 20));

        buildUI();
        refresh();
    }

    private void buildUI() {
        // Tiêu đề
        JLabel title = new JLabel("⚡  CARO  3×3", SwingConstants.CENTER);
        title.setFont(AppConfig.FONT_TITLE);
        title.setForeground(AppConfig.COLOR_TEXT_MAIN);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        add(title, BorderLayout.NORTH);

        // Khu vực giữa: 2 card + VS
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        cardX = new PlayerCard("X", AppConfig.COLOR_X);
        cardO = new PlayerCard("O", AppConfig.COLOR_O);

        JLabel vsLabel = new JLabel("VS", SwingConstants.CENTER);
        vsLabel.setFont(AppConfig.FONT_HEADING);
        vsLabel.setForeground(AppConfig.COLOR_TEXT_MUTED);

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.BOTH; g.weighty = 1.0;
        g.gridx = 0; g.weightx = 1.0; centerPanel.add(cardX, g);
        g.gridx = 1; g.weightx = 0.3; centerPanel.add(vsLabel, g);
        g.gridx = 2; g.weightx = 1.0; centerPanel.add(cardO, g);

        add(centerPanel, BorderLayout.CENTER);

        // Khu vực dưới: status
        JPanel bottomPanel = new JPanel(new BorderLayout(0, 2));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        aiThinkingLabel = new JLabel("🤖 Máy đang suy nghĩ...", SwingConstants.CENTER);
        aiThinkingLabel.setFont(AppConfig.FONT_SMALL);
        aiThinkingLabel.setForeground(AppConfig.COLOR_O);
        aiThinkingLabel.setVisible(false);

        statusLabel = new JLabel("...", SwingConstants.CENTER);
        statusLabel.setFont(AppConfig.FONT_STATUS);
        statusLabel.setForeground(AppConfig.COLOR_TEXT_MAIN);

        gameCountLabel = new JLabel("", SwingConstants.CENTER);
        gameCountLabel.setFont(AppConfig.FONT_SMALL);
        gameCountLabel.setForeground(AppConfig.COLOR_TEXT_MUTED);

        bottomPanel.add(aiThinkingLabel, BorderLayout.NORTH);
        bottomPanel.add(statusLabel,     BorderLayout.CENTER);
        bottomPanel.add(gameCountLabel,  BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Đọc trạng thái từ controller và cập nhật UI.
     */
    public void refresh() {
        Player pX = controller.getPlayerX();
        Player pO = controller.getPlayerO();

        // Card X
        cardX.setPlayerName(pX.getName() + (pX.isAI() ? " 🤖" : ""));
        cardX.setScore(pX.getWins());
        cardX.setWinStats(pX.getWins(), pX.getDraws(), pX.getLosses());

        // Card O
        cardO.setPlayerName(pO.getName() + (pO.isAI() ? " 🤖" : ""));
        cardO.setScore(pO.getWins());
        cardO.setWinStats(pO.getWins(), pO.getDraws(), pO.getLosses());

        // Highlight lượt
        boolean inProgress = !controller.getGameResult().isFinished();
        cardX.setActive(inProgress && controller.getCurrentPlayer() == pX);
        cardO.setActive(inProgress && controller.getCurrentPlayer() == pO);

        // Status text
        GameResult result = controller.getGameResult();
        if (result == GameResult.IN_PROGRESS) {
            Player cur = controller.getCurrentPlayer();
            String sym = cur.getSymbol().getSymbol();
            statusLabel.setText("🎮 Lượt: " + cur.getName() + " [" + sym + "]");
            statusLabel.setForeground(cur.getSymbol() == CellState.X
                ? AppConfig.COLOR_X : AppConfig.COLOR_O);
        } else if (result == GameResult.DRAW) {
            statusLabel.setText("🤝 Hòa nhau!");
            statusLabel.setForeground(new Color(255, 215, 0));
        } else {
            Player winner = (result == GameResult.X_WIN) ? pX : pO;
            statusLabel.setText("🏆 " + winner.getName() + " thắng!");
            statusLabel.setForeground(winner.getSymbol() == CellState.X
                ? AppConfig.COLOR_X : AppConfig.COLOR_O);
        }

        // Số ván
        int total = controller.getTotalGames();
        gameCountLabel.setText(total > 0 ? "Ván đã chơi: " + total : "");

        repaint();
    }

    // ==================== OBSERVER ====================

    @Override public void onMoveMade(Move move)             { refresh(); }
    @Override public void onNewGame()                        { refresh(); }
    @Override public void onTurnChanged(Player p)           { refresh(); }

    @Override
    public void onGameOver(GameResult result, Player winner) {
        refresh();
    }

    @Override
    public void onAIThinking(boolean isThinking) {
        aiThinkingLabel.setVisible(isThinking);
        cardO.setAIThinking(isThinking);
        refresh();
    }
}