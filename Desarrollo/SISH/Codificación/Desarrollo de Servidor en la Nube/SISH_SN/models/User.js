
const { Schema, model } = require('mongoose');

const UserSchema = new Schema({
    username: { type: String, required: true },
    password: { type: String, required: true },
    rol: { type: String, required: true },
    creadeAt: { type: Date, default: Date.now },
    updateAt: Date,
    UserImage: { type: String },
    UserPhotos: [String]

});
module.exports = model('User', UserSchema);