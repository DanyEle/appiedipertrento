finalList=[]
l=0
P=0
locl=0
locP=0
locList=[]
# l is the length of the path
# P is the pollution amount
# D is the "target length"

def findpath(adilist,roads,analizedPoint,endPoint,D,maxLength):
	global finalList
	global l
	global P
	global locl
	global locP
	global locList
	# finalPoint's memorized list's update (the memorized list is updated when locList is better)
	if analizedPoint==endPoint:
		if (abs(locl-D)*locP<abs(l-D)*P) or P==0:
			l=locl
			P=locP
			finalList=locList[:]
		
	# step
	if (locl<D or (abs(locl-D)*locP<abs(l-D)*P and (maxLength==-1 or locl<maxLength))):
		for adi in adilist[analizedPoint]:
			edge = roads[adi]
			if edge[4]==0: # if not touched
				edge[4]=1 # touched
				locList.append(adi) # new road in locList
				locl+=edge[2] # longer path
				locP+=edge[3] # more polluted path
				if analizedPoint==edge[0]:
					findpath(adilist,roads,edge[1],endPoint,D)
				if analizedPoint==edge[1]:
					findpath(adilist,roads,edge[0],endPoint,D)
				locList.pop()
				locl-=edge[2]
				locP-=edge[3]
				edge[4]=0
	return finalList


