# -*- coding: utf-8 -*-
"""
Created on Thu Feb 27 15:33:56 2014

@author: mildbyte
"""

import json
from datetime import datetime

from matplotlib.pyplot import plot_date, plot, vlines, legend, gca, ion
from pylab import movavg

def decode_series(path):
    series = json.JSONDecoder().decode(open(path).read())
    sortedseries = [(datetime.strptime(k, "%Y-%m-%d %H:%M:%S.%f"), v) for k, v in sorted(series.items())]
    times = [k for k,v in sortedseries]
    values = [v for k,v in sortedseries]
    
    return (times, values)

def plot_all():
    plot_bah()
    
    t,v2 = decode_series("/home/mildbyte/git/Algo-Trading/portfolioValue.json")
    t,v1 = decode_series("/home/mildbyte/git/Algo-Trading/availableFunds.json")

    t,p = decode_series("/home/mildbyte/git/Algo-Trading/portfolio.json")
   
    changed_t = []    
    for currt, currp, prevp, currv, prevv in zip(t, p[1:], p, v1[1:], v1):
        if set(currp.items()) != set(prevp.items()) or currv != prevv:
            changed_t.append(currt)

    plot_date(t, [a+b for a,b in zip(v1,v2)], 'r-')
    vlines(changed_t, gca().get_ylim()[0], gca().get_ylim()[1], 'k', 'dashed')
    
    legend(["Buy-and-hold equity curve", "Strategy's equity curve", "Trade times"])
        
def plot_json(path):
    (t, v) = decode_series(path)
    plot_date(t,v, 'b-')

def plot_account():
    t,v2 = decode_series("/home/mildbyte/git/Algo-Trading/portfolioValue.json")
    t,v1 = decode_series("/home/mildbyte/git/Algo-Trading/availableFunds.json")
    
    plot_date(t, [a+b for a,b in zip(v1,v2)], 'r-')

def plot_bah():
    t,v2 = decode_series("/home/mildbyte/git/Algo-Trading/portfolioValue_BAH.json")
    t,v1 = decode_series("/home/mildbyte/git/Algo-Trading/availableFunds_BAH.json")
    
    plot_date(t, [a+b for a,b in zip(v1,v2)], 'k-')

def decode_portfolio(path):
    (t,v) = decode_series(path)
    return (t,[d[u'MDAXEX'] if u'MDAXEX' in d else 0 for d in v])

def plot_portfolio():
    (t,v) = decode_portfolio("/home/mildbyte/git/Algo-Trading/portfolio.json")
    plot(t,v)

def plot_averages(sWnd, fWnd):
    t,v1 = decode_series("/home/mildbyte/git/Algo-Trading/availableFunds_BAH.json")
    t,v2 = decode_series("/home/mildbyte/git/Algo-Trading/portfolioValue_BAH.json")
    vs = [a+b for a,b in zip(v1,v2)]
    slow = movavg(vs, sWnd)
    fast = movavg(vs, fWnd)
    plot(t[fWnd-1:], fast, 'r-')
    plot(t[sWnd-1:], slow, 'g-')