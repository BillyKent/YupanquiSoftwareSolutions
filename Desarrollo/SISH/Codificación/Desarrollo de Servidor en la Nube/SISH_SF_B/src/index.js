const express = require('express');
const app = express();
const morgan = require('morgan');

//configuracion
app.set('port', process.env.PORT || 8080);

//middleware
app.use(morgan('dev'));
app.use(express.urlencoded({ extended: false }));
app.use(express.json());

//rutas


//levantar server
app.listen(app.get('port'), () => {
    console.log("Servidor en puerto: " + app.get('port'));
});