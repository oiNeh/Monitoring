jarRoot="build/libs/"
jarName="Monitoring-0.1.0-RELEASE.jar"
serverId="jeonghun.kang.dev"
serverIp="34.168.204.241"
serverRoot=":server"

sh `pwd`/gradlew build
ssh -i gcp_rsa_key ${serverId}@${serverIp} <<EOF
${jarRoot}${jarName} ${serverIp}${serverRoot}
fuser -k 8080/tcp
java -jar server/${jarName}
EOF
