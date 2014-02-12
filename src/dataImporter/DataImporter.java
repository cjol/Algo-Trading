/** DataImporter
 * This class is used to load data from the provided .m5 files
 * into our SQL database
 * @author Nick Burscough
 * 
 */

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;

public class DataImporter{

	private static String FILENAME = "ADS.h5"; //Test stock, implement all stocks later
	private static String DATASETNAME = "RetailStates"; //it seems hdf5 files have multiple tables
													   //this one looks most relevant
	private static final int DIM_X = 11;
	private static final int DIM_Y = 200000;           //table has ~110000 rows

	/**Loads all data from .h5 files into SQL database
	* @param none     Arguments could be added, though no implemented needed right now
	*/
	public static void ImportData() {   
		/*TODO: add arguments to this function i.e. which stocks do we want?*/
		/**file_id loading success flag*/
		int file_id = -1;

		/**dataset_id loading success flag*/
		int dataset_id = -1;

		/**dimensions of table in h5 file*/
		long[] dims = { DIM_X, DIM_Y }; 

		/**eventual store of data*/
		int[][] dset_data = new int[DIM_X][DIM_Y];


		//opening the file
		try{
			file_id = H5.H5Fopen(FILENAME, HDF5Constants.H5F_ACC_RDRW,
				HDF5Constants.H5P_DEFAULT);
		} catch (Exception e){
			e.printStackTrace();
		}

		//opening the specific table
		try{
			if (file_id >= 0){
				dataset_id = H5.H5Dopen(file_id, DATASETNAME,
					HDF5Constants.H5P_DEFAULT);
			}
		} catch (Exception e){
			e.printStackTrace();
		}

		//attempt to read data
		try{
			if(dataset_id >= 0){
				H5.H5Dread(dataset_id, HDF5Constants.H5T_NATIVE_INT,
					HDF5Constants.H5S_ALL,
					HDF5Constants.H5S_ALL,
					HDF5Constants.H5P_DEFAULT,
					dset_data);
			}
		} catch (Exception e){
			e.printStackTrace();
		}

		/*TODO: Route data from dset_data to SQL*/

		//close dataset
		try {
			if (dataset_id >= 0)
				H5.H5Dclose(dataset_id);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		//close file
		try {
			if (file_id >= 0)
				H5.H5Fclose(file_id);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

}