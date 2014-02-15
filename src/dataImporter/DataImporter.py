#!/usr/bin/python
import h5py
import pandas as pd

#f = h5py.File("./ADS.h5", 'r')
#d = pd.DataFrame(f[type].value)
#d.set_index('timestamp', drop=True, inplace=True, verify_integrity=False)

hdf5_file_name = './ADS.h5'
dataset_name = 'RetailStates'
event_number = 0

file = h5py.File(hdf5_file_name, 'r')   # 'r' means that hdf5 file is open in read-only mode
dataset = file[dataset_name]
for a in dataset:
    print a
file.close()

