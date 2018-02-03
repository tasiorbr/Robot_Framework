package org.usfirst.frc.team1504.robot;
import org.usfirst.frc.team1504.robot.Update_Semaphore.Updatable;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Lift implements Updatable
{
	public enum lift_position {BOTTOM, MIDDLE, TOP};
	private double[] lift_height = {Map.LIFT_MIN_HEIGHT, Map.LIFT_MAX_HEIGHT/2, Map.LIFT_MAX_HEIGHT};
	private String[] lifting_messages = {"lift is going to bottom","lift is going to mid","lift is going to top"};
	public static lift_position lift_state = lift_position.BOTTOM;
	
	private WPI_TalonSRX _motor; // declared for future use
	private Pickup _pickup = Pickup.getInstance();// declared for future use 
	
	boolean get_top_lift_sensor; // used as a value to check position of lift
	boolean get_bottom_lift_sensor; // used as a value to check position of lift 
	
	private static final Lift instance = new Lift(); // used later to initialize
	
	private Lift() //assigns motor to lift
	{	
		_motor = new WPI_TalonSRX(Map.LIFT_TALON_PORT);
		Update_Semaphore.getInstance().register(this);
	}
	
	private void update_mode() //checks where the lift is
	{
		if (get_elevator_height() == Map.LIFT_MAX_HEIGHT) 
		{
			get_top_lift_sensor = true;
		}
		else if (get_elevator_height() == Map.LIFT_MIN_HEIGHT) 
		{
			get_bottom_lift_sensor = true;
		}
		else 
		{
			get_top_lift_sensor = false;
			get_bottom_lift_sensor = false;
		}
		
		if(_pickup.lift_safe()) 
		{
			set_lift_velocity((lift_height[lift_state.ordinal()]-get_elevator_height())/Map.LIFT_MAX_HEIGHT);
			//sets lift velocity based on how far away the target is and where the lift currently is.
			//finds target height by finding element of lift_state then finds its corresponding height in the lift_height array
			//	ex: lift_state[2] = top, lift_height[2] = LIFT_MAX_HEIGHT
			//takes target height (say top:200) - current height (say 100) and then divides by the max height to get its
			//ratio within 1.0 to -1.0. and sets lift velocity to that ratio.
			//	ex: (200-0)/200 = 1.0 (max possible distance, full speed), (200-100)/200 = 0.5 (half of max, half speed)
			System.out.println(lifting_messages[lift_state.ordinal()]);
		}
		else 
		{
			set_lift_velocity((lift_height[1]-get_elevator_height())/Map.LIFT_MAX_HEIGHT);
		}	//makes the lift go to the middle
	}
	
	public void set_state(lift_position state)
	{
		lift_state = state;
	}
	
	private void set_motor() //sets the position of the lift
	{
		if (IO.get_override_lift())
		{
			set_lift_velocity(IO.intake_input());
		}
		if (get_top_lift_sensor) 
		{
			set_lift_velocity(0);
			System.out.println("At top, stopping");
			if (IO.get_lift_up()) 
			{
				set_lift_velocity(0);
			}
			if(IO.get_lift_down() && _pickup.lift_safe()) 
			{
				set_lift_velocity(Map.LIFT_DOWN);
			}
		}
		else 
		{
			System.out.println("Not at top...");
		}
		if (get_bottom_lift_sensor) 
		{
			set_lift_velocity(0);
			System.out.println("At bottom, stopping");
			if (IO.get_lift_down()) 
			{
				set_lift_velocity(0);
			}
			if(IO.get_lift_up() && _pickup.lift_safe()) 
			{
				set_lift_velocity(Map.LIFT_UP);
			}
		}
		else 
		{
			System.out.println("Not at bottom...");
		}
	}
	
	public double lift_speed(int speed)//Toggle based (position based, y)
	{
		if(speed == 1)//v = x^2
		{	
			return (((Map.LIFT_MAX_HEIGHT-get_elevator_height())*(Map.LIFT_MAX_HEIGHT-get_elevator_height()))/(Map.LIFT_MAX_HEIGHT*Map.LIFT_MAX_HEIGHT));
		}
		else//v = x
		{
			return ((Map.LIFT_MAX_HEIGHT-get_elevator_height())/Map.LIFT_MAX_HEIGHT);
		}
	}
	
	private void set_lift_velocity(double speed) {
		_motor.set(speed);
	}
	
	public static double get_elevator_height() // i don't think this is finished?
	{
		return 0.0; //_blahblahblah.magneticEncoder;
	}
	
	public boolean pickup_safe() //checks if it is safe to move lift so it won't crash into things
	{
		return (get_elevator_height() < 5);
	}
	
	public void plate_angle(double angle) // Sets angle of lift plate
	{
		// return true;
	}
	
	public static Lift getInstance() //returns instance
	{
		return instance;
	}
		
	public static void initialize() // returns instance
	{
		getInstance();
	}
	
	public void semaphore_update() //updates data from robot
	{
		update_mode();
		set_motor();
	}
}