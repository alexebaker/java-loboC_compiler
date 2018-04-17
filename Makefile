all: jar

SPIKE:=5

jar: compile
	@cp README.md README.txt
	@jar cvfe spike$(SPIKE).jar Main LICENSE Makefile README.txt sources.txt spike$(SPIKE)a.txt spikeb-10.txt src/ tests/ -C out .
	@rm README.txt

compile: clean
	@find src/ -name "*.java" > sources.txt
	@mkdir -p out
	@javac -d out @sources.txt

clean: FORCE
	@rm -rf out/* spike$(SPIKE).jar
	@ find . -name "*.class" -delete

test: jar
	@cd tlc && ./tlc -c 'java -jar ../spike$(SPIKE).jar' -recursive -test && cd ..

.PHONY: FORCE
