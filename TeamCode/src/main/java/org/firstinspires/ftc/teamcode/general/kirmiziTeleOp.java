package org.firstinspires.ftc.teamcode.general;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.general.mechanisms.AprilTagWebcam;
import org.firstinspires.ftc.teamcode.general.mechanisms.MecanumDrive;
import org.firstinspires.ftc.teamcode.general.mechanisms.Odometry;
import org.firstinspires.ftc.teamcode.general.mechanisms.Turret;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@TeleOp
public class kirmiziTeleOp extends OpMode {

    AprilTagWebcam aprilTagWebcam = new AprilTagWebcam();
    MecanumDrive drive = new MecanumDrive();
    private Turret turret;
    private Odometry odometry;

    // Joystick değişkenleri
    double forward, strafe, rotate;

    @Override
    public void init() {
        drive.init(hardwareMap);
        aprilTagWebcam.init(hardwareMap, telemetry);
        telemetry.addData("Durum", "Başlatmaya Hazır");
        telemetry.update();

        turret = new Turret(hardwareMap);
        odometry = new Odometry(hardwareMap);

        // Kırmızı ittifak başlangıç pozisyonunu buraya girebilirsin (Örn: X=15, Y=60, Açı=0)
        // odometry.setStartingPose(15.0, 60.0, 0.0);

        telemetry.addData("Durum", "Başlatmaya Hazır ve Turret Aktif");
        telemetry.update();
    }

    @Override
    public void loop() {
        // Sürüş kontrolü
        forward = -gamepad1.right_stick_y;
        strafe = gamepad1.right_stick_x;
        rotate = gamepad1.left_stick_x;
        drive.drive(forward, strafe, rotate);

        // Kamera güncellemesi
        aprilTagWebcam.update();

        // Belirli bir ID'yi (Örn: ID 20) arama
        int hedefTagID = 20;
        AprilTagDetection arananTag = aprilTagWebcam.getTagBySpecificId(hedefTagID);

        // Güvenli kontrol ve Telemetry yazdırma
        if (arananTag != null) {
            telemetry.addData("Durum", "Tag %d Görüş Alanında!", hedefTagID);
            // Mekanizma sınıfımızdaki detaylı telemetry metodunu çağırıyoruz
            aprilTagWebcam.displayDetectionTelemetry(arananTag);
        } else {
            telemetry.addData("Durum", "ID %d Aranıyor...", hedefTagID);
        }

        // TURRET
        // ODOMETRİ
        odometry.update();
        double robotX = odometry.getX();
        double robotY = odometry.getY();
        double robotHeading = odometry.getHeading();

        turret.updateAim(robotX, robotY, robotHeading, Turret.Alliance.RED);

        if (gamepad1.a) {
            odometry.setStartingPose(15.0, 60.0, 0.0);
        }

        telemetry.addData("Odometry X", "%.2f", robotX);
        telemetry.addData("Odometry Y", "%.2f", robotY);
        telemetry.addData("Heading", "%.2f", robotHeading);
        telemetry.addData("Turret Tick", turret.getCurrentPosition());
        telemetry.update();
    }

    @Override
    public void stop() {
        aprilTagWebcam.stop();
        super.stop();
    }
}