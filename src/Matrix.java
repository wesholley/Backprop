// ----------------------------------------------------------------
// The contents of this file are distributed under the CC0 license.
// See http://creativecommons.org/publicdomain/zero/1.0/
// ----------------------------------------------------------------

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Iterator;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Exception;
import java.util.HashSet;
import java.util.Set;

public class Matrix {
	// Data
	ArrayList< double[] > m_data;

	// Meta-data
	ArrayList< String > m_attr_name;
	ArrayList< TreeMap<String, Integer> > m_str_to_enum;
	ArrayList< TreeMap<Integer, String> > m_enum_to_str;

	static double MISSING = Double.MAX_VALUE; // representation of missing values in the data set

	// Creates a 0x0 matrix. You should call loadARFF or setSize next.
	public Matrix() {}
	
	// Creates a new matrix by copying the Matrix passed in
	public Matrix(Matrix that)	{
		this.m_data = new ArrayList< double[] >();

		for(int i = 0; i < that.rows(); i++)	{
			double[] rowSrc = that.row(i);
			double[] rowDest = new double[that.cols()];
			for(int j = 0; j < that.cols(); j++)	{
				rowDest[j] = rowSrc[j];
			}
			m_data.add(rowDest);
		}
		
		this.m_attr_name = new ArrayList<String>();
		this.m_str_to_enum = new ArrayList< TreeMap<String, Integer> >();
		this.m_enum_to_str = new ArrayList< TreeMap<Integer, String> >();
		
		for(int i = 0; i < that.cols(); i++) {
			m_attr_name.add(that.attrName(i));
			m_str_to_enum.add(that.m_str_to_enum.get(i));
			m_enum_to_str.add(that.m_enum_to_str.get(i));
		}
	}

	// Copies the specified portion of that matrix into this matrix
	public Matrix(Matrix that, int rowStart, int colStart, int rowCount, int colCount) {
		m_data = new ArrayList< double[] >();
		for(int j = 0; j < rowCount; j++) {
			double[] rowSrc = that.row(rowStart + j);
			double[] rowDest = new double[colCount];
			for(int i = 0; i < colCount; i++)
				rowDest[i] = rowSrc[colStart + i];
			m_data.add(rowDest);
		}
		m_attr_name = new ArrayList<String>();
		m_str_to_enum = new ArrayList< TreeMap<String, Integer> >();
		m_enum_to_str = new ArrayList< TreeMap<Integer, String> >();
		for(int i = 0; i < colCount; i++) {
			m_attr_name.add(that.attrName(colStart + i));
			m_str_to_enum.add(that.m_str_to_enum.get(colStart + i));
			m_enum_to_str.add(that.m_enum_to_str.get(colStart + i));
		}
	}
		
	// Removes all rows from the Features and Labels matrices, except where 
	// attributeColumn contains value attributeValue
	public static void selectAttributeValue(Matrix features, Matrix labels, int attributeColumn, double attributeValue)	{
		ArrayList< double[] > features_m_data_new = new ArrayList< double[] >();
		ArrayList< double[] > labels_m_data_new = new ArrayList< double[] >();
		
		for(int i = 0; i < features.rows(); i++)	{								//loop through the rows
			if(features.row(i)[attributeColumn] == attributeValue)	{				//deep copy the row
				//copy the features row
				double[] featuresSrcRow = features.row(i);
				double[] featuresNewRow = new double[features.cols()];
				for(int j = 0; j < features.cols(); j++)	{
					featuresNewRow[j] = featuresSrcRow[j];
				}
				features_m_data_new.add(featuresNewRow);
				
				//copy the labels row
				double[] labelsNewRow = new double[labels.cols()];
				labelsNewRow[0] = labels.get(i,0);
				labels_m_data_new.add(labelsNewRow);
			}
		}
		
		//copy features data
		features.m_data = features_m_data_new;
		ArrayList< String > features_m_attr_name_new = new ArrayList<String>();
		ArrayList< TreeMap<String, Integer> > features_m_str_to_enum_new = new ArrayList< TreeMap<String, Integer> >();
		ArrayList< TreeMap<Integer, String> > features_m_enum_to_str_new = new ArrayList< TreeMap<Integer, String> >();
		
		for(int i = 0; i < features.cols(); i++) {
			features_m_attr_name_new.add(features.attrName(i));
			features_m_str_to_enum_new.add(features.m_str_to_enum.get(i));
			features_m_enum_to_str_new.add(features.m_enum_to_str.get(i));
		}
		
		//copy labels data
		labels.m_data = labels_m_data_new;
		ArrayList< String > labels_m_attr_name_new = new ArrayList<String>();
		ArrayList< TreeMap<String, Integer> > labels_m_str_to_enum_new = new ArrayList< TreeMap<String, Integer> >();
		ArrayList< TreeMap<Integer, String> > labels_m_enum_to_str_new = new ArrayList< TreeMap<Integer, String> >();
		
		for(int i = 0; i < labels.cols(); i++) {
			labels_m_attr_name_new.add(labels.attrName(i));
			labels_m_str_to_enum_new.add(labels.m_str_to_enum.get(i));
			labels_m_enum_to_str_new.add(labels.m_enum_to_str.get(i));
		}
	}
	
