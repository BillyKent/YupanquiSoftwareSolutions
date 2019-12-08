var express = require('express');
var router = express.Router();


/*importing auth controller file */

const authConfiguration = require('../configuration/authConfiguration');

module.exports = {
    emailAuth : async ( req, res ) =>{

        const {email} = req.params; 

        if( !authConfiguration.verifyEmail(email))
            return res.status(205).send({message:'Cant find a user'});

       return res.status(200).send({message:'Exist user'});
    },
    codeAuth : async ( req, res ) =>{

        const {code} = req.params;

        if( !authConfiguration.verifyPinCode(code))
            return res.status(205).send({message:'Wrong Code, try again'});

        return res.status(200).status(200).send({message:'Successful'});        
    },
    createUser : async ( req, res ) =>{

        const user = req.body;



    },
} 