# Slurp
![GitHub contributors](https://img.shields.io/github/contributors/pramodkumaryadav/slurp)
![GitHub last commit](https://img.shields.io/github/last-commit/pramodkumaryadav/slurp)

## Goal: 
- Download comics from comicsworld website: https://comicsworld.in/manga/super-commando-dhruv/


## Objectives
- [X] Download each jpg page for each comic into its own repository. 
- [X] Convert all jpg's to pdf files for each comic
- [X] Make this configurable so that if I want to download any super hero comic, I am able to do that (not just for dhruv)

## How to use: 

### To download all comics from a series.
-   Go to ```main -> resources -> application.conf``` file.
-   Change the field ```series="nagayan"``` to something of your choice and something whose config is available in the
    resources directory. Such as say visharpi whose config is available in resources; say ```series="visharpi"```  ![visharpi.conf](./images/chose-a-series.png)
-   Now go to ```test -> java -> slurp -> TestSeries``` and run test ```getAllComicsFromASeriesAsPDFs```.
-   This will download all comics into the directory ```./comics/visharpi/``` as pdfs and images. ![downloaded images](./images/downloaded-images-and-pdfs.png)

### To download a single comic from a series.
-   Go to ```main -> resources -> application.conf``` file.
-   Change the field ```series="nagayan"``` to something of your choice and something whose config is available in the
    resources directory. Such as say visharpi whose config is available in resources; say ```series="visharpi"```  ![visharpi.conf](./images/chose-a-series.png)
-   Go to ```main -> resources -> nagayan.conf``` file.
-   Change the field ```singleComicUrl="whatever url is here..."``` to something of your choice from ```the same series```. 
    Something like this ![](./images/copying-a-comic-url-from-website.png)
-   Now go to ```test -> java -> slurp -> TestComics``` and run test ```getASingleComicAsPDF```.
-   This will download the comic whose URL you put in that series config into the directory, say ```./comics/visharpi/``` as pdfs and images. ![downloaded images](./images/downloaded-images-and-pdfs.png)