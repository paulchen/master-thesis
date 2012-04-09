all: thesis.pdf

thesis.pdf: thesis.tex chapters/*.tex references.bib
	pdflatex thesis
	bibtex thesis
	pdflatex thesis
	pdflatex thesis

clean:
	rm -f *.log *.bbl *.aux *.blg *.log *.out *.toc *~ *.pdf chapters/*~ chapters/*backup

