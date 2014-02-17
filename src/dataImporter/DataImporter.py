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

rawFiles = [f for f in listdir(mypath) if isfile(join(mypath, f))]
truncatedRawFiles = rawFiles[0:2]

for file in truncatedRawFiles:
    print 'Importing ', file
    ticker = os.path.splitext(os.path.basename(file))[0]  # Removing extension from filename to get ticker
    hdf5file = h5py.File(mypath+"/"+file, 'r')   # 'r' means that hdf5 file is open in read-only mode
    dataset = hdf5file['RetailStates']
    numberOfData = dataset.len()

    ListOfTimestamps = ["PlaceHolderString" for i in range(numberOfData*2)]
    ListOfIDs = [0 for i in range(numberOfData*2)]
    timeStampIterator = 0
    offsetAccumulator = 0  # need to only advance to next Raw Order after filling in 10 formatted orders
                           # This is due to there being 10 formatted orders worth of data in each raw order
    for i in range(len(ListOfTimestamps)):
        ListOfTimestamps[i] = dataset[timeStampIterator][0]
        offsetAccumulator += 1
        if offsetAccumulator > 1:
            timeStampIterator += 1
            offsetAccumulator = 0

    ListOfTickers = [ticker for i in range(numberOfData*2)]

    ListOfPrices = [-1 for i in range(numberOfData*2)]
    priceIterator = 0
    offsetAccumulator = 0
    oneEleven = 1
    for i in range(len(ListOfPrices)):
        ListOfPrices[i] = dataset[priceIterator][oneEleven]
        offsetAccumulator += 1
        if offsetAccumulator > 1:
            priceIterator += 1
            offsetAccumulator = 0
        if oneEleven == 1:
            oneEleven = 11
        else:
            oneEleven = 1

    ListOfVolumes = [-1 for i in range(numberOfData*2)]
    volumeIterator = 0
    offsetAccumulator = 0
    twoTwelve = 2
    for i in range(len(ListOfVolumes)):
        ListOfVolumes[i] = abs(dataset[volumeIterator][twoTwelve])
        offsetAccumulator +=1
        if offsetAccumulator > 1:
            volumeIterator += 1
            offsetAccumulator = 0
        if twoTwelve == 2:
            twoTwelve = 12
        else:
            twoTwelve = 2

    ListOfBidOrAsk = ["B" for i in range(numberOfData*2)]
    askIterator = 1
    for i in range(len(ListOfBidOrAsk)):
        if askIterator % 2 == 0:
            bidOrAsk = "A"
        askIterator += 1

    ListOfOrders = zip(ListOfIDs, ListOfTickers, ListOfTimestamps, ListOfBidOrAsk, ListOfPrices, ListOfVolumes)
    for order in ListOfOrders:
        cursor.execute('INSERT INTO trades(dataset_id,ticker,ts,bid_or_ask,price,volume) VALUES (%i,%s,%s,%s,%i,%i);',
                       order[0],order[1],order[2],order[3],order[4],order[5])

    ''' ----------------------------CODE PENDING RADMILLO RESPONSE------------------------------------------------
    #TODO:Transform dataset to formattedDataset
    #Creating sub lists by parameter, ready to zip together to form formattedDataset
    DataSetIDs = [0 for i in range((numberOfData)*10)]
    ListOfTickers = [ticker for i in range((numberOfData)*10)]  # Presuming ticker is the security identifier thing

    ListOfTimestamps = ["PlaceHolderString" for i in range((numberOfData)*10)]
    timeStampIterator = 0
    offsetAccumulator = 0  # need to only advance to next Raw Order after filling in 10 formatted orders
                           # This is due to there being 10 formatted orders worth of data in each raw order
    for timeStamp in ListOfTimestamps:
        timeStamp = dataset[timeStampIterator][0]
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
    '''



    hdf5file.close()

db.close()


#example insert - need to change format of data to match database schema
#INSERT INTO trades(dataset_id,ticker,ts,bid_or_ask,price,volume) VALUES (0,"FOO","2014-01-01 00:00:00","B",9,200);
#INSERT INTO trades(dataset_id,ticker,ts,bid_or_ask,price,volume) VALUES (0,"BAR","2014-01-01 00:00:00","B",9,200);
