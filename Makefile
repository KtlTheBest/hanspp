
Flags = -classpath ./java-cup-11b-runtime.jar:.

Main.class : Main.java
	javac $(Flags) Main.java

Tests.class : Tests.java
	javac $(Flags) Tests.java

Program.class : Program.java
	javac $(Flags) Program.java

ScanError.class : ScanError.java
	javac $(Flags) ScanError.java

SemanticError.class : SemanticError.java
	javac $(Flags) SemanticError.java

ast/Tree.class : ast/Tree.java
	javac ast/Tree.java

ast/Bool.class : ast/Bool.java
	javac ast/Bool.java

ast/Char.class : ast/Char.java
	javac ast/Char.java

ast/Integer.class : ast/Integer.java
	javac ast/Integer.java

ast/Double.class : ast/Double.java
	javac ast/Double.java

ast/Pointer.class : ast/Pointer.java
	javac ast/Pointer.java

ast/Identifier.class : ast/Identifier.java
	javac ast/Identifier.java

ast/String.class : ast/String.java
	javac ast/String.java

ast/Select.class : ast/Select.java
	javac ast/Select.java

ast/Apply.class : ast/Apply.java 
	javac ast/Apply.java

type/Type.class : type/Type.java
	javac $(Flags) type/Type.java

type/Void.class : type/Void.java
	javac type/Void.java

type/Bool.class : type/Bool.java
	javac type/Bool.java

type/Char.class : type/Char.java
	javac type/Char.java

type/Integer.class : type/Integer.java
	javac type/Integer.java

type/Double.class : type/Double.java
	javac type/Double.java

type/Array.class : type/Array.java
	javac type/Array.java

type/Field.class : type/Field.java
	javac type/Field.java

type/Struct.class : type/Struct.java
	javac type/Struct.java

type/StructStore.class : type/StructStore.java
	javac type/StructStore.java

type/Pointer.class : type/Pointer.java
	javac type/Pointer.java

semantic/VarData.class : semantic/VarData.java
	javac semantic/VarData.java

semantic/VarStore.class : semantic/VarStore.java
	javac semantic/VarStore.java 

Lexer.java : tokenizer.jflex
	jflex tokenizer.jflex

Lexer.class : Lexer.java 
	javac $(Flags) Lexer.java

Parser.class : Parser.java
	javac $(Flags) Parser.java

Parser.java : grammar.cup 
	java -jar java-cup-11b.jar -parser Parser -expect 1 -dump_states grammar.cup

NotFinished.class : NotFinished.java
	javac $(Flags) NotFinished.java

simulator/Memory.class : simulator/Memory.java
	javac simulator/Memory.java

simulator/CurrentPosition.class : simulator/CurrentPosition.java
	javac simulator/CurrentPosition.java

simulator/ValType.class : simulator/ValType.java
	javac simulator/ValType.java

simulator/State.class : simulator/State.java
	javac simulator/State.java

simulator/RegMap.class : simulator/RegMap.java
	javac simulator/RegMap.java

simulator/Instruction.class : simulator/Instruction.java
	javac simulator/Instruction.java

simulator/Block.class : simulator/Block.java
	javac simulator/Block.java

simulator/FunctionBody.class : simulator/FunctionBody.java
	javac simulator/FunctionBody.java

simulator/Program.class : simulator/Program.java
	javac simulator/Program.java

simulator/Error.class : simulator/Error.java
	javac simulator/Error.java

simulator/Examples.class : simulator/Examples.java
	javac simulator/Examples.java

exec : Main.java
	java $(Flags)  Main
