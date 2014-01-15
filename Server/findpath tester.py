import findpath
A = []
B = []
nV = input("How many vertices? ")
vE = input("How many edges? ")
S = input("Start: ")
E = input("End: ")
L = input("Target length: ")
M = input("Maximum length: ")

for i in range(nV):
        nN = input("How many edges near this vertex? ")
        A.append([])
        for j in range(nN):
                N = input("Neigbor: ")
                A[len(A)-1].append(N)

for i in range(nE):
        B.append([])
        s = input("start: ")
        B[len(B)-1].append(s)
        e = input("end: ")
        B[len(B)-1].append(e)
        l = input("length: ")
        B[len(B)-1].append(l)
        p = input("pollution amount: ")
        B[len(B)-1].append(p)
        B[len(B)-1].append(0)
        B[len(B)-1].append(-1)
        
print findpath.findpath(A,B,S,E,L,M)
