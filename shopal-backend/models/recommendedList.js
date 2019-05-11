var mongoose = require('mongoose');
var Schema = mongoose.Schema;

/**
 * define a schema
 */
var recommendedListSchema = mongoose.Schema({
  _id: {
    type: String
  },
  order: {
    type: [Number]
  }
});

module.exports = mongoose.model('RecommendedList', recommendedListSchema);
