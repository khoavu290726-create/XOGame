package util;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Utility class chứa các hàm vẽ tái sử dụng.
 * Tập trung logic vẽ phức tạp để các Panel không bị rối.
 *
 * @author SinhVienCNTT
 */
public final class DrawUtil {

    private DrawUtil() {} // Utility class

    /**
     * Bật tất cả các hint rendering chất lượng cao.
     */
    public static void enableAntiAlias(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,     RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,        RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,   RenderingHints.VALUE_STROKE_PURE);
    }

    /**
     * Vẽ chữ X tại tâm (cx, cy) với bán kính margin.
     * Vẽ glow trước rồi đường chính.
     */
    public static void drawX(Graphics2D g2, int cx, int cy, int margin, Color color, float alpha) {
        Composite oldComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // Glow ngoài
        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 55));
        g2.setStroke(new BasicStroke(17f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx - margin, cy - margin, cx + margin, cy + margin);
        g2.drawLine(cx + margin, cy - margin, cx - margin, cy + margin);

        // Đường chính
        g2.setColor(color);
        g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine(cx - margin, cy - margin, cx + margin, cy + margin);
        g2.drawLine(cx + margin, cy - margin, cx - margin, cy + margin);

        g2.setComposite(oldComposite);
    }

    /**
     * Vẽ chữ O tại tâm (cx, cy) với bán kính margin.
     */
    public static void drawO(Graphics2D g2, int cx, int cy, int margin, Color color, float alpha) {
        Composite oldComposite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        // Glow ngoài
        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 55));
        g2.setStroke(new BasicStroke(17f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawOval(cx - margin, cy - margin, margin * 2, margin * 2);

        // Đường chính
        g2.setColor(color);
        g2.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawOval(cx - margin, cy - margin, margin * 2, margin * 2);

        g2.setComposite(oldComposite);
    }

    /**
     * Vẽ nền rounded rectangle với màu gradient hoặc solid.
     */
    public static void fillRoundRect(Graphics2D g2, int x, int y, int w, int h, int arc, Color color) {
        g2.setColor(color);
        g2.fillRoundRect(x, y, w, h, arc, arc);
    }

    /**
     * Vẽ viền rounded rectangle.
     */
    public static void drawRoundRect(Graphics2D g2, int x, int y, int w, int h, int arc,
                                     Color color, float strokeWidth) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(strokeWidth));
        g2.drawRoundRect(x, y, w, h, arc, arc);
    }

    /**
     * Vẽ text căn giữa theo chiều ngang trong vùng [x, x+width].
     */
    public static void drawCenteredString(Graphics2D g2, String text,
                                          int x, int y, int width, Font font, Color color) {
        g2.setFont(font);
        g2.setColor(color);
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        g2.drawString(text, textX, y);
    }

    /**
     * Áp dụng alpha lên màu (giữ nguyên RGB, đổi alpha).
     */
    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(),
                         Math.max(0, Math.min(255, alpha)));
    }
}