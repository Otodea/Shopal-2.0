var express = require("express");
var router = express.Router();
var controller = require("../controllers/itemController");

router.post("/add-item", controller.addItem);
router.get("/get-price", controller.getPrice);
router.get("/get-items", controller.getItems);

module.exports = router;
