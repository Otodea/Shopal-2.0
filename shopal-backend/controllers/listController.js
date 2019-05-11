var List = require("../models/list");
var PreviousList = require("../models/previousList");
var RecommendedList = require("../models/recommendedList");
var Item = require("../models/item");
var database = require("../initdb");
var nodemailer = require('nodemailer');
var reportController = require("../controllers/reportController");
var TOTAL_NUM_ITEMS = 19;
// form validation modules
const { body,validationResult } = require('express-validator/check');
const { sanitizeBody } = require('express-validator/filter');
const request = require("request");

exports.addList = [
  (req, res, next) => {
    
    var findQuery = {_id: req.body._id};

    List.findOne(findQuery, (err, list) => {
      if (err) {
        ret.errorMessage = "Internal error";
        return res.status(500).send(ret);
      }
      
      if(list){
        // If an order already exists, then it means it's in progress so we return an error for another checkout request
        var ret = {}
        ret.errorMessage = "Invalid request, order already exists";
        return res.status(401).send(ret);
      }

      var newList = new List();
      newList._id = req.body._id;
      newList.items = req.body.items;
      newList.driver_id = req.body.driver_id;
      newList.destination_addr = req.body.destination_addr;
      newList.name = req.body.name;
      newList.mobile = req.body.mobile;
      newList.status = null; // Null for now since order doesn't have a driver yet

      // save the user
      List.create(newList, (err, retList) => {
        var ret = {};
        // failure
        if (err) {
          ret.errorMessage = "Error creating new order";
          return res.status(401).send(err);
        }


        var transporter = nodemailer.createTransport({
          service: 'gmail',
          auth: {
            user: 'shopalteam@gmail.com',
            pass: 'Shopal391!'
          }
        });

        var emailItems = "";
        var totalCost = 0.0;
        var GST = 0.0;
        var costAfterTax = 0.0;

        for(var i=0; i<newList.items.length; i++){
          emailItems += '<tr><td>'+
          newList.items[i].itemName +
          '</td> <td>'+
          newList.items[i].quantity +
          '</td><td> $'+
          newList.items[i].price +
          '</td></tr>';
          totalCost+=newList.items[i].price * newList.items[i].quantity;
        }

        GST = totalCost*0.05;
        costAfterTax = GST+totalCost;

        var mailOptions = {
          from: 'shopalteam@gmail.com',
          to: newList._id,
          subject: 'So happy to see you with us!',
          html: '   <html>  '  + 
          '       <head>  '  + 
          '           <style>  '  + 
          '               #customers {  '  + 
          '                   font-family: "Trebuchet MS", Arial, Helvetica, sans-serif;  '  + 
          '                   border-collapse: collapse;  '  + 
          '                   width: 100%;  '  + 
          '               }  '  + 
          '                 '  + 
          '               #customers td, #customers th {  '  + 
          '                   border: 1px solid #ddd;  '  + 
          '                   padding: 8px;  '  + 
          '               }  '  + 
          '                 '  + 
          '               #customers tr:nth-child(even){background-color: #f2f2f2;}  '  + 
          '     '  + 
          '               #customers tr:hover {background-color: #ddd;}  '  + 
          '                 '  + 
          '               #customers th {  '  + 
          '                   padding-top: 12px;  '  + 
          '                   padding-bottom: 12px;  '  + 
          '                   text-align: left;  '  + 
          '                   background-color: #4CAF50;  '  + 
          '                   color: white;  '  + 
          '               }  '  + 
          '           </style>  '  + 
          '         <!-- Latest compiled and minified CSS -->  '  + 
          '         <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">  '  + 
          '         <link rel="stylesheet" href="Mentors.css" type="text/css">  '  + 
          '      </head>  '  + 
          '      <div>  '  + 
          '           <div class="col-md-8 col-md-offset-2" style="margin-top: 50px">  '  + 
          '               <div class="col-md-12">  '  + 
          '                   <h4>Hello ' +
                              newList.name +
                              ',</h4>'    + 
          '                   <p>  '  + 
          '                       Thank you for shopping with us. We’ll send a confirmation once your items have shipped.   '  + 
          '                       Your order details are indicated below. If you would like to view the status of your order   '  + 
          '                       or make any changes to it, please visit your Shopal App.  '  + 
          '                   </p>  '  + 
          '                   <p style="margin-top:40px">  '  + 
          '                       See you soon,  '  + 
          '                   </p>  '  + 
          '                   <p>  '  + 
          '                       -Shopal  '  + 
          '                   </p>  '  + 
          '               </div>  '  + 
          '               <div class="rectangle col-md-12">  '  + 
          '                   <table id="customers" style="margin-top:20px">  '  + 
          '                       <tr>  '  + 
          '                       <th>Item</th>  '  + 
          '                       <th>Quantity</th>  '  + 
          '                       <th>Cost/Item</th>  '  + 
          '                       </tr>  '  + 
                                  emailItems +
          '                   </table>  '  + 
          '               </div>  '  + 
          '                 '  + 
          '               <div class="col-md-12 ">  '  + 
          '                   <hr style="margin-top:15px;margin-bottom:16px;border:0;border-top:1px solid #eeeeee;margin-top:32px;margin-bottom:32px">' +
          '                   <div align="right">'+
          '                     <p >Item Subtotal: &nbsp&nbsp&nbsp  $'+
                                totalCost.toFixed(2) +
          '                     </p>' +
          '                     <p>Shipping & Handling:  $0.00</p>' +
          '                     <p>Estimated Tax (GST/HST):	 $'+
                                GST.toFixed(2) +
          '                     </p>' +
          '                     <p>Estimated Tax (PST/QST):  $0.00</p>' +
          '                     <p><b>Order Total:  $' +
                                costAfterTax.toFixed(2) +
          '                     </b></p>' +
          '                   </div>'+
          '                   <hr style="margin-top:15px;margin-bottom:16px;border:0;border-top:1px solid #eeeeee;margin-top:32px;margin-bottom:32px">  '  + 
          '                   <p style="margin:0;font-size:14px;line-height:24px;margin-top:16px;text-align:center;color:#AAAAAA">Shopal Corp. Macleod Bldg, 2356 Main Mall, Vancouver, BC V6T 1Z4</p>  '  + 
          '                   <p style="margin:0;font-size:14px;line-height:24px;margin-top:16px;text-align:center;color:#AAAAAA">© Shopal 2019</p>  '  + 
          '                   <br>  '  + 
          '               </div>  '  + 
          '           </div>  '  + 
          '      </div>  '  + 
          '  </html>  '
        };
        
        transporter.sendMail(mailOptions, function(error, info){
          if (error) {
            console.log(error);
          } else {
            console.log('Email sent: ' + info.response);
          }
        });
        // success
        return res.status(200).send(retList);
      });

    });
  }
]