	// Adds a copy of the specified portion of that matrix to this matrix
	public void add(Matrix that, int rowStart, int colStart, int rowCount) throws Exception {
		if(colStart + cols() > that.cols())
			throw new Exception("out of range");
		for(int i = 0; i < cols(); i++) {
			if(that.valueCount(colStart + i) != valueCount(i))
				throw new Exception("incompatible relations");
		}
		for(int j = 0; j < rowCount; j++) {
			double[] rowSrc = that.row(rowStart + j);
			double[] rowDest = new double[cols()];
			for(int i = 0; i < cols(); i++)
				rowDest[i] = rowSrc[colStart + i];
			m_data.add(rowDest);
		}
	}
	
	// Resizes this matrix (and sets all attributes to be continuous)
	public void setSize(int rows, int cols) {
		m_data = new ArrayList< double[] >();
		for(int j = 0; j < rows; j++) {
			double[] row = new double[cols];
			m_data.add(row);
		}
		m_attr_name = new ArrayList<String>();
		m_str_to_enum = new ArrayList< TreeMap<String, Integer> >();
		m_enum_to_str = new ArrayList< TreeMap<Integer, String> >();
		for(int i = 0; i < cols; i++) {
			m_attr_name.add("");
			m_str_to_enum.add(new TreeMap<String, Integer>());
			m_enum_to_str.add(new TreeMap<Integer, String>());
		}
	}

	// Loads from an ARFF file
	public void loadArff(String filename) throws Exception, FileNotFoundException {
		m_data = new ArrayList<double[]>();
		m_attr_name = new ArrayList<String>();
		m_str_to_enum = new ArrayList< TreeMap<String, Integer> >();
		m_enum_to_str = new ArrayList< TreeMap<Integer, String> >();
		boolean READDATA = false;
		Scanner s = new Scanner(new File(filename));
		while (s.hasNext()) {
			String line = s.nextLine().trim();
			if (line.length() > 0 && line.charAt(0) != '%') {
				if (!READDATA) {
					
					Scanner t = new Scanner(line);
					String firstToken = t.next().toUpperCase();
					
					if (firstToken.equals("@RELATION")) {
						String datasetName = t.nextLine();
					}
					
					if (firstToken.equals("@ATTRIBUTE")) {
						TreeMap<String, Integer> ste = new TreeMap<String, Integer>();
						m_str_to_enum.add(ste);
						TreeMap<Integer, String> ets = new TreeMap<Integer, String>();
						m_enum_to_str.add(ets);

						Scanner u = new Scanner(line);
						if (line.indexOf("'") != -1) u.useDelimiter("'");
						u.next();
						String attributeName = u.next();
						if (line.indexOf("'") != -1) attributeName = "'" + attributeName + "'";
						m_attr_name.add(attributeName);

						int vals = 0;
						String type = u.next().trim().toUpperCase();
						if (type.equals("REAL") || type.equals("CONTINUOUS") || type.equals("INTEGER") || type.equals("NUMERIC")) {
						}
						else {
							try {
								String values = line.substring(line.indexOf("{")+1,line.indexOf("}"));
								Scanner v = new Scanner(values);
								v.useDelimiter(",");
								while (v.hasNext()) {
									String value = v.next().trim();
									if(value.length() > 0)
									{
										ste.put(value, new Integer(vals));
										ets.put(new Integer(vals), value);
										vals++;
									}
								}
							}
							catch (Exception e) {
								throw new Exception("Error parsing line: " + line + "\n" + e.toString());
							}
						}
					}
					if (firstToken.equals("@DATA")) {
						READDATA = true;
					}
				}
				else {
					double[] newrow = new double[cols()];
					int curPos = 0;

					try {
						Scanner t = new Scanner(line);
						t.useDelimiter(",");
						while (t.hasNext()) {
							String textValue = t.next().trim();
							//System.out.println(textValue);

							if (textValue.length() > 0) {
								double doubleValue;
								int vals = m_enum_to_str.get(curPos).size();
								
								//Missing instances appear in the dataset as a double defined as MISSING
								if (textValue.equals("?")) {
									doubleValue = MISSING;
								}
								// Continuous values appear in the instance vector as they are
								else if (vals == 0) {
									doubleValue = Double.parseDouble(textValue);
								}
								// Discrete values appear as an index to the "name" 
								// of that value in the "attributeValue" structure
								else {
									doubleValue = m_str_to_enum.get(curPos).get(textValue);
									if (doubleValue == -1) {
										throw new Exception("Error parsing the value '" + textValue + "' on line: " + line);
									}
								}
								
								newrow[curPos] = doubleValue;
								curPos++;
							}
						}
					}
					catch(Exception e) {
						throw new Exception("Error parsing line: " + line + "\n" + e.toString());
					}
					m_data.add(newrow);
				}
			}
		}
	}

