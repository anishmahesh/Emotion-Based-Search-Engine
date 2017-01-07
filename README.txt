
######################################
#
#	Web Search Engines 
#	  CSCIGA - 2580
#	    
#	    Final Project
#	     Group 8
#
#       15, December, 2016
#  @Authors: avm358, sm7029,nk2238
#####################################



------------------------------------------------------------
	    	Commands to start crawling
------------------------------------------------------------		
	1.	Mvn install commmand :
		 mvn install:install-file -Dfile=DataPreProcessing/src/main/resources/nekohtml.jar -DgroupId=com.neko.crawl -DartifactId=nekohtml -Dversion=1.0 -Dpackaging=jar
	
	
	2.	Mvn package :
		 mvn -f DataPreProcessing/ package
		 
		 
	3.	Run command	:
		 java -cp DataPreProcessing/target/*jar-with-dependencies.jar DataPreProcessing.Crawler.Controller

	*Note : 
		i.	New seeds can be added by editing urls.txt
		ii.	We have added a sample seed
		
		
		
------------------------------------------------------------
	    	Commands to start mining
------------------------------------------------------------
	1.	Mvn install commmand :
		 mvn install:install-file -Dfile=DataPreProcessing/src/main/resources/nekohtml.jar -DgroupId=com.neko.crawl -DartifactId=nekohtml -Dversion=1.0 -Dpackaging=jar
	
	
	2.	Mvn package :
		 mvn -f DataPreProcessing/ package
		 
		 
	3.	Run command	:
		 java -cp DataPreProcessing/target/*jar-with-dependencies.jar DataPreProcessing.Mining.App

	*Note :
		 i.	We have provided the mining result for our sample corpus so that the search engine could be tested without crawling and mining
		 
------------------------------------------------------------
	    Commands to run the search engine
------------------------------------------------------------
	From the root directory

	1.	Compile Code : 
		 javac -cp "lib/*" src/edu/nyu/cs/cs2580/*.java


	2.	Index Corpus
		 java -cp src edu.nyu.cs.cs2580.SearchEngine --mode=index --options=conf/engine.conf


	3.	Run Server
		 java -cp src edu.nyu.cs.cs2580.SearchEngine --mode=serve --options=conf/engine.conf --port=25808
		
------------------------------------------------------------
	    xxxxxxxxxxxxxxxxxxxxEndxxxxxxxxxxxxxxxxxx
------------------------------------------------------------
	