Estructura de mi compilador

pom.xml:Configuracion de Maven y plugins de JFlex/CUP
programa.txt:Archivo de entrada con el codigo fuente a compilar
libreria/: Contiene jflex-full-1.9.1.jar y java-cup-11b-20160615.jar

tambien estan las carpetas main y el contenido de cada una 
jflex:Especificacion lexica (Lexer.flex)
cup:Especificacion gramatical (Parser.cup)
java:Clases del AST, Tabla de Simbolos, Interprete, C3D, Assembly y Main

El compilador tiene 2 maneras de ejecutarse dependieno del editor de codigo que este utilizando

OPCION A: MAVEN O IDE (INTELLIJ IDEA)
Maven descarga y ejecuta automaticamente los plugins de JFlex (1.9.1) y
Java CUP no hay que configurar los .jar de manera manual.

Desde la consola:
  mvn clean compile
  mvn exec:java

Si quiero probar una entrada distinto a "programa.txt":
 mvn exec:java -Dexec.args="otro_archivo.txt"

SI ES LA PRIMERA VEZ QUE EJECUTA EL CODIGO : se debe seguir estos 3 pasos para que asi Jflex y Cup generen mis arcivos lexer.java y parser.java
(Tambien es necesario en el caso de que se modifique lexer o el parser )
Desde IntelliJ IDEA:
 1. Abrir el proyecto seleccionando el archivo pom.xml.
 2. En el panel derecho Maven -> Lifecycle -> ejecutar "compile".
 3. Ejecutar la clase Main (src/main/java/Main.java).

Si ya lo ejecute y genere los dos archivos con simplemente pararme en el main y ejecutarlo alcanza, si quiero probrar otro tipo de entrada debo modificar el txt llamado programa

OPCION B: Ejecucion por consola (Usa la carpeta libreria)
Si quiero compilar y ejecutar sin Maven usando los .jar que se encuentran en "libreria/":

Paso 1 - Genero el analizador lexico:
    java -jar libreria/jflex-full-1.9.1.jar -d src/main/java/lexer src/main/jflex/Lexer.flex

Paso 2 - Genero el analizador sintactico:
    java -jar libreria/java-cup-11b-20160615.jar -destdir src/main/java/parser -package parser -parser Parser -symbols sym src/main/cup/Parser.cup

Paso 3 - Compilar las clases Java:
    En Windows:
      mkdir bin
      javac -cp "libreria/java-cup-11b-20160615.jar" -d bin src/main/java/ast/*.java src/main/java/lexer/*.java src/main/java/parser/*.java src/main/java/semantic/*.java src/main/java/interpreter/*.java src/main/java/ir/*.java src/main/java/codegen/*.java src/main/java/Main.java

Linux:
      mkdir -p bin
      javac -cp "libreria/java-cup-11b-20160615.jar" -d bin src/main/java/ast/*.java src/main/java/lexer/*.java src/main/java/parser/*.java src/main/java/semantic/*.java src/main/java/interpreter/*.java src/main/java/ir/*.java src/main/java/codegen/*.java src/main/java/Main.java

Paso 4 - Ejecutar el compilador:
En windows
      java -cp "bin;libreria/java-cup-11b-20160615.jar" Main programa.txt
En Linux / macOS:
      java -cp "bin:libreria/java-cup-11b-20160615.jar" Main programa.txt
