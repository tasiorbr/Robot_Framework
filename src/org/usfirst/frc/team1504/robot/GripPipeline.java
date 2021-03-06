package org.usfirst.frc.team1504.robot;

//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//import java.util.HashMap;
import edu.wpi.first.wpilibj.vision.VisionPipeline;

import org.opencv.core.*;
//import org.opencv.core.Core.*;
//import org.opencv.features2d.FeatureDetector;
//import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
//import org.opencv.objdetect.*;

/**
* GripPipeline class.
*
* <p>An OpenCV pipeline generated by GRIP.
*
* @author GRIP
*/
public class GripPipeline implements VisionPipeline{

	//Outputs
	private Mat hsvThresholdOutput = new Mat();
	private Mat cvErodeOutput = new Mat();
	private ArrayList<MatOfPoint> findContoursOutput = new ArrayList<MatOfPoint>();
	private Point _centroid = new Point();
	private static GripPipeline _instance = new GripPipeline();
	//private List<MatOfPoint> _contours = new List<MatOfPoint>();
	Rect[] _bb; 

	public enum AimState {AIM_ROBOT, AIMED, BAD_IMAGE}
	
	public double[][] _output;
	public AimState _state;
	//public double _target = 0.0;

	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	public static GripPipeline getInstance()
	{
		return _instance;
	}
	
	/**
	 * This is the primary method that runs the entire pipeline and updates the outputs.
	 */
	public void process(Mat source0) {
		//Vision.getInstance().getImage();
		// Step HSV_Threshold0:
		Mat hsvThresholdInput = source0;
		double[] hsvThresholdHue = {Map.VISION_HUE1, Map.VISION_HUE2};//{50.17985611510791, 85.26315789473685};
		double[] hsvThresholdSaturation = {Map.VISION_SAT1, Map.VISION_SAT2};
		double[] hsvThresholdValue = {Map.VISION_VAL1, Map.VISION_VAL2};
		hsvThreshold(hsvThresholdInput, hsvThresholdHue, hsvThresholdSaturation, hsvThresholdValue, hsvThresholdOutput);

		// Step CV_erode0:
		Mat cvErodeSrc = hsvThresholdOutput;
		Mat cvErodeKernel = new Mat();
		Point cvErodeAnchor = new Point(-1, -1);
		double cvErodeIterations = 1.0;
		int cvErodeBordertype = Core.BORDER_CONSTANT;
		Scalar cvErodeBordervalue = new Scalar(-1);
		cvErode(cvErodeSrc, cvErodeKernel, cvErodeAnchor, cvErodeIterations, cvErodeBordertype, cvErodeBordervalue, cvErodeOutput);

		// Step Find_Contours0:
		Mat findContoursInput = cvErodeOutput;
		boolean findContoursExternalOnly = false;
		_output = findContours(findContoursInput, findContoursExternalOnly, findContoursOutput);
		
		double[] area = _output[4];
		//System.out.println("length of area is " + area.length);
		//System.out.println("area is "+ area);
		//double[] position = _output[0];

		if(area.length == 0)
		{
			_state = AimState.BAD_IMAGE;
			System.out.println("area is 0");
			return;
		}
		
		else {
			int largest = 0;
			int second_largest = 0;
			
			for(int i = 0; i < area.length; i++)
			{
				if(area[i] > area[largest])
				{
					largest = i;
				}
				
			}
			
			//_target = largest;
			
			for(int i = 0; i < area.length; i++)
			{
				if(area[i] > area[second_largest] && second_largest != largest)
				{
					second_largest = i;
				}
			}	
			
			//System.out.println("largest target is " + _target);
			//_target = (2 * position[largest] / Map.VISION_INTERFACE_VIDEO_WIDTH) - 1; 
			//_target *= Map.VISION_INTERFACE_VIDEO_FOV / -2.0; //TODO what is FOV of camera
			//_target = _output[0][largest];
			
			/*Mat[] array = new Mat[2];
			array[0] = findContoursOutput.get(largest);
			array[1] = findContoursOutput.get(second_largest);*/
			
			_centroid = new Point((_bb[largest].x + _bb[second_largest].x)  /  2, (_bb[largest].y + _bb[second_largest].y)  /  2);
			
			/*Moments m = Imgproc.moments(array[0]);
			Point centroid = new Point();
			centroid.x = m.get_m10() / m.get_m00();
			centroid.y = m.get_m01() / m.get_m00();*/
			//System.out.println("target is " + _target);
			//checkAim();
		}
	}
	
	public double getDistance() //get distance from y of centroid
	{
		System.out.println("distance is " + _centroid.y*.03 + 1);
		return _centroid.y*.03 + 1;
	}
	
	public double setShooterSpeed() //set speed based on distance
	{
		return getDistance()*100 + 50;
	}
	
