package ai;

import config.AppConfig;
import model.Board;
import model.CellState;
import model.Move;

import javax.swing.SwingUtilities;
import java.util.function.Consumer;

/**
 * Lớp quản lý AI Player.
 * Bọc AIStrategy và chạy việc tính toán trên luồng riêng
 * để không block giao diện Swing (EDT).
 *
 * Dùng Consumer callback để trả kết quả về controller sau khi tính xong.
 *
 * @author SinhVienCNTT
 */
public class AIPlayer {

    private AIStrategy strategy;
    private final CellState aiSymbol;
    private boolean isThinking; // AI đang tính toán không?

    public AIPlayer(CellState aiSymbol, AIStrategy strategy) {
        this.aiSymbol  = aiSymbol;
        this.strategy  = strategy;
        this.isThinking = false;
    }

    /**
     * Bắt đầu tính nước đi AI trên luồng mới.
     * Sau khi tính xong, callback được gọi trên EDT.
     *
     * @param board    Bàn cờ hiện tại (clone nếu cần thread-safe)
     * @param callback Hàm nhận kết quả Move sau khi AI tính xong
     */
    public void thinkAsync(Board board, Consumer<Move> callback) {
        if (isThinking) return; // Tránh gọi 2 lần
        isThinking = true;

        new Thread(() -> {
            try {
                // Delay nhỏ để UI kịp cập nhật
                Thread.sleep(AppConfig.AI_DELAY_MS);
                Move bestMove = strategy.findBestMove(board, aiSymbol);

                // Trả kết quả về EDT
                SwingUtilities.invokeLater(() -> {
                    isThinking = false;
                    callback.accept(bestMove);
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                isThinking = false;
            }
        }, "AI-Think-Thread").start();
    }

    public CellState getAiSymbol()    { return aiSymbol;  }
    public boolean isThinking()       { return isThinking; }
    public AIStrategy getStrategy()   { return strategy;   }

    public void setStrategy(AIStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Đổi sang chiến lược khác (Easy/Hard).
     */
    public void useHardMode() { strategy = new MinimaxStrategy(); }
    public void useEasyMode() { strategy = new RandomStrategy();  }

    @Override
    public String toString() {
        return "AI [" + aiSymbol + "] dùng: " + strategy.getStrategyName();
    }
}