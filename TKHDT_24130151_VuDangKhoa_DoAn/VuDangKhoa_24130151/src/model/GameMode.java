package model;

/**
 * Enum biểu diễn chế độ chơi.
 *
 * @author SinhVienCNTT
 */
public enum GameMode {

    TWO_PLAYER("2 Người chơi", "👥"),
    VS_AI("Người vs Máy",      "🤖");

    private final String label;
    private final String icon;

    GameMode(String label, String icon) {
        this.label = label;
        this.icon  = icon;
    }

    public String getLabel() { return label; }
    public String getIcon()  { return icon;  }

    /** Trả về chế độ kế tiếp (toggle) */
    public GameMode toggle() {
        return this == TWO_PLAYER ? VS_AI : TWO_PLAYER;
    }

    @Override
    public String toString() { return icon + " " + label; }
}