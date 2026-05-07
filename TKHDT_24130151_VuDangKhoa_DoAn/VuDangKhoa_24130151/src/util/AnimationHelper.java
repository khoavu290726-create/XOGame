package util;

import config.AppConfig;
import javax.swing.Timer;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

/**
 * Utility class hỗ trợ tạo animation trong Swing.
 * Dùng javax.swing.Timer để chạy trên EDT (an toàn với Swing).
 *
 * @author SinhVienCNTT
 */
public final class AnimationHelper {

    private AnimationHelper() {} // Utility class

    /**
     * Tạo animation fade-in (0.0 → 1.0) trong khoảng duration ms.
     *
     * @param duration   Thời gian (ms)
     * @param onProgress Callback nhận giá trị t ∈ [0.0, 1.0]
     * @param onFinish   Callback khi hoàn tất (có thể null)
     * @return Timer đã start (có thể dừng thủ công nếu cần)
     */
    public static Timer fadeIn(int duration, DoubleConsumer onProgress, Runnable onFinish) {
        int[] elapsed = {0};
        Timer timer = new Timer(AppConfig.ANIM_INTERVAL_MS, null);
        timer.addActionListener(e -> {
            elapsed[0] += AppConfig.ANIM_INTERVAL_MS;
            double t = Math.min(1.0, (double) elapsed[0] / duration);
            onProgress.accept(easeOut(t));
            if (t >= 1.0) {
                timer.stop();
                if (onFinish != null) onFinish.run();
            }
        });
        timer.start();
        return timer;
    }

    /**
     * Tạo hiệu ứng blink (nhấp nháy) với chu kỳ nhất định.
     *
     * @param periodMs Chu kỳ ms
     * @param callback Nhận true/false xen kẽ
     * @return Timer (cần stop thủ công)
     */
    public static Timer blink(int periodMs, Consumer<Boolean> callback) {
        boolean[] state = {true};
        Timer timer = new Timer(periodMs, e -> {
            state[0] = !state[0];
            callback.accept(state[0]);
        });
        timer.start();
        return timer;
    }

    /**
     * Hàm easing: ease-out cubic → chuyển động nhanh lúc đầu, chậm lúc cuối.
     */
    public static double easeOut(double t) {
        return 1 - Math.pow(1 - t, 3);
    }

    /**
     * Hàm easing: ease-in-out smooth.
     */
    public static double smoothStep(double t) {
        return t * t * (3 - 2 * t);
    }
}