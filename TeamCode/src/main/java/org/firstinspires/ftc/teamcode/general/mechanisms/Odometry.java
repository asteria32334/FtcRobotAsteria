package org.firstinspires.ftc.teamcode.general.mechanisms;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Odometry {
    private GoBildaPinpointDriver pinpoint;

    public Odometry(HardwareMap hardwareMap) {
        // HardwareMap'ten konfigürasyondaki adıyla çağırıyoruz (örn: "pinpoint")
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        // 4-Bar pod kullanıyorsan ayarları (kendi donanımına göre yönleri ayarlayabilirsin)
        pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);

        // POD ların robotun merkezine göre konumu
        double xOffsetMM = -180;   // Örn: Merkezden 15 mm öndeyse (+), arkadaysa (-)
        double yOffsetMM = -40;  // Örn: Merkezden 25 mm sağdaysa (-), soldaysa (+)

        pinpoint.setOffsets(xOffsetMM, yOffsetMM, DistanceUnit.MM);

        // Başlangıçta pozisyonu sıfırla
        pinpoint.resetPosAndIMU();
    }

    // Her döngüde (loop) çağrılarak verilerin güncellenmesini sağlar
    public void update() {
        pinpoint.update();
    }

    // Başlangıç konumunu (X, Y ve Açı) dışarıdan manuel set etmek isterseniz (TeleOp başı için)
    public void setStartingPose(double startX, double startY, double startHeadingDeg) {
        // Pinpoint'in pozisyonunu ayarlama fonksiyonu
        pinpoint.setPosition(new org.firstinspires.ftc.robotcore.external.navigation.Pose2D(
                DistanceUnit.INCH, startX, startY, AngleUnit.DEGREES, startHeadingDeg
        ));
    }

    public double getX() {
        return pinpoint.getPosition().getX(DistanceUnit.INCH);
    }

    public double getY() {
        return pinpoint.getPosition().getY(DistanceUnit.INCH);
    }

    public double getHeading() {
        return pinpoint.getPosition().getHeading(AngleUnit.DEGREES);
    }
}
