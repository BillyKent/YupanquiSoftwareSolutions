
/** Importing user model from directory */
const User = require('../models/User');

module.exports = {
    verifyPinCode  : async (code) => {

        const user = await User.findOne({code});
        if ( !user){
            return false;
        }
        return true;
    },
    verifyEmail : async (email) =>{
        const email = await User.findOne({email});
        
        if ( !user ){
            return false;
        }
        return true;
    }
}