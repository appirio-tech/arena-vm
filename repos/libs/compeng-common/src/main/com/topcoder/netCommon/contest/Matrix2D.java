package com.topcoder.netCommon.contest;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 *
 * Matrix2D is a TopCoder developed class to represent two dimensional matrices.
 * The Matrix2D has R rows and C columns numbered 0 to R-1 and 0 to C-1.
 * Element are referred to by ordered pairs of the form (int row, int column).
 * (0,0) is the upper left element, (R-1, C-1) is the lower right element.
 * <br/>
 * <br/>
 * <a href="../classes/Matrix2D.class">Click here to Download Matrix2D.class</a>
 * <br/>
 * <a href="../source/Matrix2D.java">Click here to Download Matrix2D.java</a>
 * <br/>
 * @author talub and ads09
 *
 */

public final class Matrix2D implements Serializable, CustomSerializable {


    /**
     *
     * default constructor - makes a 0x0 matrix
     *
     */
    public Matrix2D() {
        _matrix = new ArrayList();
        _rows = 0;
        _cols = 0;
    }


    /**
     *
     * Creates a Matrix2D based on mArray.  mArray is an ArrayList of ArrayLists.
     * Each ArrayList in mArray has the same length.
     *
     * @param mArray an ArrayList of ArrayLists.
     *
     */
    public Matrix2D(ArrayList mArray) {
        setRowsCols(mArray);

        _matrix = new ArrayList();
        int count1, count2;
        ArrayList temp1,temp2;


        for (count1 = 0; count1 < _rows; count1++) {
            temp1 = (ArrayList) mArray.get(count1);
            temp2 = new ArrayList();
            for (count2 = 0; count2 < _cols; count2++)
                temp2.add(temp1.get(count2));
            _matrix.add(temp2);
        }
    }

    private void setRowsCols(ArrayList mArray) {
        _rows = mArray.size();
        if (_rows > 0)
            _cols = ((ArrayList) mArray.get(0)).size();
        else
            _cols = 0;
    }


    /**
     *
     * Creates rows0 rows cols0 columns.
     * Matrix2D and initializes every element to the String "0".
     *
     * @param rows0 int number of rows.
     * @param cols0 int number of columns.
     *
     */
    public Matrix2D(int rows0, int cols0) {
        _matrix = new ArrayList();
        ArrayList ltemp;
        int a,b;
        for (a = 0; a < rows0; a++) {
            ltemp = new ArrayList();
            for (b = 0; b < cols0; b++) {
                ltemp.add("0");
            }
            _matrix.add(ltemp);
        }
        _rows = rows0;
        _cols = cols0;
    }


    /**
     *
     * Creates rows0 rows cols0 columns.
     * Matrix2D and initializes every element to the String "0" if initialized boolean true.
     *
     * @param rows0 int number of rows.
     * @param cols0 int number of columns.
     * @param initialized boolean initializes every element to the String "0" if initialized boolean true.
     * If false, there are 0 rows.
     *
     */
/*
  public Matrix2D(int rows0,int cols0, boolean initialized)
  {
    _matrix = new ArrayList();
    ArrayList ltemp;
    int a,b;
    for (a=0;a<rows0;a++)
    {
      ltemp = new ArrayList(cols0);
      if(initialized)
      {
        for (b=0;b<cols0;b++)
        {
          ltemp.add("0");
        }
      }
      _matrix.add(ltemp);
    }

    if(initialized)
    {
      _rows = rows0;
      _cols = cols0;
    }
    else
    {
      _rows = 0;
      _cols = cols0;
    }
  }
  */


    /**
     *
     * Sets the element at (row,col) in the Matrix2D to element0.
     *
     * @param row0
     * @param col0
     * @param element0
     *
     */
    public void set(int row0, int col0, Object element0) {
        ArrayList ltemp = (ArrayList) _matrix.get(row0);
        ltemp.set(col0, element0);
        _matrix.set(row0, ltemp);
    }


    /**
     *
     * Returns the Object at (row,col) in the Matrix2D.
     *
     * @param row0
     * @param col0
     * @return Object from Matrix2D
     *
     */
    public Object get(int row0, int col0) {
        ArrayList ltemp = (ArrayList) _matrix.get(row0);
        return ltemp.get(col0);
    }


