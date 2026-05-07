package view.panel;

import config.AppConfig;
import controller.GameController;
import controller.GameEventListener;
import model.*;
import util.AnimationHelper;
import util.DrawUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Panel vẽ bàn cờ 3x3.
 * Implements GameEventListener để lắng nghe sự kiện từ Controller.
 *
 * Tính năng:
 * - Vẽ ô với hover effect và preview mờ ký hiệu sắp đánh
 * - Animation fade-in khi đánh quân
 * - Highlight đường thắng với đường kẻ vàng
 * - Disabled khi AI đang tính toán
 *
 * @author SinhVienCNTT
 */
public class BoardPanel extends JPanel implements GameEventListener {

    private static final int BOARD_PX = AppConfig.BOARD_SIZE * AppConfig.CELL_SIZE
                                       + AppConfig.CELL_PAD * 2;

    private final GameController controller;

    // Trạng thái hover
    private int hoverRow = -1, hoverCol = -1;

    // Animation: opacity từng ô khi vừa đánh [row*3 + col]
    private final float[] cellAlpha = new float[AppConfig.BOARD_SIZE * AppConfig.BOARD_SIZE];

    // Đường thắng fade-in
    private float winLineAlpha = 0f;

    // Disabled khi AI đang nghĩ
    private boolean blocked = false;

    public BoardPanel(GameController controller) {
        this.controller = controller;
        controller.addListener(this);

        for (int i = 0; i < cellAlpha.length; i++) cellAlpha[i] = 1f;

        setPreferredSize(new Dimension(BOARD_PX, BOARD_PX));
        setBackground(AppConfig.COLOR_BG_MAIN);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Timer repaint liên tục (~60fps) để animation mượt
        new Timer(AppConfig.ANIM_INTERVAL_MS, e -> repaint()).start();

        addMouseListeners();
    }

    // ==================== MOUSE EVENTS ====================

