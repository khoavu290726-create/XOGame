package config;

import java.awt.Color;
import java.awt.Font;

/**
 * Lớp chứa các hằng số cấu hình toàn cục cho ứng dụng.
 * Theo nguyên tắc DRY (Don't Repeat Yourself) - tập trung mọi cấu hình
 * vào một nơi để dễ bảo trì và thay đổi sau này.
 *
 * @author SinhVienCNTT
 * @version 1.0
 */
public final class AppConfig {

    // ==================== THÔNG TIN ỨNG DỤNG ====================
    public static final String APP_TITLE   = "Caro 3x3 – TicTacToe";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_AUTHOR  = "Sinh Vien CNTT";

    // ==================== KÍCH THƯỚC ====================
    public static final int BOARD_SIZE  = 3;    // Bàn cờ 3x3
    public static final int CELL_SIZE   = 150;  // Pixel mỗi ô
    public static final int CELL_PAD    = 18;   // Padding bàn cờ
    public static final int CELL_ARC    = 16;   // Bo góc ô
    public static final int SYMBOL_MARGIN = 30; // Lề vẽ X/O

    // ==================== THỜI GIAN (ms) ====================
    public static final int AI_DELAY_MS      = 450;  // Delay trước khi AI đi
    public static final int RESULT_DELAY_MS  = 700;  // Delay trước khi hiện dialog
    public static final int ANIM_INTERVAL_MS = 16;   // ~60fps

    // ==================== MÀU SẮC – DARK THEME ====================
    public static final Color COLOR_BG_MAIN     = new Color(13, 15, 24);
    public static final Color COLOR_BG_CARD     = new Color(20, 23, 38);
    public static final Color COLOR_BG_HOVER    = new Color(35, 42, 70);
    public static final Color COLOR_GRID        = new Color(50, 60, 95);
    public static final Color COLOR_ACTIVE_BG   = new Color(30, 40, 70);

    public static final Color COLOR_X          = new Color(255, 95, 95);
    public static final Color COLOR_O          = new Color(70, 195, 255);
    public static final Color COLOR_X_GLOW     = new Color(255, 95, 95, 55);
    public static final Color COLOR_O_GLOW     = new Color(70, 195, 255, 55);

    public static final Color COLOR_WIN_LINE   = new Color(255, 215, 0);
    public static final Color COLOR_WIN_BG     = new Color(255, 215, 0, 28);
    public static final Color COLOR_SHADOW     = new Color(0, 0, 0, 90);

    public static final Color COLOR_TEXT_MAIN  = new Color(215, 220, 240);
    public static final Color COLOR_TEXT_DIM   = new Color(110, 125, 160);
    public static final Color COLOR_TEXT_MUTED = new Color(70, 80, 110);

    // ==================== FONT ====================
    public static final Font FONT_TITLE   = new Font("SansSerif", Font.BOLD, 22);
    public static final Font FONT_HEADING = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_BODY    = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONT_SCORE   = new Font("SansSerif", Font.BOLD, 30);
    public static final Font FONT_SYMBOL  = new Font("SansSerif", Font.BOLD, 36);
    public static final Font FONT_STATUS  = new Font("SansSerif", Font.BOLD, 15);

    // ==================== NÚT BẤM ====================
    public static final Color BTN_NEW_GAME = new Color(55, 125, 255);
    public static final Color BTN_RESET    = new Color(210, 75, 75);
    public static final Color BTN_MODE     = new Color(50, 60, 100);
    public static final Color BTN_ABOUT    = new Color(60, 70, 110);
    public static final int   BTN_WIDTH    = 140;
    public static final int   BTN_HEIGHT   = 40;
    public static final int   BTN_ARC      = 14;

    // Ngăn không cho khởi tạo (Utility class)
    private AppConfig() {
        throw new UnsupportedOperationException("Đây là utility class, không khởi tạo!");
    }
}