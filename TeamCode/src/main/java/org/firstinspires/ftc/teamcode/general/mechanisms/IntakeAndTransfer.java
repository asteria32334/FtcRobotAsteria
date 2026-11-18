package org.firstinspires.ftc.teamcode.general.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class IntakeAndTransfer {
    private DcMotor intakeDcMotor;
    private DcMotor transferDcMotor;
    private Servo flowerServoLeft;
    private Servo flowerServoRight;

    public IntakeAndTransfer(HardwareMap hardwareMap) {
        intakeDcMotor = hardwareMap.get(DcMotor.class, "intake");
        intakeDcMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        intakeDcMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeDcMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        transferDcMotor = hardwareMap.get(DcMotor.class, "transfer");
        transferDcMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        transferDcMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        transferDcMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        flowerServoLeft = hardwareMap.get(Servo.class, "intake_rampa_sol");
        flowerServoLeft.setDirection(Servo.Direction.REVERSE);

        flowerServoRight = hardwareMap.get(Servo.class, "intake_rampa_sag");
        //flowerServoRight.setDirection(Servo.Direction.REVERSE);
    }

    public void intakeAndTransfer(boolean intake, boolean transfer, boolean rampa) {

        if (intake) {
            intakeDcMotor.setPower(0.8);
        }
        else {
            intakeDcMotor.setPower(0);
        }

        if (transfer) {
            transferDcMotor.setPower(1);
        }
        else {
            transferDcMotor.setPower(0);
        }

        if (rampa) {
            flowerServoLeft.setPosition(0.46);
            flowerServoRight.setPosition(0.46);
        }
        else {
            flowerServoLeft.setPosition(0);
            flowerServoRight.setPosition(0);
        }
    }
}
