.PHONY: clean

all: presentation.pdf

presentation.pdf: presentation.tex references.bib
	pdflatex presentation.tex
#	bibtex presentation
#	pdflatex presentation.tex
#	bibtex presentation
#	pdflatex presentation.tex

clean:
	rm -f presentation.pdf presentation.aux presentation.log presentation.nav presentation.out presentation.snm presentation.toc presentation.bbl presentation.blg presentation.vrb

