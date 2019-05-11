console.log("Starting database...");

// set up the connection
var ObjectId = require("mongodb").ObjectID;
var mongoose = require("mongoose");
var mongoDB = "mongodb+srv://kimpeter:kimpeter@cluster0-lux2o.gcp.mongodb.net/test?retryWrites=true";
mongoose.connect(mongoDB);
mongoose.Promise = global.Promise;
var db = mongoose.connection;
db.on("error", console.error.bind(console, "MongoDB connection error:"));
db.on("close", () => console.log("database closed"));
