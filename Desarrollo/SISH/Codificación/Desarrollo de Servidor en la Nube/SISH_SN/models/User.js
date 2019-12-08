
const { Schema, model } = require('mongoose');

const UserSchema = new Schema({
    username: { type: String, required: true },
    password: { type: String, required: true },
    role: { type: String, required: true },
    creadeAt: { type: Date, default: Date.now },
    updateAt: Date,
    UserImage: { type: String },
    UserPhotos: [String],
    firstname: { type: String, required:true },
    lastname: { type: String , required:true },
    email: { type: String, required: true },
    code: { type: Number, required: true}

});
module.exports = model('User', UserSchema);