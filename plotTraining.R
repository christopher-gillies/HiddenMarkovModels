library(ggplot2)
tbl <- read.table("/var/folders/p5/p5e3cNFiEwyu+CaRoVbRuE+++TI/-Tmp-/test.txt",sep="\t",header=FALSE);

ggplot(tbl,aes(x=V1,y=V2)) + geom_line() + scale_x_continuous(breaks=seq(1000,21000,1000))