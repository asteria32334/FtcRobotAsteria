package org.firstinspires.ftc.teamcode.general.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Turret {
    private DcMotor turretMotor;

    private static final double TICKS_PER_DEGREE = 1.2133; // Dişli oranınız
    private static final double MIN_ANGLE = -90.0; // saat yönünün tersi -
    private static final double MAX_ANGLE = 90.0; // saat yönü +

    public enum Alliance {
        RED,
        BLUE
    }

    // --- SAHA HEDEF KOORDİNATLARI (İNÇ CİNSİNDEN) ---
    // Sahanın ortasındaki Y sınırı: 70.75 inç
    private static final double Y_THRESHOLD = 70.75;

    // Kırmızı İttifak için 2 Hedef
    private static final double RED_TARGET_1_X = 57.75;
    private static final double RED_TARGET_1_Y = 56.75; // Örn: 70.75'in altındaki hedef (kendi yarısı)

    private static final double RED_TARGET_2_X = 57.75;
    private static final double RED_TARGET_2_Y = 84.50; // Örn: 70.75'in üstündeki hedef (rakip yarısı)

    // Mavi İttifak için 2 Hedef
    private static final double BLUE_TARGET_1_X = 84.2;
    private static final double BLUE_TARGET_1_Y = 56.65; // Örn: Mavi tarafın kendi yarısındaki hedef

    private static final double BLUE_TARGET_2_X = 84.2;
    private static final double BLUE_TARGET_2_Y = 84.95; // Örn: Mavi tarafın rakip yarısındaki hedef

    public Turret(HardwareMap hardwareMap) {
        turretMotor = hardwareMap.get(DcMotor.class, "turret_motor");
        turretMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turretMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        turretMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    /**
     * Güncellenmiş Nişan Alma Metodu:
     * Robotun X ve Y konumuna bakar, Y 70.75'i geçti mi diye kontrol eder,
     * ittifak rengine göre doğru hedefi seçip kilitlenir.
     */
    public void updateAim(double robotX, double robotY, double robotHeadingDeg, Alliance alliance) {
        double targetX;
        double targetY;

        // Y = 76.75 sınırına göre hangi hedefe nişan alacağını seçiyoruz
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

        // Trigonometrik Açı Hesabı
        double deltaX = targetX - robotX;
        double deltaY = targetY - robotY;
        double globalAngleToTarget = Math.toDegrees(Math.atan2(deltaY, deltaX));

        double turretTargetAngle = globalAngleToTarget - robotHeadingDeg;
        turretTargetAngle = normalizeAngle(turretTargetAngle);

        // --- GÜVENLİK VE 0'A DÖNÜP TAKİBE DEVAM ETME MANTIĞI ---
        double targetTicks;

        // Eğer hesaplanan açı sınırların dışındaysa -> 0'a dön!
        if (turretTargetAngle < MIN_ANGLE || turretTargetAngle > MAX_ANGLE) {
            targetTicks = 0; // Güvenli merkez konumu (0 tick)
        } else {
            // Sınırlar içindeyse normal takibe devam et ve hedef tick'e git
            targetTicks = turretTargetAngle * TICKS_PER_DEGREE;
        }

        // Motora komutu gönder
        turretMotor.setTargetPosition((int) targetTicks);
        turretMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turretMotor.setPower(0.5);
    }

    public void setPower(double power) {
        turretMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        turretMotor.setPower(power);
    }

    private double normalizeAngle(double angle) {
        while (angle > 180.0) angle -= 360.0;
        while (angle < -180.0) angle += 360.0;
        return angle;
    }

    public int getCurrentPosition() {
        return turretMotor.getCurrentPosition();
    }
}
