/**
 * 
 */
package org.usfirst.frc.team1504.robot;

import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;
import edu.wpi.first.wpilibj.DriverStation;
import com.ctre.CANTalon;

/**
 * @author Alex
 *
 */
public class Winch implements Updatable{
	
	private CANTalon _motorL;
	private CANTalon _motorR;
	DriverStation _ds;
	
	private Winch()
	{
		_motorL = new CANTalon(Map.WINCH_LEFT);
		_motorR = new CANTalon(Map.WINCH_RIGHT);
		
		_ds = DriverStation.getInstance();
		Update_Semaphore.getInstance().register(this);
	}
	private static Winch instance = new Winch();
	public static Winch getInstance()
	{
		return Winch.instance;
	}
	public void semaphore_update() {
		if(!IO.get_winch_override() || _ds.getMatchTime() > 30)
		{
			return;
		}
		else
		{
			_motorL.set(IO.winch_input() * Map.WINCH_MOTOR_MAGIC_NUMBERS[0]);
			_motorR.set(IO.winch_input() * Map.WINCH_MOTOR_MAGIC_NUMBERS[1]);
		}
	}

}
