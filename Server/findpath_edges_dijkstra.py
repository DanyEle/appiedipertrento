import heapq
import bisect

def rotate(l):
	ris = []
	for i in range(0, len(l)):
		ris.append(l[len(l)-i-1])
	return ris

#edges contains the list of the edges. Each edge is a list of three elements: the length, the pollution amount and the list of connected edges' ID
#inhibitedEdges is a list of the IDs of the inhibited edges
def findpath(edges, start, end, inhibitedEdges):
	inhibitedEdges.sort()
	dist = []
	pq = []
	prec = []
	finalList = []
	for e in edges:
		dist.append(-1)
		prec.append(-1)
	
	dist[start]=(edges[start][0]*edges[start][1])
	heapq.heappush(pq, [(edges[start][0]*edges[start][1]), start])
	while(len(pq)>0):
		front = heapq.heappop(pq)
		d = front[0]
		u = front[1];
		if(u == end):
			while u!=start:
				finalList.append(u)
				u = prec[u]
			finalList.append(u)
			return rotate(finalList)
		
		if(d<=dist[u]):
			for nei in edges[u][2]:
				i = bisect.bisect_left(inhibitedEdges,nei)
				if (i>=len(inhibitedEdges) or inhibitedEdges[i]!=nei) and (dist[nei]==-1 or d+edges[nei][0]*edges[nei][1]<dist[nei]):
					dist[nei] = d+edges[nei][0]*edges[nei][1]
					prec[nei] = u
					heapq.heappush(pq,[dist[nei], nei])
				

