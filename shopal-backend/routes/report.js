var express = require('express');
var router = express.Router();
var controller = require("../controllers/reportController");

router.post("/send-report", controller.sendReport);

module.exports = router;
