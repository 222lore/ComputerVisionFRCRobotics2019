package portdetector;

import edu.wpi.first.networktables.*;
import java.util.ArrayList;
import java.util.List;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

/**
 *
 * @author greenie
 */

public class PortDetectorRunner {
    public static void main(String[] args) {
        PortDetector detector = new PortDetector();
        
        MatOfPoint left = detector.process().get(0);
        System.out.println(detector.process().size() + " contours found. \n");
        
        List<Point> centroids = new ArrayList<Point>();
        
        for(MatOfPoint mop : detector.process()) {
            Moments moments = Imgproc.moments(mop);
            
            Point centroid = new Point();
            centroid.x = moments.get_m10() / moments.get_m00();
            centroid.y = moments.get_m01() / moments.get_m00();
            
            System.out.println("("+centroid.x+", "+centroid.y+")");
            
            centroids.add(centroid);
        }
        
        try {
            NetworkTableInstance instance = NetworkTableInstance.getDefault();
            NetworkTable table = instance.getTable("rocket-port-targets");

            NetworkTableEntry leftCentroid = table.getEntry("LeftCentroid");
            NetworkTableEntry rightCentroid = table.getEntry("RightCentroid");

            leftCentroid.setValue(new double[] {centroids.get(0).x, centroids.get(0).y});
            rightCentroid.setValue(new double[] {centroids.get(1).x, centroids.get(1).y});
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
