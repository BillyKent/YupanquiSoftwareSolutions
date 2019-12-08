from flask import Flask, render_template, Response
from ReconocedorRostros import ReconocedorRostros
from ControladorApertura import ControladorApertura
import threading

app = Flask(__name__)

@app.route('/')
def index():
    return render_template('index.html')

def gen(reconocedor):
    while True:
        frame = reconocedor.ObtenerFotografia()
        yield (b'--frame\r\n'
               b'Content-Type: image/jpeg\r\n\r\n' + frame + b'\r\n\r\n')

@app.route('/video_feed')
def video_feed():
    return Response(gen(reconocedor),
                    mimetype='multipart/x-mixed-replace; boundary=frame')

def grabar():
    while True:
        camara.get_frame()
        print "frame"

def prueba():
    controladorApertura = ControladorApertura()
    while True:
        controladorApertura.VerificarSolicitudApertura(reconocedor.ObtenerRostros())

if __name__ == '__main__':
    
    reconocedor = ReconocedorRostros()
    threading.Thread(target = prueba).start()
    threading.Thread(app.run(host='192.168.1.13', threaded = True, debug=False)).start()


        


    #threading.Thread(app.run(host='192.168.1.13', threaded = True, debug=False)).start()