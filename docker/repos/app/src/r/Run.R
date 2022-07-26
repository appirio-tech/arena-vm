library(compiler)
require(methods)

args <- commandArgs(trailingOnly = TRUE)
scriptPath <- dirname(args[1])

if(length(args) != 1){
	print ("Incorrect Arguments!")
	print ("Please Follow:")
	print ("    R Run.R [Compiled File]")
}else{
	loadcmp(args[1])
}
