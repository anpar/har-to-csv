compile:
	javac CliArgs.java
	javac TextFile.java
	javac -cp .:wink-json4j-1.1.2-incubating.jar HarFile.java
	javac -cp .:wink-json4j-1.1.2-incubating.jar har2csv.java

all: compile
	java -cp .:wink-json4j-1.1.2-incubating.jar har2csv --in in.har --out out/all.csv --filter all

domains: compile
	java -cp .:wink-json4j-1.1.2-incubating.jar har2csv --in in.har --out out/domains.csv --filter domains

clean:
	rm -rf *.class
	rm -rf out/*.csv
