const mongoose = require('mongoose');

const URL_MONGODB = process.env.URL_MONGODB;

mongoose.connect(URL_MONGODB,{useNewUrlParser:true,useUnifiedTopology:true});
