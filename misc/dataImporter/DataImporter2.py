__author__ = 'nick'
import h5py
import pandas as pd
import psycopg2
import getpass
import subprocess
from paramiko import SSHClient
import sys
from os import listdir
from os.path import isfile, join

#!/usr/bin/python

#grabbing all .m5 files
#TODO: IMPLEMENT SSH TUNNEL TO DB CORRECTLY
rawDataPath = raw_input("Path to Raw Data Folder: ")
rawFiles = [f for f in listdir(rawDataPath) if isfile(join(rawDataPath, f))]

#setting up database connection
databaseName = raw_input("Database Name: ")
db = psycopg2.connect("dbname=testenv user=postgres")
cursor = db.cursor()

#populating dataset table
cursor.execute('INSERT INTO datasets(dataset_id,name) VALUES (0,"Default Data"')

#populating securities table
rawFiles = [f for f in listdir(mypath) if isfile(join(mypath, f))]
truncatedRawFiles = rawFiles[0:2]  # TODO: Currently only imports first 2 files in path
for file in truncatedRawFiles:
    print 'Importing ', file, ' into Securities table'
    ticker = os.path.splitext(os.path.basename(file))[0]  # Removing extension from filename to get ticker
    cursor.execute('INSERT INTO securities(dataset_id,ticker) VALUES (0,%s);',ticker)

#populating orders table

for file in truncatedRawFiles:
    ticker = os.path.splitext(os.path.basename(file))[0]  # Removing extension from filename to get ticker
    hdf5file = h5py.File(mypath+"/"+file, 'r')
    dataset = hdf5file['RetailStates']
    numberOfData = dataset.len()
    ListOfOrders = [[0, ticker, dataset[i][0],
                     dataset[i][1], dataset[i][2],
                     dataset[i][3], dataset[i][4],
                     dataset[i][5], dataset[i][6],
                     dataset[i][7], dataset[i][8],
                     dataset[i][9], dataset[i][10],
                     dataset[i][10], dataset[i][12],
                     dataset[i][13], dataset[i][14],
                     dataset[i][15], dataset[i][16],
                     dataset[i][18], dataset[i][17],
                     dataset[i][19], dataset[i][20], ] for i in range(numberOfData)]
    for i in range(numberOfData):
        cursor.execute('INSERT INTO order_books(dataset_id, ticker, ts, bid1_price, bid1_volume,'
            'bid2_price, bid2_volume, bid3_price, bid3_volume,'
            'bid4_price, bid4_volume, bid5_price, bid5_volume,'
            'ask1_price, ask1_volume, ask2_price, ask2_volume,'
            'ask3_price, ask3_volume, ask4_price, ask4_volume,'
            'ask5_price, ask5_volume)'
            'VALUES (0,%s,%d,'
            '%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d)',
                     ticker, dataset[i][0],
                     dataset[i][1], dataset[i][2],
                     dataset[i][3], dataset[i][4],
                     dataset[i][5], dataset[i][6],
                     dataset[i][7], dataset[i][8],
                     dataset[i][9], dataset[i][10],
                     dataset[i][10], dataset[i][12],
                     dataset[i][13], dataset[i][14],
                     dataset[i][15], dataset[i][16],
                     dataset[i][18], dataset[i][17],
                     dataset[i][19], dataset[i][20],)

        hdf5file.close()

db.close()
