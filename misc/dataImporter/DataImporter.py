#!/usr/bin/python

import h5py
import psycopg2
import sys
from os import listdir
from os.path import isfile, join
import os.path

# max # of rows to insert at a time
CHUNK_SIZE = 1024

def connect():
    conn = psycopg2.connect(dbname=databaseName,host=databaseHost,port=databasePort,user="alpha")
    conn.autocommit = True
    return conn

def insertData(datasetName):
    #populating dataset table
    cursor.execute("""INSERT INTO datasets(name) VALUES (%s);""", (datasetName,))

    # get dataset ID
    cursor.execute("""SELECT dataset_id FROM datasets WHERE name=%s""", (datasetName,))
    (datasetID,) = cursor.fetchone()

    return datasetID

def insertSecurity(datasetID, securityName):
    cursor.execute("""INSERT INTO securities(dataset_id,ticker) VALUES (%s,%s)""",(datasetID,securityName))

def convertTimestamp(rawTimestamp):
    # rawTimestamp is in nanoseconds
    # discard nanosecond component.
    # this is necessary as psycopg2 does not play nicely with it
    # (triggers subtle error when nanoseconds would round up)
    ticksTimestamp = rawTimestamp // 1000
    # convert to floating-point seconds
    ticksTimestamp = ticksTimestamp / 1000000.0
    return psycopg2.TimestampFromTicks(ticksTimestamp)

def importOrderBooks(hdf5file, datasetID, ticker): 
    dataset = hdf5file['RetailStates']
    nrows = dataset.len()
    
    rowsToInsert = []
    for i in range(nrows):
        row = dataset[i]
        timestamp = convertTimestamp(row[0])
        bidPrices = []
        bidVolumes = []
        askPrices = []
        askVolumes = []

        # TODO: Are prices always to nearest cent?
        # TODO: Can there ever be missing data?
        for j in range(5):
            bidPrices.append(int(row[2*j+1]*100))
            bidVolumes.append(int(row[2*j+2]))
            askPrices.append(int(row[2*j+11]*100))
            # volumes are negative, which breaks our schema
            askVolumes.append(-1*int(row[2*j+12]))

        rowsToInsert.append(
                    (datasetID, ticker, timestamp,
                     bidPrices[0],bidVolumes[0],bidPrices[1],bidVolumes[1],
                     bidPrices[2],bidVolumes[2],bidPrices[3],bidVolumes[3],
                     bidPrices[4],bidVolumes[4],
                     askPrices[0],askVolumes[0],askPrices[1],askVolumes[1],
                     askPrices[2],askVolumes[2],askPrices[3],askVolumes[3],
                     askPrices[4],askVolumes[4]))

        if i % CHUNK_SIZE == 0 or i == nrows-1:
            args_str = ','.join(cursor.mogrify(
                '(%s,%s,%s,'
                '%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,'
                '%s,%s,%s,%s,%s,%s,%s,%s,%s,%s)', x) for x in rowsToInsert)
            rowsToInsert = []

            cursor.execute('INSERT INTO order_books(dataset_id, ticker, ts, bid1_price, bid1_volume,'
                'bid2_price, bid2_volume, bid3_price, bid3_volume,'
                'bid4_price, bid4_volume, bid5_price, bid5_volume,'
                'ask1_price, ask1_volume, ask2_price, ask2_volume,'
                'ask3_price, ask3_volume, ask4_price, ask4_volume,'
                'ask5_price, ask5_volume)'
                'VALUES ' + args_str)

            print "Imported %d/%d rows" % (i, nrows)

def importMatches(hdf5file, datasetID, ticker): 
    dataset = hdf5file['LastDones']
    nrows = dataset.len()

    rowsToInsert = []
    for i in range(nrows):
        row = dataset[i]
        timestamp = convertTimestamp(row[0])
        price = int(row[1]*100)
        volume = int(row[2])

        # TODO: Throwing away aggressor side
        rowsToInsert.append((datasetID, ticker, timestamp, price, volume))

        if i % CHUNK_SIZE == 0 or i == nrows-1:
            args_str = ','.join(cursor.mogrify('(%s,%s,%s,%s,%s)', x)
                                               for x in rowsToInsert)
            rowsToInsert = []
            cursor.execute('INSERT INTO matches(dataset_id,ticker,ts,price,volume) '
                           'VALUES ' + args_str)

            print "Imported %d/%d rows" % (i, nrows)

def extractTicker(fname):
    return os.path.splitext(os.path.basename(fname))[0]

if len(sys.argv) != 6:
    print >>sys.stderr, "usage: %s <data folder> <name of dataset> <database name> <host> <port>" % sys.argv[0]
    sys.exit(-1)

dataPath = sys.argv[1]
datasetName = sys.argv[2]
databaseName = sys.argv[3]
databaseHost = sys.argv[4]
databasePort = sys.argv[5]

dataFiles = [f for f in listdir(dataPath) if isfile(join(dataPath, f))]

# connect to database
conn = connect()
cursor = conn.cursor()

datasetID = insertData(datasetName)
print "Inserted %s into dataset table with ID %d" % (datasetName, datasetID)

#populating securities table
for fname in dataFiles:
    ticker = extractTicker(fname)
    insertSecurity(datasetID, ticker)
print "Inserted into securities table"

#populating orders table
for fname in dataFiles:
    ticker = extractTicker(fname)
    fullyQualifiedPath = os.path.join(dataPath, fname)
    hdf5file = h5py.File(fullyQualifiedPath, 'r')

    print "Importing order books for %s" % ticker
    sys.stdout.flush()
    importOrderBooks(hdf5file, datasetID, ticker)
    print "and matches"
    sys.stdout.flush()
    importMatches(hdf5file, datasetID, ticker)
    print "Done!"

    hdf5file.close()

cursor.close()
conn.close()
