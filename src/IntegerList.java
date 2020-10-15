
/**
 * Used to manage a list of integers.
 */
public class IntegerList {
	
	/**
	 * Default constructor.
	 */
	IntegerList() {
		data = null;
		used = 0;
		allocation = 0;
	}
	

	/**
	 * Copy constructor.
	 */
	IntegerList(IntegerList src) {
		used = src.used;
		allocation = src.allocation;
		if (src.data == null) {
			data = null;
		} else {
			data = new int[allocation];
			for (int i=0; i<used; i++) {
				data[i] = src.data[i];
			}
		}
	}
	
	/**
	 * Add a new integer value to the end of the list.
	 * @param newValue The new integer to add to the list.
	 */
	public void add(int newValue) {
		if (used >= allocation) expand(0);
		data[used++] = newValue;
	}

	/**
	 * Remove the integer value from the list at a given index.
	 * Any values located after the target value will be moved forward within the list to fill the gap.
	 * @param index Index of the requested integer.
	 */
	public void remove(int index) {
		assert (0 <= index) && (index < used);
		for (int i=used-1; i>index; i--) {
			data[i - 1] = data[i];
		}
		used--;
	}
	
	/**
	 * Add a new integer value to the end of the list.
	 * @param newValue The new integer to add to the list.
	 */
	public void push(int newValue) {
		if (used >= allocation) expand(0);
		data[used++] = newValue;
	}

	/**
	 * Remove a value from the end of the list.
	 * The list must not be empty.
	 */
	public void pop() {
		assert used > 0;
		used--;
	}
	
	/**
	 * Set the value of an integer at a given index within the list.
	 * @param index Index of the integer to be changed.
	 * @param newValue New value of the target integer.
	 */
	public void set(int index, int newValue) {
		data[index] = newValue;
	}
	
	/**
	 * Get the integer value from the list at a given index.
	 * @param index Index of the requested integer.
	 * @return Value at the requested index.
	 */
	public int get(int index) {
		return data[index];
	}
	
	/**
	 * Returns the total number of integers within the list.
	 * @return Number of integers within the list.
	 */
	public int size() {
		return used;
	}
	
	/**
	 * Check if the list is empty.
	 * @return Returns true if the list contains no values.
	 */
	public boolean isEmpty() {
		return used == 0;
	}
	
	/**
	 * Remove all values from the list.
	 * This will not free the internally allocated memory.
	 */
	public void clear() {
		used = 0;
	}
	
	/**
	 * Change the value returned by size()
	 * If the new size is greater than the previous size then value of new integers is undefined.
	 * @param newSize The new size of the list.
	 */
	public void resize(int newSize) {
		assert newSize >= 0;
		if (newSize > allocation) expand(newSize);
		used = newSize;
	}
	
	/**
	 * Increase the size of the memory allocation for the list.
	 * @param requestedSize If this value is non-zero then make sure the allocation is at least this large.
	 */
	private void expand(int requestedSize) {
		
		// Make an initial guess of the new size for the array
		int newAllocation = (allocation < minAllocation) ? minAllocation : allocation * 2;
		
		// Make sure the new size can fit the requested size
		if (requestedSize > 0) {
			while (newAllocation < requestedSize) {
				
				// Increase allocation
				newAllocation *= 2;
				
				// Check for overflow
				if (newAllocation < 0) {
					newAllocation = Integer.MAX_VALUE;
					break;
				}
			}
		}
		
		// Allocate new array
		int[] temp = new int[newAllocation];

		// Copy values from previous array
		for (int i=0; i<used; i++) {
			temp[i] = data[i];
		}
		
		// Save new data
		data = temp;
		allocation = newAllocation;
	}
	
	private int[] data;
	private int used;
	private int allocation;
	private static final int minAllocation = 8;
}
