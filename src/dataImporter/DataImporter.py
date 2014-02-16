#!/usr/bin/python
import h5py
import pandas as pd
import MySQLdb
import getpass
from os import listdir
from os.path import isfile, join

#setting up hdf5 variables
hdf5_file_name = '../RawData/ADS.h5'
dataset_name = 'RetailStates'
event_number = 0

#grabbing all .m5 files
mypath = '../RawData'
rawFiles = [f for f in listdir(mypath) if isfile(join(mypath, f))]

#setting up database connection
databaseURL = raw_input("Database URL: ")
databaseUser = raw_input("Username: ")
databasePassword = getpass.getpass()
databaseName = raw_input("Database Name: ")
db = MySQLdb.connect(databaseURL, databaseUser, databasePassword, databaseName)
cursor = db.cursor()

#populating securities table
securityIDIterator = 0
for file in rawFiles:
    print 'Importing ',file
    cursor.execute('INSERT INTO datasets(dataset_id,name) VALUES (%s,%s);'
                   , securityIDIterator, rawFiles[securityIDIterator])
    securityIDIterator += 1

#TODO:Change this to iterate over all files instead of a single test file
file = h5py.File(hdf5_file_name, 'r')   # 'r' means that hdf5 file is open in read-only mode
dataset = file[dataset_name]
numberOfData = dataset.len()
#TODO:formattedDataset = <Array of tuples of form (int,String,timestamp(String?),ask/buy,int,int)>
#TODO:Transform dataset to formattedDataset
#Creating sub lists by parameter, ready to zip together to form formattedDataset
ListOfSecuritiesCodes = [0 for i in range(numberOfData-1)]  # TODO: should increment per security
ListOfTickers = ["FOO" for i in range(numberOfData-1)]  # Not entirely sure what the ticker part is
ListOfTimestamps = ["PlaceHolderString" for i in range(numberOfData-1)]
timeStampIterator = 0
for timeStamp in ListOfTimestamps:
    timeStamp = dataset[timeStampIterator][2]
    timeStampIterator += 1
#TODO: ListOfAsk/Buy
#TODO: ListOfPrice
#TODO: ListOfVolume
#TODO: ZIP FIELDS TOGETHER


for data in dataset:
    print data
    #TODO: insert into database

file.close()
db.close()


#example insert - need to change format of data to match database schema
#INSERT INTO trades(dataset_id,ticker,ts,bid_or_ask,price,volume) VALUES (0,"FOO","2014-01-01 00:00:00","B",9,200);
#INSERT INTO trades(dataset_id,ticker,ts,bid_or_ask,price,volume) VALUES (0,"BAR","2014-01-01 00:00:00","B",9,200);
