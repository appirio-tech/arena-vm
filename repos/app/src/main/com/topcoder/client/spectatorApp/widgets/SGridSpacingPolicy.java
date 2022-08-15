package com.topcoder.client.spectatorApp.widgets;

/**
 * This grid weight policy details the policy that should be used to weigh each
 * column/row for how much of the size should be allocated to that column/row.
 * 
 * @author Tim "Pops" Roberts
 * @version 1.0
 */
public abstract class SGridSpacingPolicy {
	/** Implementation that allocates no space between */
	public final static SGridSpacingPolicy NONE = new FixedSpacingPolicy(0);

	/**
	 * Returns the space that should be allocated between each row/column. Note:
	 * the array will be the size of the row/col + 2. The first element will be
	 * the space BEFORE the first rowcol, the last element will be the space
	 * AFTER the last row/col
	 * 
	 * @param values
	 *           the values to assign
	 * @param total
	 *           the total amount of space available
	 */
	public abstract void assignSpacing(int[] values, int total);

	/**
	 * Helper method to calculate the total size of some value
	 * 
	 * @param values
	 *           the values to total
	 * @return the total value
	 */
	private static int getTotalSize(int[] values) {
		int rc = 0;
		for (int x = values.length - 1; x >= 0; x--)
			rc += values[x];
		return rc;
	}

	/**
	 * Spacing policy that will allocate a fixed amount between each column
	 */
	public static class FixedSpacingPolicy extends SGridSpacingPolicy {
		/** The space */
		private int space;

		/** Constructor */
		public FixedSpacingPolicy(int space) {
			this.space = space;
		}

		/** The implementation */
		public void assignSpacing(int[] values, int total) {
			for (int x = values.length - 1; x >= 0; x--)
				values[x] = space;
		}
	}

	/**
	 * Spacing policy that will allocate a fixed amount between each column
	 */
	public static class FixedInsideSpacingPolicy extends SGridSpacingPolicy {
		/** The space */
		private int space;

		/** Constructor */
		public FixedInsideSpacingPolicy(int space) {
			this.space = space;
		}

		/** The implementation */
		public void assignSpacing(int[] values, int total) {
			for (int x = values.length - 2; x >= 1; x--)
				values[x] = space;
		}
	}

	/**
	 * Spacing policy that will allocate a percentage of the extra space to be
	 * devoted to the spacing between columns
	 */
	public static class PercentageSpacingPolicy extends SGridSpacingPolicy {
		/** The percentage of extra space to use */
		private double percentage;

		/** Constructor */
		public PercentageSpacingPolicy(double percentage) {
			if (percentage > 1.0) throw new IllegalArgumentException("Percentage cannot be greater than 1.0 [100%]");
			this.percentage = percentage;
		}

		/** The implementation */
		public void assignSpacing(int[] values, int total) {
			int extraSpace = total - getTotalSize(values);
			if (extraSpace < 0) extraSpace = 0;
			double perValue = (total * percentage) / values.length;
			for (int x = values.length - 1; x >= 0; x--)
				values[x] = (int) perValue;
		}
	}

	/**
	 * Spacing policy that will allocate a percentage of the extra space to be
	 * devoted to the spacing between columns
	 */
	public static class PercentageInsideSpacingPolicy extends SGridSpacingPolicy {
		/** The percentage of extra space to use */
		private double percentage;

		/** Constructor */
		public PercentageInsideSpacingPolicy(double percentage) {
			if (percentage > 1.0) throw new IllegalArgumentException("Percentage cannot be greater than 1.0 [100%]");
			this.percentage = percentage;
		}

		/** The implementation */
		public void assignSpacing(int[] values, int total) {
			if (values.length <= 2) return;
			int extraSpace = total - getTotalSize(values);
			if (extraSpace < 0) extraSpace = 0;
			double perValue = (total * percentage) / (values.length - 2);
			for (int x = values.length - 2; x >= 1; x--)
				values[x] = (int) perValue;
		}
	}

	/**
	 * Spacing policy that will allocate a percentage of the extra space to be
	 * devoted to the spacing between columns
	 */
	public static class EdgePercentageSpacingPolicy extends SGridSpacingPolicy {
		/** The percentage of extra space to use */
		private double percentage;

		/** The percentage of the percentage above to allocate to the edges */
		private double edgePercentage;

		/** Constructor */
		public EdgePercentageSpacingPolicy(double percentage, double edgePercentage) {
			if (percentage > 1.0) throw new IllegalArgumentException("Percentage cannot be greater than 1.0 [100%]");
			this.percentage = percentage;
			this.edgePercentage = edgePercentage;
		}

		/** The implementation */
		public void assignSpacing(int[] values, int total) {
//			System.out.println(">>> Total length: " + total + " over " + values.length);
			// Figure out how much space we work with
			double workWith = (total * percentage);
			
			// What portion is allocated to the edges
			double edges = workWith * edgePercentage;
			
			// Special cases
			switch (values.length) {
			case 0:
				return;
			case 1: {
				values[0] = (int) workWith;
				return;
			}
			case 2: {
				values[0] = (int) workWith / 2;
				values[1] = values[0];
				return;
			}
			}
			// NOTE: we can assume there are 3 or more columns at this point...
			// Figure out what the internal values will have
			double perValue = (workWith - edges) / (values.length - 2.);
			// Figure out how to allocate them...
			for (int x = values.length - 1; x >= 0; x--) {
				if (x == 0 || x == values.length - 1) {
					values[x] = (int) (edges / 2.0);
				} else {
					values[x] = (int) perValue;
				}
//				System.out.println(">>> Assigning: " + x + ":" + values[x]);
			}
		}
	}

	/**
	 * Spacing policy that will set the space after the first row to a constant
	 * value
	 */
	public static class HeaderSpacingPolicy extends SGridSpacingPolicy {
		/** The the other grid spacing policy */
		private SGridSpacingPolicy other;

		/** Minimum Space between the header and the first row */
		private int headerSpace;

		/** Constructor */
		public HeaderSpacingPolicy(int headerSpace, SGridSpacingPolicy other) {
			this.other = other;
			this.headerSpace = headerSpace;
		}

		/** The implementation */
		public void assignSpacing(int[] values, int total) {
			// If none, ignore
			if (values.length == 0) return;
			if (values.length == 1) {
				values[0] = headerSpace;
				return;
			}
//			System.out.println(">>> Header: " + total + ":" + headerSpace + " over " + values.length);
			// Allocate the array minus 1 (header and space before header)
			int[] v2 = new int[values.length - 1];
			System.arraycopy(values, 1, v2, 0, v2.length);
			
			// Ask the other for the spacing
			other.assignSpacing(v2, total - headerSpace);
			
			// Assign them back to the non-zero element
			System.arraycopy(v2, 0, values, 1, v2.length);
			values[0] = 0;
			values[1] = Math.max(headerSpace, values[1]);
//			for(int x = 0; x < values.length; x++) {
//				System.out.println("Assigning: " + values[x]);
//			}
		}
	}
	
}
