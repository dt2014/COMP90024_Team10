import couchdb

couch = couchdb.Server()

db = couch['new_york']


def map_fun(doc):
    result = 0
    if 'sentiments' in doc:
        result = doc['sentiments']
    if 'created_at' in doc:
        date_time = doc['created_at'].split()
        # Convert UTC to New York local time
        time = date_time[3]
        hour = ((int(time.split(':')[0]) - 4) + 24) % 24
        if (hour >= 5 and hour <= 12):
            yield 'morning',(result,1)
        elif (hour >= 17 and hour <= 23) or hour == 0:
            yield 'night',(result,1)
                
	   
	         
def reduce_fun(key,value):
    return sum(value)

################################################################################


# The way to run a temperateary view include map and reduce
alist = list()
for result in db.query(map_fun,reduce_fun=reduce_fun,language='python',group_level=1):
    alist.append(result.value)
    print result.key,result.value