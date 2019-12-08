import RPi.GPIO as GPIO
import threading
import time

from ConexionBD import ConexionBD

class ControladorApertura(object):
    __instancia = None

    def __new__(cls):
        if ControladorApertura.__instancia is None:
            ControladorApertura.__instancia = object.__new__(cls)
        return ControladorApertura.__instancia

    def __init__(self):
        self.teclado = PinNumerico()
        self.cerradura = Cerradura()

    def VerificarSolicitudApertura(self, rostros):
    	self.verificarSolicitudLocal(rostros)
        self.verificarSolicitudRemota()

    def verificarSolicitudLocal(self, rostros):
        #print rostros
        for rostro in rostros:
            if rostro[2] == self.teclado.Leer():
                self.cerradura.Abrir()
                break
    def verificarSolicitudRemota(self):
    	sistema = ConexionBD().ObtenerVariablesSistema()
        if sistema['instruccionCerradura'] == True:
            self.cerradura.Abrir()
            ConexionBD().NotificarCambioApertura(sistema, False)

class PinNumerico():

    VALORES = [
        [1,2,3],
        [4,5,6],
        [7,8,9],
        ["*",0,"#"]
    ]

    FILAS         = [25,24,23,18]
    COLUMNAS      = [27,17,4]

    def __init__(self):
        GPIO.setmode(GPIO.BCM)
        
        for j in range(len(self.COLUMNAS)):
            GPIO.setup(self.COLUMNAS[j], GPIO.OUT)
            GPIO.output(self.COLUMNAS[j], 1)
        
        for i in range(len(self.FILAS)):
            GPIO.setup(self.FILAS[i], GPIO.IN, pull_up_down=GPIO.PUD_UP)

        self.tiempo_pasado = 0
        self.tiempo_refresco = 2000
        self.lectura = ''

        threading.Thread(target = self.rutina_leer).start()
    
    def Leer(self):
        return self.lectura

    def obtener_tecla(self):
        for j in range(len(self.COLUMNAS)):
            GPIO.output(self.COLUMNAS[j],0)
            for i in range(len(self.FILAS)):
                if GPIO.input(self.FILAS[i]) == 0:
                    
                    if len(self.lectura) < 4:
                        self.lectura = self.lectura + str(self.VALORES[i][j])
                    
                    self.tiempo_pasado = 0
                    
                    #print 'lectura: ' + self.lectura
                    
                    while GPIO.input(self.FILAS[i]) == 0:
                        pass
            GPIO.output(self.COLUMNAS[j], 1)
        
        self.tiempo_pasado = self.tiempo_pasado + .1
        #print self.tiempo_pasado
        if self.tiempo_pasado > self.tiempo_refresco:
            self.tiempo_pasado = 0
            #print 'Se limpio la lectura'
            self.lectura = ''
    
    def rutina_leer(self):
        while True:
            self.obtener_tecla()
            
class Cerradura():

    abierto = False


    def __init__(self):
        #GPIO.setmode(GPIO.BOARD)
        GPIO.setmode(GPIO.BCM)
        GPIO.setup(22,GPIO.OUT)
        #se crea el objeto en el pin 11 con 50 Hz
        self.pwm = GPIO.PWM(22,50)

        #self.anchoPulso = 0.0014
        #self.ciclo = self.anchoPulso * 50 * 100 # %

        self.ciclo = [6.5,4.5]
        self.pwm.start(self.ciclo[1])
        time.sleep(0.5)
        self.pwm.ChangeDutyCycle(0)


    def Abrir(self):
        if self.abierto == False:
            self.abierto = True
            self.pwm.ChangeDutyCycle(self.ciclo[0])
            time.sleep(.5)
            self.pwm.ChangeDutyCycle(self.ciclo[1])
            time.sleep(.5)
            self.pwm.ChangeDutyCycle(0)
            self.abierto = False
        #self.Abrir()
        #self.senal()

    #def Abrir(self):
        #prueba : 1.4 milisegundos --> 0.0018 seg
        #self.anchoPulso = 0.0014
        #self.ciclo = self.anchoPulso * 50 * 100 # %

    #def senal(self):
    #    self.pwm.start(self.ciclo)

    #def Abrir(self):
