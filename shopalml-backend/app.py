from flask import Flask, request, jsonify, Response
from pymongo import MongoClient
import pandas as pd
import numpy as np
import json
import re
import bson

#import string

from machinelearn import machinetrain, recommendation, calculate_similarity, shopping_list

client = MongoClient("mongodb+srv://kimpeter:kimpeter@cluster0-lux2o.gcp.mongodb.net/machinetime?retryWrites=true")
db = client.machinetime

app = Flask(__name__)
    
    
@app.route("/get_recommendation", methods = ['POST'])
def get_recommendation():
  # try:
  data = request.get_json()
  order = data['order']     # receive order field
  id = data['_id']          # receive id field
  
  reclist = recommendation(order) # return an index list of recommended items   
  
  # turn all items in order to 0
  for i in range(len(order)):
    order[i] = 0
  
  for var in reclist:
    order[var] = 1
  
  # insert into DB
  orderstr = ','.join(map(str, order)) 
  db.orderdata.insert_one({'order': orderstr})
  
  responseDict = { "order": order,        # create a dictionary. NOTE: python dict is a json Object
                    "_id" : id}
                    
  return jsonify(responseDict)
  
  
# testing routes  
@app.route('/hello', methods = ['GET'])
def hello():
    data = {
        'hello'  : 'world',
        'number' : 3,
        'order' : 2
    }

    return jsonify(data)    
    
@app.route('/receive', methods = ['POST'])
def receive():
  data = request.get_json()
  order = data['order']
  id = data['_id']
  
  for i in range(len(order)):
    order[i] = 0
    
  newdict = { "order": order,
              "_id" : id}
  
  return jsonify(newdict)
# end of testing routes

    
if __name__ == '__main__':
    # port = 8000 #the custom port you want
    # app.run(host='0.0.0.0', port=port)
    app.run()