JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        Agent.java \
        AStarSearch.java \
        Engine.java \
        World.java \
        Step.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
