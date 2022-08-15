package com.topcoder.client.spectatorApp.widgets;

/**
 * This grid sizing policy details the policy that should be used to size each
 * column/row
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public abstract class SGridSizingPolicy {
	/** @see SGridSizingPolicy.EvenSizingPolicy */
	public final static SGridSizingPolicy EVEN_SIZING = new SGridSizingPolicy.EvenSizingPolicy();

	/** Policy that sizes all columns/rows evenly */
	public final static SGridSizingPolicy HEADEREVEN_SIZING = new SGridSizingPolicy.HeaderEvenSizingPolicy();

	/** Policy that sizes extra space to the edges */
	public final static SGridSizingPolicy EDGE_SIZING = new SGridSizingPolicy.EdgeSizingPolicy();

	/** Policy that sizes extra space into the last column */
	public final static SGridSizingPolicy RIGHT_SIZING = new SGridSizingPolicy.RightSizingPolicy();

	/** Policy that does NOT resize */
	public final static SGridSizingPolicy NONE = new SGridSizingPolicy.NoneSizingPolicy();

	/** Policy that distributes the excess space evenly */
	public final static SGridSizingPolicy DISTRIBUTEEVEN_SIZING = new SGridSizingPolicy.DistributeEvenSizingPolicy();

	/** Policy that distributes the excess space evenly */
	public final static SGridSizingPolicy BESTFIT_SIZING = new SGridSizingPolicy.BestFitSizingPolicy();

	/**
	 * Assigns the space to the values given the total space available
	 * 
	 * @param values
	 *           the column/rows values to assign. The values in this array are
	 *           the pre-size values.
	 * @param total
	 *           the total size to assign
	 */
	public abstract void assignSizes(int[] values, int total);

	/**
	 * Helper method to calculate the total size of some value
	 * 
	 * @param values
	 *           the values to total
	 * @return the total value
	 */
	protected static int getTotalSize(int[] values) {
		int rc = 0;
		for (int x = values.length - 1; x >= 0; x--)
			rc += values[x];
		return rc;
	}

	/**
	 * Sizing policy that will ignore the 'header' row
	 */
	public static class IgnoreHeaderSizingPolicy extends SGridSizingPolicy {
		/** The the other grid spacing policy */
		private SGridSizingPolicy other;

		/** Constructor */
		public IgnoreHeaderSizingPolicy(SGridSizingPolicy other) {
			this.other = other;
		}

		/** The implementation */
		public void assignSizes(int[] values, int total) {
			// If none, ignore
			if (values.length == 0) return;
			
			// Allocate the array minus 1 (header)
			int[] v2 = new int[values.length - 1];
			System.arraycopy(values, 1, v2, 0, v2.length);
			
			// Ask the other for the spacing
			other.assignSizes(v2, total - values[0]);
			
			// Assign them back to the non-zero element
			System.arraycopy(v2, 0, values, 1, v2.length);
		}
	}

	/**
	 * Sizing policy that will ignore the 'header' row
	 */
	public static class PointGridLayout extends SGridSizingPolicy {
		/** The the other grid spacing policy */
		private final int[] cols;

		/** Constructor */
		public PointGridLayout(int[] cols) {
			this.cols = cols;
		}

		/** The implementation */
		public void assignSizes(int[] values, int total) {
			// If none, ignore
			if (values.length == 0) return;
			
			// Set all the point panels and total panel
			for(int x = 0; x <= values.length; x++) {
				if (x + 1 >= cols.length) continue;
				values[x+1] = cols[x+1];
				total -= cols[x+1];
			}
			
			if (total > cols[0]) {
				values[0] = cols[0];
			} else {
				values[0] = total;
			}
		}
	}

	/**
	 * Sizing policy that set the header to a specific size
	 */
	public static class HeaderSizingPolicy extends SGridSizingPolicy {
		/** The the other grid spacing policy */
		private SGridSizingPolicy other;

		private int headerSize;
		
		/** Constructor */
		public HeaderSizingPolicy(int headerSize, SGridSizingPolicy other) {
			this.other = other;
			this.headerSize = headerSize;
		}

		/** The implementation */
		public void assignSizes(int[] values, int total) {
			// If none, ignore
			if (values.length == 0) return;
			if (values.length == 1) {
				values[0] = headerSize;
				return;
			}
			
			// Allocate the array minus 1 (header)
			int[] v2 = new int[values.length - 1];
			System.arraycopy(values, 1, v2, 0, v2.length);
			
			// Ask the other for the spacing
			other.assignSizes(v2, total - headerSize);
			
			// Assign them back to the non-zero element
			System.arraycopy(v2, 0, values, 1, v2.length);
			values[0] = headerSize;
		}
	}

	/**
	 * Sizing policy that makes all value the same size that will make up the
	 * total
	 */
	private static class EvenSizingPolicy extends SGridSizingPolicy {
		public void assignSizes(int[] values, int total) {
			for (int x = values.length - 1; x >= 0; x--)
				values[x] = total / values.length;
		}
	}

	/**
	 * Sizing policy that makes all values the same size that will make up the
	 * total EXCEPT for the header value - leaves that one alone
	 */
	private static class HeaderEvenSizingPolicy extends SGridSizingPolicy {
		public void assignSizes(int[] values, int total) {
			for (int x = values.length - 1; x >= 1; x--)
				values[x] = total / (values.length - 1);
		}
	}

	/**
	 * Sizing policy that distributes excess space evenly to all columns
	 */
	private static class DistributeEvenSizingPolicy extends SGridSizingPolicy {
		public void assignSizes(int[] values, int total) {
			int min = getTotalSize(values);
			int pad = (total - min) / values.length;
			if (pad < 0) return;
			for (int x = values.length - 1; x >= 0; x--)
				values[x] += pad;
		}
	}

	/**
	 * Sizing policy that will distribute extra space on the first/last col/row
	 * or take away extra space on the first/last col/row
	 */
	private static class EdgeSizingPolicy extends SGridSizingPolicy {
		public void assignSizes(int[] values, int totals) {
			if (values.length > 0) {
				// Figure out the total to be allocated
				int over = totals - getTotalSize(values);
				// Allocate excess
				values[0] += over / 2;
				values[values.length - 1] += over / 2;
			}
		}
	}

	/**
	 * Sizing policy that will distribute extra space to the right/bottom
	 */
	private static class RightSizingPolicy extends SGridSizingPolicy {
		public void assignSizes(int[] values, int totals) {
			if (values.length > 0) {
				// Figure out the total to be allocated
				int over = totals - getTotalSize(values);
				values[values.length - 1] += over;
			}
		}
	}

	/**
	 * Policy that will collapse the first value (typcially the user handle
	 * column) if there is more size that total
	 */
	public static class CollapseStrategy extends SGridSizingPolicy {
		/** The value number to collapse */
		private int num;

		/** The other policy */
		private SGridSizingPolicy other;

		/** Constructor the defaults to first */
		public CollapseStrategy() {
			this(0);
		}

		/** Constructs specifying the number */
		public CollapseStrategy(int num) {
			this(num, SGridSizingPolicy.EVEN_SIZING);
		}

		/** Constructor specifying the number and other policy */
		public CollapseStrategy(int num, SGridSizingPolicy other) {
			this.num = num;
			this.other = other;
		}

		/** Assign */
		public void assignSizes(int[] values, int total) {
			// If no values - return
			if (values.length == 0) return;
			// Calc total size
			int calc = getTotalSize(values);
			// If size greater than total - then collapse something
			if (calc > total) {
				// Specified number not in range, default to last
				if (num >= values.length) {
					values[values.length - 1] -= calc - total;
				} else {
					values[num] -= (calc - total);
				}
			} else {
				other.assignSizes(values, total);
			}
		}
	}

	/**
	 * Sizing policy that will NOT distribute extra weight
	 */
	private static class NoneSizingPolicy extends SGridSizingPolicy {
		public void assignSizes(int[] values, int totals) {
		// Do nothing
		}
	}

	/**
	 * Sizing policy that will NOT distribute extra weight
	 */
	public static class FixedSizingPolicy extends SGridSizingPolicy {
		private int num;

		public FixedSizingPolicy(int num) {
			this.num = num;
		}

		public void assignSizes(int[] values, int totals) {
//			System.out.println(">> Height: " + num + ":" + totals);
			for (int x = 0; x < values.length; x++)
				values[x] = num;
		}
	}

	/**
	 * Sizing policy that attempts to find the best fit
	 */
	public static class BestFitSizingPolicy extends SGridSizingPolicy {
		public void assignSizes(int[] values, int total) {
			// If no space - ignore
			if (total == 0) return;
			// Calculate space taken by the values
			int calc = getTotalSize(values);
			// Do we have excess space
			if (calc < total) {
				// Calculate the best fit
				//int left = bestFit(values, Integer.MAX_VALUE, total - calc);
				// Put what's left across evenly
				SGridSizingPolicy.DISTRIBUTEEVEN_SIZING.assignSizes(values, total);
			} else {
				// Take away from the largest ones first
				bestCollapse(values, calc - total);
			}
		}

		/** This can be improved IMMENSELY in performance - just didn't have time */
		private int bestFit(int[] values, int maxSize, int freeSpace) {
			// Find the maximum value thats below the max size
			int tempSize = -1;
			for (int x = values.length - 1; x >= 0; x--) {
				if (values[x] < maxSize && values[x] > tempSize) tempSize = values[x];
			}
			// If none found - return the total
			if (tempSize == -1) return freeSpace;
			// Set lower sizes to and give use what's left
			int left = bestFit(values, tempSize, freeSpace);
			// If there is no more room to expand - simply return nothing
			// Note: we blantantly ignore odd pixels here
			if (left < values.length || maxSize == Integer.MAX_VALUE) return 0;
			// Find how many values will be adjusted
			int num = 0;
			for (int x = values.length - 1; x >= 0; x--) {
				if (values[x] < maxSize) num++;
			}
			// Determine how much we can pad out
			// (either our tempSize or maxSize- whichever is minimal)
			int pad = maxSize - tempSize;
			if (pad * num > left) {
				pad = left / num;
			}
			// Resize all of them to be the pad length
			for (int x = values.length - 1; x >= 0; x--) {
				if (values[x] <= tempSize) {
					left -= pad;
					values[x] += pad;
				}
			}
			// Return what's left over
			return left;
		}

		/** Again - can be improved here */
		private void bestCollapse(int[] values, int spaceToTake) {
			// Keep taking space away until it's all done
			while (spaceToTake > values.length) {
				int maxSize = 0;
				int nextToMaxSize = 0;
				int num = 0;
				// Loop finding the biggest and next biggest sizes
				for (int x = 0; x < values.length; x++) {
					if (values[x] > maxSize) {
						num = 1;
						nextToMaxSize = maxSize;
						maxSize = values[x];
					} else if (values[x] == maxSize) {
						num++;
					} else if (values[x] > nextToMaxSize) {
						nextToMaxSize = values[x];
					}
				}
				// If none found - break out
				if (num == 0) break;
				// Caluclate the distance between the biggest and next
				int dist = maxSize - nextToMaxSize;
				// If the space is too much - take only what we need
				if (dist * num > spaceToTake) dist = spaceToTake / num;
				// Take it away
				for (int x = 0; x < values.length; x++) {
					if (values[x] == maxSize) {
						values[x] -= dist;
						spaceToTake -= dist;
					}
				}
			}
		}
	}
}
