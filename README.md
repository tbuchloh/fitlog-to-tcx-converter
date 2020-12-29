![Java CI with Maven](https://github.com/tbuchloh/fitlog-to-tcx-converter/workflows/Java%20CI%20with%20Maven/badge.svg)

# fitlog-to-tcx-converter
Convert SportTracks 3.1 (https://sporttracks.mobi/) .fitlog to Garmin's .tcx files using Spring Batch Framework.

What it does:

1. Read all activities from .fitlog XML file.
2. Convert Fitlog Activity to TCX Activity:
	1. Handle manual edits/corrections to GPS track data.
	2. Compute missing distances for Trackpoints.
3. Write all TCX Activity into outout file.
4. Log some output file characteristics for manual verification purposes:
	1. Log some TrainingCenterDatabase metrics:
		1. Activity Count
		2. Total Distance Meters
		3. Total Time Seconds
	2. Log some Activity summary:
		1. Activity Id
		2. Lap Distance Meters
		3. Trackpoint Distance Meters
		4. Total Lap Time Seconds
		5. Total Lap Calories
		6. Lap Count
		7. Trackpoint Count
		8. Trackpoint Heart Rate Average
	3. If logging.level=debug log some Trackpoint information:
		1. Start Time
		2. Distance Meters
		3. Latitude
		4. Longitude
5. Validate output file schema conformance.

# Prerequisites

1. Java 14 or higher (e. g. OpenJDK 14)
2. Maven 3.5 or higher
3. Exported .fitlog files (see SportTracks Documentation)

# Usage

```
mvn spring-boot:run input.file=/path/to/foo.fitlog output.file=/path/to/foo.tcx
```

or

```
mvn clean package # creates an executable jar file
cd target
java -jar fitlog-to-tcs-converter.jar input.file=/path/to/foo.fitlog output.file=/path/to/foo.tcx
```

# Known Issues

1. Prefer BigDecimal over double in jaxb generated code.
2. Enhance Test Coverage from 74% to at least 80% (functional 
   code has more than 90% line coverage).

