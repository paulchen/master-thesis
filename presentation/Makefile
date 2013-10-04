DIA_DIR		= figures/dia

DIA		= dia
INKSCAPE	= inkscape

.PHONY: clean figures clean-presentation clean-figures presentation presentation-exam

define dia-command
$(DIA) --export $@ $<
endef

define inkscape-command
$(INKSCAPE) $< --export-pdf=$@
endef

.PHONY: clean clean-figures figures

all: presentation presentation-exam

presentation: presentation.pdf

presentation-exam: presentation-exam.pdf

figures: $(addsuffix .pdf, $(basename $(wildcard $(DIA_DIR)/*.dia)))

presentation.pdf: presentation.tex figures
	pdflatex -shell-escape presentation.tex
	pdflatex -shell-escape presentation.tex

presentation-exam.pdf: presentation-exam.tex figures
	pdflatex -shell-escape presentation-exam.tex
	pdflatex -shell-escape presentation-exam.tex

$(DIA_DIR)/%.pdf: $(DIA_DIR)/%.svg
	$(inkscape-command)

$(DIA_DIR)/%.svg: $(DIA_DIR)/%.dia
	$(dia-command)

clean: clean-presentation clean-figures

clean-presentation:
	rm -f *.pdf *.aux *.log *.nav *.out *.snm *.toc *.bbl *.blg *.vrb *.aex *.pyg

clean-figures:
	rm -f figures/dia/*.pdf

