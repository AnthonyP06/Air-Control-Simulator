# Air-Control-Simulator
Simulation of Air Traffic Control. Multithreading and Networks (Java Programming)

------------------------------------------------------------------

System requirements
-------------------
  - 2 PCs linked to an Internet network. If this network uses a PROXY, you must configure it in Eclipse.
  - Eclipse (recommended), or a similar IDE to import and run the project. This IDE must be installed on both PCs.

How can I use this simulator ?
------------------------------

1. Prepare a CSV file for each PC (i.e. for each airport) named "airport_name.csv". For instance, if you want to simulate the air traffic control of an airport named "Paris", the file must be "Paris.csv". All available airports can be consulted in "AeroportEnum" enum class.
2. Change in "Main" class: the "aeroportName" field as the name of the airport. (eg: "Paris").
3. Change in "AeroportEnum": hostnames of all PCs used during the simulation.
4. Run "Main".
5. Get the final CSV file named as "arrival_airport_Arrivees.csv". For instance, for aircrafts landing at Paris, you will get a CSV named "Paris_Arrivees.csv".

Miscellaneous
-------------
This project might contain bugs, it is not a steady version and cannot be used for a commercial use. It is no more than a study project.
