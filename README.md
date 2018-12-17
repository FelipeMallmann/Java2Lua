## Java 2 Lua
A Simple Java to Lua compiler using [ANTLR](https://www.antlr.org)

##### How to use
```cd /usr/local/lib
wget https://www.antlr.org/download/antlr-4.7.1-complete.jar
export CLASSPATH=".:/usr/local/lib/antlr-4.7.1-complete.jar:$CLASSPATH"
alias antlr4='java -jar /usr/local/lib/antlr-4.7.1-complete.jar'
alias grun='java org.antlr.v4.gui.TestRig'
```
1. antlr4 Java.g4
2. javac *.java
3. java Java2LuaTool Demo1.java (Demo2, Demo3, etc) >> converted.lua

Made for Translators classes.