exports.getList = (req, res, next) => {

  var findQuery = {_id: req.query._id};

  List.findOne(findQuery, (err, list) => {
    var ret = {};
    // failure
    if (err) {
      ret.errorMessage = "Internal error";
      return res.status(500).send(ret);
    }

    if (!list) {
      ret.errorMessage = "Invalid request";
      return res.status(401).send(ret);
    }


    // success
    return res.status(200).send(JSON.stringify(list));
  });
}

exports.recommendedLists = (req, res, next) => {
  var recommendedList = new RecommendedList();
  var newList = new List();
  newList.items = [];
  recommendedList._id = req.body._id;
  // Initialize array to all 0s
  for(var j = 0; j<TOTAL_NUM_ITEMS; j++){
    recommendedList.order[j] = 0;
  }

  for(var i = 0; i<req.body.items.length; i++){
    var itemIndex = req.body.items[i].index;
    recommendedList.order[itemIndex] = 1;
  }

  var recommendedVector;
  
  request({
    url: "https://shopalml-backend.herokuapp.com/get_recommendation",
    method: "POST",
    json: true,  
    body: recommendedList
  }, function (error, response, body){
      console.log(body.order);
      recommendedVector = body.order;
      if(error) {
        var ret = {};
        ret.errorMessage = "Error with post call to ML backend"
        ret.error = error;
        return res.status(500).send(ret);
      }

      getRecommendedList(recommendedVector).then(function(retList) {
        newList.items = retList;
        newList._id = req.body._id;
        newList.driver_id = req.body.driver_id;
        newList.destination_addr = req.body.destination_addr;
        newList.name = req.body.name;
        newList.mobile = req.body.mobile;
        newList.status = req.body.status;
        return res.status(200).send(newList); 
      });
  })
}

