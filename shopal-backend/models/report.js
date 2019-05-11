var mongoose = require('mongoose');
var Schema = mongoose.Schema;

/**
 * define a schema
 */
var reportSchema = mongoose.Schema({
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
      min:0
    },
    description:{
      type:String
    }
});

module.exports = mongoose.model('Report', reportSchema);
