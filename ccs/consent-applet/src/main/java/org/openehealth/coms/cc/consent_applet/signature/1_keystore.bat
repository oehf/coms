c:
set path=C:\Programme\Java\jdk1.6.0_18\bin
d:
cd studium/eclipse/workspace/consentapplet/src/signed
keytool -genkey -alias signFiles -keystore compstore -keypass kpi135 -dname "cn=ISIS" -storepass ab987c
Pause