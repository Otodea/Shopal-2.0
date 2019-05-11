var mongoose = require('mongoose');
var Schema = mongoose.Schema;

/**
 * define a schema
 */
var listSchema = mongoose.Schema({
  _id: {
    type: String
  },
  destination_addr: {
    type: String
  },
  name:{
    type: String
  },
  mobile:{
    type: String
  },
  driver_id: {
    type: String,
    default: null
  },
  items:[
    {
      itemName: {
        type: String
      },
      imageUrl: {
        type: String
      },
      price:{
        type: Number, 
        min:0
      },
      brand: {
        type: String
      },
      quantity:{
        type: Number, 
        min:0 
      },
      description:{
        type:String
      },
      index:{
        type: Number
      }
    }
  ]
  ,
  status: {
    type: String,
    enum: ['Received', 'Delivering', 'Completed', null]
  }
});

module.exports = mongoose.model('List', listSchema);
