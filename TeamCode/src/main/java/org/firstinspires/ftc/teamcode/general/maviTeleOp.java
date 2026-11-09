package org.firstinspires.ftc.teamcode.general;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.general.mechanisms.AprilTagWebcam;
import org.firstinspires.ftc.teamcode.general.mechanisms.Flywheel;
import org.firstinspires.ftc.teamcode.general.mechanisms.IntakeAndTransfer;
import org.firstinspires.ftc.teamcode.general.mechanisms.LiftAndGrip;
import org.firstinspires.ftc.teamcode.general.mechanisms.MecanumDrive;
import org.firstinspires.ftc.teamcode.general.mechanisms.Odometry;
import org.firstinspires.ftc.teamcode.general.mechanisms.Turret;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;

@TeleOp
public class maviTeleOp extends OpMode {

    AprilTagWebcam aprilTagWebcam = new AprilTagWebcam(); // webcam i mekanizmalardan çekme satırı
    MecanumDrive drive = new MecanumDrive(); // mekanum u mekanizmalardan çekme satırı
    private Turret turret; // turret i mekanizmalardan çekme satırı
    private Odometry odometry; // odometri yi mekanizmalardan çekme satırı
    private IntakeAndTransfer intakeAndTransfer; // intake ve transfer i mekanizmalardan çekme satırı
    private Flywheel updateFlywheel; // flywheel ı mekanizmalardan çekme satırı
    private LiftAndGrip liftAndGrip;

    // Joystick değişkenleri
    double forward, strafe, rotate; // mekanum değişkenlerini importlama
    boolean isEmergencyStopped = false; // TURRET için acil durdurma kontrol değişkeni
    int stateLift = 0;
    boolean flowerRamp = false;

    @Override
    public void init() {
        drive.init(hardwareMap); // mekanum motor verileri verme
        aprilTagWebcam.init(hardwareMap, telemetry); // webcam başlangıç ayarı verme
        telemetry.addData("Durum", "Başlatmaya Hazır"); // telemetri bilgisi verme
        telemetry.update(); // telemetri güncelleme komutu

        turret = new Turret(hardwareMap); // TURRET motor verileri verme
        odometry = new Odometry(hardwareMap); // odometri verileri verme
        //intake ve transfer çağırımı
        intakeAndTransfer = new IntakeAndTransfer(hardwareMap); // intake ve transfer motor verileri verme
        updateFlywheel = new Flywheel(hardwareMap); // FLYWHEEL motor verileri verme
        liftAndGrip = new LiftAndGrip(hardwareMap);

        // Kırmızı ittifak başlangıç pozisyonunu buraya girebilirsin (Örn: X=15, Y=60, Açı=0)
        // odometry.setStartingPose(15.0, 60.0, 0.0);

        telemetry.addData("Durum", "Başlatmaya Hazır ve Turret Aktif");
        telemetry.update(); // telemetri güncelleme komutu
    }

    @Override
    public void loop() {
        // Sürüş kontrolü
        forward = -gamepad1.right_stick_y; // mekanum ileri-geri verisi verme
        strafe = gamepad1.right_stick_x; // mekanum yanal (sağ-sol) verisi verme
        rotate = gamepad1.left_stick_x; // mekanum dönüş veris verme
        drive.drive(forward, strafe, rotate); // mekanuma verileri bildirme (hareket)

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

        if (gamepad1.startWasReleased()) {
            odometry.setStartingPose(7.87, 7.65, 0.0);
        }

        if (gamepad1.left_bumper){
            flowerRamp = true;
        } else {
            flowerRamp = false;
        }

        // INTAKE in ne zaman çalışacağını ve transferin ne zaman çalışacağını komutlama
        intakeAndTransfer.intakeAndTransfer(forward > 0 || gamepad1.left_trigger > 0, gamepad1.right_bumper, gamepad1.left_bumper);

        // FLYWHEEL tuş ataması
        if (gamepad1.right_trigger > 0) {
            // FLYWHEEL robot konumu ve ittifak rengi verisi verme
            updateFlywheel.updateFlywheel(robotX, robotY, Flywheel.Alliance.BLUE);
        }
        else {
            updateFlywheel.stop();
        }

        // TURRET robot konumu ve ittifak rengi verisi verme
        turret.updateAim(robotX, robotY, robotHeading, Turret.Alliance.BLUE);

        // TURRET acil konum kitleme tuş ve basılmışlık kontrolü ataması
        if (gamepad1.bWasReleased()) {
            isEmergencyStopped = !isEmergencyStopped;
        }
        // TURRET emergency stop durumunda konumu kontrolü
        if (isEmergencyStopped) {
            // TURRET acil stop komutu ve telemetri verisi
            turret.emergencyStop();
            telemetry.addData("TURRET KİLİTLENDİ:","Acil DURUM, turret bir nedenle kilitlendi","");
        }

        //LIFT için veri göndferimi
        if (gamepad1.aWasReleased()){ // a ya basılmış mı kontrolü
            if (!flowerRamp){
                stateLift += 1; // state i değiştirme
                liftAndGrip.setLiftTargetPosition(stateLift); // state i aktarma
            }

            if (stateLift >= 5){ // state i maksimum state e kontrol etme
                stateLift = 0; // maksimum state olduğunda sıfırlama
            }
        }

        if (gamepad1.xWasReleased()){
            stateLift = 0;
            liftAndGrip.setLiftTargetPosition(stateLift); // state i aktarma
        }

        // telemetri verileri
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