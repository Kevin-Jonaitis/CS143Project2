import java.util.LinkedList;


public class MemoryManager {

	//The allocation strategies 
	public static final int FIRSTFIT = 0;
	public static final int NEXTFIT = 1;
	
	public static final int TAGSIZE = 2;
	
	
	int currentIndex;
	int allocationStrategy;
	int[] memory;
	
	
	LinkedList<Integer> allocatedSpaces = new LinkedList<Integer>();
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		MemoryManager m = new MemoryManager(10,FIRSTFIT);
		m.mmRequest(5);


	}
	
	public MemoryManager(int memorySize, int allocationStrategy) {
		memory = new int[memorySize + TAGSIZE];
		memory[0] = memorySize * -1; //First size tag
		memory[1] = -1; //Pointer to previous free block
		memory[2] = -1; //Pointer to next free block
		currentIndex = 0;
		memory[memorySize+ TAGSIZE - 1] = memorySize * -1; //Second tag size
		this.allocationStrategy = allocationStrategy;
	}
	
	public void mmInit() {
	}
	
	/**
	 * Allocate a piece of memory, and return false if it fails, true if it works
	 * @param memorySize
	 */
	public boolean mmRequest(int memorySize) {
		int startIndex = currentIndex;
		boolean allocated = false; // Whether a successful allocation was made or not
		
		do{
			if(doesFit(currentIndex, memorySize)) {
				alloc(currentIndex,memorySize);
				allocated = true;
				break;
				
			}
			
			currentIndex = getRightMemoryIndex(currentIndex);
			if(currentIndex == -1) //We've reached the end, loop back around
				currentIndex = 0;	
			
		} while(currentIndex != startIndex);

		
		if(allocationStrategy == FIRSTFIT)
			currentIndex = 0;
		else { //for best fit, we keep the index where it is
		}
		
		return allocated;
			
	}
	
	public void mmRelease() {
//		int leftMemoryIndex = getMemory(true,currentIndex);
//		int rightMemoryIndex = getMemory(false,currentIndex);
		
	}
	
	public void alloc(int currentIndex, int size) {
		
		int freeSize = memory[currentIndex] * -1; // Value is negative, so multiply by -1
		System.out.println("Free size before allocating: " + freeSize);
		memory[currentIndex] = size; // Set left size tag
		memory[currentIndex + 1] = 0; // Set the pointers to 0
		memory[currentIndex + 2] = 0; // Set the pointers to 0
		memory[currentIndex + size + 1] = size; // Set right size tag, giving one more than the total space
		
		int rightIndex = getRightMemoryIndex(currentIndex);
		if(rightIndex == -1) //We fully allocated the whole space, return 
			return;
		
		if(memory[rightIndex] > 0) //We perfectly allocated a space, so no need to add the free space 
			return;
		int sizeLeft = freeSize - size - 2 - 2; //2 for the new tags, 2 for the new pointers;
		System.out.println("Free size after allocating: " + sizeLeft);

		memory[rightIndex] = sizeLeft * -1;
		memory[rightIndex + 1] = findNextLeftFreeSpace(rightIndex);
		memory[rightIndex + 2] = findNextRightFreeSpace(rightIndex);
		memory[rightIndex + -1 * sizeLeft + 1] = sizeLeft * -1;
		
	}
	
	/**
	 * Gets the amount of memory to the allocated blocks left and right of the current block.
	 * Returns -1 if there is nothing to the left or right
	 * @param left set to true for the left block, false for the right block
	 * @return
	 */
	
	public int findNextLeftFreeSpace(int currentIndex){
		currentIndex = getLeftMemoryIndex(currentIndex);
		while(currentIndex > 0){
			if(memory[currentIndex] > 0) {
				return currentIndex;
			}
			
			currentIndex = getLeftMemoryIndex(currentIndex);

		}
		
		return -1; //If a space isn't found, there is no free space to the left, so make the pointer -1
	}
	
	public int findNextRightFreeSpace(int currentIndex) {
		currentIndex = getRightMemoryIndex(currentIndex);

		while(currentIndex > 0){
			if(memory[currentIndex] > 0)
				return currentIndex;
			currentIndex = getRightMemoryIndex(currentIndex);
		}
		
		return -1; //If a space isn't found, there is no free space to the right, so make the pointer -1
	}
	
	public int getLeftMemoryIndex(int currentIndex) {
		if(currentIndex - 1 < 0)
			return -1;
		else
			return currentIndex - 1;
	}
	
	public int getRightMemoryIndex(int currentIndex) {
		if(currentIndex + Math.abs(memory[currentIndex]) + 2 >= memory.length)
			return -1;
		else
			return currentIndex + Math.abs(memory[currentIndex]) + 2; // Current index + the size of the currentIndex + tag for this + next tag 
	}

	/**
	 * Checks to see if there's enough memory at this space to create an insert.
	 * NOTE that when we allocate free memory, we need at least 3 spaces for the tag + 2 pointers for the rest of the free space
	 * If this doesn't exist, we can't satisfy the request. Also note a prefectly fitting allocation works as well.
	 */
	public boolean doesFit(int currentIndex, int size) {
		if(memory[currentIndex] >= 0) // This space is occupied
			return false;
		
		//If the space requested EXACTLY equals the size, or the space requested has at least 3 extra spaces after it, return true
		if(memory[currentIndex] == size * -1 || memory[currentIndex] <= ((size + 4) * -1))
			return true;
		else
			return false;
	}
	
	public static int getNextFreeIndex() {
		return -1;
		
	}
	public void setFreeTags(int index, int freeSpaceSize) {
		memory[index] = freeSpaceSize * -1; // Set free/allocated tag along with the amount of space that is free
		
	}

}
