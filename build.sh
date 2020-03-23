if type -p java; then
    echo found java executable in PATH
    _java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]];  then
    echo found java executable in JAVA_HOME
    _java="$JAVA_HOME/bin/java"
else
    echo "no java";
    exit 1;
fi

if [[ "$_java" ]]; then
    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    echo version "$version"
    if [[ "$version" < "11" ]]; then
        echo invalid version, must be 11
        exit 1;
    fi
fi

mkdir ~/opt/build3 -p
export WORKDIR=~/opt/build3

if [[ ! -d "$WORKDIR/maven3" ]];
  then
    cd $WORKDIR && wget https://apache.volia.net/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz
    cd $WORKDIR && tar -zxvf apache-maven-3.6.3-bin.tar.gz
    mv $WORKDIR/apache-maven-3.6.3 $WORKDIR/maven3
fi

export PATH=$WORKDIR/maven3:$PATH

if [[ ! -d "$WORKDIR/graph-editor" ]];
  then git clone git@github.com:kurisumakise2011/graph-editor.git
  else git pull origin master
fi

cd "$WORKDIR/graph-editor" && mvn clean package
export TARGET_JAR="$WORKDIR/graph-editor/demo/target/deploy"

java --module-path="$TARGET_JAR/libs" --add-modules=javafx.controls,javafx.base,javafx.fxml,javafx.graphics -jar "$TARGET_JAR"/GraphEditorDemo.jar

