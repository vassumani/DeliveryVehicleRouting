package dvr;

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
		
		// Make sure enough memory is allocated
		if (used >= allocation) {
			
			// If false then there is a bug somewhere
			assert used == allocation;

			// Make an initial guess of the new size for the array
			int newAllocationSize = (allocation < minAllocation) ? minAllocation : allocation * 2;
			
			// Resize memory allocation
			resizeAllocation(newAllocationSize);
		}

		// Add value to list
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
	 * Remove the integer value from the list at a given index.
	 * The value at the end of the list is swapped with the target value, and then the list is shortened.
	 * @param index Index of the requested integer.
	 */
	public void removeUnordered(int index) {
		assert (0 <= index) && (index < used);
		if (index < --used) {
			data[index] = data[used];
		}
	}
	
	/**
	 * Add a new integer value to the end of the list.
	 * @param newValue The new integer to add to the list.
	 */
	public void push(int newValue) {
		add(newValue);
	}

	/**
	 * Remove a value from the end of the list and return it.
	 * The list must not be empty.
	 * @return The value which was removed from the list.
	 */
	public int pop() {
		assert used > 0;
		return data[--used];
	}
	
	/**
	 * Set the value of an integer at a given index within the list.
	 * @param index Index of the integer to be changed.
	 * @param newValue New value of the target integer.
	 */
	public void set(int index, int newValue) {
		assert index < used;
		data[index] = newValue;
	}
	
	/**
	 * Get the integer value from the list at a given index.
	 * @param index Index of the requested integer.
	 * @return Value at the requested index.
	 */
	public int get(int index) {
		assert index < used;
		return data[index];
	}
	
	/**
	 * Find the location of the first matching value within the list.
	 * @param value Value to search for.
	 * @return Index of the value, or -1 if not found.
	 */
	public int find(int value) {
		for (int i=0; i<used; i++) {
			if (data[i] == value) return i;
		}
		return -1;
	}

	/**
	 * Find the location of the first matching value within the list.
	 * @param value Value to search for.
	 * @param startIndex Index at which to start searching.
	 * @return Index of the value, or -1 if not found.
	 */
	public int find(int value, int startIndex) {
		for (int i=startIndex; i<used; i++) {
			if (data[i] == value) return i;
		}
		return -1;
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
		if (newSize > allocation) resizeAllocation(newSize);
		used = newSize;
	}

	/**
	 * Get the maximum number of values this list can store before the memory allocations needs to be expanded.
	 * @return Current memory allocation, measured in number of stored values.
	 */
	public int capacity() {
		return allocation;
	}
	
	/** Ensure that the list can contain at least this number of total values.
	 * @param requestedSize If this value is non-zero then make sure the allocation is at least this large.
	 */
	public void reserve(int requestedTotalCapacity) {
		assert requestedTotalCapacity >= 0;
		if (allocation < requestedTotalCapacity) {
			resizeAllocation(requestedTotalCapacity);
		}
	}
	
	/**
	 * Reduce the memory allocation so that it only contains the current data.
	 */
	public void shrinkToFit() {
		resizeAllocation(used);
	}
	
	/**
	 * Increase the size of the memory allocation for the list.
	 * @param newSize If this value is non-zero then make sure the allocation is at least this large.
	 */
	private void resizeAllocation(int newSize) {
		
		// Check if allocating or deleting memory
		int[] temp = null;
		if (newSize > 0) {
		
			// Allocate new array
			temp = new int[newSize];
	
			// Copy values from previous array
			for (int i=0; i<used; i++) {
				temp[i] = data[i];
			}
		}
		
		// Save new data
		data = temp;
		allocation = newSize;
	}
	
	private int[] data;
	private int used;
	private int allocation;
	private static final int minAllocation = 8;
}
