import pickle
import couchdb
import re


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

dirOfWord_feature = 'Desktop/Cloud Assignment/Map Reduce Analysis/word_features'
dirOfClassifier = 'Desktop/Cloud Assignment/my_classifier.pickle'

# Get the classifier 
f = open(dirOfClassifier)
classifier = pickle.load(f)
f.close()

# Get the word_feature(all the possible word in the classifer) 
thefile = open(dirOfWord_feature, 'r')
word_features = [line.strip() for line in thefile]
thefile.close()

# Get the couchdb database
couch = couchdb.Server()
db = couch['new_york']

# Set the pattern of the regular expression in order to split the sentence
pattern = re.compile('([A-z]{2,})|([:;]o?\){1,3})|(>?[:;]o?\()')


# Update document in new_york( Adding sentiments)
for fid in db:
    doc = db[fid]
    if 'sentiments' in doc:
        continue
    tweet = doc['text']
    tweet = split_into_words(tweet,pattern)
    doc['sentiments'] = classifier.classify(extract_features(tweet))
    print tweet
    print 'sentiment = ', doc['sentiments']
    db.save(doc)



