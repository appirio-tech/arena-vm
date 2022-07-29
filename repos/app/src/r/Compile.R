library(compiler)
library(methods)
library(base)

args <- commandArgs(trailingOnly = TRUE)

if(length(args) != 2){
	print ("Incorrect Arguments! Please Use:")
	print ("      R Compile.R [Script File] [Output File]") 
	print ("All arguments are needed.")
}else{
	cmpfile(args[1], args[2], options = list(supressAll = TRUE))
}
