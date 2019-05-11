var Report = require("../models/report");
var database = require("../initdb");
var nodemailer = require('nodemailer');

// form validation modules
const { body,validationResult } = require('express-validator/check');

function updateReport(list){

        var i = 0;
        var length = list.items.length;
        var myobj;

        for(i =0; i<length; i++){
            myobj = list.items[i];
            updateItem(myobj);
        }
}

module.exports.updateReport = updateReport; 


function updateItem(myObj){

    var findQuery = {itemName: myObj.itemName};

    Report.findOne(findQuery, (err, report) => {
        if(report){

            var quantity = parseInt(report.quantity, 10) + parseInt(myObj.quantity, 10);
            var updateDoc = {$set: { 'quantity': quantity }};

            Report.updateOne(findQuery, updateDoc, (err, updatedReport) => {
                var ret = {};
            
                if (err) {
                ret.errorMessage = "Invalid request";
      //          return res.status(401).send(ret);
                }
                // success
                //return res.status(200).send(updatedReport);
            });
            
        }
        
        else{
            var report = new Report();
            report.itemName = myObj.itemName;
            report.imageUrl = myObj.imageUrl;
            report.price = myObj.price;
            report.brand = myObj.brand;
            report.quantity = myObj.quantity;
            report.description = myObj.description;


            // save the report
            report.save((err) => {
            var ret = {};
            // failure
            if (err) {
                ret.errorMessage = "Invalid request";
 //               return res.status(401).send(ret);
            }
            
            // success
            //return res.status(200).send(report);
            });
        }
    });
  }



exports.sendReport = (req, res, next) => {

    Report.find((err, curReport) => {
        var ret = {};
        // failure
        if (err) {
          ret.errorMessage = "Internal error";
          return res.status(500).send(curReport);
        }
    
        console.log(JSON.stringify(curReport) + "\n");
        console.log(curReport.length + "\n");

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

        for(var i=0; i<curReport.length; i++){
            emailItems += '<tr><td>'+
            curReport[i].itemName +
            '</td> <td>'+
            curReport[i].quantity +
            '</td><td> $'+
            curReport[i].price +
            '</td></tr>';
            totalCost+=curReport[i].price * curReport[i].quantity;
        }

        GST = totalCost*0.05;
        costAfterTax = GST+totalCost;

        var mailOptions = {
        from: 'shopalteam@gmail.com',
        to: 'amir.akp1@gmail.com',
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
                            "Shopal owner" +
                            ',</h4>'    + 
        '                   <p>  '  + 
        '                       Thank you for being a Shopal partner.   '  + 
        '                       Your report summary details are indicated below.    '  + 
        '                       If you have have any question or concern regarding the report email us directly at shopalteam@gmail.com.'  + 
        '                   </p>  '  + 
        '                   <p style="margin-top:40px">  '  + 
        '                       Thank you for your partnership,  '  + 
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
        '                     <p >Item Subtotal: $'+
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
        
        Report.deleteMany({}, function(err, report) {
            if (err) {
              console.log("Error deleting report");
              throw err;
            }
            console.log("Deleted the report");
            return res.status(200).send(report);
        });
    });
}
  