DIA_DIR		= figures/dia

DIA		= dia
INKSCAPE	= inkscape

.PHONY: clean figures clean-presentation clean-figures

define dia-command
$(DIA) --export $@ $<
endef

define inkscape-command
$(INKSCAPE) $< --export-pdf=$@
endef

.PHONY: clean clean-figures figures

all: presentation.pdf

figures: $(addsuffix .pdf, $(basename $(wildcard $(DIA_DIR)/*.dia)))

presentation.pdf: presentation.tex figures
	pdflatex -shell-escape presentation.tex
	pdflatex -shell-escape presentation.tex

$(DIA_DIR)/%.pdf: $(DIA_DIR)/%.svg
	$(inkscape-command)

$(DIA_DIR)/%.svg: $(DIA_DIR)/%.dia
	$(dia-command)

clean: clean-presentation clean-figures

clean-presentation:
	rm -f presentation.pdf presentation.aux presentation.log presentation.nav presentation.out presentation.snm presentation.toc presentation.bbl presentation.blg presentation.vrb *.aex *.pyg

clean-figures:
	rm -f figures/dia/*.pdf

