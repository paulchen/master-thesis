.PHONY: clean

all: poster.pdf

poster.pdf: poster.tex references.bib
	pdflatex -shell-escape poster.tex
	bibtex poster
	pdflatex -shell-escape poster.tex
	bibtex poster
	pdflatex -shell-escape poster.tex

clean:
	rm -f poster.pdf poster.aux poster.log poster.nav poster.out poster.snm poster.toc poster.bbl poster.blg poster.vrb poster.aex poster.out.pyg poster.pyg

