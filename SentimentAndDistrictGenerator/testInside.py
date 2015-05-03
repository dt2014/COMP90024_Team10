import numpy as np
import matplotlib.path as mplPath
import couchdb
from random import randint

queenPoly = [
[40.812176, -73.781744],
[40.752384, -73.700720],
[40.589517, -73.767852],
[40.544137, -73.948440],
[40.626008, -73.839263],
[40.731717, -73.966979],
[40.800364, -73.874282]
]

brooklynPoly = [
[40.736972, -73.966924],
[40.683361, -74.045888],
[40.565579, -74.025289],
[40.602083, -73.829595],
[40.704186, -73.876287],
[40.688047, -73.894826],
[40.739833, -73.946496]
]

statenIslandPoly = [
[40.656870, -74.059271],
[40.585989, -74.046225],
[40.492582, -74.252219],
[40.558867, -74.247412],
[40.646451, -74.191794]
]

manhattanPoly = [
[40.684345, -74.041267],
[40.886586, -73.934837],
[40.872050, -73.893638],
[40.795680, -73.908744],
[40.710375, -73.971916],
[40.679659, -74.029594]
]

bronxPoly = [
[40.880876, -73.928133],
[40.921617, -73.911310],
[40.886327, -73.775011],
[40.812313, -73.746172],
[40.785283, -73.870454],
[40.804257, -73.931222],
[40.836474, -73.933969],
[40.873089, -73.910966],
[40.879578, -73.926416]
]

nameDic = {1:'Queens',2:'Brooklyn',3:'Staten Island',4:'Manhattan',5:'Bronx'}
polyDic = {1:queenPoly,2:brooklynPoly,3:statenIslandPoly,4:manhattanPoly,5:bronxPoly}

def testInsidePologon(targetPoint):
    answer = []
    for i in range(1,6):
        bbPath = mplPath.Path(np.array(polyDic[i]))
        if( bbPath.contains_point(targetPoint) == 1):
            answer.append(nameDic[i])
    return answer
    

#because some point may be in the overlap area, just randomly pick one
def randomPickOneDistrict(listOfDistrict,length):
    return listOfDistrict[randint(0,length - 1)]
     
#Some points may be outside all the five district COZ we gather tweet by radius
def cloestDistrict(latLon):
    lat = latLon[0]
    lon = latLon[1]
    shortest = 100
    name = 'unknown'
    for ploygon in polyDic:
        distance = 0.0
        for points in polyDic[ploygon]:
            pLat = points[0]
            pLon = points[1]
            distance = distance + (lat-pLat) * (lat-pLat) + (lon - pLon) * (lon - pLon)
        distance = distance / len(polyDic[ploygon])  
        if(distance < shortest):
            shortest = distance
            name = nameDic[ploygon]
    return name


def calculateDistrict(doc):
    length = 0
    latLon = []
    if(doc['geo'] != None):
        latLon = doc['geo']['coordinates']
    else:
        latLon = doc['retweeted_status']['geo']['coordinates']
    district =  testInsidePologon(latLon)
    length = len(district)
    # Solve district overlap problem
    if(length > 1):
        district = randomPickOneDistrict(district,length)
    # Solve outside all district prblem(not in any of these districts
    elif(length == 0):
       district = cloestDistrict(latLon)
    else:
        district = district[0]
    return district
'''
couch = couchdb.Server()
db = couch['new_york']
count = 0
for fid in db:
    doc = db[fid]
    district = calculateDistrict(doc)
    count = count + 1
    print 'district = ', district
    if count > 10:
        break
'''