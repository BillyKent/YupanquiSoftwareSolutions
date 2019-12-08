var express = require('express');
var router = express.Router();

const authController = require('../controllers/auth.controller');


/** functions for user authentication */
router.post('/verifyEmail', authController.emailAuth);
router.post('/verifyEmail', authController.codeAuth);

module.exports = router;