const User = require('../models/User');

exports.crearUser = async (req, res) => {
    const { nombre } = req.body;

    const user = await User.findOne({ nombre });
    if (user) {
        res.status(400).send({ Error: 'Usuario Existente' });
    } else {
        const newUser = new User({});
        newUser.username = req.body.username;
        newUser.password = req.body.password;
        newUser.rol = req.body.rol;
        newUser.UserImage = req.body.UserImage;
        await newUser.save();
        res.status(200).send({ Mensaje: 'Creado' });
    }
};

exports.actualizarUser = async (req, res) => {
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

exports.eliminarUsuario = async (req, res) => {
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
exports.listarUsuarios = async (req, res) => {
    User.find()
        .then(users => {
            return res.status(200).send(users);
        }).catch(err => {
            return res.status(500).send({
                message: err.message || "Error de Servidor"
            });
        });
};
