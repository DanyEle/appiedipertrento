import numpy

#poi è la lista dei punti. Ogni lista contiene una lista di 2 elementi: le coordinate x e y
#sou e una lista di due elementi: le coordinate x e y del punto di cui cercare il vicino
#la funzione ritorna la posizione in poi del punto più vicino a sou

def nns(poi, sou):
	points = numpy.array(poi)
	source = numpy.array(sou)
	return numpy.argmin(((points - source)*(points - source)).sum(1))
