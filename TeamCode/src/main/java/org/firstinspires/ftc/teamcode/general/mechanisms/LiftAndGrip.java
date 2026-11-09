package org.firstinspires.ftc.teamcode.general.mechanisms;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class LiftAndGrip {
    private DcMotor liftMotor;
    private Servo gripper;
    private Servo rotateGripper;
    private HuskyLens huskylens;
    public static final int LIFT_MAX_HEIGHT = 2500; // Encoder tık sayısı
    public static final int LIFT_MIN_HEIGHT = 0;

    public static final double GRIPPER_OPEN = 0.1;
    public static final double GRIPPER_POLLEN = 0.4;
    public static final double GRIPPER_NECTAR = 0.3;
    public static final double GRIPPER_ROTATE_WAIT = 0.4;
    public static final double GRIPPER_ROTATE_GRIP = 0;

    public LiftAndGrip(HardwareMap hardwareMap) {
        // --- 1. LİFT MOTORU TANIMLAMALARI ---
        liftMotor = hardwareMap.get(DcMotor.class, "lift_motor");
        liftMotor.setDirection(DcMotor.Direction.FORWARD); // Gerekirse REVERSE yapılabilir
        liftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE); // Havada asılı kalması için BRAKE şart!

        // Encoder'ı sıfırla
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // --- 2. GRİP (KISKAÇ) SERVȎLARI TANIMLAMALARI ---
        gripper = hardwareMap.get(Servo.class, "gripper");
        rotateGripper = hardwareMap.get(Servo.class, "gripper_rotate");

        huskylens = hardwareMap.get(HuskyLens.class, "huskylens");
        huskylens.selectAlgorithm(HuskyLens.Algorithm.COLOR_RECOGNITION);

        // Genellikle çift servolu kıskaçlarda biri ters yöne dönmelidir ki simetrik sıksınlar:
        //rotateGripper.setDirection(Servo.Direction.REVERSE);
        //gripper.setDirection(Servo.Direction.REVERSE);
    }

    // --- LİFT KONTROLÜ ---
    public void setLiftPower(double power) {
        // Güvenlik sınırları (en üst veya en alttayken motoru zorlamamak için eklenebilir)
        liftMotor.setPower(power);
    }

    public void setLiftTargetPosition(int state) {
        liftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftMotor.setPower(0.8);

        HuskyLens.Block[] blocks = huskylens.blocks();
        int detectedID = 0;
        if (blocks.length > 0){
            int screenCenterX = 320 / 2; // HuskyLens yatay çözünürlük merkezi (genelde 160)
            int closestBlockID = blocks[0].id;
            int minDistance = Math.abs(blocks[0].x - screenCenterX);

            // Görüş alanındaki tüm blokları tara, merkeze en yakın olanı seç
            for (int i = 1; i < blocks.length; i++) {
                int distance = Math.abs(blocks[i].x - screenCenterX);
                if (distance < minDistance) {
                    minDistance = distance;
                    closestBlockID = blocks[i].id; // Merkeze en yakın olanın ID'si
                }
            }

            // Bulunan en yakın nesne ID'sine göre pozisyonu ayarla
            detectedID = closestBlockID;
        }

        if (state == 0){ // kapalı bekleme pozisyonu
            liftMotor.setTargetPosition(LIFT_MIN_HEIGHT);
            gripper.setPosition(GRIPPER_OPEN);
            rotateGripper.setPosition(GRIPPER_ROTATE_WAIT);
        }
        else if (state == 1){ // açık bekleme pozisyonu
            liftMotor.setTargetPosition(LIFT_MIN_HEIGHT);
            gripper.setPosition(GRIPPER_OPEN);
            rotateGripper.setPosition(GRIPPER_ROTATE_GRIP);
        }
        else if (state == 2){ // açık NEKTAR kıstırma
            liftMotor.setTargetPosition(LIFT_MIN_HEIGHT);
            rotateGripper.setPosition(GRIPPER_ROTATE_GRIP);
            if (detectedID == 1){
                gripper.setPosition(GRIPPER_POLLEN);
            }
            else if (detectedID == 2 || detectedID == 3){
                gripper.setPosition(GRIPPER_NECTAR);
            }

        }
        else if (state == 3){// yukarıya taşıma
            liftMotor.setTargetPosition(LIFT_MAX_HEIGHT);
        }
        else if (state == 4){// yukarıa topu bırakma
            liftMotor.setTargetPosition(LIFT_MAX_HEIGHT);
            gripper.setPosition(GRIPPER_OPEN);
            rotateGripper.setPosition(GRIPPER_ROTATE_GRIP);
        }
        else {// eğer hiçbiri olmazsa
            liftMotor.setTargetPosition(LIFT_MIN_HEIGHT);
            gripper.setPosition(GRIPPER_OPEN);
            rotateGripper.setPosition(GRIPPER_ROTATE_WAIT);
        }

    }

    // --- KISKAÇ (GRİP) KONTROLÜ ---
    // Açık / Kapalı pozisyonları (0.0 ile 1.0 arası)
}
