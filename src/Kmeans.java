import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Kmeans {
	
	int k = 0;
	int rounds = 0;
	ArrayList<Point> data = new ArrayList<Point>();
	ArrayList<Point> means = new ArrayList<Point>();
	static Random rand = new Random(); 
	static NumberFormat formatter = new DecimalFormat("#0.0000"); 
	
	
	/**
	 * constructor
	 */
	public Kmeans(int k, int rounds){
		
		this.k = k;
		this.rounds = rounds;
		for (int i=0; i<k; i++){
			double x = rand.nextDouble();
			double y = rand.nextDouble();
			means.add(new Point(x, y, i));
		}
		
	}
	
	
	/**
	 * load data
	 * @param dataFile file path
	 */
	public void loadData (String dataFile) {
		
		Charset charset = Charset.forName("UTF-8");
        List<String> lines = null;
        
		try {
            lines = Files.readAllLines(FileSystems.getDefault().getPath(dataFile), charset);
        }catch (Exception e){
            System.err.println("Invalid input file!");
            e.printStackTrace(System.err);
            System.exit(1);
        }
		
		for (int i =0; i<lines.size(); i++){
			String str = lines.get(i).trim();
			String[] tokens = str.split("\\s+");
			
			if (tokens.length != 2){
				System.err.println("Invalid data point at line File " + dataFile + ", at line " + i);
				System.exit(0);
			}
			
			double x;
			double y;
			
			try {
				x = Double.parseDouble(tokens[0]);
				y = Double.parseDouble(tokens[1]);
				Point point = new Point(x, y);
				data.add(point);
			} catch (Exception e){
				System.err.println("Invalid data point at line File " + dataFile + ", at line " + i);
				System.exit(0);
			}
			
		}
	}
	
	/**
	 * compute eular distance of two points
	 * @param p1 the first point
	 * @param p2 the second point
	 * @return the distance between the two points
	 */
	double distance(Point p1, Point p2){
		
		double dx = p1.x-p2.x;
		double dy = p1.y-p2.y;
		double dis = Math.sqrt(dx*dx + dy*dy);
		return dis;
	}
	
	/**
	 * update the label of each point based on the closest mean
	 */
	void updateLabel(){
		for (Point point: data){
			double dis = Double.MAX_VALUE;
			for (Point mean: means){
				double distance = distance(point, mean);
				if (distance<dis){
					dis = distance;
					point.label = mean.label;
				}
			}
		}
	}
	
	/**
	 * update the position of mean based on the average of all points in the same cluster
	 */
	ArrayList<Point> newMeans(){
		double[] sum_x = new double[k];
		double[] sum_y = new double[k];
		double[] count_x = new double[k];
		double[] count_y = new double[k];
		for (Point point: data){
			sum_x[point.label] += point.x;
			sum_y[point.label] += point.y;
			count_x[point.label] ++;
			count_y[point.label] ++;
		}
		
		ArrayList<Point> newMeans = new ArrayList<Point>(k);
		for (int i=0; i<k; i++){
			double mean_x = sum_x[i]/count_x[i];
			double mean_y = sum_y[i]/count_y[i];
			Point mean = new Point(mean_x, mean_y, i);
			newMeans.add(mean);
		}
		
		return newMeans;
	}
	
	/**
	 * validate two arrays of means are identical, which indicates the algorithm should stop earlier
	 * @param mean1 the first array
	 * @param mean2 the second array
	 * @return whether the two arrays are identical, i.e. whether the loop should stop
	 */
	boolean checkStop(ArrayList<Point> mean1, ArrayList<Point> mean2){
		for (int i=0; i<1; i++){
			Point p1 = mean1.get(i);
			Point p2 = mean2.get(i);
			if (p1.x != p2.x || p1.y != p2.y){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * print the data points
	 */
	public void printPoints(){
		    
		if (data.size() == 0){
			System.out.println("No data points");
		} else {
			for (int i=0; i<data.size(); i++){
				Point point = data.get(i);
				System.out.println("Point " + i 
						+ ": x = " + formatter.format(point.x)
						+ " y = " + formatter.format(point.y)
						+ " label = " + point.label);
			}
		}
	}
	
	/**
	 * print the means
	 */
	public void printMeans(){
		
		if (means.size() == 0){
			System.out.println("No means assigned");
		} else {
			for (int i=0; i<means.size(); i++){
				Point mean = means.get(i);
				System.out.println("Mean " + i 
						+ ": x = " + formatter.format(mean.x)
						+ " y = " + formatter.format(mean.y)
						+ " label = " + mean.label);
			}
		}
		
	}
	
	/**
	 * run the k-means algorithm
	 */
	public void compute(){
		for (int i=0; i<rounds; i++){
			updateLabel();
//			printPoints();
			ArrayList<Point> newMeans = newMeans();
//			printMeans();
			if (checkStop(means, newMeans)){
				break;
			} else {
				means = newMeans;
			}
		}
		
		System.out.println("Results: ");
		printMeans();
	}
	
	/**
	 * this is particular used for testing, so every parameters can be set in one go
	 * @param dataFileh the path of the file to load
	 * @param value_k the number of clusters
	 * @param value_rounds the number of loops to run
	 */
	public static void execute(int value_k, int value_rounds, String dataFileh){
		Kmeans kmenas = new Kmeans(value_k, value_rounds);
		kmenas.loadData(dataFileh);
		kmenas.compute();
	}
	
	/**
	 * main method
	 */
	public static void main(String[] args){
		int value_k = 0;
		int value_rounds = 0;
		try {
			value_k = Integer.parseInt(args[0]);
			value_rounds = Integer.parseInt(args[1]);
		} catch (Exception e){
			System.err.println("The first two arguments must be integers!");
			e.getMessage();
			System.exit(0);
		}
		
		Kmeans kmenas = new Kmeans(value_k, value_rounds);
		kmenas.loadData(args[0]);
		kmenas.compute();
	}

}

class Point {
	
	double x;
	double y;
	int label;
	
	public Point(double x, double y){
		this.x = x;
		this.y = y;
		this.label = -1;
	}
	
	public Point(double x, double y, int label){
		this.x = x;
		this.y = y;
		this.label = label;
	}

}
