#!/usr/bin/python
import h5py
import pandas as pd
import MySQLdb
from sql import *
from sql.aggregate import *
from sql.conditionals import *
import getpass


#setting up hdf5 variables
hdf5_file_name = './ADS.h5'
dataset_name = 'RetailStates'
event_number = 0

#setting up database connection
databaseURL = raw_input("Database URL: ")
databaseUser = raw_input("Username: ")
databasePassword = getpass.getpass()
databaseName = raw_input("Database Name: ")
db = MySQLdb.connect(databaseURL, databaseUser, databasePassword, databaseName)

file = h5py.File(hdf5_file_name, 'r')   # 'r' means that hdf5 file is open in read-only mode
dataset = file[dataset_name]
for a in dataset:
    print a
    #TODO: insert into database
    
file.close()
db.close()

