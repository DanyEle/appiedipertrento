import findpath_edges_dijkstra_length
import findpath_edges_dijkstra

def circolare(edges, start, length):
	path = []
	path2 = []
	finalPath = []
	path = findpath_edges_dijkstra_length.findpath(edges, start, length/2, [])
	end = path.pop(len(path)-1)
	if len(path)>0:
		path.pop(0)
	path2 = findpath_edges_dijkstra.findpath(edges, end, start, path)
	finalPath = [start] + path + path2
	finalPath.pop(len(finalPath)-1)
	return finalPath
