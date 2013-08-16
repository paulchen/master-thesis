FIGURES_DIR	= figures
DIAGRAMS_DIR	= $(FIGURES_DIR)/diagrams

DIA		= dia
INKSCAPE	= inkscape

.PHONY: clean figures clean-thesis clean-figures

define dia-command
$(DIA) --export $@ $<
endef

define inkscape-command
$(INKSCAPE) $< --export-pdf=$@
endef

all: thesis.pdf

figures: $(addsuffix .pdf, $(basename $(wildcard $(FIGURES_DIR)/*.dia))) $(addsuffix .pdf, $(basename $(wildcard $(DIAGRAMS_DIR)/*.dia)))

thesis.pdf: *.tex chapters/*.tex references.bib figures
	pdflatex -shell-escape thesis
	bibtex thesis
	makeglossaries thesis
	pdflatex -shell-escape thesis
	makeglossaries thesis
	pdflatex -shell-escape thesis

$(FIGURES_DIR)/%.pdf: $(FIGURES_DIR)/%.svg
	$(inkscape-command)

$(FIGURES_DIR)/%.svg: $(FIGURES_DIR)/%.dia
	$(dia-command)

clean: clean-thesis clean-figures

clean-thesis:
	rm -f *.log *.bbl *.aux *.blg *.log *.out *.toc *~ *.pdf chapters/*~ chapters/*backup *.glg *.glo *.gls *.ilg *.ind *.ist *.xdy *backup *.lof *.lot *.aex *.lolistingx *.lomintlisting *.pyg

clean-figures:
	find $(FIGURES_DIR) -name "*.svg" -delete
	find $(FIGURES_DIR) -name "*.pdf" -delete

