var User = require("../models/user");
var database = require("../initdb");
var nodemailer = require('nodemailer');

// form validation modules
const { body,validationResult } = require('express-validator/check');
const { sanitizeBody } = require('express-validator/filter');


exports.addUser = [

  (req, res, next) => {
    
    var findQuery = {_id: req.body._id};

    User.findOne(findQuery, (err, user) => {

      if(user){
        var ret = {};
        ret.errorMessage = "This email already exists.";
        return res.status(401).send(ret);
      }

      var user = new User();
      user._id = req.body._id;
      user.name = req.body.name;
      user.contact = req.body.contact;
      user.profile_photo = req.body.profile_photo;
      user.user_type = req.body.user_type;
      user.password = req.body.password;



      //transporter.sendMail();
      // save the user
      user.save((err) => {
        var ret = {};
        // failure
        if (err) {
          ret.errorMessage = "Invalid request";
          return res.status(401).send(ret);
        }

        var transporter = nodemailer.createTransport({
          service: 'gmail',
          auth: {
            user: 'shopalteam@gmail.com',
            pass: 'Shopal391!'
          }
        });
        
        var mailOptions = {
          from: 'shopalteam@gmail.com',
          to: user._id,
          subject: 'So happy to see you with us!',
          html: '   <!DOCTYPE html>  '  + 
          '   <html>  '  + 
          '      <table width="100%" border="0" cellspacing="0" cellpadding="0">  '  + 
          '         <tr>  '  + 
          '            <td style="text-align: center;">  '  + 
          '               <body  style="font-family:Helvetica Neue, Arial, sans-serif;width:100%;-webkit-text-size-adjust:100%;-ms-text-size-adjust:100%;margin:0;padding:0;font-size:16px;line-height:24px">  '  + 
          '         <tr>  '  + 
          '         <td>  '  + 
          '         <div>  '  + 
          '         <table cellspacing="0" cellpadding="0" border="0" width="100%" style="border-bottom:1px solid #eeeeee;margin-bottom:48px;border-collapse:collapse;min-width:100% !important;width:100% !important">  '  + 
          '         <tr>  '  + 
          '         <td align="center" style="border-collapse:collapse;text-align:center">  '  + 
          '         <h1 style="margin:0;margin-top:16px;margin-bottom:16px;margin-left:16px;margin-right:16px">  '  + 
          '         <img  height="90" alt="Republic" title="Republic" src="https://goo.gl/6dE2ze">  '  + 
          '         </h1>  '  + 
          '         </td>  '  + 
          '         </tr>  '  + 
          '         </table>  '  + 
          '         </div>  '  + 
          '         </td>  '  + 
          '         </tr>  '  + 
          '         <tr>  '  + 
          '         <td>  '  + 
          '         <table cellspacing="0" cellpadding="0" border="0" width="450" id="content-wrapper" align="center" style="border-collapse:collapse;table-layout:fixed;margin:0 auto">  '  + 
          '         <tr>  '  + 
          '         <td>  '  + 
          '         <div>  '  + 
          '         <p style="margin:0;font-size:16px;line-height:24px;color:#777777;text-align:center;font-weight:300;margin-bottom:16px">  '  + 
          '         <img width="75" height="68" src="https://republic.co/assets/mailers/signup_welcome/welcome-b2e17100521c87167e0d46ef9cc99a4949153529d71e60e8af7e8e7a98e9598b.png" alt="Welcome" style="border:0 none;height:auto;line-height:100%;outline:none;text-decoration:none;display:inline-block">  '  + 
          '         </p>  '  + 
          '         <h1 style="margin:0;font-size:30px;line-height:48px;color:#333333;font-weight:200;text-align:center;margin-bottom:16px">  '  + 
          '         Hi ' +
                    user.name.first +
          '         ! Welcome to Shopal <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/e/e6/Noto_Emoji_KitKat_263a.svg/1200px-Noto_Emoji_KitKat_263a.svg.png" alt="Smiley face" height="42" width="42">  '  + 
          '         </h1>  '  + 
          '         <p style="margin-left:10px;margin-right:10px;font-size:16px;line-height:24px;color:#777777;text-align:left;font-weight:300;margin-bottom:16px">  '  + 
          '         Thanks for creating an account with us. Having an account makes it easy to track orders, make lists, and so much more.  '  + 
          '         </p>  '  + 
          '         <p style="margin-left:10px;margin-right:10px;font-size:16px;line-height:24px;text-align:left;font-weight:300;margin-bottom:16px">  '  + 
          '         <b> Your username: <span style="color:blue">' +
                    user._id +
          '         </span></b>  '  + 
          '         </p>  '  + 
          '         <p style="margin-left:10px;margin-right:10px;font-size:16px;line-height:24px;text-align:left;font-weight:300">  '  + 
          '         See you soon,  '  + 
          '         </p>  '  + 
          '         <p style="margin-left:10px;margin-right:10px;font-size:16px;line-height:24px;text-align:left;font-weight:300;margin-bottom:10px">  '  + 
          '         -Shopal  '  + 
          '         </p>  '  + 
          '         <hr style="margin-top:15px;margin-bottom:16px;border:0;border-top:1px solid #eeeeee;margin-bottom:32px;margin-top:30px">  '  + 
          '         <table cellspacing="0" cellpadding="0" border="0" width="400" align="center">  '  + 
          '         <tr>  '  + 
          '         <td valign="middle" style="border-collapse:collapse;vertical-align:middle">  '  + 
          '         <img width="60px" height="60px" src="https://republic.co/assets/mailers/user_mailer/signup/discover-95fd5a6c059c1bd9c44c7e9a191f15a6d823e6c9ada10a29c7ea170a621ef7ea.png" alt="Discover" style="margin-top:8px;border:0 none;height:auto;line-height:100%;outline:none;text-decoration:none;display:inline-block">  '  + 
          '         </td>  '  + 
          '         <td style="padding-top:16px;padding-bottom:16px;padding-left:32px;border-collapse:collapse">  '  + 
          '         <div style="font-size:22px;line-height:32px;font-weight:300;color:#222222"><b>Products you love</b></div>  '  + 
          '         <div style="font-size:17px;line-height:32px;margin-top:8px;font-weight:300;color:#888888">  '  + 
          '         Find thousands of productsfrom the stores you already shop at.  '  + 
          '         </div>  '  + 
          '         </td>  '  + 
          '         </tr>  '  + 
          '         <tr>  '  + 
          '         <td valign="middle" style="border-collapse:collapse;vertical-align:middle">  '  + 
          '         <img width="60px" height="44px" src="https://goo.gl/zfyiaY" alt="Learn" style="margin-top:8px;border:0 none;height:auto;line-height:100%;outline:none;text-decoration:none;display:inline-block">  '  + 
          '         </td>  '  + 
          '         <td style="padding-top:16px;padding-bottom:16px;padding-left:32px;border-collapse:collapse">  '  + 
          '         <div style="font-size:22px;line-height:32px;font-weight:300;color:#222222"><b>Same-day delivery</b></div>  '  + 
          '         <div style="font-size:17px;line-height:32px;margin-top:8px;font-weight:300;color:#888888">  '  + 
          '         We make deliveries in cities like Vancouver, Toronto, Montreal and many more.  '  + 
          '         </div>  '  + 
          '         </td>  '  + 
          '         </tr>  '  + 
          '         <tr>  '  + 
          '         <td valign="middle" style="border-collapse:collapse;vertical-align:middle">  '  + 
          '         <img width="60px" height="49px" src="https://republic.co/assets/mailers/user_mailer/signup/invest-356e3dee480ada4fdd4ec4d077c6ef958b88020d0dbc853baf2986c3c0e70a66.png" alt="Invest" style="margin-top:8px;border:0 none;height:auto;line-height:100%;outline:none;text-decoration:none;display:inline-block">  '  + 
          '         </td>  '  + 
          '         <td style="padding-top:16px;padding-bottom:16px;padding-left:32px;border-collapse:collapse">  '  + 
          '         <div style="font-size:22px;line-height:32px;font-weight:300;color:#222222"><b>Save time & money</b></div>  '  + 
          '         <div style="font-size:17px;line-height:32px;margin-top:8px;font-weight:300;color:#888888">  '  + 
          '         Find exclusive deals on popular products — delivered to your front door!  '  + 
          '         </div>  '  + 
          '         </td>  '  + 
          '         </tr>  '  + 
          '         </table>  '  + 
          '         <hr style="margin-top:15px;margin-bottom:16px;border:0;border-top:1px solid #eeeeee;margin-top:32px;margin-bottom:32px">  '  + 
          '         <p style="margin:0;font-size:14px;line-height:24px;margin-top:16px;text-align:center;color:#AAAAAA">Shopal Corp. Macleod Bldg, 2356 Main Mall, Vancouver, BC V6T 1Z4</p>  '  + 
          '         <p style="margin:0;font-size:14px;line-height:24px;margin-top:16px;text-align:center;color:#AAAAAA">© Shopal 2019</p>  '  + 
          '         <br>  '  + 
          '         </div>  '  + 
          '         </td>  '  + 
          '         </tr>  '  + 
          '         </table>  '  + 
          '         </td>  '  + 
          '         </tr>  '  + 
          '         </body>  '  + 
          '         </td>  '  + 
          '         </tr>  '  + 
          '      </table>  '  + 
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
        return res.status(200).send(user);
      });

    });
  }
]



exports.getUser = (req, res, next) => {

  var findQuery = {_id: req.query._id};

  User.findOne(findQuery, (err, user) => {
    var ret = {};
    // failure
    if (err) {
      ret.errorMessage = "Internal error";
      return res.status(500).send(ret);
    }

    if (!user) {
      ret.errorMessage = "Invalid request";
      return res.status(401).send(ret);
    }



    // success
    return res.status(200).send(JSON.stringify(user));
  });
}


exports.login = (req, res, next) => {

  var findQuery = {_id: req.body._id, password: req.body.password};


  User.findOne(findQuery, (err, user) => {
    var ret = {};
    // failure
    if (err) {
      ret.errorMessage = "Internal error";
      return res.status(500).send(ret);
    }

    if (!user) {
      ret.errorMessage = "Invalid email or password.";
      return res.status(401).send(ret);
    }



    // success
    return res.status(200).send(JSON.stringify(user));
  });
}