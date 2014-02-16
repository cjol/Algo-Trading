#!/usr/bin/python
import h5py
import pandas as pd
import MySQLdb
import getpass
from os import listdir
from os.path import isfile, join

#setting up hdf5 variables
hdf5_file_name = '../RawData/ADS.h5'
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

#populating dataset table
cursor.execute('INSERT INTO datasets(dataset_id,name) VALUES (0,"Default Data"')

#populating securities table
for file in rawFiles:
    print 'Importing ', file
    ticker = os.path.splitext(os.path.basename(file))[0]  # Removing extension from filename to get ticker
    cursor.execute('INSERT INTO securities(dataset_id,ticker) VALUES (0,%s);', ticker)
    hdf5file = h5py.File(mypath+"/"+file, 'r')   # 'r' means that hdf5 file is open in read-only mode
    dataset = hdf5file['RetailStates']
    numberOfData = dataset.len()
    #TODO:Transform dataset to formattedDataset
    #Creating sub lists by parameter, ready to zip together to form formattedDataset
    DataSetIDs = [0 for i in range((numberOfData)*10)]  # TODO: should increment per security
    ListOfTickers = [ticker for i in range((numberOfData)*10)]  # Presuming ticker is the security identifier thing

    ListOfTimestamps = ["PlaceHolderString" for i in range((numberOfData)*10)]
    timeStampIterator = 0
    offsetAccumulator = 0  # need to only advance to next Raw Order after filling in 10 formatted orders
                           # This is due to there being 10 formatted orders worth of data in each raw order
    for timeStamp in ListOfTimestamps:
        timeStamp = dataset[timeStampIterator][2]
        offsetAccumulator += 1
        if offsetAccumulator > 9:
            timeStampIterator += 1
            offsetAccumulator = 0

    ListOfAskBuy = ["PlaceHolderString" for i in range((numberOfData)*10)]

    #Should create a List of size numberOfData in the form (B,B,B,B,B,A,A,A,A,A), to match with the 10 order pattern
    askbuyAccumulator = 0
    askbuyString = "B"
    for askbuy in ListOfAskBuy:
        if askbuyAccumulator > 4:
            askbuyString = "A"
        askbuy = askbuyString
        askbuyAccumulator += 1
        if askbuyAccumulator > 9:
            askbuyString = "B"
            askbuyAccumulator = 0


    #TODO: ListOfPrice

    ListOfPrices = [-1 for i in range((numberOfData)*10)]  # placeholder list, now fill in actual prices
    #TODO: ListOfVolume
    #TODO: ZIP FIELDS TOGETHER
    #populating trades table
    for data in dataset:
        #TODO: insert into database
    hdf5file.close()

db.close()


#example insert - need to change format of data to match database schema
#INSERT INTO trades(dataset_id,ticker,ts,bid_or_ask,price,volume) VALUES (0,"FOO","2014-01-01 00:00:00","B",9,200);
#INSERT INTO trades(dataset_id,ticker,ts,bid_or_ask,price,volume) VALUES (0,"BAR","2014-01-01 00:00:00","B",9,200);
