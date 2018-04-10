all: jar

SPIKE:=5
TEST_SUFFIX:=s4

jar: compile
	@cp README.md README.txt
	@jar cvfe spike$(SPIKE).jar Main LICENSE Makefile README.md README.txt sources.txt spike$(SPIKE).txt src/ tests/ -C out .
	@rm README.txt

compile: clean
	@find src/ -name "*.java" > sources.txt
	@mkdir -p out
	@javac -d out @sources.txt

clean: FORCE
	@rm -rf out/* spike$(SPIKE).jar
	@ find . -name "*.class" -delete

TEST_FILES:=$(wildcard tests/*.$(TEST_SUFFIX)i)
TEST_RESULTS:=$(patsubst tests/%.$(TEST_SUFFIX)i, tests/%.$(TEST_SUFFIX)o, $(TEST_FILES))

test: jar $(TEST_RESULTS)

tests/%.$(TEST_SUFFIX)o: tests/%.$(TEST_SUFFIX)i FORCE
	@echo -n "[Test $< -> $@ file: "
	@java -jar spike$(SPIKE).jar $< | diff -EZBw $@ -
	@echo -n "OK, stdin: "
	@cat $< | java -jar spike$(SPIKE).jar | diff -EZBw $@ -
	@echo "OK]"

.PHONY: FORCE
