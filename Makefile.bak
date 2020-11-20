
Flags = -classpath ./java-cup-11b-runtime.jar:.

all: tree/Tree.class tree/Integer.class tree/Double.class tree/String.class Lexer.class Parser.class Parser.class BufferedLexer.class SyntaxError.class Quit.class NotFinished.class Main.class

clean:
	rm -rf Parser.java
	rm -rf Lexer.java
	rm -rf *.class
	rm -rf tree/*.class

Main.class : Main.java Parser.java
	javac $(Flags) Main.java

tree/Tree.class : tree/Tree.java 
	javac tree/Tree.java

tree/Integer.class : tree/Integer.java
	javac tree/Integer.java

tree/Double.class : tree/Double.java
	javac tree/Double.java

tree/String.class : tree/String.java
	javac tree/String.java

VarStore.class : VarStore.java
	javac VarStore.java 

Lexer.java: tokenizer.jflex
	jflex tokenizer.jflex

Lexer.class : Lexer.java
	javac $(Flags) Lexer.java

Parser.class : Parser.java
	javac $(Flags) Parser.java

Parser.java : grammar.cup 
	java -jar java-cup-11b.jar -parser Parser -dump_states grammar.cup

BufferedLexer.class : BufferedLexer.java
	javac $(Flags) BufferedLexer.java

SyntaxError.class : SyntaxError.java
	javac SyntaxError.java

Quit.class : Quit.java
	javac Quit.java

NotFinished.class : NotFinished.java
	javac $(Flags) NotFinished.java

TopDown.class : TopDown.java
	javac $(Flags) TopDown.java

exec : Main.java
	java $(Flags)  Main
