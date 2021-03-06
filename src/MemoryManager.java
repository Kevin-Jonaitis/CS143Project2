import java.util.LinkedList;
import java.util.Random;


public class MemoryManager {

	//The allocation strategies 
	public static final int FIRSTFIT = 0;
	public static final int NEXTFIT = 1;
	public static final int BESTFIT = 2;
	
	
	public static final int TAGSIZE = 2;
	
	
	int currentFreeIndex; //This is the index of the current freed block
	int allocationStrategy;
	int[] memory;
	
	int count; // The amount of holes searched in this iteration
	
	Random r; //Used to randomly generate a block to release
	
	public LinkedList<Integer> allocatedSpaces = new LinkedList<Integer>();
	
	
	public MemoryManager(int memorySize, int allocationStrategy) {
		memory = new int[memorySize + TAGSIZE];
		r = new Random();
		memory[0] = memorySize * -1; //First size tag
		memory[1] = -99; //Pointer to previous free block
		memory[2] = -99; //Pointer to next free block
		currentFreeIndex = 0;
		memory[memorySize + TAGSIZE - 1] = memorySize * -1; //Second tag size
		this.allocationStrategy = allocationStrategy;
		count = 0;
	}
	
	public void resetCount() {
		count = 0;
	}
	public int getCount() {
		return count;
	}
	
	public void releaseRandomBlock() {
		if(allocatedSpaces.size() == 0)
			return;
		int randomValue = r.nextInt(allocatedSpaces.size());
		int index = allocatedSpaces.remove(randomValue);
		mmRelease(index);
	}
	
	/** 
	 * Grabs the next free memory connected to this block's freed memory.
	 * If the currentIndex is a free memory, it will use the pointer to get the next free memory.
	 * 
	 * Otherwise, it will seek allocation by allocation until it finds a free memory
	 * @param currentIndex
	 * @return
	 */
	public int getNextFreeMemory(int currentIndex, int startIndex) {
		do {
			currentIndex = getRightMemoryIndex(currentIndex);
			if(currentIndex == -1)
				currentIndex = 0;
			if (currentIndex == startIndex)
					return -1;
			if(memory[currentIndex] < 0)
				return currentIndex;
		}while(true); //If we loop through all values and there's nothing empty, we'll get this	
	}
	
	

	
	/**
	 * Allocate a piece of memory, and return false if it fails, true if it works
	 * @param memorySize
	 */
	public boolean mmRequest(int memorySize) {
		if(allocationStrategy == BESTFIT){
			int startIndex = currentFreeIndex;
			int bestFit = Integer.MAX_VALUE;
			int bestFitIndex = -1;
			boolean allocated = false; // Whether a successful allocation was made or not
			
			do{
				if(doesFit(currentFreeIndex, memorySize)) {
					if(Math.abs(memory[currentFreeIndex]) < bestFit) {
						bestFit = Math.abs(memory[currentFreeIndex]);
						bestFitIndex = currentFreeIndex;
					}					
				}
				//Turns -1 when we've looped through the whole index
				currentFreeIndex = getNextFreeMemory(currentFreeIndex,startIndex);
				count++;
			} while(currentFreeIndex != -1);

			if(bestFitIndex != -1) {
				alloc(bestFitIndex,memorySize);
				allocated = true;
				allocatedSpaces.add(bestFitIndex);		
			}
			
			 if(currentFreeIndex == -1) { //Reset when we get to the end
					currentFreeIndex = 0;
				}
			return allocated;
			
			
		} else {	
			int startIndex = currentFreeIndex;
			int bestFit = -1;
			boolean allocated = false; // Whether a successful allocation was made or not
			
			do{
				if(doesFit(currentFreeIndex, memorySize)) {
					alloc(currentFreeIndex,memorySize);
					allocated = true;
					allocatedSpaces.add(currentFreeIndex);
					break;
					
				}
				currentFreeIndex = getNextFreeMemory(currentFreeIndex,startIndex);
				count++;
			} while(currentFreeIndex != -1);
	
			
			if(allocationStrategy == FIRSTFIT)
				currentFreeIndex = 0;
			else if(currentFreeIndex == -1) { //for best fit, we keep the index where it is, but if it's -1 reset to 0
				currentFreeIndex = 0;
			}
			//System.out.println("Current index:" + currentFreeIndex);
			return allocated;
		}
			
	}
	
