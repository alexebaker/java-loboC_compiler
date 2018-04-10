FILES+=tlc
FILES+=loboc0.s
FILES+=README.txt
FILES+=Makefile
FILES+=$(wildcard is?_*.loboc)

all:	test

test:	checklc
	./tlc -c $(LOBOC) -test

checklc:	FORCE
	@if [ "x$(LOBOC)" = "x" ] ; then echo "Use 'LOBOC=path_to_your_loboc_compiler make test'"; exit 1 ; fi


tar:	VERSION=$(shell ./tlc -version)
tar:	TAR_FILE=../tlc-$(VERSION).tar

tar:	$(FILES)
	tar cvf $(TAR_FILE) $(FILES)

.PHONY:	FORCE
