import cv2, numpy, threading
from os import listdir
from os.path import isfile, join, split
from ConexionBD import ConexionBD
from PIL import Image
import threading

class ReconocedorRostros(object):
    __instancia = None

    def __new__(cls):
        if ReconocedorRostros.__instancia is None:
                ReconocedorRostros.__instancia = object.__new__(cls)
        return ReconocedorRostros.__instancia

    def __init__(self):
        
        self.rostrosReconocidos = []
        
        self.reconocedor = cv2.face.LBPHFaceRecognizer_create()
        self.reconocedor.read('trainer/trainer.yml')

        self.clasificador_rostro = cv2.CascadeClassifier("classifiers/face.xml")
        self.cambiandoTrainer = False

        self.camara = cv2.VideoCapture(0)
        self.fuente = cv2.FONT_HERSHEY_SIMPLEX

        print ('Inicializando...')
        
        self.realimentar()
        
        threading.Thread(target = self.reconocer).start()
        print('Analisis empezado')
        threading.Thread(target = self.verificarCambiosHabitantes).start()
        print('Verificacion empezado')

    def realimentar(self):
        print ('Alimentando al entrenador...')
        self.alimentar()
        print ('Alimentacion terminada.')
        print ('Empezando entrenamiento...')
        self.entrenar()
        print ('Entrenamiento terminado, listo para reconocer')
    

    def alimentar(self):
        detector=cv2.CascadeClassifier('classifiers/face.xml')
        ConexionBD().ObtenerFotos()

        direccion_fotos='bd_local/fotos'
        archivos_fotos = [ f for f in listdir(direccion_fotos) if isfile(join(direccion_fotos,f)) ]
        archivos_fotos = sorted(archivos_fotos)
        fotos = numpy.empty(len(archivos_fotos), dtype=object)

        offset=50
        
        numero_habitante = 0
        numero_foto_habitante = 0
        
        for i in range(0, len(archivos_fotos)):
            fotos[i] = cv2.imread( join(direccion_fotos,archivos_fotos[i]) )
            fotos_grises=cv2.cvtColor(fotos[i],cv2.COLOR_BGR2GRAY)
            rostros=detector.detectMultiScale(fotos_grises, scaleFactor=1.2, minNeighbors=5, minSize=(50, 50), flags=cv2.CASCADE_SCALE_IMAGE)
            
            for(x,y,w,h) in rostros:
                cv2.imwrite("bd_local/dataset/" + str(numero_habitante) +'-'+ str(numero_foto_habitante) + ".jpg", fotos_grises[y-offset:y+h+offset,x-offset:x+w+offset])
                cv2.rectangle(fotos[i],(x-50,y-50),(x+w+50,y+h+50),(225,0,0),2)
                break
            
            numero_foto_habitante = numero_foto_habitante + 1
            if numero_foto_habitante == 10:
                numero_foto_habitante = 0
                numero_habitante = numero_habitante + 1

        bd = open('bd_local/bd.txt')
        lineas = [linea.rstrip('\n') for linea in bd]
        print lineas
        self.info_habitantes_buffer = []

        for linea in lineas:
            self.info_habitantes_buffer.append(linea.split("|"))

    def entrenar(self):
        direccion_dataset = "bd_local/dataset"
        direcciones_fotos = [join(direccion_dataset, f) for f in listdir(direccion_dataset)]

        fotos = []
        labels = []
        self.cambiandoTrainer = True

        for direccion_foto in direcciones_fotos:
            foto_pil = Image.open(direccion_foto).convert('L')
            foto = numpy.array(foto_pil, 'uint8')
            label = int(split(direccion_foto)[1].split('-')[0])
            
            rostros = self.clasificador_rostro.detectMultiScale(foto)
            for (x, y, w, h) in rostros:
                fotos.append(foto[y: y + h, x: x + w])
                labels.append(label)
                break;

        self.reconocedor.train(fotos, numpy.array(labels))
        self.reconocedor.save('trainer/trainer.yml')
        self.reconocedor.read('trainer/trainer.yml')
        
        self.info_habitantes = self.info_habitantes_buffer
        print self.info_habitantes

        self.cambiandoTrainer = False


    def reconocer(self):
        while True:
            ret, imagen = self.camara.read()
            imagen_grises = cv2.cvtColor(imagen, cv2.COLOR_BGR2GRAY)
            if self.cambiandoTrainer == False:
                rostros = self.clasificador_rostro.detectMultiScale(imagen_grises, scaleFactor=1.2, minNeighbors=5, minSize=(50, 50), flags=cv2.CASCADE_SCALE_IMAGE)

                self.rostrosReconocidos = []
                rostrosReconocidosBufer = []

                for(x, y, w, h) in rostros:
                    label, conf = self.reconocedor.predict(imagen_grises[y : y + h, x : x + w])
                    cv2.rectangle(imagen, (x - 50, y - 50), (x + w + 50, y + h + 50), (255, 0, 0), 2)

                    num = 0
                    encontrado = False
                    pin = '-'
                    #print 'label inicial: ' + str(label)
                    #print self.info_habitantes
                    for info_habitante in self.info_habitantes:
                        if (num == label and conf < 70):
                            label = info_habitante[1]
                            pin = info_habitante[2]
                            encontrado = True
                            break
                        num = num + 1
                    if encontrado == False:
                        label = 'Desconocido'

                    rostrosReconocidosBufer.append([encontrado, label, pin])
                    
                    #print self.rostrosReconocidos

                    cv2.putText(imagen, label + '--' + str(conf), (x, y + h), self.fuente, 1, (255, 255, 255), 2, cv2.LINE_AA)
            self.rostrosReconocidos = rostrosReconocidosBufer
	    print self.rostrosReconocidos
	    ConexionBD().NotificarRostrosEncontrados(self.rostrosReconocidos)
            ret_1, jpeg = cv2.imencode('.jpg', imagen)
            self.foto_actual = jpeg.tobytes()

    def verificarCambiosHabitantes(self):
        while True:
            sistema = ConexionBD().ObtenerVariablesSistema()
            if sistema['cambiosHabitantes'] == True:
                self.realimentar()
                ConexionBD().NotificarCambioHabitante(sistema, False)

    def ObtenerFotografia(self):
        return self.foto_actual

    def ObtenerRostros(self):
        return self.rostrosReconocidos
