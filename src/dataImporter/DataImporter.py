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
cursor = db.cursor()
cursor.execute('INSERT INTO datasets(dataset_id,name) VALUES (0,"ADS");')

file = h5py.File(hdf5_file_name, 'r')   # 'r' means that hdf5 file is open in read-only mode
dataset = file[dataset_name]
#TODO:formattedDataset = <Array of tuples of form (int,string,timestamp,ask/buy,int,int)>
#TODO:Transform dataset to formattedDataset

for data in dataset:
    print data
    #TODO: insert into database

file.close()
db.close()

#example insert - need to change format of data to match database schema
#INSERT INTO trades(dataset_id,ticker,ts,bid_or_ask,price,volume) VALUES (0,"FOO","2014-01-01 00:00:00","B",9,200);
#INSERT INTO trades(dataset_id,ticker,ts,bid_or_ask,price,volume) VALUES (0,"BAR","2014-01-01 00:00:00","B",9,200);