    /**
     *
     * Adds a row to the bottom of the Matrix2D.  Initializes elements in row to "0" as a String.
     *
     */
/*
  public void addRow()
  {
    _rows++;
    ArrayList al = new ArrayList(_cols);
    for (int i = 0; i < _cols; i++)
      al.add("0");
    _matrix.add(al);

    if(VERBOSE) System.out.println("AFTER ADD ROW(): " + _matrix);
  }
  */


    /**
     *
     * Adds a column to the right of the Matrix2D.  Initializes elements in column to "0" as a String.
     *
     */
/*
  public void addCol()
  {
    _cols++;
    for (int i = 0; i < _rows; i++)
    {
      ((ArrayList) _matrix.get(i)).add("0");
    }

    if(VERBOSE) System.out.println("AFTER ADD COL(): " + _matrix);
  }
  */


    /**
     *
     * Inserts a row into the Matrix2D at the index row.  Initializes elements in row to "0" as a String.
     *
     * @param index int index for the location to insert row.
     *
     */
    public void addRow(int index) {
        _rows++;
        ArrayList al = new ArrayList(_cols);
        for (int i = 0; i < _cols; i++)
            al.add("0");
        _matrix.add(index, al);

        if (VERBOSE) System.out.println("AFTER ADD ROW(" + index + "): " + _matrix);
    }


    /**
     *
     * Inserts a column into Matrix2D at the index column.  Initializes elements in column to "0" as a String.
     *
     * @param index int index for the location to insert column.
     *
     */
/*
  public void addCol(int index)
  {
    _cols++;
    for (int i = 0; i < _rows; i++)
    {
      ((ArrayList) _matrix.get(i)).add(index, "0");
    }

    if(VERBOSE) System.out.println("AFTER ADD COL(" + index + "): " + _matrix);
  }
  */


    /**
     *
     * Removes the bottom row of the Matrix2D and returns the row as an ArrayList.
     *
     * @return ArrayList bottom row of the Matrix2D.
     *
     */
/*
  public ArrayList removeRow()
  {
    _rows--;
    return (ArrayList) _matrix.remove(_rows);
  }
  */


    /**
     *
     * Removes and return as an ArrayList the index column and shifts left all rows to its right.
     *
     * @return ArrayList
     *
     */
/*
  public ArrayList removeCol()
  {
    ArrayList retVal = new ArrayList(_rows);
    _cols--;
    ArrayList row = null;
    for (int i = 0; i < _rows; i++)
    {
      row = (ArrayList) _matrix.get(i);
      retVal.add(row.remove(_cols));
    }

    return retVal;
  }
  */


    /**
     *
     * Removes and returns as an ArrayList the index row and shifts up all rows below it.
     *
     * @param index int for location to remove row from.
     * @return ArrayList
     *
     */
/*
  public ArrayList removeRow(int index)
  {
    _rows--;
    return (ArrayList) _matrix.remove(index);
  }
  */


    /**
     *
     * Removes and return as an ArrayList the index column and shifts left all rows to its right.
     *
     * @param index int for location to remove column from.
     * @return ArrayList
     *
     */
/*
  public ArrayList removeCol(int index)
  {
    ArrayList retVal = new ArrayList(_rows);

    _cols--;
    ArrayList row = null;
    for (int i = 0; i < _rows; i++)
    {
      row = (ArrayList) _matrix.get(i);
      retVal.add(row.remove(index));
    }

    return retVal;
  }
  */


    /**
     *
     * Returns the index row as an ArrayList.
     *
     * @param index int for location to remove row from.
     * @return ArrayList
     *
     */
    public ArrayList getRow(int index) {
        return (ArrayList) _matrix.get(index);
    }


    /**
     *
     * Returns the index column as an ArrayList.
     *
     * @param index int for location to remove column from.
     * @return ArrayList
     *
     */
    public ArrayList getCol(int index) {
        ArrayList retVal = new ArrayList(_rows);

        for (int i = 0; i < _rows; i++) {
            retVal.add(((ArrayList) _matrix.get(i)).get(index));
        }

        return retVal;

    }


