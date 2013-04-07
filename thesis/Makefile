all: thesis.pdf

thesis.pdf: *.tex chapters/*.tex references.bib 
	pdflatex thesis
	bibtex thesis
	makeglossaries thesis
	pdflatex thesis
	makeglossaries thesis
	pdflatex thesis

clean:
	rm -f *.log *.bbl *.aux *.blg *.log *.out *.toc *~ *.pdf chapters/*~ chapters/*backup *.glg *.glo *.gls *.ilg *.ind *.ist *.xdy *backup

