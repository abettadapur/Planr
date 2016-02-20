class Coordinate:
    def __init__(self, lat, long):
        self.lat = lat
        self.long = long

    def __str__(self):
        return str('%0.6f' % self.lat) + ',' + str('%0.6f' % self.long)
