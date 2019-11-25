from pymongo import MongoClient
import base64, glob, os
import time, threading, json

class ConexionBD(object):
    __instancia = None
    credenciales = 'mongodb://carloslrsa:carloslrsa1@ds145113.mlab.com:45113/safe_home'
    direccionFotos = 'bd_local/fotos/'
    direccionDataset = 'bd_local/dataset/'
    direccionBd = 'bd_local/bd.txt'

    def __new__(cls):
        if ConexionBD.__instancia is None :
            ConexionBD.__instancia = object.__new__(cls)
        return ConexionBD.__instancia	

    def __init__(self):
        self.cliente = MongoClient(ConexionBD.credenciales)
        self.bd = self.cliente['safe_home']

    def ObtenerFotos(self):
        coleccionFotos = self.bd['fotos']
        cursor = coleccionFotos.find({})

        i = 0
        
        archivoBd = open(ConexionBD.direccionBd, 'w')
        
        self.eliminar_fotos()
        
        for documento in cursor:
            j = 0
            fotos = documento['fotos']
            
            archivoBd.write(documento['_id'] + '|' + documento['nombre'] + '|' + documento['pin'] + '\n')
            for fotoBase64 in fotos:
                fotoActual = base64.b64decode(fotoBase64)
                nombreArchivo = ConexionBD.direccionFotos + str(i) + '-' + str(j) + '.jpg'

                with open(nombreArchivo, 'wb') as archivo:
                    archivo.write(fotoActual)
                j = j + 1
            i = i + 1

    def ObtenerVariablesSistema(self):
    	coleccionSistema = self.bd['sistema']
    	cursor = coleccionSistema.find({})

    	for documento in cursor:
    		sistema = documento
    		break

	#print 'Id del sistema: ' + str(sistema['_id'])
    	return sistema

    def NotificarCambioHabitante(self, sistema, cambio):
        colleccionSistema = self.bd['sistema']
        colleccionSistema.find_one_and_update({"_id": sistema['_id']},
                                                              {"$set": {
                                                                  "cambiosHabitantes" : cambio}})
     
    def NotificarCambioApertura(self, sistema, cambio):
    	colleccionSistema = self.bd['sistema']
        colleccionSistema.find_one_and_update({"_id": sistema['_id']},
                                                              {"$set": {
                                                                  "instruccionCerradura" : cambio}})



    puedeNotificarRostros = True

    def NotificarRostrosEncontrados(self, rostros):
    	if self.puedeNotificarRostros == True and len(rostros) > 0:
		self.puedeNotificarRostros = False
		sistema = self.ObtenerVariablesSistema()
	     	colleccionSistema = self.bd['sistema']
	        colleccionSistema.find_one_and_update({"_id": sistema['_id']},
	                                                              {"$set": {
	                                                                  "notificacionRostros" : True,
	                                                                  "ultimosRostrosReconocidos" : rostros}})

	        threading.Thread(target = self.rutinaNotificacionRostros, args = (sistema,)).start()

    def rutinaNotificacionRostros(self,sistema):
	
	#time.sleep(2)
	prev = time()

	while True:
		now = time()
		if now - prev > 2:
			break
		else
			pass

	colleccionSistema = self.bd['sistema']
	colleccionSistema.find_one_and_update({"_id": sistema['_id']},{"$set":{"notificacionRostros" : False,"ultimosRostrosReconocidos":[]}})
	self.puedeNotificarRostros = True


    def eliminar_fotos(self):
        fotos = glob.glob(ConexionBD.direccionFotos + '*')
        dataset = glob.glob(ConexionBD.direccionDataset + '*')
        
        for foto in fotos:
            os.remove(foto)
            
        for data in dataset:
            os.remove(data)
