import pickle
import couchdb
import re

pm = __import__('testInside')

word_feature_filePath = "./word_features.txt"
classifier_filePath = "./classifier_Utral"

#word_feature_filePath = "Desktop/Cloud Assignment/Map Reduce Analysis/word_features.txt"
#classifier_filePath = "Desktop/Cloud Assignment/Map Reduce Analysis/classifier_Utral"

pattern = re.compile('(([A-z]{2,}(\'t)?))|([:;]o?\){1,3})|(>?[:;]o?\()')


#Some helper function
################################################################################
def extract_features(document):
    document_words = set(document)
    features = {}
    for word in word_features:
        features[word] = (word in document_words)
    return features
    
def split_into_words(tweet,pattern):
    words_filtered = pattern.findall(tweet)
    new_words_filtered = []
    for atuple in words_filtered:
        atuple = filter(None, atuple)
        new_words_filtered.append(atuple[0])
        # Turn it into lower case
        new_words_filtered = [x.lower() for x in new_words_filtered]
    words_filtered =  new_words_filtered
    return words_filtered
################################################################################

classifier_list = []

word_features = [line.strip() for line in open(word_feature_filePath, 'r')]


# Read all the word_feature and classifier into the lists
for i in range(1,4):    
    new_classifier_filePath = classifier_filePath + str(i) + ".pickle"
    f = open(new_classifier_filePath)
    new_classifier = pickle.load(f)
    classifier_list.append(new_classifier)
    f.close()

# Get the couchdb database
couch = couchdb.Server()
db = couch['new_york']

answer = {1:'Positive',-1:'Negative'}


# Update document in new_york( Adding sentiments)
for fid in db:
    doc = db[fid]
    if 'sentiment' in doc and 'district' in doc:
        continue
    tweet = doc['text']
    print tweet
    tweet = split_into_words(tweet,pattern)
    count = 0
    for i in range(3):
        individual_result = classifier_list[i].classify(extract_features(tweet))
        if individual_result == '0':
            individual_result = -1
        else:
            individual_result = 1             
        # some exception case that is hard to trained to classify too strong
        if 'not' in tweet:
            if 'good' in tweet or 'happy' in tweet or 'success' in tweet or 'funny' in tweet:
                individual_result = -1 
        count = count + individual_result
        print "classifier " +  str(i) + " beliver " + "sentiment = ", answer[individual_result]
    if count > 0:
        doc['sentiment'] = 'Positive'
    else:
        doc['sentiment'] = 'Negative'
    print 'sentiment = ', doc['sentiment']
    district = pm.calculateDistrict(doc)
    doc['district'] = district
    print 'district = ',district
    db.save(doc)

