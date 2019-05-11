var mongoose = require('mongoose');
var Schema = mongoose.Schema;

/**
 * define a schema
 */
var itemSchema = mongoose.Schema({
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
  quantity:{
    type: Number,
    default: 0
  },
  brand: {
    type: String
  },
  description:{
    type:String
  },
  index:{
    type: Number
  }
});

module.exports = mongoose.model('Item', itemSchema);

