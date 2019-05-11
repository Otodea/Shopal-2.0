var express = require('express');
var router = express.Router();
var controller = require("../controllers/userController");

router.post("/add-user", controller.addUser);
router.get("/get-user", controller.getUser);
router.post("/login", controller.login);

module.exports = router;
