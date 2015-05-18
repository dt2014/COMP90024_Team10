'''
    * Team 10
    * New York
    * Fengmin Deng      659332
    * Jiajie Li         631482
    * Shuangchao Yin    612511
    * Weijia Chen       616213
    * Yun Shi           621761
'''

# -*- coding: utf-8 -*-
import nltk 
import csv 
import itertools
import re
import pickle

################################################################################
def extract_features(document):
    document_words = set(document)
    features = {}
    for word in word_features:
        features[word] = (word in document_words)
    return features
################################################################################


pattern = re.compile('(([A-z]{2,}(\'t)?))|([:;]o?\){1,3})|(>?[:;]o?\()')

dirOfTraininData = './Training'
dirOfWord_feature = './word_features.txt'
dirOfClassifier = './classifier_Utral2.pickle'


# The training tweets split
TrainingTweets = []
count = 0
with open(dirOfTraininData) as csvfile:
    reader = csv.reader(csvfile)
    for row in reader:
        if((count >= 150000 and count < 300000) or (count >= 950000 and count < 1100000)):
            words = row[1]
            sentiment = row[0]
            words_filtered = pattern.findall(words)
            new_words_filtered = []
            for atuple in words_filtered:
                atuple = filter(None, atuple)
                new_words_filtered.append(atuple[0])
                new_words_filtered = [x.lower() for x in new_words_filtered]
            words_filtered =  new_words_filtered
            TrainingTweets.append((words_filtered,sentiment))
        count = count + 1
            
thefile = open(dirOfWord_feature, 'r')
word_features = [line.strip() for line in thefile]
thefile.close()

# The real training data set
training_set = nltk.classify.apply_features(extract_features, TrainingTweets)
#print training_set

# Use Naive Bayes as our traning model 
classifier = nltk.NaiveBayesClassifier.train(training_set)


# Save the classifier
f = open(dirOfClassifier, 'wb')
pickle.dump(classifier, f)
f.close()





