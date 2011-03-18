== Real Time Search (alpha) ==

= How to compile/build? =
> mvn install

= How to run TwitterStreamIndexer? =
> mnv exec:java -Dexec.mainClass="com.rtsearch.indexing.TwitterStreamIndexer"

= How to install Lingping to local Maven repo?
> mvn install:install-file -DgroupId=com.aliasi -DartifactId=lingpipe -Dversion=4.0.1 -Dpackaging=jar -Dfile=lib/lingpipe-4.0.1.jar

> mvn install:install-file -DgroupId=edu.smu.tspell.wordnet -DartifactId=jaws-bin -Dversion=1.3 -Dpackaging=jar -Dfile=lib/jaws-bin-1.3.jar