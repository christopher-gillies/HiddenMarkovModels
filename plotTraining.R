library(ggplot2)
tbl <- read.table("/Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/HiddenMarkovModels/src/test/resources/training.data.text",sep="\t",header=FALSE);

colnames(tbl)[1] = "Number_of_Training_Observations";
colnames(tbl)[2] = "Error";

tbl$AVG_Error = filter(tbl$Error,filter=rep(1/3,3),sides=1)

pdf("/Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/HiddenMarkovModels/training.samples.pdf",width=10)
ggplot(tbl,aes(x=Number_of_Training_Observations,y=Error)) + geom_line() + scale_x_continuous(breaks=seq(1000,20000,1000))
ggplot(tbl,aes(x=Number_of_Training_Observations,y=AVG_Error)) + geom_line() + scale_x_continuous(breaks=seq(1000,20000,1000))
dev.off()




# cp /var/folders/2c/z4wvmddx7w9gnkh2qgh183080000gn/T/test.txt /Users/cgillies/Documents/workspace-sts-3.6.1.RELEASE/HiddenMarkovModels/src/test/resources/training.data.text