async function getRecommendedList(recommendedVector) {
  var i = 0;
  var items = [];
  for(i = 0; i< TOTAL_NUM_ITEMS; i++){
    if(recommendedVector[i] == 1){
      var item = await getRecommendedItem(i);
      items.push(item);
    }
  }
  console.log(items);
  return items;
}

async function getRecommendedItem(k) {
  var filterQuery = {index : k};
  var item = await Item.findOne(filterQuery);
  return item;
}

exports.getNoDriverLists = (req, res, next) => {
  var findQuery = {driver_id: null};

  List.find(findQuery, (err, list) => {
    var ret = {};
    // failure
    if (err) {
      ret.errorMessage = "Internal error";
      return res.status(500).send(ret);
    }

    if (!list) {
      ret.errorMessage = "Invalid request";
      return res.status(401).send(ret);
    }

    // success
    return res.status(200).send(JSON.stringify(list));
  });
}

exports.testRecommendation = (req,res,next) => {
  var recommendedList = new RecommendedList();
  for(var i = 0; i<TOTAL_NUM_ITEMS; i++){
    if(i == 0 || i == 1 || i == 2){
      recommendedList.order[i] = 1;
    }
    else{
      recommendedList.order[i] = 0;
    }
  }
  recommendedList._id = "ndrkrstl@gmail.com";
  return res.status(200).send(recommendedList);
}

exports.updateListDriver = (req, res, next) => {
  var findQuery = {driver_id: req.body.driver_id};

  List.findOne(findQuery, (err, list) => {
    if (err) {
      ret.errorMessage = "Internal error";
      return res.status(500).send(ret);
    }
    
    if(list){
      // If an order already exists, then it means driver already accepted one 
      var ret = {}
      ret.errorMessage = "Invalid request, driver already has an order";
      return res.status(401).send(ret);
    }

    var filterQuery = {_id: req.body._id};
    var updateDoc = {$set: { 'driver_id': req.body.driver_id, 'status': req.body.status}};

    List.updateOne(filterQuery, updateDoc, (err, list) => {
      var ret = {};

      if (err) {
        ret.errorMessage = "Invalid request";
        return res.status(401).send(ret);
      }

      // success
      return res.status(200).send(list);
    });
  });
}

exports.updateOrderStatus = (req, res, next) => {
  var filterQuery = {driver_id: req.body.driver_id};
  var updateDoc = {$set: { 'status': req.body.status }};
  console.log(req.body);
  List.updateOne(filterQuery, updateDoc, (err, list) => {
    var ret = {};

    if (err) {
      ret.errorMessage = "Invalid request";
      return res.status(401).send(ret);
    }
    console.log(JSON.stringify(list));

    List.findOne(filterQuery, (err, newList) => {
        var ret = {};
        // failure
        if (err) {
          ret.errorMessage = "Internal error";
          return res.status(500).send(ret);
        }
    
        // success
        console.log(JSON.stringify(newList));
        if(newList && req.body.status == "Delivering")
          reportController.updateReport(newList);
      });


      // success
    return res.status(200).send(list);
  });
}

exports.deleteOrder = (req, res, next) => {
  var filterQuery = {_id: req.body._id};
  var saveList = new PreviousList();
  saveList.user_id = req.body._id;
  saveList.items = req.body.items;
  saveList.driver_id = req.body.driver_id;
  saveList.destination_addr = req.body.destination_addr;
  saveList.name = req.body.name;
  saveList.mobile = req.body.mobile;
  saveList.status = req.body.status;

  List.findOneAndDelete(filterQuery, function(err, order) {
      if (err) {
        console.log("Error deleting order");
        throw err;
      }

      PreviousList.create(saveList, (err, retList) => {
        console.log(saveList);
        if(err){
          console.log("Error saving previous order");
          throw err;
        }

      });

      console.log("Deleted 1 order");
      return res.status(200).send(order);
  });
}


exports.getListOfDriver = (req, res, next) => {

  var findQuery = {driver_id: req.query.driver_id};

  List.findOne(findQuery, (err, list) => {
    var ret = {};
    // failure
    if (err) {
      ret.errorMessage = "Internal error";
      return res.status(500).send(ret);
    }

    if (!list) {
      ret.errorMessage = "Invalid request";
      return res.status(401).send(ret);
    }


    // success
    return res.status(200).send(JSON.stringify(list));
  });
}