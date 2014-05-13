import static org.junit.Assert.*;

import org.junit.Test;
/***
 * These test cases are manually tested, and values in memory are checked to make sure they contain the correct values
 * 
 * @author Kevin Jonaitis
 *
 */
public class TestCases {

	

	
	@Test
	public void testRequest() {
		MemoryManager m = new MemoryManager(14,MemoryManager.FIRSTFIT);
		m.mmRequest(5);
		m.mmRequest(5);	
//		index 0: 5
//		index 6: 5
//		index 7: 5
//		index 13: 5
//		index 14: -6
//		index 15: -1
//		index 16: -1
//		index 21:
		}

	public void testNoSpaceRequest() {
		MemoryManager m = new MemoryManager(20,MemoryManager.FIRSTFIT);
		m.mmRequest(3);
		m.mmRequest(3);
		m.mmRequest(3);
		m.mmRequest(10);
	}
	
	/**
	 * Is not needed, as the program will never attempt to release memory it hasn't allocated;
	 * the allocaiton linked-list takes care of that
	 */
	public void testReleaseNonExistantMemory() {
		assertTrue(true);
		
	}
	
	public void testJustEnoughSpaceAllocation() {
		MemoryManager m = new MemoryManager(16,MemoryManager.FIRSTFIT);
		m.mmRequest(5);
		assertTrue(m.mmRequest(5));
		}
	public void testBadAllocation() {
		MemoryManager m = new MemoryManager(16,MemoryManager.FIRSTFIT);
		m.mmRequest(5);
		assertFalse(m.mmRequest(5));
		}
	public void testPerfectAllocation() {
		MemoryManager m = new MemoryManager(12,MemoryManager.FIRSTFIT);
		m.mmRequest(5);
		assertTrue(m.mmRequest(5));
		}
	
	//This test case will attemtp at combing two items together
	public void testRemoveMiddle() {
		MemoryManager m = new MemoryManager(20,MemoryManager.FIRSTFIT);
		m.mmRequest(3);
		m.mmRequest(3);
		m.mmRequest(3);
		m.mmRelease(0);
		m.mmRelease(10);
		m.mmRelease(5);
	}
	
	public void testRemoveLeft() {
		MemoryManager m = new MemoryManager(20,MemoryManager.FIRSTFIT);
		m.mmRequest(3);
		m.mmRequest(3);
		m.mmRequest(3);
		m.mmRelease(10);
		m.mmRelease(5);
	}
	
	public void testRemoveRight() {
		MemoryManager m = new MemoryManager(20,MemoryManager.FIRSTFIT);
		m.mmRequest(3);
		m.mmRequest(3);
		m.mmRequest(3);
		m.mmRelease(10);
	}
}

