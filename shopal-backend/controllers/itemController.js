var Item = require("../models/item");
var database = require("../initdb");

// form validation modules
const { body,validationResult } = require('express-validator/check');
const { sanitizeBody } = require('express-validator/filter');

exports.addItem = [
  // has a valid name
  body("itemName")
  .trim()
  .isLength({min: 5})
  .withMessage("Invalid item name"),

  // has a valid url
  body("imageUrl")
  .trim()
  .isLength({min: 1})
  .withMessage("Invalid image url"),

  (req, res, next) => {
    
    // validate request
    const errors = validationResult(req);
    var ret = {};
    if(!errors.isEmpty()){
      ret = {errorMessage : errors.array()[0].msg};
      return res.status(401).send(ret);
    }

    var item = new Item();
    item.itemName = req.body.itemName;
    item.imageUrl = req.body.imageUrl;
    item.price = req.body.price;
    item.quantity = req.body.quantity;
    item.brand = req.body.brand;
    item.description = req.body.description;

    // save the user
    item.save((err) => {
      var ret = {};
      // failure
      if (err) {
        ret.errorMessage = "Invalid request";
        return res.status(401).send(ret);
      }

      // success
      return res.status(200).send(item);
    });
  }
]

exports.getPrice = (req, res, next) => {

  var findQuery = {itemName: req.query.itemName};

  Item.findOne(findQuery, (err, item) => {
    var ret = {};
    // failure
    if (err) {
      ret.errorMessage = "Internal error";
      return res.status(500).send(ret);
    }

    if (!item) {
      ret.errorMessage = "Invalid request";
      return res.status(401).send(ret);
    }

    // success
    return res.status(200).send(JSON.stringify(item.price));
  });
}


exports.getItems = (req, res, next) => {
  //var findQuery = {itemName: req.query.itemName};

  Item.find((err, item) => {
    var ret = {};
    // failure
    if (err) {
      ret.errorMessage = "Internal error";
      return res.status(500).send(ret);
    }

    if (!item) {
      ret.errorMessage = "Invalid request";
      return res.status(401).send(ret);
    }

    // success
    return res.status(200).send(JSON.stringify(item));
  });
}

