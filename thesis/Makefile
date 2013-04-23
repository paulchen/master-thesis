FIGURES_DIR	= figures
DIAGRAMS_DIR	= $(FIGURES_DIR)/diagrams

DIA		= dia

.PHONY: clean figures

define dia-command
$(DIA) --export $@ $<
endef

all: thesis.pdf

figures: $(addsuffix .eps, $(basename $(wildcard $(FIGURES_DIR)/*.dia))) $(addsuffix .eps, $(basename $(wildcard $(DIAGRAMS_DIR)/*.dia)))

thesis.pdf: *.tex chapters/*.tex references.bib figures
	pdflatex thesis
	bibtex thesis
	makeglossaries thesis
	pdflatex thesis
	makeglossaries thesis
	pdflatex thesis

$(FIGURES_DIR)/%.eps: $(FIGURES_DIR)/%.dia
	$(dia-command)

$(DIAGRAMS_DIR)/%.eps: $(DIAGRAMS_DIR)/%.dia
	$(dia-command)

clean:
	rm -f *.log *.bbl *.aux *.blg *.log *.out *.toc *~ *.pdf chapters/*~ chapters/*backup *.glg *.glo *.gls *.ilg *.ind *.ist *.xdy *backup *.lof *.lot
	find $(FIGURES_DIR) -path $(FIGURES_DIR)/titlepage -prune -o -name "*.eps" -print | xargs rm -f

