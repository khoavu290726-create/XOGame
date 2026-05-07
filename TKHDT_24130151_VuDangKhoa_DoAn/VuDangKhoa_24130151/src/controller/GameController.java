package controller;

import ai.AIPlayer;
import ai.MinimaxStrategy;
import exception.InvalidMoveException;
import model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller trung tâm điều phối toàn bộ luồng game.
 * Áp dụng MVC Pattern: kết nối Model (Board, Player) với View (Panel).
 *
 * Trách nhiệm:
 * - Quản lý trạng thái game (đang chơi / kết thúc)
 * - Xử lý nước đi của người và AI
 * - Cập nhật thống kê người chơi
 * - Ghi lịch sử ván chơi
 * - Thông báo đến View qua Observer
 *
 * @author SinhVienCNTT
 */
public class GameController {

    // === Model ===
    private final Board  board;
    private Player playerX;
    private Player playerO;
    private Player currentPlayer;
    private GameResult gameResult;
    private GameMode gameMode;

    // === AI ===
    private AIPlayer aiPlayer;

    // === Lịch sử ===
    private final List<GameSession> sessionHistory;
    private int totalGames;

    // === Observer ===
    private final List<GameEventListener> listeners;

    // ==================== KHỞI TẠO ====================

    public GameController() {
        board          = new Board();
        listeners      = new ArrayList<>();
        sessionHistory = new ArrayList<>();
        totalGames     = 0;
        gameMode       = GameMode.TWO_PLAYER;

        initPlayers(GameMode.TWO_PLAYER);
    }

    /**
     * Khởi tạo / reset người chơi theo chế độ game.
     */
    public void initPlayers(GameMode mode) {
        this.gameMode = mode;

        if (mode == GameMode.TWO_PLAYER) {
            playerX = new Player("Người chơi 1", CellState.X, false);
            playerO = new Player("Người chơi 2", CellState.O, false);
            aiPlayer = null;
        } else {
            playerX = new Player("Bạn", CellState.X, false);
            playerO = new Player("Máy",  CellState.O, true);
            aiPlayer = new AIPlayer(CellState.O, new MinimaxStrategy());
        }
        startNewGame();
    }

    // ==================== LUỒNG GAME ====================

    /**
     * Bắt đầu ván mới (giữ nguyên điểm số).
     */
    public void startNewGame() {
        board.reset();
        currentPlayer = playerX;   // X luôn đi trước
        gameResult    = GameResult.IN_PROGRESS;
        notifyNewGame();
        notifyTurnChanged();
    }

    /**
     * Xử lý nước đi của người chơi thật (gọi từ BoardPanel khi click).
     *
     * @param row Hàng ô click
     * @param col Cột ô click
     */
    public void handleHumanMove(int row, int col) {
        // Bảo vệ: chỉ cho đi khi game chưa kết thúc và đúng lượt
        if (gameResult.isFinished())          return;
        if (currentPlayer.isAI())             return;
        if (aiPlayer != null && aiPlayer.isThinking()) return;

        Move move = new Move(row, col, currentPlayer.getSymbol());
        try {
            board.makeMove(move);
        } catch (InvalidMoveException e) {
            // Ô đã có quân → bỏ qua, không làm gì
            System.out.println("[Controller] " + e.getMessage());
            return;
        }

        notifyMoveMade(move);
        processAfterMove();
    }

    /**
     * Xử lý nước đi của AI (gọi sau khi AI thinkAsync hoàn tất).
     */
    private void handleAIMove() {
        if (gameResult.isFinished()) return;

        notifyAIThinking(true);
        aiPlayer.thinkAsync(board, bestMove -> {
            notifyAIThinking(false);
            if (bestMove == null || gameResult.isFinished()) return;

            try {
                board.makeMove(bestMove);
                notifyMoveMade(bestMove);
                processAfterMove();
            } catch (InvalidMoveException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Xử lý sau mỗi nước đi: kiểm tra kết thúc, đổi lượt, gọi AI nếu cần.
     */
    private void processAfterMove() {
        gameResult = board.checkResult();

        if (gameResult.isFinished()) {
            handleGameOver();
        } else {
            switchTurn();
            // Nếu lượt tiếp là AI → kích hoạt AI
            if (currentPlayer.isAI()) {
                handleAIMove();
            }
        }
    }

    /**
     * Xử lý khi game kết thúc: cập nhật điểm, lưu lịch sử, thông báo.
     */
    private void handleGameOver() {
        totalGames++;
        Player winner = null;

        if (gameResult == GameResult.X_WIN) {
            winner = playerX;
            playerX.addWin();
            playerO.addLoss();
        } else if (gameResult == GameResult.O_WIN) {
            winner = playerO;
            playerO.addWin();
            playerX.addLoss();
        } else if (gameResult == GameResult.DRAW) {
            playerX.addDraw();
            playerO.addDraw();
        }

        // Lưu lịch sử ván chơi
        GameSession session = new GameSession(
            totalGames, playerX.getName(), playerO.getName(),
            gameResult, board.getMoveCount(), gameMode
        );
        sessionHistory.add(session);
        System.out.println("[History] " + session);

        notifyGameOver(gameResult, winner);
    }

    private void switchTurn() {
        currentPlayer = (currentPlayer == playerX) ? playerO : playerX;
        notifyTurnChanged();
    }

    // ==================== RESET / CONFIG ====================

    /**
     * Reset hoàn toàn: xóa điểm, xóa lịch sử.
     */
    public void fullReset() {
        playerX.resetStats();
        playerO.resetStats();
        sessionHistory.clear();
        totalGames = 0;
        startNewGame();
    }

    /**
     * Đổi chế độ game và bắt đầu lại.
     */
    public void switchMode(GameMode newMode) {
        initPlayers(newMode);
    }

    // ==================== OBSERVER ====================

    public void addListener(GameEventListener l)    { listeners.add(l);    }
    public void removeListener(GameEventListener l) { listeners.remove(l); }

    private void notifyMoveMade(Move move) {
        listeners.forEach(l -> l.onMoveMade(move));
    }
    private void notifyGameOver(GameResult result, Player winner) {
        listeners.forEach(l -> l.onGameOver(result, winner));
    }
    private void notifyNewGame() {
        listeners.forEach(l -> l.onNewGame());
    }
    private void notifyTurnChanged() {
        listeners.forEach(l -> l.onTurnChanged(currentPlayer));
    }
    private void notifyAIThinking(boolean thinking) {
        listeners.forEach(l -> l.onAIThinking(thinking));
    }

    // ==================== GETTERS ====================

    public Board  getBoard()         { return board;         }
    public Player getPlayerX()       { return playerX;       }
    public Player getPlayerO()       { return playerO;       }
    public Player getCurrentPlayer() { return currentPlayer; }
    public GameResult getGameResult(){ return gameResult;    }
    public GameMode getGameMode()    { return gameMode;      }
    public int getTotalGames()       { return totalGames;    }
    public List<GameSession> getSessionHistory() { return sessionHistory; }
    public AIPlayer getAiPlayer()    { return aiPlayer;      }
}