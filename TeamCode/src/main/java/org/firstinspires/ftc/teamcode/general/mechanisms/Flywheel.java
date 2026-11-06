package org.firstinspires.ftc.teamcode.general.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Flywheel {
    private DcMotorEx flywheelMotor;

    // REV HD Hex motor max RPM değeri
    private static final double MAX_RPM = 6000.0;

    // --- TURRET İLE AYNI SAHA HEDEF KOORDİNATLARI (İNÇ CİNSİNDEN) ---
    private static final double Y_THRESHOLD = 70.75;

    public enum Alliance {
        RED,
        BLUE
    }

    // Kırmızı İttifak için 2 Hedef
    private static final double RED_TARGET_1_X = 57.75;
    private static final double RED_TARGET_1_Y = 56.75;
    private static final double RED_TARGET_2_X = 57.75;
    private static final double RED_TARGET_2_Y = 84.50;

    // Mavi İttifak için 2 Hedef
    private static final double BLUE_TARGET_1_X = 84.2;
    private static final double BLUE_TARGET_1_Y = 56.65;
    private static final double BLUE_TARGET_2_X = 84.2;
    private static final double BLUE_TARGET_2_Y = 84.95;

    public Flywheel(HardwareMap hardwareMap) {
        flywheelMotor = hardwareMap.get(DcMotorEx.class, "flywheel");
        flywheelMotor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
        flywheelMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        flywheelMotor.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.FLOAT);
    }

    /**
     * Turret ile birebir aynı mantıkla: İttifaka ve Y sınırına (70.75) göre
     * doğru hedefi seçer, robotun o hedefe olan mesafesini hesaplar ve uygun RPM'i ayarlar.
     */
    public void updateFlywheel(double robotX, double robotY, Alliance alliance) {
        double targetX;
        double targetY;

        // Turret mantığıyla aynı hedef seçimi
        if (alliance == Alliance.RED) {
            if (robotY < Y_THRESHOLD) {
                targetX = RED_TARGET_1_X;
                targetY = RED_TARGET_1_Y;
            } else {
                targetX = RED_TARGET_2_X;
                targetY = RED_TARGET_2_Y;
            }
        } else { // BLUE
            if (robotY < Y_THRESHOLD) {
                targetX = BLUE_TARGET_1_X;
                targetY = BLUE_TARGET_1_Y;
            } else {
                targetX = BLUE_TARGET_2_X;
                targetY = BLUE_TARGET_2_Y;
            }
        }

        // Pisagor teoremi ile hedefe olan mesafe (inç)
        double deltaX = targetX - robotX;
        double deltaY = targetY - robotY;
        double distanceInches = Math.hypot(deltaX, deltaY);

        double targetRpm;

        // Mesafeye göre hedef RPM tablosu (Saha testlerine göre ayarlayabilirsin)
        if (distanceInches < 40.0) {
            targetRpm = 2500.0;
        } else if (distanceInches < 80.0) {
            targetRpm = 4000.0;
        } else {
            targetRpm = 5500.0;
        }

        // RPM'i motor gücüne (0.0 - 1.0) dönüştürme
        double powerCommand = targetRpm / MAX_RPM;
        powerCommand = Math.max(0.0, Math.min(1.0, powerCommand));

        flywheelMotor.setPower(powerCommand);
    }

    public void stop() {
        flywheelMotor.setPower(0.0);
    }

    public double getCurrentRpm() {
        return flywheelMotor.getVelocity();
    }
}