    private void addMouseListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (blocked) return;
                int[] rc = getCellAt(e.getX(), e.getY());
                if (rc != null) {
                    // Animation fade-in cho ô vừa click
                    triggerCellFadeIn(rc[0], rc[1]);
                    controller.handleHumanMove(rc[0], rc[1]);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverRow = hoverCol = -1;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int[] rc = getCellAt(e.getX(), e.getY());
                if (rc != null) { hoverRow = rc[0]; hoverCol = rc[1]; }
                else            { hoverRow = hoverCol = -1; }
            }
        });
    }

    private void triggerCellFadeIn(int row, int col) {
        int idx = row * AppConfig.BOARD_SIZE + col;
        cellAlpha[idx] = 0f;
        AnimationHelper.fadeIn(300, t -> cellAlpha[idx] = (float) t, null);
    }

    // ==================== PAINTING ====================

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        DrawUtil.enableAntiAlias(g2);

        paintBackground(g2);
        paintCells(g2);
        paintGrid(g2);
        paintSymbols(g2);
        paintWinLine(g2);
        paintHoverPreview(g2);
    }

    private void paintBackground(Graphics2D g2) {
        GradientPaint grad = new GradientPaint(
            0, 0, new Color(16, 20, 36),
            getWidth(), getHeight(), new Color(11, 13, 22));
        g2.setPaint(grad);
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    private void paintCells(Graphics2D g2) {
        Board board = controller.getBoard();
        int[] win   = board.getWinningLine();

        for (int r = 0; r < AppConfig.BOARD_SIZE; r++) {
            for (int c = 0; c < AppConfig.BOARD_SIZE; c++) {
                int x = AppConfig.CELL_PAD + c * AppConfig.CELL_SIZE;
                int y = AppConfig.CELL_PAD + r * AppConfig.CELL_SIZE;
                int sz = AppConfig.CELL_SIZE - 6;

                boolean isWinCell = isWinCell(r, c, win);

                // Shadow
                g2.setColor(AppConfig.COLOR_SHADOW);
                g2.fillRoundRect(x + 3, y + 4, sz, sz, AppConfig.CELL_ARC, AppConfig.CELL_ARC);

                // Nền ô
                Color bg = isWinCell ? AppConfig.COLOR_WIN_BG
                         : (r == hoverRow && c == hoverCol
                           && board.isEmpty(r, c)
                           && !controller.getGameResult().isFinished()
                           && !blocked)
                           ? AppConfig.COLOR_BG_HOVER
                           : AppConfig.COLOR_BG_CARD;
                g2.setColor(bg);
                g2.fillRoundRect(x + 2, y + 2, sz, sz, AppConfig.CELL_ARC, AppConfig.CELL_ARC);

                // Viền ô
                Color border = isWinCell
                    ? DrawUtil.withAlpha(AppConfig.COLOR_WIN_LINE, 120)
                    : AppConfig.COLOR_GRID;
                DrawUtil.drawRoundRect(g2, x + 2, y + 2, sz, sz,
                                       AppConfig.CELL_ARC, border, isWinCell ? 2f : 1f);
            }
        }
    }

    private void paintGrid(Graphics2D g2) {
        g2.setColor(AppConfig.COLOR_GRID);
        g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int pad  = AppConfig.CELL_PAD;
        int total = AppConfig.BOARD_SIZE * AppConfig.CELL_SIZE;

        for (int i = 1; i < AppConfig.BOARD_SIZE; i++) {
            int pos = pad + i * AppConfig.CELL_SIZE;
            g2.drawLine(pad + 8, pos, pad + total - 8, pos); // ngang
            g2.drawLine(pos, pad + 8, pos, pad + total - 8); // dọc
        }
    }

    private void paintSymbols(Graphics2D g2) {
        Board board = controller.getBoard();
        for (int r = 0; r < AppConfig.BOARD_SIZE; r++) {
            for (int c = 0; c < AppConfig.BOARD_SIZE; c++) {
                CellState cell = board.getCell(r, c);
                if (cell.isEmpty()) continue;

                float alpha = cellAlpha[r * AppConfig.BOARD_SIZE + c];
                int cx = AppConfig.CELL_PAD + c * AppConfig.CELL_SIZE + AppConfig.CELL_SIZE / 2;
                int cy = AppConfig.CELL_PAD + r * AppConfig.CELL_SIZE + AppConfig.CELL_SIZE / 2;

                if (cell == CellState.X) {
                    DrawUtil.drawX(g2, cx, cy, AppConfig.SYMBOL_MARGIN, AppConfig.COLOR_X, alpha);
                } else {
                    DrawUtil.drawO(g2, cx, cy, AppConfig.SYMBOL_MARGIN, AppConfig.COLOR_O, alpha);
                }
            }
        }
    }

    private void paintHoverPreview(Graphics2D g2) {
        Board board = controller.getBoard();
        if (hoverRow < 0 || hoverCol < 0) return;
        if (!board.isEmpty(hoverRow, hoverCol)) return;
        if (controller.getGameResult().isFinished()) return;
        if (blocked) return;
        if (controller.getCurrentPlayer().isAI()) return;

        int cx = AppConfig.CELL_PAD + hoverCol * AppConfig.CELL_SIZE + AppConfig.CELL_SIZE / 2;
        int cy = AppConfig.CELL_PAD + hoverRow * AppConfig.CELL_SIZE + AppConfig.CELL_SIZE / 2;
        CellState cur = controller.getCurrentPlayer().getSymbol();

        if (cur == CellState.X) {
            DrawUtil.drawX(g2, cx, cy, AppConfig.SYMBOL_MARGIN, AppConfig.COLOR_X, 0.22f);
        } else {
            DrawUtil.drawO(g2, cx, cy, AppConfig.SYMBOL_MARGIN, AppConfig.COLOR_O, 0.22f);
        }
    }

    private void paintWinLine(Graphics2D g2) {
        int[] win = controller.getBoard().getWinningLine();
        if (win == null || winLineAlpha <= 0f) return;

        int x1 = AppConfig.CELL_PAD + win[1] * AppConfig.CELL_SIZE + AppConfig.CELL_SIZE / 2;
        int y1 = AppConfig.CELL_PAD + win[0] * AppConfig.CELL_SIZE + AppConfig.CELL_SIZE / 2;
        int x3 = AppConfig.CELL_PAD + win[5] * AppConfig.CELL_SIZE + AppConfig.CELL_SIZE / 2;
        int y3 = AppConfig.CELL_PAD + win[4] * AppConfig.CELL_SIZE + AppConfig.CELL_SIZE / 2;

        Composite old = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, winLineAlpha));
        g2.setColor(AppConfig.COLOR_WIN_LINE);
        g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(x1, y1, x3, y3);
        g2.setComposite(old);
    }

    // ==================== HELPERS ====================

    private boolean isWinCell(int r, int c, int[] win) {
        if (win == null) return false;
        for (int k = 0; k < 6; k += 2)
            if (win[k] == r && win[k+1] == c) return true;
        return false;
    }

    private int[] getCellAt(int px, int py) {
        int col = (px - AppConfig.CELL_PAD) / AppConfig.CELL_SIZE;
        int row = (py - AppConfig.CELL_PAD) / AppConfig.CELL_SIZE;
        if (row >= 0 && row < AppConfig.BOARD_SIZE
            && col >= 0 && col < AppConfig.BOARD_SIZE
            && px > AppConfig.CELL_PAD && py > AppConfig.CELL_PAD) {
            return new int[]{row, col};
        }
        return null;
    }

    // ==================== OBSERVER CALLBACKS ====================

    @Override
    public void onMoveMade(Move move) { repaint(); }

    @Override
    public void onGameOver(GameResult result, Player winner) {
        winLineAlpha = 0f;
        AnimationHelper.fadeIn(500, t -> winLineAlpha = (float) t, null);
    }

    @Override
    public void onNewGame() {
        for (int i = 0; i < cellAlpha.length; i++) cellAlpha[i] = 1f;
        winLineAlpha = 0f;
        blocked = false;
        repaint();
    }

    @Override
    public void onTurnChanged(Player currentPlayer) { repaint(); }

    @Override
    public void onAIThinking(boolean isThinking) {
        blocked = isThinking;
        setCursor(Cursor.getPredefinedCursor(
            isThinking ? Cursor.WAIT_CURSOR : Cursor.HAND_CURSOR));
        repaint();
    }
}