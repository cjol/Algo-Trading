sed -i "s/<include>.*<\/include>/<include>\*\*\/$1\*<\/include>/g" samples/pom.xml
mvn package -DskipTests=true -pl samples
cp samples/target/samples.jar $2