	public void mmRelease(int indexToBeReleased) {
		//System.out.println("Releasing index:" + indexToBeReleased);
		
		int leftMemoryIndex = getLeftMemoryIndex(indexToBeReleased);
		int rightMemoryIndex = getRightMemoryIndex(indexToBeReleased);
		int size = memory[indexToBeReleased];
		int newSize = -1;

		if(leftMemoryIndex != -1 && memory[leftMemoryIndex] < 0 
				&& rightMemoryIndex != -1 && memory[rightMemoryIndex] < 0) { //left and right
				newSize = memory[indexToBeReleased] + memory[leftMemoryIndex] * -1 + memory[rightMemoryIndex] * -1 + 4; //8 is for 4 sizes, and 4 pointers that are no longer needed

							
				//Clear all the current data
				clearAllDataAllocated(indexToBeReleased);
				clearAllDataFree(leftMemoryIndex);
				clearAllDataFree(rightMemoryIndex);

				//Set new pointers
				memory[leftMemoryIndex] = newSize * -1;
				memory[leftMemoryIndex + 1] = 99; //  
				memory[leftMemoryIndex + 2] = 99;
				memory[leftMemoryIndex + newSize + 1] = newSize * -1;
		} else if(leftMemoryIndex != -1 && memory[leftMemoryIndex] < 0) { //Left is free
			newSize = memory[indexToBeReleased] + memory[leftMemoryIndex] * -1 + 2;
			
		
			//Clear all the current data
			clearAllDataAllocated(indexToBeReleased);
			clearAllDataFree(leftMemoryIndex);

			//Set new pointers
			memory[leftMemoryIndex] = newSize * -1;
			memory[leftMemoryIndex + 1] = 99; //  
			memory[leftMemoryIndex + 2] = 99;
			memory[leftMemoryIndex + newSize + 1] = newSize * -1;

		} else if (rightMemoryIndex != -1 && memory[rightMemoryIndex] < 0) { //Just the right
			newSize = memory[indexToBeReleased] + memory[rightMemoryIndex] * -1 + 2;
		
			//Clear all the current data
			clearAllDataAllocated(indexToBeReleased);
			clearAllDataFree(rightMemoryIndex);

			//Set new pointers
			memory[indexToBeReleased] = newSize * -1;
			memory[indexToBeReleased + 1] = 99; //  
			memory[indexToBeReleased + 2] = 99;
			memory[indexToBeReleased + newSize + 1] = newSize * -1;
		} else { //Just this item
			newSize = memory[indexToBeReleased];
			
		
			//Clear all the current data
			clearAllDataAllocated(indexToBeReleased);

			//Set new pointers
			memory[indexToBeReleased] = newSize * -1;
			memory[indexToBeReleased + 1] = 99; //  
			memory[indexToBeReleased + 2] = 99;
			memory[indexToBeReleased + newSize + 1] = newSize * -1;
		}
		
	}
	
	/**
	 * Clear all the data associated with this index
	 * @param index
	 */
	public void clearAllDataFree(int index) {
		int size = memory[index] * -1;
		memory[index] = 0;
		memory[index + 1] = 0; //left pointer is 0
		memory[index + 2] = 0; //right pointer is 0
		memory[index + size + 1] = 0;
	}
	
	/**
	 * Clear all the data associated with this index
	 * @param index
	 */
	public void clearAllDataAllocated(int index) {
		int size = memory[index];
		memory[index] = 0;
		memory[index + size + 1] = 0;
	}
	
	public void alloc(int currentIndex, int size) {

		//System.out.println("Requesting index:" + currentIndex + " with size" + size);
		int freeSize = memory[currentIndex] * -1; // Value is negative, so multiply by -1
		memory[currentIndex] = size; // Set left size tag
		memory[currentIndex + 1] = 0; // Set the pointers to 0
		memory[currentIndex + 2] = 0; // Set the pointers to 0
		memory[currentIndex + size + 1] = size; // Set right size tag, giving one more than the total space
		
		int rightIndex = getRightMemoryIndex(currentIndex);
		if(rightIndex == -1) //We fully allocated the whole space, return 
			return;
		
		//MEMORY MUST CLEAR CORRECTLY IN ORDER FOR THIS TO WORK
		if(memory[rightIndex] > 0) //We perfectly allocated a space, so no need to add the free space 
			return;
		int sizeLeft = freeSize - size - 2; //2 for the new tags,

		memory[rightIndex] = sizeLeft * -1;
		memory[rightIndex + 1] = 99;
		memory[rightIndex + 2] = 99;
		memory[rightIndex + sizeLeft + 1] = sizeLeft * -1; //Go forward free space + the size pointer
		
	}
	/**
	 * The following two methods return the beginning indexes of allocated memory blocks to the left and right of the current allocated memory block
	 * @param currentIndex
	 * @return
	 */
	public int getLeftMemoryIndex(int currentIndex) {
		if(currentIndex - 1 < 0)
			return -1;
		else {
			int size = memory[currentIndex - 1];
			return currentIndex - 1 - Math.abs(size) - 1; //current index - 1 to get to size - size to get to beginning of size - 1 to get to index
		}
	}
	
	public int getRightMemoryIndex(int currentIndex) {
		if(currentIndex + Math.abs(memory[currentIndex]) + 2 >= memory.length)
			return -1;
		else
			return currentIndex + Math.abs(memory[currentIndex]) + 2; // Current index + the size of the currentIndex + tag for this + tag for next 
	}

	/**
	 * Checks to see if there's enough memory at this space to create an insert.
	 * NOTE that when we allocate free memory, we need at least 3 spaces for the tag + 2 pointers for the rest of the free space
	 * If this doesn't exist, we can't satisfy the request. Also note a perfectly fitting allocation works as well.
	 */
	public boolean doesFit(int currentIndex, int size) {
		if(memory[currentIndex] >= 0) // This space is occupied
			return false;
		
		//If the space requested EXACTLY equals the size, or the space requested has at least 3 extra spaces after it, return true
		if(memory[currentIndex] == (size * -1) || memory[currentIndex] <= ((size + 4) * -1))
			return true;
		else
			return false;
	}

	public void setFreeTags(int index, int freeSpaceSize) {
		memory[index] = freeSpaceSize * -1; // Set free/allocated tag along with the amount of space that is free
		
	}

	public int getMemoryUtilization() {
		int total = 0;
		for(Integer i : allocatedSpaces) {
			total = total + memory[i];
		}
		return total;
	}

	public int getSearchTime() {
		int currentCount = this.count;
		resetCount();
		return currentCount;
	}
}