	// Returns the number of rows in the matrix
	int rows() { return m_data.size(); }

	// Returns the number of columns (or attributes) in the matrix
	int cols() { return m_attr_name.size(); }

	// Returns the specified row
	double[] row(int r) { return m_data.get(r); }
	
	// Returns the specified column
	double[] getColumn(int colNum)	{
		int numRows = rows();
		double column[] = new double[rows()];
		
		for(int rowNum = 0; rowNum < numRows; rowNum++){
			column[rowNum] = get(rowNum,colNum);
		}
		
		return column;
	}
	
	// Return the number of unique values in a column
	int getUniqueValues(int c)	{
		double[] columnValues = getColumn(c);
		Set<Double> uniqueNumbers = new HashSet<Double>();
		
		for(double d : columnValues)
			uniqueNumbers.add(d);
		
		return uniqueNumbers.size();
	}
	
	// Return a Set of unique values in a column
	Set<Double> getUniqueValuesSet(int c)	{
		double[] columnValues = getColumn(c);
		Set<Double> uniqueNumbers = new HashSet<Double>();
		
		for(double d : columnValues)
			uniqueNumbers.add(d);
		
		return uniqueNumbers;
	}
	
	// Return an array of unique values in a column
	double[] getUniqueValuesArray(int c)	{
		double[] columnValues = getColumn(c);
		Set<Double> uniqueNumbers = new HashSet<Double>();
		
		for(double d : columnValues)
			uniqueNumbers.add(d);
		
		ArrayList<Double> uniqueValues = new ArrayList<Double>(uniqueNumbers);
		double[] toReturn = new double[uniqueValues.size()];
		
		for(int i = 0; i < toReturn.length; i++)	{
			toReturn[i] = uniqueValues.get(i);
		}
			
		return toReturn;
	}
	
	//REMOVE
//	// Returns a map of the unique values in a column as well as the name of the value they map to
//	TreeMap<Double,String> getUniqueValuesMap(int c)	{
//		TreeMap<Double,String> valuesAndNames = new TreeMap<Double,String>();
//		double[] columnValues = getColumn(c);
//		for(int i = 0; i < columnValues.length; i++)	{
//			this.print();
////			System.out.println("TTRIBUTEVALUE: " + attrValue(c,i));
//			System.out.println("\t" + attrValue(3,2));
//			System.out.println("\t" + get(3,2));
//			valuesAndNames.put(columnValues[i], attrValue(c, i));
//		}
//		
//		return valuesAndNames;
//	}
	
	// Returns the element at the specified row and column
	double get(int r, int c) { return m_data.get(r)[c]; }

	// Sets the value at the specified row and column
	void set(int r, int c, double v) { row(r)[c] = v; }

	// Returns the name of the specified attribute
	String attrName(int col) { return m_attr_name.get(col); }

	// Set the name of the specified attribute
	void setAttrName(int col, String name) { m_attr_name.set(col, name); }

