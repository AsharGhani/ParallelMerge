#!/bin/bash
userName=$1
repositoryName=$2
CIP=$3
CP=$4

java -jar MergeClient.jar $userName $repositoryName -allBranches -CIP=$CIP -CP=$CP
print "java -jar MergeClient.jar $userName $repositoryName -allBranches -CIP=$CIP -CP=$CP "
