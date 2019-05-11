from flask import Flask, request, jsonify
from pymongo import MongoClient
from bson.json_util import dumps
import pandas as pd
import numpy as np
import json
import re
import bson
#import string

from machinelearn import machinetrain, recommendation, calculate_similarity

client = MongoClient("mongodb+srv://kimpeter:kimpeter@cluster0-lux2o.gcp.mongodb.net/machinetime?retryWrites=true")
db = client.machinetime

app = Flask(__name__)
    
print('testing pls')

rows = []
for doc in db.orderdata.find():
  cur =  doc['order'].encode("ascii").split(',')
  cur = list(map(int,cur))
  rows.append(cur)
 
data = np.array(rows)    
data = pd.DataFrame(data)
print(data)

# TRAIN DATA
machinetrain(data)

# NEW ORDER! get recommendation
order = np.array([0,1,0,1,0,1])
recommendation(order)




    
    
    
    
    
    
    
# NOTE: must pass an np MATRIX into machinelearn.py!!!    

# @app.route("/train_data", methods = ['PUT'])  # when to train? once in the beginning, and whenever new data

# @app.route("/get_recommendation", methods = ['GET'])  # receive hot recs

    
# @app.route("/add_contact", methods = ['POST'])
# def add_contact():
    # try:
        # data = json.loads(request.data)
        # user_name = data['name']
        # user_contact = data['contact']
        # if user_name and user_contact:
            # status = db.recommendation.insert_one({
                # "name" : user_name,
                # "contact" : user_contact
            # })
        # return dumps({'message' : 'SUCCESS'})
    # except Exception as e:
        # return dumps({'error' : str(e)})

        
# @app.route("/get_all_contact", methods = ['GET'])
# def get_all_contact():
    # try:
        # contacts = db.orderdata.find()
        # return dumps(contacts)
    # except Exception as e:
        # return dumps({'error' : str(e)})
        
# add_contact()