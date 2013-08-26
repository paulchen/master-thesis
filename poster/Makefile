FIGURES_DIR     = figures
DIA_DIR         = $(FIGURES_DIR)/dia
INKSCAPE_DIR    = $(FIGURES_DIR)/inkscape

DIA             = dia
INKSCAPE        = inkscape
GHOSTSCRIPT     = gs

.PHONY: clean dia inkscape clean-dia clean-inkscape clean-poster poster print

define cmyk-command
$(GHOSTSCRIPT) -o $@ -sDEVICE=pdfwrite -sProcessColorModel=DeviceCMYK -sColorConversionStrategy=CMYK -sColorConversionStrategyForImages=CMYK $<
endef

define dia-command
$(DIA) --export $@ $<
endef

define inkscape-command
$(INKSCAPE) $< --export-pdf=$@
endef

all: poster print
       
poster: poster.pdf

print: poster_print.pdf

dia: $(addsuffix .pdf, $(basename $(wildcard $(DIA_DIR)/*.dia)))

$(DIA_DIR)/%.pdf: $(DIA_DIR)/%_rgb.pdf
	$(cmyk-command)

$(DIA_DIR)/%_rgb.pdf: $(DIA_DIR)/%.svg
	$(inkscape-command)

$(DIA_DIR)/%.svg: $(DIA_DIR)/%.dia
	$(dia-command)

inkscape: $(addsuffix .pdf, $(basename $(wildcard $(INKSCAPE_DIR)/*.svg)))

$(INKSCAPE_DIR)/%.pdf: $(INKSCAPE_DIR)/%_rgb.pdf
	$(cmyk-command)

$(INKSCAPE_DIR)/%_rgb.pdf: $(INKSCAPE_DIR)/%.svg
	$(inkscape-command)

poster.pdf: poster.tex dia inkscape
	pdflatex -shell-escape poster.tex

poster_print.pdf: poster.tex dia inkscape
	pdflatex -shell-escape poster_print.tex

clean: clean-poster clean-dia clean-inkscape

clean-dia:
	rm -f $(DIA_DIR)/*.pdf

clean-inkscape:
	rm -f $(INKSCAPE_DIR)/*.pdf

clean-poster:
	rm -f *.pdf *.aux *.log *.nav *.out *.snm *.toc *.bbl *.blg *.vrb *.aex *.pyg

