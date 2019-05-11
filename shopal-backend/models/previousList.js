var mongoose = require('mongoose');
var Schema = mongoose.Schema;

/**
 * define a schema
 */
var previousListSchema = mongoose.Schema({
  user_id: {
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
        min:0, 
        max:200
      },
      brand: {
        type: String
      },
      quantity:{
        type: Number, 
        min:0, 
        max:50
      },
      description:{
        type:String
      },
      index: {
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

module.exports = mongoose.model('PreviousList', previousListSchema);
