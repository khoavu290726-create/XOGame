package model;

/**
 * Lớp đại diện cho một người chơi.
 * Lưu tên, ký hiệu, điểm số và loại (người thật hay AI).
 *
 * @author SinhVienCNTT
 */
public class Player {

    private String name;
    private final CellState symbol;  // X hoặc O
    private int wins;
    private int draws;
    private int losses;
    private final boolean isAI;

    public Player(String name, CellState symbol, boolean isAI) {
        if (symbol == CellState.EMPTY) {
            throw new IllegalArgumentException("Ký hiệu người chơi không được là EMPTY!");
        }
        this.name   = name;
        this.symbol = symbol;
        this.isAI   = isAI;
        this.wins   = 0;
        this.draws  = 0;
        this.losses = 0;
    }

    // ==================== Getters / Setters ====================

    public String getName()       { return name;   }
    public CellState getSymbol()  { return symbol; }
    public boolean isAI()         { return isAI;   }
    public int getWins()          { return wins;   }
    public int getDraws()         { return draws;  }
    public int getLosses()        { return losses; }

    public void setName(String name) { this.name = name; }

    // ==================== Thống kê ====================

    public void addWin()    { wins++;   }
    public void addDraw()   { draws++;  }
    public void addLoss()   { losses++; }

    /** Tổng số ván đã chơi */
    public int getTotalGames() { return wins + draws + losses; }

    /** Tỉ lệ thắng (%) */
    public double getWinRate() {
        if (getTotalGames() == 0) return 0.0;
        return (double) wins / getTotalGames() * 100.0;
    }

    /** Reset toàn bộ thống kê */
    public void resetStats() {
        wins   = 0;
        draws  = 0;
        losses = 0;
    }

    @Override
    public String toString() {
        return name + " [" + symbol + "]" + (isAI ? " (AI)" : "");
    }
}