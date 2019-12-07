var express = require('express');
var router = express.Router();

const userController = require('../controllers/user.controller');

/* User endpoints */
router.get('/all', userController.allUsers);
router.get('/userByEmail', userController.getUserByEmail);
router.put('/update',userController.updateUser);
router.delete('/delete', userController.deleteUser);

module.exports = router;
