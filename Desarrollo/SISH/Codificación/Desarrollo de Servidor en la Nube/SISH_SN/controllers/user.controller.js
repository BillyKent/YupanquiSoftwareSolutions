const User = require('../models/User');

exports.updateUser = async (req, res) => {
    const { username } = req.params;
    const user = {};
    user.username = req.body.username;
    user.password = req.body.password;
    user.rol = req.body.rol;
    user.UserImage = req.body.UserImage;

    User.findOneAndUpdate({ username })
        .then(user => {
            if (!user) {
                return res.status(404).send({
                    message: "No se encontro usuario " + username
                });
            }
            return res.send(user);
        }).catch(err => {
            return res.status(500).send({
                message: "Error de actualizacion al usuario " + username
            });
        });
};

exports.deleteUser = async (req, res) => {
    const { username } = req.params;
    User.findOneAndDelete({ username })
        .then(user => {
            if (!user) {
                return res.status(404).send({
                    message: "No se encontro el usuario " + username
                });
            }
            return res.status(200).send({ message: "Usuario eliminado" });
        }).catch(err => {
            return res.status(404).send({
                message: "No se encontro el usuario " + username
            });
        });

}
exports.allUsers = async (req,res) => {
    
    const users  = await User.find({});
    
    if ( users.length == 0 )
        
        return res.status(205).send({message:'No user found'});

    return res.status(200).send(users);
}

exports.getUserByEmail = async (req,res) => {
    
    const user = await User.findOne({email});

    if ( !user )
        return res.status(205).send({message:'The user was not found with the email entered'}) 

    return res.status(200).send(user);
}