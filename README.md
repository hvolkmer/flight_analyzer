# flight_analyzer

A clojure program that analyses gps flight data and shows a graph of the altitude over time.

## Usage

Create a flight-profile.clj file with a format like this:

    {:data-file "/Users/hv/2014-07-12.txt"
     :enroute [
        {:start "2014-07-12T10:21:01" :end "2014-07-12T10:49:11" :altitude 2200}
        {:start "2014-07-12T10:50:12" :end "2014-07-12T12:04:51" :altitude 2300}
        ]
    }
    
The referenced data file should contain two colums with a header:

    time elevation
    2014-07-12T12:04:41 2208.005253
    2014-07-12T12:04:51 2201.443573

Then run the project (e.g. via leinigen) giving the profile file as parameter

    lein run flight-profile.clj
    
## Creating data files

To create the data files use [gpsbabel](http://www.gpsbabel.org/)

    gpsbabel -t -i wintec_tes -f 14-07-12.TES -o gpx -F 2014-07-12.gpx

Then use [gpxplot](http://gpxplot.googlecode.com/svn/trunk/gpxplot.py) to conver the gps into the data-txt:

    gpxplot.py -E 2014-07-12.gpx > 2014-07-12.txt

## License

Copyright © 2014 Hendrik Volkmer

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
