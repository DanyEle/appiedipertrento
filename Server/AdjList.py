import sys
import ogr

ds = ogr.Open( "territorio_line.shp" )
if ds is None:
    print "Open failed.\n"
    sys.exit( 1 )

lyr = ds.GetLayer()

lyr.ResetReading()

listaLinee = []

points = []
AdiList = []

for feat in lyr:
    listaLinee.append(feat)
    AdiList.append([])

for i in range(0, len(listaLinee)-1):
    geom0 = listaLinee[i].GetGeometryRef()
    start0x = geom0.GetX(0)
    start0y = geom0.GetY(0)
    end0x = geom0.GetX(geom0.GetPointCount()-1)
    end0y = geom0.GetY(geom0.GetPointCount()-1)
    startPointTouched=0
    endPointTouched=0
    for j in range(i+1, len(listaLinee)):
        geom1 = listaLinee[j].GetGeometryRef()
        start1x = geom1.GetX(0)
        start1y = geom1.GetY(0)
        end1x = geom1.GetX(geom1.GetPointCount()-1)
        end1y = geom1.GetY(geom1.GetPointCount()-1)
        if ((start0x==start1x and start0y==start1y) or (start0x==end1x and start0y==end1y)):
            AdiList[i].append(j)
            AdiList[j].append(i)
            startPointTouched=1
        if (end0x==start1x and end0y==start1y) or (end0x==end1x and end0y==end1y):
            AdiList[i].append(j)
            AdiList[j].append(i)
            endPointTouched=1
        if startPointTouched==0:
            points.append((start0x,start0y))
        if endPointTouched==0:
            points.append((end0x,end0y))

for i in range(0, len(listaLinee)):
    print AdiList[i]

ds = None
