package controller;

import model.GameResult;
import model.Move;
import model.Player;

/**
 * Interface Observer — lắng nghe sự kiện từ GameController.
 * Các View (Panel) implement interface này để tự cập nhật.
 *
 * Đây là Observer Pattern trong MVC:
 * Model thay đổi → Controller thông báo → View cập nhật.
 *
 * @author SinhVienCNTT
 */
public interface GameEventListener {

    /** Gọi sau mỗi nước đi thành công */
    void onMoveMade(Move move);

    /** Gọi khi game kết thúc (thắng / hòa) */
    void onGameOver(GameResult result, Player winner);

    /** Gọi khi bắt đầu ván mới */
    void onNewGame();

    /** Gọi khi đến lượt người chơi khác */
    void onTurnChanged(Player currentPlayer);

    /** Gọi khi AI đang suy nghĩ */
    void onAIThinking(boolean isThinking);
}