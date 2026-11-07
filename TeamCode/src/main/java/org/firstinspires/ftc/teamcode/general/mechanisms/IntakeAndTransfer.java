package org.firstinspires.ftc.teamcode.general.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class IntakeAndTransfer {
    private DcMotor intakeDcMotor;
    private DcMotor transferDcMotor;
    private Servo flowerServo;

    public IntakeAndTransfer(HardwareMap hardwareMap) {
        intakeDcMotor = hardwareMap.get(DcMotor.class, "intake");
        intakeDcMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        intakeDcMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeDcMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        transferDcMotor = hardwareMap.get(DcMotor.class, "transfer");
        transferDcMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        transferDcMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        transferDcMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        flowerServo = hardwareMap.get(Servo.class, "intake_rampa");
        flowerServo.setDirection(Servo.Direction.REVERSE);
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
            flowerServo.setPosition(0.46);
        }
        else {
            flowerServo.setPosition(0);
        }
    }
}
