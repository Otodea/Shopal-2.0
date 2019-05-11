var mongoose = require('mongoose');

/**
 * define a schema
 */
var userSchema = mongoose.Schema({
    _id: {
        type:String
    },
    name: {
        first: String,
        last:String
    },
    password: {
        type : String
    },
    contact: {
      phone_num: String,
      email: String,
      work_addr: String
    },
    profile_photo: {
        type: String
    }, 
    user_type: {
        type : String,
        enum: ['Driver', 'User']
    }
});

module.exports = mongoose.model('User', userSchema);


