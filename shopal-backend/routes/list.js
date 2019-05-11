var express = require("express");
var router = express.Router();
var controller = require("../controllers/listController");

router.post("/add-list", controller.addList);
router.post("/update-driver", controller.updateListDriver);
router.post("/delete-order", controller.deleteOrder);
router.post("/update-order-status", controller.updateOrderStatus);
router.post("/recommendations", controller.recommendedLists);
router.post("/fake-url", controller.testRecommendation); 

router.get("/get-list", controller.getList);
router.get("/no-drivers", controller.getNoDriverLists);

router.get("/get-listofdriver", controller.getListOfDriver);

module.exports = router;
