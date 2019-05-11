import pandas as pd
import numpy as np

from sklearn.metrics.pairwise import cosine_similarity
from scipy import sparse

from pymongo import MongoClient
client = MongoClient("mongodb+srv://kimpeter:kimpeter@cluster0-lux2o.gcp.mongodb.net/machinetime?retryWrites=true")
db = client.machinetime

def shopping_list(index):
  return {
    0 : "banana",
    1 : "apple, gala",
    2 : "tomato, roma",
    3 : "cucumber, seedless",
    4 : "orange, seedless",
    5 : "great value half & half cream",
    6 : "lucern 2% milk",
    7 : "kraft foods canada kraft",
    8 : "astro original balkan style yogurt",
    9 : "goldegg free run large eggs",
    10 : "dempster's everything bagels",
    11 : "dempster's white bread",
    12 : "pringles sour cream n onion",
    13 : "your fresh market lean ground beef",
    14 : "boneless chicken breasts",
    15 : "wild sockeye salmon fillet",
    16 : "coca cola",
    17 : "pepsi",
    18 : "red bull energy drink"
  }.get(index, "Item does not exist")

# machine learning code

# find the recommended item out of the already trained data
def recommendation(order):
  data_matrix = update_machine()  # pull all the items out of the mongodb and return a dataframe object
  rows = []
  for index, row in data_matrix.iterrows(): # creating a 2d array of the data in the data_matrix dataframe object
    curRow = []
    for index2, val in enumerate(row):
      if index2 == index:
        curRow.append(0)  # reduce any same values to 0
      else:
        curRow.append(val)
    rows.append(curRow)

  finalCol = [] # reducing the user columns into a single column
  for row in rows:
    curRow = []
    for i , val in enumerate(order):
      if val == 1:
        curRow.append(row[i]) 
    finalCol.append(max(curRow))    

  ranks = sorted([(x,i) for (i,x) in enumerate(finalCol)], reverse=True)
  # create index list of recommended items
  final = []
  count = 0
  for val,i in ranks:
    if order[i] != 1 and count < 3:
      final.append(i)
      count+=1
  
  if not final:   # check empty list
    for val,i in ranks[:3]:
      final.append(i)
  
  print("Your recommendation vector");
  print(final)
  # return string of recommended items
  list = []
  for i,val in enumerate(final):
    list.append(shopping_list(val))
  
  print(list)
  
  return final
  
  
# update machine 
def update_machine():
  print("updatetime")

  rows = []
  for doc in db.orderdata.find():
    cur =  doc['order'].split(',')
    cur = list(map(int,cur))
    rows.append(cur)
  data = np.array(rows)    
  data = pd.DataFrame(data)
  print(data)
  # TRAIN DATA
  data_matrix = machinetrain(data)
  return data_matrix
 
 
# method to train data
def machinetrain(data_items): # data_items is a DataFrame
  ### item-item collaborative filtering ###
  # Normalize the user vectors to unit vectors
  magnitude = np.sqrt(np.square(data_items).sum(axis=1))
  data_items = data_items.divide(magnitude, axis='index')

  # Build the cosine similarity matrix
  data_matrix = calculate_similarity(data_items)

  print(data_matrix)
  # Build csv file
  #data_matrix.to_csv('data/traineddata.csv')
  return data_matrix
  

    
def calculate_similarity(data_items): # data_items is a DataFrame
  """Calculate the column-wise cosine similarity for a sparse
  matrix. Return a new dataframe matrix with similarities.
  """
  data_sparse = sparse.csr_matrix(data_items)
  similarities = cosine_similarity(data_sparse.transpose())
  sim = pd.DataFrame(data=similarities)
  return sim
  

