#!/usr/bin/env python

import json
from datetime import datetime
from matplotlib.pyplot import plot_date
import os
import sys

def decode_series(path):
    series = json.JSONDecoder().decode(open(path).read())
    sortedseries = [(datetime.strptime(k, "%Y-%m-%d %H:%M:%S.%f"), v) for k, v in sorted(series.items())]
    times = [k for k,v in sortedseries]
    values = [v for k,v in sortedseries]
    return (times, values)

class Plot:
    def __init__(self, pathPrefix="."):
        (times, availableFunds) = decode_series(os.path.join(pathPrefix,"availableFunds.json"))
        (times, portfolioValue) = decode_series(os.path.join(pathPrefix,"portfolioValue.json"))
        # note times same for both series
        self.times = times
        self.availableFunds = availableFunds
        self.portfolioValue = portfolioValue
        self.accountValue = [x + y for x,y in 
            zip(self.availableFunds, self.portfolioValue)]
        
    def plot(self, values, *args, **kwargs):
        plot_date(self.times,values, 'b-', *args, **kwargs)

    def plotAvailableFunds(self):
        self.plot(self.availableFunds, color='g')

    def plotPortfolioValue(self):
        self.plot(self.portfolioValue, color='r')

    def plotAccountValue(self):
        self.plot(self.accountValue, color='b')

    def plotAll(self):
        self.plotAvailableFunds()
        self.plotPortfolioValue()
        self.plotAccountValue()

if __name__ == "__main__":
    import pylab
    if len(sys.argv) == 2:
        dataPath = sys.argv[1]
        plot = Plot(dataPath)
    elif len(sys.argv) > 2:
        print >>sys.stderr, "usage: %s [data path]" % sys.argv[0]
        sys.exit(1)
    else:
        plot = Plot()
    plot.plotAll()
    pylab.show()
