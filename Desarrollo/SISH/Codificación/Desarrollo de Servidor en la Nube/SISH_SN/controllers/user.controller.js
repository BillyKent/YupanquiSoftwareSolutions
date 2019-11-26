const User = require('../models/User');

exports.crearUser = async (req, res) => {
    const { nombre } = req.body;

    const user = await User.findOne({ nombre });
    if (user) {
        res.status(400).send({ Error: 'Usuario Existente' });
    } else {
        const newUser = new User({});
        newUser.nombre = req.body.nombre;
        newUser.password = req.body.password;
        newUser.rol = req.body.rol;
        newUser.UserImage = req.body.UserImage;
        await newUser.save();
        res.status(200).send({ Mensaje: 'Creado' });
    }
};