all: jar

SPIKE:=5b

jar: compile
	@cp README.md README.txt
	@jar cvfe loboc.jar Main LICENSE Makefile README.md README.txt sources.txt spike5a.txt spikeb-10.txt HP_AppA.pdf spike5-grammar.txt spike5-type-rules.txt src/ tests/ tlc/ -C out . > /dev/null
	@rm README.txt

compile: clean
	@find src/ -name "*.java" > sources.txt
	@mkdir -p out
	@javac -d out @sources.txt

clean: FORCE
	@rm -rf out/* loboc.jar
	@find . -name "*.class" -delete

test: jar
	@cd tlc && chmod +x tlc && ./tlc -c 'java -jar ../loboc.jar' -recursive -test && cd ..

.PHONY: FORCE
