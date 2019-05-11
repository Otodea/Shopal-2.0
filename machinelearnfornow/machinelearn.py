import pandas as pd
import numpy as np

from sklearn.metrics.pairwise import cosine_similarity
from scipy import sparse

# machine learning code

# find the recommended item out of the already trained data
def recommendation(order):
  data_matrix = pd.read_csv('test/traineddata.csv')
  
  rows = []
  for index, row in data_matrix.iterrows():
    curRow = []
    for index2, val in enumerate(row):
      if index2 == index:
        curRow.append(0)
      else:
        curRow.append(val)
    rows.append(curRow)

  finalCol = []
  for row in rows:
    curRow = []
    for i , val in enumerate(order):
      if val == 1:
        curRow.append(row[i]) 
    finalCol.append(max(curRow))    

  ranks = sorted([(x,i) for (i,x) in enumerate(finalCol)], reverse=True)

  final = []
  for val,i in ranks[:3]:
    final.append(i)
  
  print("Your recommendation vector");
  print(final)

  
  
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
  data_matrix.to_csv('test/traineddata.csv')

    
def calculate_similarity(data_items): # data_items is a DataFrame
  """Calculate the column-wise cosine similarity for a sparse
  matrix. Return a new dataframe matrix with similarities.
  """
  data_sparse = sparse.csr_matrix(data_items)
  similarities = cosine_similarity(data_sparse.transpose())
  sim = pd.DataFrame(data=similarities)
  return sim