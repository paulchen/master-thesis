.PHONY: clean

all: poster.pdf

poster.pdf: poster.tex references.bib
	pdflatex poster.tex
	bibtex poster
	pdflatex poster.tex
	bibtex poster
	pdflatex poster.tex

clean:
	rm -f poster.pdf poster.aux poster.log poster.nav poster.out poster.snm poster.toc poster.bbl poster.blg poster.vrb

