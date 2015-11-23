compile:
	javac CliArgs.java
	javac TextFile.java
	javac -cp .:wink-json4j-1.1.2-incubating.jar HarFile.java
	javac -cp .:wink-json4j-1.1.2-incubating.jar har2csv.java

run:
	java -cp .:wink-json4j-1.1.2-incubating.jar har2csv --in test.har --out out.csv

clean:
	rm -rf *.class
