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

wget https://github.com/kurisumakise2011/graph-editor/raw/master/artifactory/standalone-jar-with-dependencies.jar
mv standalone-jar-with-dependencies.jar standalone.jar
java -jar standalone.jar