	// Returns the name of the specified value
	String attrValue(int attr, int val) { return m_enum_to_str.get(attr).get(val); }

	// Returns the number of values associated with the specified attribute (or column)
	// 0=continuous, 2=binary, 3=trinary, etc.
	int valueCount(int col) { return m_enum_to_str.get(col).size(); }

	// Shuffles the row order
	void shuffle(Random rand) {
		for(int n = rows(); n > 0; n--) {
			int i = rand.nextInt(n);
			double[] tmp = row(n - 1);
			m_data.set(n - 1, row(i));
			m_data.set(i, tmp);
		}
	}

	// Returns the mean of the specified column
	double columnMean(int col) {
		double sum = 0;
		int count = 0;
		for(int i = 0; i < rows(); i++) {
			double v = get(i, col);
			if(v != MISSING)
			{
				sum += v;
				count++;
			}
		}
		return sum / count;
	}

	// Returns the median of the specified column
	double columnMedian(int col)	{
		double[] column = getColumn(col);
		Arrays.sort(column);
		int middle = column.length / 2;
		
		if (column.length % 2 == 0)
	    {
	      double left = column[middle - 1];
	      double right = column[middle];
	      return (left + right) / 2;
	    }
	    else
	    {
	      return column[middle];
	    }
	}
	
	// Returns the min value in the specified column
	double columnMin(int col) {
		double m = MISSING;
		for(int i = 0; i < rows(); i++) {
			double v = get(i, col);
			if(v != MISSING)
			{
				if(m == MISSING || v < m)
					m = v;
			}
		}
		return m;
	}

	// Returns the max value in the specified column
	double columnMax(int col) {
		double m = MISSING;
		for(int i = 0; i < rows(); i++) {
			double v = get(i, col);
			if(v != MISSING)
			{
				if(m == MISSING || v > m)
					m = v;
			}
		}
		return m;
	}

	// Returns the most common value in the specified column
	double mostCommonValue(int col) {
		TreeMap<Double, Integer> tm = new TreeMap<Double, Integer>();
		for(int i = 0; i < rows(); i++) {
			double v = get(i, col);
			if(v != MISSING)
			{
				Integer count = tm.get(v);
				if(count == null)
					tm.put(v, new Integer(1));
				else
					tm.put(v, new Integer(count.intValue() + 1));
			}
		}
		int maxCount = 0;
		double val = MISSING;
		Iterator< Entry<Double, Integer> > it = tm.entrySet().iterator();
		while(it.hasNext())
		{
			Entry<Double, Integer> e = it.next();
			if(e.getValue() > maxCount)
			{
				maxCount = e.getValue();
				val = e.getKey();
			}
		}
		return val;
	}

	void normalize() {
		for(int i = 0; i < cols(); i++) {
			if(valueCount(i) == 0) {
				double min = columnMin(i);
				double max = columnMax(i);
				for(int j = 0; j < rows(); j++) {
					double v = get(j, i);
					if(v != MISSING)
						set(j, i, (v - min) / (max - min));
				}
			}
		}
	}

	void print() {
		System.out.println("@RELATION Untitled");
		for(int i = 0; i < m_attr_name.size(); i++) {
			System.out.print("@ATTRIBUTE " + m_attr_name.get(i));
			int vals = valueCount(i);
			if(vals == 0)
				System.out.println(" CONTINUOUS");
			else
			{
				System.out.print(" {");
				for(int j = 0; j < vals; j++) {
					if(j > 0)
						System.out.print(", ");
					System.out.print(m_enum_to_str.get(i).get(j));
				}
				System.out.println("}");
			}
		}
		System.out.println("@DATA");
		for(int i = 0; i < rows(); i++) {
			double[] r = row(i);
			for(int j = 0; j < r.length; j++) {
				if(j > 0)
					System.out.print(", ");
				if(valueCount(j) == 0)
					System.out.print(r[j]);
				else
					System.out.print(m_enum_to_str.get(j).get((int)r[j]));
			}
			System.out.println("");
		}
	}

//	public void printValues(String label)	{
//		System.out.println("\n" + label);
//		for(int i = 0; i < rows(); i++)	{
//			for(int j = 0; j < cols(); j++)	{
//				System.out.print(get(i, j) + "\t");
//			}
//			System.out.println("");
//		}
//		System.out.println("\n");
//	}
	
}