    /**
     *
     * Sets the index row to be data.
     *
     * @param index int for location to set row.
     * @param data to set at index.
     *
     */
/*
  public void setRow(int index, ArrayList data)
  {
    _matrix.set(index, data);

    if(VERBOSE) System.out.println("AFTER SET ROW(" + index + ", " + data+"): " + _matrix);
  }
  */


    /**
     *
     * Sets the index column to be data.
     *
     * @param index int for location to set column.
     * @param data to set at index.
     *
     */
/*
  public void setCol(int index, ArrayList data)
  {
    ArrayList row = null;

    for(int i = 0; i < _rows; i++)
    {
      row = (ArrayList) _matrix.get(i);
      row.set(index, data.get(i));
    }

    if(VERBOSE) System.out.println("AFTER SET COL(" + index + ", " + data+"): " + _matrix);
  }
  */


    /**
     *
     * Returns the matrix formed by the first r rows and c columns as an ArrayList of ArrayLists, in row-major order.
     *
     * @param r int number of rows used to generate matrix.
     * @param c int number of columns used to generate matrix.
     * @return ArrayList matrix.
     *
     */
/*
  public ArrayList getSubMatrix(int r, int c)
  {
    ArrayList retVal = new ArrayList(r);

    for(int i = 0; i < r; i++)
    {
      retVal.add(new ArrayList(((ArrayList) _matrix.get(i)).subList(0, c)));
    }

    return retVal;
  }
  */


    /**
     *
     * Returns the number of rows in the Matrix2D.
     *
     * @return int number of rows.
     *
     */
    public int numRows() {
        return _rows;
    }


    /**
     *
     * Returns the number of columns in the Matrix2D.
     *
     * @return int number of columns.
     *
     */
    public int numCols() {
        return _cols;
    }


    /**
     *
     * Returns the Matrix2D as a string of form:
     *
     * Example:<br>
     * [row0]<br>
     * [row1]<br>
     * [row2]<br>
     * ...<br>
     *
     * @return String representation of matrix.
     *
     */
    public String toString() {
/*    String lout="[";
    ArrayList ltemp=new ArrayList();
    int b;
    for (int a=0;a<_rows;a++)
    {
      lout += "[";
      ltemp=(ArrayList)_matrix.get(a);
      for (b=0;b<_cols;b++)
      {
        if (b!=0) lout+=",";
        lout += ltemp.get(b).toString();
      }
      lout += "]";
    }
    return lout+"]";*/

        String retVal = "";
        for (int i = 0; i < _rows; i++)
            retVal += _matrix.get(i).toString() + "\n";

        return retVal;
    }


    /**
     *
     * Returns the Matrix2D as an ArrayList of ArrayLists, in row-major order (ArrayLists of rows).
     *
     * @return ArrayList of ArrayLists.
     *
     */
    public ArrayList toArrayList() {
        return _matrix;
    }


    /**
     *
     * Returns a shallow copy of the Matrix2D, as an Object.
     *
     * @return Object copy of Matrix2D.
     *
     */
    public Object clone() {
        Matrix2D second = new Matrix2D();
        second._rows = _rows;
        second._cols = _cols;
        second._matrix = new ArrayList(_rows);
        for (int i = 0; i < _rows; i++) {
            ArrayList nextRow = (ArrayList) ((ArrayList) _matrix.get(i)).clone();
            second._matrix.add(nextRow);
        }
        return second;
    }

    public boolean equals(Object object) {
        if (!(object instanceof Matrix2D)) {
            return false;
        }
        return _matrix.equals(((Matrix2D) object)._matrix);
    }


    private ArrayList _matrix;
    private int _rows;
    private int _cols;
    private static boolean VERBOSE = false;

    public void customWriteObject(CSWriter writer) throws IOException {
        // Synchronization here to keep the data consistent while sending to a client in the case of sets called
        // during writing. The ContestRoom updateChallengeTable is the main contention for this lock.
        synchronized (this) {
            writer.writeArrayList(_matrix);
        }
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        _matrix = reader.readArrayList();
        setRowsCols(_matrix);
    }

}
