#MAKEFILE V2
CLASS_OUTPUT = ../EXE/output/

JAR_OUTPUT = ../EXE/

JGOODIES_JARS = ./lib/jgoodies-forms-1.9.0.jar:./lib/forms_rt.jar:./lib/jgoodies-binding-2.13.0.jar:./lib/jgoodies-common-1.8.1.jar:./lib/jgoodies-validation-2.5.1.jar

JAVA_SOURCES =	./src/domini/controladores/*.java \
				./src/domini/datatypes/*.java \
				./src/domini/indexs/*.java \
				./src/transversal/*.java \
				./src/persistencia/*.java \
				./src/presentacio/*.java \
				./src/excepcions/*.java

#COMPILAR PROGRAMA
compile: clean
	javac -classpath $(JGOODIES_JARS) -d $(CLASS_OUTPUT) $(JAVA_SOURCES)
	jar vxf ./lib/forms_rt.jar
	jar vxf ./lib/jgoodies-forms-1.9.0.jar
	jar vxf ./lib/jgoodies-binding-2.13.0.jar
	jar vxf ./lib/jgoodies-common-1.8.1.jar
	jar vxf ./lib/jgoodies-validation-2.5.1.jar
	mv ./com $(CLASS_OUTPUT)
	cp -r ./src/presentacio/moreOptions.png ./src/presentacio/icons $(CLASS_OUTPUT)presentacio
	rm -rf META-INF __index__
	jar cvmf ./src/presentacio/MANIFEST.MF $(JAR_OUTPUT)Documenteitor.jar -C $(CLASS_OUTPUT) .

#EXECUTAR PROGRAMA
exec:
	cd $(JAR_OUTPUT); java -jar Documenteitor.jar

#COMPILAR I EXECUTAR PROGRAMA
comp_exec_program: compile
	cd $(JAR_OUTPUT); java -jar Documenteitor.jar

javadoc:
	javadoc -cp $(JGOODIES_JARS) -d "./javadoc/" -sourcepath "./src/" -subpackages domini drivers excepcions persistencia presentacio transversal

#CLEAN
clean:
	rm -rf ../EXE

