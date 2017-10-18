# WordCounter
The intention of this project is to parse large dataset and show occurrences of words.
Taking an xml file, the dataset will split each line into individual words and count their occurences.
<xs:element name="customer" type="xs:string"/>
will extract the values xs, element, name, customer, type, string
These values will be added to a hashmap accounting for the number of occurences of each.
The final values will be inserted into a MongoDB database as Keys and Values.
As parsing large files the work is divided up into units of work which defaults to 100 lines per time.
The preferred purpose of calculating the occurences woulld be the Extended HyperLogLog algorithm however getting this to work was problematic.

Running the file can be done with

java –Xmx8192m -jar challenge.jar –source dump.xml –mongo [hostname]:[port] [-lines int] [-test bool]

The values of -lines and -test are optional with the parameters being defaults to 100 and true.
This allows for an adjustment of the workload, with the test parameter being true only 5 iterations will run.
