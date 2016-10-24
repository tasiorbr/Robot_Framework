package org.usfirst.frc.team1504.robot;

public class IO
{
	private static Latch_Joystick _drive_forward = new Latch_Joystick(Map.DRIVE_CARTESIAN_JOYSTICK);
	private static Latch_Joystick _drive_rotation = new Latch_Joystick(Map.DRIVE_POLAR_JOYSTICK);
	
	public static final long ROBOT_START_TIME = System.currentTimeMillis();
	
	/**
	 * Drive stuff
	 */
	
	/**
	 * Handle getting joystick values
	 * @return
	 */
	public static double[] drive_input() {
		double[] inputs = new double[2];
		boolean potFlicked1 = _drive_forward.getRawAxis(Map.JOYSTICK_POT_AXIS) < 1 || _drive_forward.getRawAxis(Map.JOYSTICK_POT_AXIS) > 0.5 ;
		boolean potFlicked2 = _drive_rotation.getRawAxis(Map.JOYSTICK_POT_AXIS) < 1 || _drive_rotation.getRawAxis(Map.JOYSTICK_POT_AXIS) > 0.5 ;
		//-1 to 1
		if(!potFlicked1 && !potFlicked2){
			inputs[0] = Map.DRIVE_INPUT_MAGIC_NUMBERS[0] * Math.pow(Utils.deadzone(_drive_forward.getRawAxis(Map.JOYSTICK_Y_AXIS)), 2) * Math.signum(_drive_forward.getRawAxis(Map.JOYSTICK_Y_AXIS));// y
			inputs[1] = Map.DRIVE_INPUT_MAGIC_NUMBERS[1] * Math.pow(Utils.deadzone(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS)), 2) * Math.signum(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS));// w
		} else if(potFlicked1){
			inputs[0] = Map.DRIVE_INPUT_MAGIC_NUMBERS[0] * Math.pow(Utils.deadzone(_drive_forward.getRawAxis(Map.JOYSTICK_Y_AXIS)), 2) * Math.signum(_drive_forward.getRawAxis(Map.JOYSTICK_Y_AXIS));// y
			inputs[1] = Map.DRIVE_INPUT_MAGIC_NUMBERS[1] * Math.pow(Utils.deadzone(_drive_forward.getRawAxis(Map.JOYSTICK_X_AXIS)), 2) * Math.signum(_drive_forward.getRawAxis(Map.JOYSTICK_X_AXIS));// w
		}
		else if(potFlicked2){
			inputs[0] = Map.DRIVE_INPUT_MAGIC_NUMBERS[0] * Math.pow(Utils.deadzone(_drive_rotation.getRawAxis(Map.JOYSTICK_Y_AXIS)), 2) * Math.signum(_drive_rotation.getRawAxis(Map.JOYSTICK_Y_AXIS));// y
			inputs[1] = Map.DRIVE_INPUT_MAGIC_NUMBERS[1] * Math.pow(Utils.deadzone(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS)), 2) * Math.signum(_drive_rotation.getRawAxis(Map.JOYSTICK_X_AXIS));// w
		}
		return inputs;
	}
	
	public static double drive_wiggle()
	{
		return (_drive_rotation.getRawButton(4) ? -1.0 : 0.0) + (_drive_rotation.getRawButton(5) ? 1.0 : 0.0);
	}

}
