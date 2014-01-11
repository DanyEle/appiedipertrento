finalList=[]
l=0
P=0
locl=0
locP=0
locList=[]

def findpath(adilist,roads,analizedPoint,arrivo,D):
	global finalList
	global l
	global P
	global locl
	global locP
	global locList
	if analizedPoint==arrivo:
		if (abs(locl-D)*locP<abs(l-D)*P) or P==0:
			l=locl
			P=locP
			finalList=locList[:]
	if (locl<D or (abs(locl-D)*locP<abs(l-D)*P and locl<2*D)):
		for adi in adilist[analizedPoint]:
			arco = roads[adi]
			if arco[4]==0:
				arco[4]=1
				locList.append(adi)
				locl+=arco[2]
				locP+=arco[3]
				if analizedPoint==arco[0]:
					findpath(adilist,roads,arco[1],arrivo,D)
				if analizedPoint==arco[1]:
					findpath(adilist,roads,arco[0],arrivo,D)
				locList.pop()
				locl-=arco[2]
				locP-=arco[3]
				arco[4]=0
	return finalList