	public boolean checkAim()
	{
		if(offset_aim_factor() < 10)// TODO Map.VISION_INTERFACE_AIM_DEADZONE)
		{
			_state = AimState.AIMED;
			System.out.println("camera interface checkAim = aimed");
			return true;
		}
		
		else
		{
			_state = AimState.AIM_ROBOT;
			//set_drive_input();
			System.out.println("camera interface checkAim = need to aim");
			return false;
		}	
	}
	
	public double [] set_drive_input()
	{	
		double[] input = new double[3];
		if(!checkAim())
		{
			input[0] = 0.0;
			input[1] = 0.0;
			input[2] = offset_aim_factor() * .1; //TODO
			System.out.println("turn value = " + input[2]);
		}
		else
			input[0] = input[1] = input[2] = 0.0;
		
		return input;
	}
	
	private double offset_aim_factor()
	{
		System.out.println(80 - _centroid.x);
		return Math.abs(80 - _centroid.x);//_gyro.getAngle(); // offset
	}

	/**
	 * This method is a generated getter for the output of a HSV_Threshold.
	 * @return Mat output from HSV_Threshold.
	 */
	public Mat hsvThresholdOutput() {
		return hsvThresholdOutput;
	}

	/**
	 * This method is a generated getter for the output of a CV_erode.
	 * @return Mat output from CV_erode.
	 */
	public Mat cvErodeOutput() {
		return cvErodeOutput;
	}

	/**
	 * This method is a generated getter for the output of a Find_Contours.
	 * @return ArrayList<MatOfPoint> output from Find_Contours.
	 */
	public ArrayList<MatOfPoint> findContoursOutput() {
		return findContoursOutput;
	}


	/**
	 * Segment an image based on hue, saturation, and value ranges.
	 *
	 * @param input The image on which to perform the HSL threshold.
	 * @param hue The min and max hue
	 * @param sat The min and max saturation
	 * @param val The min and max value
	 * @param output The image in which to store the output.
	 */
	private void hsvThreshold(Mat input, double[] hue, double[] sat, double[] val,
	    Mat out) {
		Imgproc.cvtColor(input, out, Imgproc.COLOR_BGR2HSV);
		Core.inRange(out, new Scalar(hue[0], sat[0], val[0]),
			new Scalar(hue[1], sat[1], val[1]), out);
	}

	/**
	 * Expands area of lower value in an image.
	 * @param src the Image to erode.
	 * @param kernel the kernel for erosion.
	 * @param anchor the center of the kernel.
	 * @param iterations the number of times to perform the erosion.
	 * @param borderType pixel extrapolation method.
	 * @param borderValue value to be used for a constant border.
	 * @param dst Output Image.
	 */
	private void cvErode(Mat src, Mat kernel, Point anchor, double iterations,
		int borderType, Scalar borderValue, Mat dst) {
		if (kernel == null) {
			kernel = new Mat();
		}
		if (anchor == null) {
			anchor = new Point(-1,-1);
		}
		if (borderValue == null) {
			borderValue = new Scalar(-1);
		}
		Imgproc.erode(src, dst, kernel, anchor, (int)iterations, borderType, borderValue);
	}

	/**
	 * Sets the values of pixels in a binary image to their distance to the nearest black pixel.
	 * @param input The image on which to perform the Distance Transform.
	 * @param type The Transform.
	 * @param maskSize the size of the mask.
	 * @param output The image in which to store the output.
	 */
	
	private double[][] findContours(Mat input, boolean externalOnly, List<MatOfPoint> contours) 
	{
			//_contours = contours;
			Mat hierarchy = new Mat();
			contours.clear();
			int mode;
			if (externalOnly) {
				mode = Imgproc.RETR_EXTERNAL;
			}
			else {
				mode = Imgproc.RETR_LIST;
			}
			int method = Imgproc.CHAIN_APPROX_SIMPLE;
			Imgproc.findContours(input, contours, hierarchy, mode, method);
			
			Rect[] bb = new Rect[(int) contours.size()];
			_bb = bb;
			double[][] output = {
					new double[(int) contours.size()],
					new double[(int) contours.size()],
					new double[(int) contours.size()],
					new double[(int) contours.size()],
					new double[(int) contours.size()]
					};
			
			for(int i = 0; i < contours.size(); i++)
			{
				bb[i] = Imgproc.boundingRect(contours.get(i));
				
				output[0][i] = bb[i].x + bb[i].width / 2.0;
				output[1][i] = bb[i].y + bb[i].height;// / 2.0;
				output[2][i] = bb[i].width;
				output[3][i] = bb[i].height;
				output[4][i] = Imgproc.contourArea(contours.get(i), false);

				//contours.get(i).
				System.out.println("area is " + output[4][i]);
				System.out.println("center is " + output[0][i]);
				System.out.println("x is " + bb[i].x);// + "y is " + bb[i].y + "width is " + bb[i].width + "height is " + bb[i].height + "area is " + output[4][i]);
			}
			System.out.println("number of contours " + contours.size());
			
			//_contours = contours;
			//_output = output;
			return output;
		}
}

