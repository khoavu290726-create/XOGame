package main;

import ai.MinimaxStrategy;

import ai.RandomStrategy;
import config.AppConfig;
import controller.GameController;
import controller.GameEventListener;
import exception.InvalidMoveException;
import model.*;
import view.component.PlayerCard;
import view.component.RoundButton;
import view.panel.BoardPanel;
import view.panel.ScorePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Main {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        boolean testOnly = args.length > 0 && args[0].equals("--test");
        runLogicTests();
        if (!testOnly) {
            SwingUtilities.invokeLater(Main::launchGUI);
        }
    }

    private static void launchGUI() {
        GameController controller = new GameController();
        JFrame frame = new JFrame(AppConfig.APP_TITLE + " – v" + AppConfig.APP_VERSION);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.getContentPane().setBackground(AppConfig.COLOR_BG_MAIN);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(AppConfig.COLOR_BG_MAIN);

        ScorePanel scorePanel = new ScorePanel(controller);
        scorePanel.setPreferredSize(new Dimension(0, 180));
        root.add(scorePanel, BorderLayout.NORTH);

        BoardPanel boardPanel = new BoardPanel(controller);
        JPanel boardWrapper = new JPanel(new GridBagLayout());
        boardWrapper.setBackground(AppConfig.COLOR_BG_MAIN);
        boardWrapper.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        boardWrapper.add(boardPanel);
        root.add(boardWrapper, BorderLayout.CENTER);

        JPanel btnPanel = buildButtonPanel(controller, frame, scorePanel);
        root.add(btnPanel, BorderLayout.SOUTH);

        frame.setContentPane(root);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("\n[Game] Đóng cửa sổ – Tổng ván: " + controller.getTotalGames());
            }
        });
        System.out.println("[GUI] Cửa sổ game đã mở: " + frame.getTitle());
    }

    private static JPanel buildButtonPanel(GameController controller,
                                           JFrame frame, ScorePanel scorePanel) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 12));
        panel.setBackground(AppConfig.COLOR_BG_MAIN);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        RoundButton btnNew = new RoundButton("▶  Ván mới", AppConfig.BTN_NEW_GAME);
        btnNew.addActionListener(e -> {
            controller.startNewGame();
            System.out.println("[GUI] Bắt đầu ván mới");
        });

        RoundButton btnReset = new RoundButton("↺  Reset", AppConfig.BTN_RESET);
        btnReset.addActionListener(e -> {
            int opt = JOptionPane.showConfirmDialog(frame,
                    "Reset toàn bộ điểm và lịch sử?", "Xác nhận Reset",
                    JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                controller.fullReset();
                System.out.println("[GUI] Đã reset toàn bộ");
            }
        });

        RoundButton btnMode = new RoundButton(getModeLabel(controller), AppConfig.BTN_MODE);
        btnMode.addActionListener(e -> {
            GameMode next = controller.getGameMode().toggle();
            controller.switchMode(next);
            btnMode.setText(getModeLabel(controller));
            scorePanel.refresh();
            System.out.println("[GUI] Đổi chế độ → " + next);
        });

        RoundButton btnHistory = new RoundButton("📋  Lịch sử", AppConfig.BTN_ABOUT);
        btnHistory.addActionListener(e -> showHistoryDialog(frame, controller));

        RoundButton btnDifficulty = new RoundButton("🤖  Độ khó", AppConfig.BTN_ABOUT);
        btnDifficulty.addActionListener(e -> showDifficultyDialog(frame, controller));

        panel.add(btnNew);
        panel.add(btnReset);
        panel.add(btnMode);
        panel.add(btnHistory);
        panel.add(btnDifficulty);

        controller.addListener(new GameEventListener() {
            @Override public void onMoveMade(Move m) {}
            @Override public void onNewGame()         { btnMode.setText(getModeLabel(controller)); }
            @Override public void onTurnChanged(Player p) {}
            @Override public void onGameOver(GameResult r, Player w) {
                Timer delay = new Timer(AppConfig.RESULT_DELAY_MS, ev -> {
                    String msg = buildResultMessage(r, w, controller);
                    int opt = JOptionPane.showOptionDialog(frame, msg,
                            "Kết quả ván #" + controller.getTotalGames(),
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.INFORMATION_MESSAGE, null,
                            new String[]{"▶  Ván mới", "✖  Đóng"}, "▶  Ván mới");
                    if (opt == JOptionPane.YES_OPTION) controller.startNewGame();
                });
                delay.setRepeats(false);
                delay.start();
            }
            @Override public void onAIThinking(boolean t) {}
        });

        return panel;
    }

    private static String getModeLabel(GameController ctrl) {
        return ctrl.getGameMode() == GameMode.TWO_PLAYER ? "👥  2 Người" : "🤖  vs Máy";
    }

    private static void showHistoryDialog(JFrame parent, GameController ctrl) {
        java.util.List<GameSession> history = ctrl.getSessionHistory();
        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Chưa có ván nào.", "Lịch sử",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (GameSession s : history) sb.append(s).append("\n");
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(AppConfig.FONT_SMALL);
        area.setBackground(new Color(20, 23, 38));
        area.setForeground(AppConfig.COLOR_TEXT_MAIN);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(560, 220));
        JOptionPane.showMessageDialog(parent, scroll, "Lịch sử các ván", JOptionPane.PLAIN_MESSAGE);
    }

    private static void showDifficultyDialog(JFrame parent, GameController ctrl) {
        if (ctrl.getAiPlayer() == null) {
            JOptionPane.showMessageDialog(parent,
                    "Đang ở chế độ 2 người chơi.\nChuyển sang chế độ vs Máy để đổi độ khó.",
                    "Độ khó AI", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String current = ctrl.getAiPlayer().getStrategy().getStrategyName();
        String[] options = {"🟢  Dễ (Ngẫu nhiên)", "🔴  Khó (Minimax)"};
        int choice = JOptionPane.showOptionDialog(parent,
                "Chiến lược hiện tại: " + current + "\n\nChọn độ khó:",
                "Đổi độ khó AI", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        if (choice == 0)      { ctrl.getAiPlayer().setStrategy(new RandomStrategy());  System.out.println("[GUI] AI → Easy"); }
        else if (choice == 1) { ctrl.getAiPlayer().setStrategy(new MinimaxStrategy()); System.out.println("[GUI] AI → Hard"); }
    }

    private static String buildResultMessage(GameResult result, Player winner,
                                              GameController ctrl) {
        String line1 = result == GameResult.DRAW
                ? "🤝  Hòa nhau!"
                : "🏆  " + winner.getName() + " [" + winner.getSymbol() + "] thắng!";
        return line1 + "\n\nX: " + ctrl.getPlayerX().getWins() + " thắng  |  "
                + "O: " + ctrl.getPlayerO().getWins() + " thắng  |  "
                + "Hòa: " + ctrl.getPlayerX().getDraws()
                + "\n\nChơi ván tiếp?";
    }

    // ═══════════════════════════════════════════════════════
    //  LOGIC + GUI COMPONENT TESTS
    // ═══════════════════════════════════════════════════════

    private static void runLogicTests() {
        printBanner();
        test01_BoardResetAndValidMove();
        test02_InvalidMove();
        test03_XWinsRow();
        test04_OWinsDiagonal();
        test05_Draw();
        test06_UndoMove();
        test07_RandomAI();
        test08_MinimaxAINeverLoses();
        test09_GameControllerObserver();
        test10_PlayerStats();
        test11_SwingComponentsCreation();
        test12_BoardPanelObserver();
        test13_ScorePanelObserver();
        test14_RoundButtonExists();
        test15_PlayerCardUpdates();
        printSummary();
    }

    static void test01_BoardResetAndValidMove() {
        header("Test 01: Board – khởi tạo và nước đi hợp lệ");
        Board b = new Board();
        bool("Tất cả ô ban đầu là EMPTY", allEmpty(b));
        bool("moveCount ban đầu = 0", b.getMoveCount() == 0);
        bool("size = 3", b.getSize() == 3);
        try {
            b.makeMove(new Move(0, 0, CellState.X));
            bool("Ô (0,0) = X", b.getCell(0, 0) == CellState.X);
            bool("moveCount = 1", b.getMoveCount() == 1);
            bool("isEmpty(0,0) = false", !b.isEmpty(0, 0));
        } catch (InvalidMoveException e) { bool("Không nên có exception: " + e.getMessage(), false); }
        b.reset();
        bool("Sau reset: (0,0) = EMPTY", b.isEmpty(0, 0));
        bool("Sau reset: moveCount = 0", b.getMoveCount() == 0);
    }

    static void test02_InvalidMove() {
        header("Test 02: Board – nước đi không hợp lệ");
        Board b = new Board();
        try { b.makeMove(new Move(0, 0, CellState.X)); } catch (InvalidMoveException e) {}
        try {
            b.makeMove(new Move(0, 0, CellState.O));
            bool("Phải ném exception khi đánh ô đã chiếm", false);
        } catch (InvalidMoveException e) {
            bool("Exception: ô đã chiếm ✓", true);
            System.out.println("    msg: " + e.getMessage());
        }
        try {
            b.makeMove(new Move(9, 9, CellState.X));
            bool("Phải ném exception khi ô ngoài bounds", false);
        } catch (InvalidMoveException e) {
            bool("Exception: ô ngoài bounds ✓", true);
        }
    }

    static void test03_XWinsRow() {
        header("Test 03: Board – X thắng hàng ngang");
        Board b = new Board();
        try {
            b.makeMove(new Move(0,0,CellState.X)); b.makeMove(new Move(1,0,CellState.O));
            b.makeMove(new Move(0,1,CellState.X)); b.makeMove(new Move(1,1,CellState.O));
            b.makeMove(new Move(0,2,CellState.X));
            b.printToConsole();
            GameResult r = b.checkResult();
            bool("Kết quả X_WIN", r == GameResult.X_WIN);
            bool("isFinished = true", r.isFinished());
            bool("hasWinner = true", r.hasWinner());
            int[] wl = b.getWinningLine();
            bool("winningLine != null", wl != null);
            System.out.println("    Đường thắng: ["+wl[0]+","+wl[1]+" → "+wl[4]+","+wl[5]+"]");
        } catch (InvalidMoveException e) { bool("Lỗi: "+e.getMessage(), false); }
    }

    static void test04_OWinsDiagonal() {
        header("Test 04: Board – O thắng đường chéo chính");
        Board b = new Board();
        try {
            b.makeMove(new Move(0,1,CellState.X)); b.makeMove(new Move(0,0,CellState.O));
            b.makeMove(new Move(0,2,CellState.X)); b.makeMove(new Move(1,1,CellState.O));
            b.makeMove(new Move(2,1,CellState.X)); b.makeMove(new Move(2,2,CellState.O));
            b.printToConsole();
            bool("Kết quả O_WIN", b.checkResult() == GameResult.O_WIN);
        } catch (InvalidMoveException e) { bool("Lỗi: "+e.getMessage(), false); }
    }

    static void test05_Draw() {
        header("Test 05: Board – Hòa");
        Board b = new Board();
        int[][] seq = {{0,0,1},{0,1,0},{0,2,1},{1,0,1},{1,1,1},{1,2,0},{2,0,0},{2,1,1},{2,2,0}};
        try {
            for (int[] m : seq)
                b.makeMove(new Move(m[0], m[1], m[2]==1 ? CellState.X : CellState.O));
            b.printToConsole();
            GameResult r = b.checkResult();
            bool("Kết quả DRAW", r == GameResult.DRAW);
            bool("isFinished = true", r.isFinished());
            bool("hasWinner = false", !r.hasWinner());
            bool("isFull = true", b.isFull());
        } catch (InvalidMoveException e) { bool("Lỗi: "+e.getMessage(), false); }
    }

    static void test06_UndoMove() {
        header("Test 06: Board – Undo nước đi");
        Board b = new Board();
        try {
            b.makeMove(new Move(1,1,CellState.X)); b.makeMove(new Move(0,0,CellState.O));
            bool("moveCount = 2", b.getMoveCount() == 2);
            b.undoMove();
            bool("moveCount = 1 sau undo", b.getMoveCount() == 1);
            bool("(0,0) = EMPTY", b.isEmpty(0,0));
            bool("(1,1) vẫn = X", b.getCell(1,1) == CellState.X);
            b.undoMove(); b.undoMove();
            bool("Undo trên bàn trống không crash", true);
            bool("moveCount = 0", b.getMoveCount() == 0);
        } catch (InvalidMoveException e) { bool("Lỗi: "+e.getMessage(), false); }
    }

    static void test07_RandomAI() {
        header("Test 07: RandomStrategy");
        Board b = new Board();
        RandomStrategy ai = new RandomStrategy();
        Move m = ai.findBestMove(b, CellState.O);
        bool("Tìm được nước đi", m != null);
        bool("Nước đi trong bounds", b.isInBounds(m.getRow(), m.getCol()));
        System.out.println("    Random chọn: " + m);
        bool("Tên chiến lược không rỗng", !ai.getStrategyName().isEmpty());
        try {
            int[][] seq = {{0,0},{0,1},{0,2},{1,0},{1,1},{1,2},{2,0},{2,1}};
            for (int[] rc : seq) b.makeMove(new Move(rc[0], rc[1], CellState.X));
            Move last = ai.findBestMove(b, CellState.O);
            bool("Ô cuối là (2,2)", last != null && last.getRow()==2 && last.getCol()==2);
            b.makeMove(new Move(2,2,CellState.X));
            bool("Trả null khi bàn đầy", ai.findBestMove(b, CellState.O) == null);
        } catch (InvalidMoveException e) { bool("Lỗi: "+e.getMessage(), false); }
    }

    static void test08_MinimaxAINeverLoses() {
        header("Test 08: MinimaxStrategy – Minimax vs Minimax luôn hòa");
        Board b = new Board();
        MinimaxStrategy mmX = new MinimaxStrategy();
        MinimaxStrategy mmO = new MinimaxStrategy();
        CellState turn = CellState.X;
        try {
            for (int i = 0; i < 9; i++) {
                if (b.checkResult().isFinished()) break;
                MinimaxStrategy cur = (turn == CellState.X) ? mmX : mmO;
                Move best = cur.findBestMove(b, turn);
                if (best == null) break;
                b.makeMove(new Move(best.getRow(), best.getCol(), turn));
                System.out.println("    [" + turn + "] → " + best);
                turn = turn.opponent();
            }
            b.printToConsole();
            GameResult r = b.checkResult();
            System.out.println("    Kết quả: " + r.getDescription()
                    + " | Nodes X=" + mmX.getNodesEvaluated()
                    + " O=" + mmO.getNodesEvaluated());
            bool("Minimax vs Minimax luôn DRAW", r == GameResult.DRAW);
        } catch (InvalidMoveException e) { bool("Lỗi: "+e.getMessage(), false); }
    }

    static void test09_GameControllerObserver() {
        header("Test 09: GameController + Observer pattern");
        GameController ctrl = new GameController();
        boolean[] flags = new boolean[5];
        ctrl.addListener(new GameEventListener() {
            @Override public void onMoveMade(Move m)                { flags[0] = true; }
            @Override public void onGameOver(GameResult r, Player w){ flags[1] = true; }
            @Override public void onNewGame()                        { flags[2] = true; }
            @Override public void onTurnChanged(Player p)           { flags[3] = true; }
            @Override public void onAIThinking(boolean t)           { flags[4] = true; }
        });
        ctrl.startNewGame();
        bool("onNewGame được gọi", flags[2]);
        bool("onTurnChanged được gọi", flags[3]);
        ctrl.handleHumanMove(0, 0);
        bool("onMoveMade được gọi", flags[0]);
        bool("Lượt chuyển sang O", ctrl.getCurrentPlayer().getSymbol() == CellState.O);
        int before = ctrl.getBoard().getMoveCount();
        ctrl.handleHumanMove(0, 0);
        bool("Nước trùng bị bỏ qua", ctrl.getBoard().getMoveCount() == before);
        ctrl.handleHumanMove(1, 0);
        ctrl.handleHumanMove(0, 1);
        ctrl.handleHumanMove(1, 1);
        ctrl.handleHumanMove(0, 2);
        bool("onGameOver được gọi", flags[1]);
        bool("Kết quả X_WIN", ctrl.getGameResult() == GameResult.X_WIN);
        bool("playerX.getWins() = 1", ctrl.getPlayerX().getWins() == 1);
        bool("playerO.getLosses() = 1", ctrl.getPlayerO().getLosses() == 1);
        bool("Lịch sử có 1 ván", ctrl.getSessionHistory().size() == 1);
        System.out.println("    " + ctrl.getSessionHistory().get(0));
        ctrl.fullReset();
        bool("Sau fullReset: wins = 0", ctrl.getPlayerX().getWins() == 0);
        bool("Sau fullReset: lịch sử rỗng", ctrl.getSessionHistory().isEmpty());
    }

    static void test10_PlayerStats() {
        header("Test 10: Player – thống kê và winRate");
        Player p = new Player("Khoa", CellState.X, false);
        bool("Ban đầu 0 ván", p.getTotalGames() == 0);
        bool("WinRate ban đầu = 0", p.getWinRate() == 0.0);
        p.addWin(); p.addWin(); p.addDraw(); p.addLoss();
        bool("Tổng 4 ván", p.getTotalGames() == 4);
        bool("2 thắng", p.getWins() == 2);
        bool("1 hòa",  p.getDraws() == 1);
        bool("1 thua", p.getLosses() == 1);
        bool("WinRate = 50%", Math.abs(p.getWinRate() - 50.0) < 0.001);
        System.out.printf("    WinRate: %.1f%%%n", p.getWinRate());
        p.resetStats();
        bool("Sau resetStats: 0 ván", p.getTotalGames() == 0);
        try {
            new Player("Bad", CellState.EMPTY, false);
            bool("Phải ném IllegalArgumentException", false);
        } catch (IllegalArgumentException e) {
            bool("IllegalArgumentException với EMPTY symbol", true);
        }
    }

    static void test11_SwingComponentsCreation() {
        header("Test 11: Swing – Tạo BoardPanel và ScorePanel");
        try {
            GameController ctrl = new GameController();
            BoardPanel bp = new BoardPanel(ctrl);
            bool("BoardPanel tạo được", bp != null);
            int expectedPx = AppConfig.BOARD_SIZE * AppConfig.CELL_SIZE + AppConfig.CELL_PAD * 2;
            bool("BoardPanel preferredSize.width = " + expectedPx,
                    bp.getPreferredSize().width == expectedPx);
            bool("BoardPanel preferredSize.height = " + expectedPx,
                    bp.getPreferredSize().height == expectedPx);
            System.out.println("    BoardPanel size: " + bp.getPreferredSize());
            ScorePanel sp = new ScorePanel(ctrl);
            bool("ScorePanel tạo được", sp != null);
            bool("ScorePanel opaque = false", !sp.isOpaque());
        } catch (Exception e) {
            bool("Khởi tạo Swing components thất bại: " + e.getMessage(), false);
        }
    }

    static void test12_BoardPanelObserver() {
        header("Test 12: BoardPanel – Observer callbacks không crash");
        try {
            GameController ctrl = new GameController();
            BoardPanel bp = new BoardPanel(ctrl);
            bp.onNewGame();
            bool("onNewGame() OK", true);
            bp.onMoveMade(new Move(1, 1, CellState.X));
            bool("onMoveMade() OK", true);
            bp.onTurnChanged(ctrl.getPlayerO());
            bool("onTurnChanged() OK", true);
            bp.onAIThinking(true);
            bool("onAIThinking(true) → WAIT cursor", true);
            bp.onAIThinking(false);
            bool("onAIThinking(false) → HAND cursor", true);
            bp.onGameOver(GameResult.X_WIN, ctrl.getPlayerX());
            bool("onGameOver() OK", true);
        } catch (Exception e) {
            bool("BoardPanel Observer crash: " + e.getMessage(), false);
        }
    }

    static void test13_ScorePanelObserver() {
        header("Test 13: ScorePanel – Observer callbacks và refresh");
        try {
            GameController ctrl = new GameController();
            ScorePanel sp = new ScorePanel(ctrl);
            sp.onNewGame();
            bool("onNewGame() OK", true);
            sp.onMoveMade(new Move(0, 0, CellState.X));
            bool("onMoveMade() OK", true);
            sp.onTurnChanged(ctrl.getPlayerO());
            bool("onTurnChanged() OK", true);
            sp.onAIThinking(true);
            bool("onAIThinking(true) OK", true);
            sp.onAIThinking(false);
            bool("onAIThinking(false) OK", true);
            sp.onGameOver(GameResult.DRAW, null);
            bool("onGameOver(DRAW) OK", true);
            ctrl.startNewGame();
            sp.refresh();
            bool("refresh() sau startNewGame OK", true);
        } catch (Exception e) {
            bool("ScorePanel Observer crash: " + e.getMessage(), false);
        }
    }

    static void test14_RoundButtonExists() {
        header("Test 14: RoundButton – khởi tạo và thuộc tính");
        try {
            RoundButton btn = new RoundButton("Test Btn", AppConfig.BTN_NEW_GAME);
            bool("RoundButton tạo được", btn != null);
            bool("Text đúng", "Test Btn".equals(btn.getText()));
            bool("preferredSize.width = " + AppConfig.BTN_WIDTH,
                    btn.getPreferredSize().width == AppConfig.BTN_WIDTH);
            bool("preferredSize.height = " + AppConfig.BTN_HEIGHT,
                    btn.getPreferredSize().height == AppConfig.BTN_HEIGHT);
            bool("ContentAreaFilled = false", !btn.isContentAreaFilled());
            bool("BorderPainted = false", !btn.isBorderPainted());
            bool("FocusPainted = false", !btn.isFocusPainted());
        } catch (Exception e) {
            bool("RoundButton crash: " + e.getMessage(), false);
        }
    }

    static void test15_PlayerCardUpdates() {
        header("Test 15: PlayerCard – cập nhật name/score/stats/active/AI");
        try {
            PlayerCard card = new PlayerCard("X", AppConfig.COLOR_X);
            bool("PlayerCard tạo được", card != null);
            card.setPlayerName("Vu Dang Khoa");
            bool("setPlayerName OK", true);
            card.setScore(5);
            bool("setScore(5) OK", true);
            card.setWinStats(5, 2, 1);
            bool("setWinStats(5,2,1) OK", true);
            card.setActive(true);
            bool("setActive(true) OK", true);
            card.setActive(false);
            bool("setActive(false) OK", true);
            card.setAIThinking(true);
            bool("setAIThinking(true) OK", true);
            card.setAIThinking(false);
            bool("setAIThinking(false) OK", true);
            bool("preferredSize > 0",
                    card.getPreferredSize().width > 0 && card.getPreferredSize().height > 0);
        } catch (Exception e) {
            bool("PlayerCard crash: " + e.getMessage(), false);
        }
    }

    // ─── Helpers ───────────────────────────────────────────

    private static boolean allEmpty(Board b) {
        for (int r = 0; r < b.getSize(); r++)
            for (int c = 0; c < b.getSize(); c++)
                if (!b.isEmpty(r, c)) return false;
        return true;
    }

    static void bool(String desc, boolean cond) {
        if (cond) { System.out.println("  ✅ PASS: " + desc); passed++; }
        else       { System.out.println("  ❌ FAIL: " + desc); failed++; }
    }

    static void header(String title) {
        System.out.println("\n┌─── " + title);
    }

    static void printBanner() {
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║  TicTacToe – Vu Dang Khoa – 24130151              ║");
        System.out.println("║  Logic + GUI Component Test Suite (15 tests)      ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
    }

    static void printSummary() {
        int total = passed + failed;
        System.out.println("\n╔════════════════════════════════════════════════════╗");
        System.out.printf( "║  KẾT QUẢ: %d/%d test | %s%n",
                passed, total,
                failed == 0 ? "✅ TẤT CẢ PASS ✅            ║" : "❌ " + failed + " FAIL          ║");
        System.out.println("╚════════════════════════════════════════════════════╝\n");
    }
}