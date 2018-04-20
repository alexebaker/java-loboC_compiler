all: jar

SPIKE:=5b

jar: compile
	@cp README.md README.txt
	@jar cvfe spike$(SPIKE).jar Main LICENSE Makefile README.md README.txt sources.txt spike5a.txt spikeb-10.txt HP_AppA.pdf src/ tests/ tlc/ -C out . > /dev/null
	@rm README.txt

compile: clean
	@find src/ -name "*.java" > sources.txt
	@mkdir -p out
	@javac -d out @sources.txt

clean: FORCE
	@rm -rf out/* spike$(SPIKE).jar
	@ find . -name "*.class" -delete

test: jar
	@cd tlc && chmod +x tlc && ./tlc -c 'java -jar ../spike$(SPIKE).jar' -recursive -test && cd ..

.PHONY: FORCE
