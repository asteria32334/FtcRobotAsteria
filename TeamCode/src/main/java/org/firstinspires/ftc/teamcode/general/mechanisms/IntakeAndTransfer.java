package org.firstinspires.ftc.teamcode.general.mechanisms;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class IntakeAndTransfer {
    private DcMotor intakeDcMotor;
    private DcMotor transferDcMotor;

    public IntakeAndTransfer(HardwareMap hardwareMap) {
        intakeDcMotor = hardwareMap.get(DcMotor.class, "intake");
        intakeDcMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        intakeDcMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intakeDcMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        transferDcMotor = hardwareMap.get(DcMotor.class, "transfer");
        transferDcMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        transferDcMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        transferDcMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void intakeAndTransfer(boolean intake, boolean transfer) {

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
    }
}
