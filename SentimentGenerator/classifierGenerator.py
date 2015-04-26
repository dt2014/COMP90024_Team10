# -*- coding: utf-8 -*-
import nltk 
import csv 
import itertools
import re
import pickle

################################################################################
def get_words_in_tweets(tweets):
    all_words = []
    for (words, sentiment) in tweets:
      all_words.extend(words)
    return all_words
    
def get_word_features(wordlist):
    wordlist = nltk.FreqDist(wordlist)
    word_features = wordlist.keys()
    return word_features

def extract_features(document):
    document_words = set(document)
    features = {}
    for word in word_features:
        features[word] = (word in document_words)
    return features


################################################################################


pattern = re.compile('([A-z]{3,})|([:;]o?\){1,3})|(>?[:;]o?\()')

dirOfTraininData = 'Downloads/SentimentAnalysisDataset.csv'
dirOfWord_feature = 'Desktop/Cloud Assignment/Map Reduce Analysis/word_features'
dirOfClassifier = 'Desktop/Cloud Assignment/my_classifier.pickle'

rawTestTweets = []



# The training tweets split
rawTrainingTweets = []
with open(dirOfTraininData) as csvfile:
    reader = csv.reader(csvfile)
    for row in itertools.islice(reader,120000,130001):
        rawTrainingTweets.append((row[3],row[1]))


# The training tweets split by word
TrainingTweets = []
for (words, sentiment) in rawTrainingTweets:
    words_filtered = pattern.findall(words)
    new_words_filtered = []
    for atuple in words_filtered:
        atuple = filter(None, atuple)
        new_words_filtered.append(atuple[0])
        new_words_filtered = [x.lower() for x in new_words_filtered]
    words_filtered =  new_words_filtered
    TrainingTweets.append((words_filtered, sentiment))
    


# word_features keep a list of what words the training set has
word_features = get_word_features(get_words_in_tweets(TrainingTweets))



# Write the word_feature to a file in order for furture used(classifier need this)
thefile = open(dirOfWord_feature, 'wb')
for item in word_features:
  thefile.write("%s\n" % item)
thefile.close()
  

# The real training data set
training_set = nltk.classify.apply_features(extract_features, TrainingTweets)

# Use Naive Bayes as our traning model 
classifier = nltk.NaiveBayesClassifier.train(training_set)

# Save the classifier
f = open(dirOfClassifier, 'wb')
pickle.dump(classifier, f)
f.close()


'''
# FOR VALIDATA THE CLASSIFIER THAT I JUST CREATEed
f = open('Desktop/Cloud Assignment/my_classifier.pickle')
classifier = pickle.load(f)
f.close()
print nltk.classify.util.accuracy(classifier,training_set)
'''








