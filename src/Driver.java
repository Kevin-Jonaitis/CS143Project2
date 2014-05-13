import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;

public class Driver {
	static PrintWriter writer;
	MemoryManager m;
	
	int simSteps;
	int memorySize;
	Random r;
	int a;
	int d;
	int strategy;
	
	//Values used for calculations
	int[] memoryUtilization;
	int[] searchTime;

	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		writer = new PrintWriter("results.txt", "UTF-8");
		writer.write("d,a,Average Memory Utilization, Average Search Time, Strategy\n");
		Driver driver = null;
		Driver driver2 = null;
		int mm = 100000;
		int steps = 100000;
		int[] a = new int[] { 100,200,300,500,(int) (0.1*mm),(int) (0.2*mm),(int) (0.3*mm)};
		int[] d = new int[] { (int) (0.01 * mm), (int) (0.1 * mm), (int) (0.3 * mm)};
		
		for(int i = 0; i < d.length; i++) {
			for(int j = 0; j < a.length; j++){
				driver = new Driver(steps,mm,a[j],d[i],MemoryManager.FIRSTFIT);
				driver.run();
			}
			for(int j = 0; j < a.length; j++){	
				driver2 = new Driver(steps,mm,a[j],d[i],MemoryManager.BESTFIT);
				driver2.run();	

			}
		}
		
		
		System.out.println("Done");
		writer.close();

	}
	
	public Driver(int simSteps, int memorySize, int a, int d,int strategy) {
		this.simSteps = simSteps;
		this.memorySize = memorySize;
		this.a = a;
		this.d = d;
		this.strategy = strategy;
		this.memoryUtilization = new int[simSteps];
		this.searchTime = new int[simSteps];
		this.m = new MemoryManager(memorySize,strategy);
		r = new Random();
	}
	
	public int recordUtilization(int currentStep) {
		memoryUtilization[currentStep] = m.getMemoryUtilization();
		searchTime[currentStep] = m.getSearchTime();
		return -1;
	}
	
	public int getRequestSize() {
		int requestSize = -1;
		while(requestSize < 2 || requestSize > memorySize) // Minimum request size allowed is 2; anything smaller will cause an error when you go from allocated to deallocated, because allocated spots take a minimum of 2 spaces, but free spaces take a minimum of 4
			requestSize = (int) (d * r.nextGaussian() + a);
		return requestSize;
	}
	
	public void run() {
		boolean success = true;
		for(int i = 0; i <simSteps; i++){
			do {
				int n = getRequestSize();
				success = m.mmRequest(n);
				
			} while(success);
			recordUtilization(i);
			m.releaseRandomBlock();
		}
		compileStatistics();
	}
	
	private void compileStatistics() {
		double averageMemoryUtilization = 0;
		double averageSearchTime = 0;
		double averageMemoryRequest = 0;
		for(int i = 0; i <simSteps; i++) {
			averageMemoryUtilization = averageMemoryUtilization + memoryUtilization[i]; 
			averageSearchTime = averageSearchTime + searchTime[i];
		}
		averageMemoryUtilization = averageMemoryUtilization / simSteps;
		averageMemoryUtilization = (averageMemoryUtilization / this.memorySize) * 100;
		averageSearchTime = averageSearchTime / simSteps;
		averageMemoryRequest = (((double) a) / this.memorySize) * 100;
		double standardDeviation = (((double) d) / this.memorySize) * 100;

		String line = standardDeviation + "," + averageMemoryRequest + "," + averageMemoryUtilization + "," + averageSearchTime + "," + strategy + "\n";
		System.out.print(line);
		writer.write(line);
	}
}
